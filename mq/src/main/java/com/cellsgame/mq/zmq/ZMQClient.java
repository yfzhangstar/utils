package com.cellsgame.mq.zmq;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZLoop;
import org.zeromq.ZLoop.IZLoopHandler;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;
import org.zeromq.ZThread;
import org.zeromq.ZThread.IAttachedRunnable;

import com.cellsgame.conc.disruptor.DpEventHandler;
import com.cellsgame.conc.disruptor.DpEvt;
import com.cellsgame.conc.disruptor.SingleDisruptor;
import com.cellsgame.serdser.protostuff.ProtostuffUtil;


public class ZMQClient{
	private static final Map<String,Class<? extends ZMQData>> msgNameClassMapping = new HashMap<>();
	private static enum MANAGE_CMD{
		MANAGE_STOP,
		MANAGE_START,
		MANAGE_ADD,
		MANAGE_RM;
		
		private volatile int linger;

		private volatile Map<String,String> brkAdrs;

		private volatile String selfServerId;
		
		private volatile String selfGroupId;
		
		private volatile int ioThread;
		
		private volatile ZMQMsgHandler clientHandler;
		
		
		public Map<String,String> getBrkAdrs() {
			return brkAdrs;
		}

		public void setBrkAdrs(Map<String,String> routerAdrs) {
			this.brkAdrs = routerAdrs;
		}

		public int getLinger() {
			return linger;
		}

		public void setLinger(int linger) {
			this.linger = linger;
		}

		public String getSelfServerId() {
			return selfServerId;
		}

		public void setSelfServerId(String selfServerId) {
			this.selfServerId = selfServerId;

		}

		public String getSelfGroupId() {
			return selfGroupId;
		}

		public void setSelfGroupId(String selfGroupId) {
			this.selfGroupId = selfGroupId;
		}

		public int getIoThread() {
			return ioThread;
		}

		public void setIoThread(int ioThread) {
			this.ioThread = ioThread;
		}

		public ZMQMsgHandler getClientHandler() {
			return clientHandler;
		}

		public void setClientHandler(ZMQMsgHandler clientHandler) {
			this.clientHandler = clientHandler;
		}
		

	}


	
	private static class HdlDeser implements DpEventHandler{

		private ZMQMsgHandler clientHandler;

		public HdlDeser(ZMQMsgHandler clientHandler) {
			this.clientHandler = clientHandler;
		}

		public void onEvent(DpEvt event, long sequence, boolean endOfBatch) throws Exception {
			Object[] datas = (Object[]) event.getData();
			if(clientHandler==null)
				return;
			byte[] serverId = (byte[]) datas[0];
			byte[] groupId = (byte[]) datas[1];
			String className = new String((byte[])datas[2]);
			Class<? extends ZMQData> dataClass = msgNameClassMapping.get(className);
			if(dataClass == null)
				msgNameClassMapping.put(className, dataClass = (Class<? extends ZMQData>) Class.forName(className));
			ZMQData deser = (ZMQData) ProtostuffUtil.deser((byte[])datas[3], dataClass);
			clientHandler.handleEvent(new ZMQMsg(serverId, groupId, deser));
		}
	}

	private class HdlSer  implements DpEventHandler{
		
		public byte[] group;
		
		private Socket pipe;
		
