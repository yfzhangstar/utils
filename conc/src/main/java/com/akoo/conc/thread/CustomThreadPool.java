package com.akoo.conc.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomThreadPool extends ThreadPoolExecutor {
	
    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();
    
    private static Logger log = LoggerFactory.getLogger(CustomThreadPool.class);
	

	public CustomThreadPool(int corePoolSize,
			int maximumPoolSize,
			long keepAliveTime,
			TimeUnit unit,
			BlockingQueue<Runnable> workQueue,
			ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, defaultHandler);
	}
	
	
    protected void afterExecute(Runnable r, Throwable t) { 
    	if(t != null){
    		log.error("", t);
    	}
    }




}
