package com.akoo.conc.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory{

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public  CustomThreadFactory(String name){
    	this(null, name);
    }
   
    public CustomThreadFactory(ThreadGroup group, String name){
    	if(group!= null)
    		this.group = group;
    	else{
            SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() :   Thread.currentThread().getThreadGroup();
    	}
        namePrefix = "pool-" +name+"-"+ poolNumber.getAndIncrement() +"-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement() ,0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

}