		public void onEvent(DpEvt event, long sequence, boolean endOfBatch) throws Exception {
			Object objdata = event.getData();

			if(objdata instanceof ZMQMsg){
				if(pipe == null)
					return;
				ZMQMsg msg = (ZMQMsg)objdata;
				byte[] tgtServer = msg.getServer();
				byte[] tgtGroup = msg.getGroup();
				byte[] classNameData = msg.getData().getClass().getName().getBytes();
				byte[] data = ProtostuffUtil.ser(msg.getData());
				
				ZMsg send = new ZMsg();
				send.wrap(new ZFrame(group));
				send.wrap(new ZFrame(tgtGroup));//order: tgtServer, tgtGroup, group
				send.wrap(new ZFrame(tgtServer));
				send.add(classNameData);
				send.add(data);
				send.push(CMD.MSG.getBytes());
				send.send(pipe);
			}else if(objdata instanceof MANAGE_CMD){
				MANAGE_CMD cmd = (MANAGE_CMD)objdata;
				switch((MANAGE_CMD)objdata){
				case MANAGE_START:
					group = cmd.getSelfGroupId().getBytes();
					ctx = new ZContext();
					ctx.setIoThreads(cmd.getIoThread());
					ctx.setLinger(cmd.getLinger());
					pipe = ZThread.fork(ctx, new MainSocketProc(), cmd);
					break;
				case MANAGE_STOP:{
					ZMsg msg = new ZMsg();
					msg.add(CMD.STOP.getBytes());
					msg.send(pipe);
					Thread.sleep(1000);
					ctx.close();
					break;
				}
				case MANAGE_ADD:{
					ZMsg msg = new ZMsg();
					msg.add(CMD.ADD.getBytes());
					Set<Entry<String, String>> es = cmd.getBrkAdrs().entrySet();
					
					for (Entry<String, String> e : es) {
						String id = e.getKey();
						String adrs = e.getValue();
						msg.add(id.getBytes());
						msg.add(adrs.getBytes());
					}
					msg.send(pipe);
					break;
				}
				case MANAGE_RM:{
					ZMsg msg = new ZMsg();
					msg.add(CMD.REMOVE.getBytes());
					Set<String> idSet = cmd.getBrkAdrs().keySet();
					for (String id : idSet) {
						msg.add(id.getBytes());
					}
					msg.send(pipe);
					break;
				}	
					
				}
			}
		}
	}

	

	
	private	class MainSocketProc implements IAttachedRunnable{
		
		public byte[] group;
		
		private Map<NodeId,String> brkIdAdrMapping = new HashMap<>();
		
		private Map<NodeId,NodeId> hashBrkIdMapping = new HashMap<>();
		
		private RingArray<NodeId> brkQueue;
		
		private IZLoopHandler hdlBrk = new IZLoopHandler() {
			@Override
			public int handle(ZLoop loop, PollItem item, Object arg) {
				ZMsg msg = ZMsg.recvMsg(item.getSocket());
				ZFrame brkIdF = msg.unwrap();
				refBrkId(brkIdF.getData());//刷新routerId, 实现LRU
				if(msg.size()>1){//非ping消息继续处理
					ZFrame fromServerF = msg.unwrap();
					ZFrame fromGroupF = msg.unwrap();
					ZFrame bizMsgClassF = msg.pop();
					ZFrame bizMsgDataF = msg.pop();
					((SingleDisruptor)arg).publish(fromServerF.getData(),fromGroupF.getData(),bizMsgClassF.getData(),bizMsgDataF.getData());//
				}
				return 0;
			}
		};
		
		
		private IZLoopHandler hdlPipe = new IZLoopHandler() {
			@Override
			public int handle(ZLoop loop, PollItem item, Object arg) {
				ZMsg msg = ZMsg.recvMsg(item.getSocket());
				Socket broker =  (Socket)arg;
				ZFrame cmdF = msg.pop();
				String cmd = new String(cmdF.getData());
				switch(cmd){
				case CMD.STOP:
					Thread.currentThread().interrupt();
					break;
				case CMD.ADD://添加路由
					while(msg.size()>1){
						ZFrame idF = msg.pop();
						ZFrame adrsF = msg.pop();
						NodeId brkId = new NodeId(idF.getData(), ROUTER_INVALIDATE_TIME);
						addBrokerAdrs(brkId, new String(adrsF.getData()), true);
					}
					break;
				case CMD.REMOVE:
					while(msg.size()>0){
						ZFrame idF = msg.pop();
						NodeId brkId = new NodeId(idF.getData(), ROUTER_INVALIDATE_TIME);
						rmBrokerAdrs(brkId);
					}
					break;
				case CMD.MSG:
					byte[] id = Util.getNextNodeId(brkQueue);
					if(id!=null){
						msg.wrap(new ZFrame(id));//获取最优路由
						msg.send(broker);
					}
					break;
				}
				
				
				return 0;
			}
		};
		
