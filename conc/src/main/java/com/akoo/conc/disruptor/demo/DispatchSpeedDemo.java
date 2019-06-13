package com.akoo.conc.disruptor.demo;

import com.akoo.conc.disruptor.DpEventHandler;
import com.akoo.conc.disruptor.DpEvt;
import com.akoo.conc.disruptor.SingleDisruptor;

public class DispatchSpeedDemo {

	private static class DemoHandler implements DpEventHandler {
		private volatile int i;//处理时仅使用一个线程故不需要任何同步
		
		private long ms;
		
		public void recordMillis(){
			ms = System.currentTimeMillis();
		}
		@Override
		public void onEvent(DpEvt event, long sequence, boolean endOfBatch) throws Exception {
			i++;
			if(i == (Integer)event.getData()){
				long ms = System.currentTimeMillis()- this.ms;
				System.out.println( i+" times dispatch cost millis:"+ms);
			}
		}
		
		
	}
	
	public static void main(String[] args) {
		DemoHandler demoHandler = new DemoHandler();
		SingleDisruptor gdp = new SingleDisruptor(777, "testThread",new ThreadGroup("testThreadGroup"),demoHandler);
		gdp.start();
		demoHandler.recordMillis();
		int testTimes = 10000000;
		
		for (int i = 0; i < testTimes; i++) {//1千万次生产分发速度测试
			gdp.publish(testTimes);
		}
		gdp.shutdown();
		
		
	}
}
