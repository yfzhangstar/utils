package com.cellsgame.mq.zmq.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import com.cellsgame.mq.zmq.ZMQClient;
import com.cellsgame.mq.zmq.ZMQData;
import com.cellsgame.mq.zmq.ZMQMsg;

public class ClientDemo {
	public static class TestZMQData extends com.cellsgame.mq.zmq.ZMQData{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String dataStr;
		
		
		public TestZMQData() {
		}
		
		public TestZMQData(String data){
			setDataStr(data);
		}
		
		
		@Override
		public String toString() {
			return getDataStr();
		}


		public String getDataStr() {
			return dataStr;
		}


		public void setDataStr(String dataStr) {
			this.dataStr = dataStr;
		}
	}
	
	public static void main(String[] args) {
		
		String serverid = args[0];
		String groupid = args[1];
		System.out.println("start client id:"+serverid+" group:"+groupid);
		Map<String,String> idAdrsMapping = new HashMap<>();
		for (int i = 2; i < args.length&& i+1 < args.length; i+=2) {
			idAdrsMapping.put(args[i], args[i+1]);
		}
		System.out.println("connect brokers:"+idAdrsMapping);
		AtomicInteger count = new AtomicInteger();
		ZMQClient client = new ZMQClient();
		Runtime r = Runtime.getRuntime();
		client.start((ZMQMsg msg)->{
			System.out.println();
			System.out.println(count.incrementAndGet());
		}, 1000, r.availableProcessors(), idAdrsMapping, serverid, groupid);
		
		while(true){
			Scanner sc = new Scanner(System.in);
			String cmd = sc.nextLine();
			if(cmd.startsWith("send")){
				String[] cmds = cmd.split(" ");
				for (int i = 0; i < 10000; i++) {
					try {
						String server = cmds[1];
						String group = cmds[2];
						String data = cmds[3]+i;
						System.out.println("send msg to group["+group+"]'s server["+server+"] with data:"+data);
						client.sendMsg(new ZMQMsg(server.getBytes(), group.getBytes(), new TestZMQData(data)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}else if(cmd.equals("stop")){
				client.stop();
				break;
			}
		}
	}
	
	
	
	
}