		private IZLoopHandler hdlPing = new IZLoopHandler() {
			
			@Override
			public int handle(ZLoop loop, PollItem item, Object arg) {
				ping((Socket) arg);
				return 0;
			}
		};
		
		@Override
		public void run(Object[] args, ZContext ctx, Socket pipe) {
			MANAGE_CMD cmd = (MANAGE_CMD) args[0];
			
			deserProc = new SingleDisruptor(DESER_DISRUPTOR_BUFFER_SIZE, "zmq_deser_dsrupt_thread", null, ProducerType.MULTI, new BlockingWaitStrategy(), new HdlDeser(cmd.getClientHandler()));
			deserProc.start();
			
			Map<String, String> routerAdrs = cmd.getBrkAdrs();
			group = cmd.getSelfGroupId().getBytes();
			/*设置socket标识*/
			Socket broker = ctx.createSocket(ZMQ.ROUTER);
			broker.setSndHWM(SEND_HWM);
			broker.setRcvHWM(RECV_HWM);
			byte[] serverId = cmd.getSelfServerId().getBytes();
			broker.setIdentity(serverId);
			
			brkQueue = new RingArray<>(routerAdrs.size());
			Set<Entry<String, String>> es = routerAdrs.entrySet();
			for (Entry<String, String> e : es) {
				String idStr = e.getKey();
				String adr = e.getValue();
				broker.connect(adr);
				NodeId brkId = new NodeId(idStr.getBytes(), ROUTER_INVALIDATE_TIME);
				addBrokerAdrs(brkId, adr, false);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			ZLoop loop = new ZLoop();
			ping(broker);
			loop.addTimer(PING_INTERVAL, 0, hdlPing, broker);
			loop.addPoller(new PollItem(broker, Poller.POLLIN), hdlBrk, deserProc);
			loop.addPoller(new PollItem(pipe, Poller.POLLIN), hdlPipe, broker);
			loop.start();//内部作轮询检查事件， 线程interrupt标识被设置时， 退出轮询
			deserProc.shutdown();

		}

		private void addBrokerAdrs(NodeId brkId, String adr, boolean rebuildRing) {
			/*
			 * 当承担负载均衡任务的RingArray的大小等同于broker成员总数时，
			 * 配合broker成员的reping动作，能够在宕机，断电，系统崩溃等灾难后重启时以最快的速度恢复负载均衡
			 * 又因为RingArray无法在创建后增加大小, 故当broker新成员进入时，需要重建RingArray
			 */
			if(rebuildRing&&!hashBrkIdMapping.containsKey(brkId)){
				int oldSize = brkQueue.getSize();
				int newSize = oldSize+1;
				RingArray<NodeId> newRing = new RingArray<>(newSize);
				for (int i = 0; i < oldSize; i++) {
					newRing.reenter(brkQueue.next());
				}
				brkQueue = newRing;
			}
			brkIdAdrMapping.put(brkId, adr);
			hashBrkIdMapping.put(brkId, brkId);
			brkQueue.reenter(brkId);
		}

		private void rmBrokerAdrs(NodeId brkId) {
			brkIdAdrMapping.remove(brkId);
			hashBrkIdMapping.remove(brkId);
			brkQueue.remove(brkId);
		}
		
		/**
		 * ping 每一个Router
		 * @param broker
		 */
		private void ping(Socket broker) {
			Set<NodeId> brkIds = brkIdAdrMapping.keySet();
			for (NodeId brkId : brkIds) {
				ZMsg ping = new ZMsg();
				ping.wrap(new ZFrame(brkId.getId()));
				ping.add(group);
				ping.send(broker);
			}
		}

		
		

		protected void refBrkId(byte[] data) {
			NodeId rtId = hashBrkIdMapping.get(new NodeId(data));//NodeId的equals以及hashcode已被重写
			if(rtId == null)
				return;
			rtId.refresh();//刷新router的过期时间
			brkQueue.reenter(rtId);
		}
	};


