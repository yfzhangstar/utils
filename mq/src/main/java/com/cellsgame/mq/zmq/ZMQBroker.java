package com.cellsgame.mq.zmq;

import java.util.HashMap;
import java.util.Map;

import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZLoop;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;
import org.zeromq.ZThread;
import org.zeromq.ZLoop.IZLoopHandler;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZThread.IAttachedRunnable;


public class ZMQBroker{
	private static final int DEFAULT_RECV_HWM = 100000;
	private static final int DEFAULT_SEND_HWM = 100000;
	private static final int INIT_GROUP_CAPACITY = 1;//每组的初始大小

	
	private	static class MainSocketProc implements IAttachedRunnable{
		
		private Map<NodeId,RingArray<NodeId>> cliGroupMapping = new HashMap<>();
		
		private Map<NodeId,NodeId> cliMap = new HashMap<>();
		
		private void refCliId(byte[] groupId, byte[] cliId){
			 NodeId gid =  new NodeId(groupId);
			 RingArray<NodeId> ringArray = cliGroupMapping.get(gid);
			 boolean rebuild = false;
			 if(ringArray == null)
				 cliGroupMapping.put(gid, ringArray = new RingArray<>(INIT_GROUP_CAPACITY));
			 else
				 rebuild = true;
			 
			 NodeId cliNodeId = new NodeId(cliId, 10000);
			 NodeId cachedNode = cliMap.get(cliNodeId);
			 
			 if(cachedNode == null){
				 if(rebuild){
					 int oldSize = ringArray.getSize();
					 int newSize = oldSize+1;
					 /*
					  * 当承担负载均衡任务的RingArray的大小等同于group成员总数时，
					  * 配合group成员的ping动作，能够在宕机，断电，系统崩溃等灾难后重启时以最快的速度恢复负载均衡
					  * 又因为RingArray无法在创建后增加大小, 故当group新成员进入时，需要重建RingArray
					  */
					 RingArray<NodeId> newRing = new RingArray<>(newSize);
					 for (int i = 0; i < oldSize; i++) {
						 newRing.reenter(ringArray.next());
					 }
					 System.out.println("rebuild group:"+gid.toString()+"'s RingArray, new size:"+newRing.getSize());
					 cliGroupMapping.put(gid, ringArray = newRing);
				 }
				 cliMap.put(cliNodeId, cliNodeId);
			 }else{
				 cliNodeId = cachedNode;
				 cliNodeId.refresh();
			 }
			 ringArray.reenter(cliNodeId);
		}
		
		private ZFrame getTgtSF(ZFrame tgtGF) {
			
			if(tgtGF.getData().length>0){
				byte[] gid = tgtGF.getData();
				RingArray<NodeId> group = cliGroupMapping.get(new NodeId(gid));
				if(group == null)
					return null;
				byte[] nextNodeId = Util.getNextNodeId(group);
				if(nextNodeId == null)
					return null;
				ZFrame tgtSF = new ZFrame(nextNodeId);
				return tgtSF;
			}
			return null;
		}


		private IZLoopHandler hdlCli = new IZLoopHandler() {
			@Override
			public int handle(ZLoop loop, PollItem item, Object arg) {
				Socket socket = item.getSocket();
				ZMsg msg = ZMsg.recvMsg(socket);
				int size = msg.size();
				if(size == 3){//发送目标为broker的消息只有ping消息， 此时进行reping即可
					ZFrame fromSF = msg.peek();//ping消息头部即为来源服务器id，不从ZMsg中取出，减小开销
					ZFrame fromGF = msg.peekLast();//ping消息尾部为客户端服务器组id，同样不从ZMsg中取出，减小开销
					refCliId(fromGF.getData(), fromSF.getData());//下一帧即是fromGroup
				}else{
					ZFrame fromSF = msg.unwrap();
					ZFrame tgtSF = msg.unwrap();
					ZFrame tgtG = msg.unwrap();
					refCliId(msg.peek().getData(), fromSF.getData());//下一帧即是fromGroup
					if(tgtSF.getData().length==0)
						tgtSF = getTgtSF(tgtG);
					if(tgtSF == null)
						return 0;
					msg.wrap(fromSF);
					msg.wrap(tgtSF);
				}
				msg.send(socket);
				
				return 0;
			}

			
		};
		
		
		private IZLoopHandler hdlPipe = new IZLoopHandler() {
			@Override
			public int handle(ZLoop loop, PollItem item, Object arg) {
				ZMsg msg = ZMsg.recvMsg(item.getSocket());
				ZFrame cmdF = msg.pop();
				String cmd = new String(cmdF.getData());
				System.out.println("recv cmd:"+cmd);
				switch(cmd){
				case CMD.STOP:
						System.out.println("exec stop");
						Thread.currentThread().interrupt();
						break;
				}
				return 0;
			}
		};
		
		
		
		@Override
		public void run(Object[] args, ZContext ctx, Socket pipe) {
			byte[] id = ((String) args[0]).getBytes();
			String adr = (String) args[1];
			


			/*设置socket标识*/
			Socket broker = ctx.createSocket(ZMQ.ROUTER);
			broker.setSndHWM(DEFAULT_SEND_HWM);
			broker.setRcvHWM(DEFAULT_RECV_HWM);
			broker.setIdentity(id);
			broker.bind(adr);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			ZLoop loop = new ZLoop();
			
			loop.addPoller(new PollItem(broker, Poller.POLLIN), hdlCli, null);
			loop.addPoller(new PollItem(pipe, Poller.POLLIN), hdlPipe, broker);
			loop.start();//内部作轮询检查事件， 线程interrupt标识被设置时， 退出轮询
			ctx.close();
			System.out.println("main zmq sokcet ctx closed");
			
		}

		
	};
	
	private Socket pipe;
	
	private ZContext ctx;
	
	public void start(int ioThread, int linger, String id, String address){
		ctx = new ZContext();
		ctx.setIoThreads(ioThread);
		ctx.setLinger(linger);
		pipe = ZThread.fork(ctx, new MainSocketProc(), id, address);
	}

	public void stop() throws InterruptedException{
		if(pipe == null)
			return;
		pipe.send(CMD.STOP);
		Thread.sleep(1000);
		ctx.close();
		System.out.println("cmd zmq sokcet ctx closed");
	}
	
	
	
	
	
}
