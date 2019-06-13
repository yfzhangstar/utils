package com.akoo.conc.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadManager {

    private static Logger log = LoggerFactory.getLogger(ThreadManager.class);

    private static ThreadManager pool;

    private List<ThreadObj> lst;

    private Map<IThreadHolder, ThreadObj> mapping = new HashMap<>();

    private Comparator comparator = new Comparator<ThreadObj>() {
        @Override
        public int compare(ThreadObj o1, ThreadObj o2) {
            if(o1.atomic.get() < o2.atomic.get())
                return -1;
            else
                return 1;
        }
    };

    private ThreadManager(int size, String threadName){
        CustomThreadFactory cf = new CustomThreadFactory(threadName);
        lst = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ThreadObj threadObj = new ThreadObj(cf);
            lst.add(threadObj);
        }
    }

    public static ThreadManager getInstance(){
        return pool;
    }

    public static ThreadManager init(String threadName, int size){
        pool = new ThreadManager(size, threadName);
        return pool;
    }

    public void bind(IThreadHolder holder){
        if(lst == null) {
            throw new RuntimeException("thread manager not init ...");
        }
        synchronized (lst) {
            ThreadObj threadObj = lst.remove(0);
            holder.bind(threadObj.threadPool);
            lst.add(threadObj);
            threadObj.bind();
            mapping.put(holder, threadObj);
            lst.sort(comparator);
        }
    }


    public void release(IThreadHolder holder){
        if(lst == null) {
            throw new RuntimeException("thread manager not init ...");
        }
        synchronized (lst) {
            ThreadObj threadObj = mapping.get(holder);
            if(threadObj == null) return;
            threadObj.unBind();
            mapping.remove(holder);
            holder.unBind();
            lst.sort(comparator);
        }
    }

    public void stop() {
        if(lst == null) {
            log.error("thread manager not init ...");
            return;
        }
        for (ThreadObj threadObj : lst) {
            shutdownPool(threadObj.threadPool);
        }
    }

    private void shutdownPool(ExecutorService pool) {
        if (pool == null || pool.isShutdown())
            return;
        pool.shutdown();
        try {
            pool.awaitTermination(300, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {
        }
        log.warn(pool.toString() + " finished");
    }


    private class ThreadObj {

        private ExecutorService threadPool;

        private AtomicInteger atomic;

        public ThreadObj(CustomThreadFactory cf){
            threadPool =  new CustomThreadPool(1,
                    1,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    cf);
            atomic = new AtomicInteger(0);
        }

        public void unBind(){
            atomic.decrementAndGet();
        }

        public void bind(){
            atomic.incrementAndGet();
        }


    }
}