	private	int SER_DISRUPTOR_BUFFER_SIZE = 10000;
	private int DESER_DISRUPTOR_BUFFER_SIZE = 10000;
	private int RECV_HWM = 100000;
	private int SEND_HWM = 100000;
	private int PING_INTERVAL = 5000;//ms
	private int ROUTER_INVALIDATE_TIME = 10000;//ms
	
	private SingleDisruptor serProc;

	private SingleDisruptor deserProc;

	private ZContext ctx;

	private AtomicBoolean flag = new AtomicBoolean(false);
	
	public ZMQClient(){}
	
	/**
	 * 带参数构造
	 * @param deserBufferSize	反序列化disruptor缓存元素数量
	 * @param serBufferSize		序列化disruptor缓存元素数量
	 * @param recvHWM			zmq内部接收消息缓存数量
	 * @param sendHWM			zmq内部发送消息缓存数量
	 * @param pingInterval		向路由服务器发送ping消息的时间间隔(毫秒)
	 * @param routerInvalidateTime	路由服务器失效时间
	 */
	public ZMQClient(int deserBufferSize, int serBufferSize, int recvHWM, int sendHWM,  int pingInterval, int routerInvalidateTime){
		if(deserBufferSize>=0)
			SER_DISRUPTOR_BUFFER_SIZE = deserBufferSize;
		if(serBufferSize>=0)
			DESER_DISRUPTOR_BUFFER_SIZE = serBufferSize;
		if(recvHWM>=0)
			RECV_HWM = recvHWM;
		if(sendHWM>=0)
			SEND_HWM = sendHWM;
		if(pingInterval>=0)
			PING_INTERVAL = pingInterval;
		if(routerInvalidateTime>=0)
			ROUTER_INVALIDATE_TIME = routerInvalidateTime;
	}


	public void start(ZMQMsgHandler handler, int linger,int ioThread, Map<String,String> rAdr, String serverid, String groupid){
		if(flag.compareAndSet(false, true)){
			serProc = new SingleDisruptor(SER_DISRUPTOR_BUFFER_SIZE, "zmq_ser_dsrupt_thread", null, ProducerType.MULTI, new BlockingWaitStrategy(), this.new HdlSer());
			serProc.start();
			MANAGE_CMD start = MANAGE_CMD.MANAGE_START;
			start.setSelfServerId(serverid);
			start.setSelfGroupId(groupid);
			start.setBrkAdrs(rAdr);
			start.setLinger(linger);
			start.setClientHandler(handler);
			start.setIoThread(ioThread);
			serProc.publish(start);
		}
	}

	public void stop(){
		serProc.publish(MANAGE_CMD.MANAGE_STOP);
		serProc.shutdown();
	}
	
	public void addBroker(Map<String,String> rAdr){
		MANAGE_CMD add = MANAGE_CMD.MANAGE_ADD;
		add.setBrkAdrs(rAdr);
		serProc.publish(add);
	}
	
	public void rmBroker(Set<String> brokerIds){
		MANAGE_CMD rm = MANAGE_CMD.MANAGE_RM;
		Map idMap = new HashMap();
	
		for (String id : brokerIds) {
			idMap.put(id, null);
		}
		rm.setBrkAdrs(idMap);
		serProc.publish(rm);
	}

	public void sendMsg(ZMQMsg msg){
		serProc.publish(msg);
	}


	public void setZipMode(boolean zipMode) {
		ProtostuffUtil.setZip(zipMode);
	}

}
