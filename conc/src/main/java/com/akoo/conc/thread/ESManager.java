package com.akoo.conc.thread;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ESManager {
    private static Logger log = LoggerFactory.getLogger(ESManager.class);
    private Map<String, ExecutorService> threadPoolMap = new ConcurrentHashMap<>();

    public void exec(ThreadJob runnable) {
        String sign = runnable.getThreadPoolSign();
        if (sign == null) {
            log.error("线程为空, 线程名字 = null", new Throwable());
            return;
        }
        ExecutorService tp = threadPoolMap.get(sign);
        if (tp != null) {
            tp.execute(runnable);
        } else {
            log.error("线程为空, 线程名字 = " + sign, new Throwable());
        }
    }

    public ExecutorService getExecutorService(String sign) {
        return threadPoolMap.get(sign);

    }

    public ESManager(Map<String, Integer> threadPoolSetting) {
        Set<Entry<String, Integer>> es = threadPoolSetting.entrySet();
        for (Entry<String, Integer> e : es) {
            String threadPoolName = e.getKey();
            Integer threadNum = e.getValue();
            if (threadPoolName == null || threadNum == null)
                continue;
            ExecutorService threadPool;
            CustomThreadFactory cf = new CustomThreadFactory(threadPoolName);
            if (threadNum == 0) {//线程数量设置为0时构造无界线程池
                threadPool = new CustomThreadPool(0, 
                		Integer.MAX_VALUE, 
                		60L, 
                		TimeUnit.SECONDS, 
                		new SynchronousQueue<Runnable>(), 
                		cf);          
                		//Executors.newCachedThreadPool(new CustomThreadFactory(threadPoolName));
            } else {//线程数量大于1时构造固定容量线程池
                threadPool =  new CustomThreadPool(threadNum, 
                		threadNum,
                        0L, 
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        cf);
                		// Executors.newFixedThreadPool(threadNum, new CustomThreadFactory(threadPoolName));
            }
            if (threadPool != null)
                threadPoolMap.put(threadPoolName, threadPool);
        }
    }

    public void stop() {
        Set<Entry<String, ExecutorService>> es = threadPoolMap.entrySet();
        for (Entry<String, ExecutorService> e : es) {
            String poolName = e.getKey();
            ExecutorService pool = e.getValue();
            shutdownPool(poolName, pool);
        }
    }

    public void stop(String poolName) {
        ExecutorService pool = threadPoolMap.get(poolName);
        if (pool == null)
            return;
        shutdownPool(poolName, pool);
    }

    public void shutdownPool(String poolName, ExecutorService pool) {
        if (pool == null || pool.isShutdown())
            return;
        pool.shutdown();
        try {
            pool.awaitTermination(300, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {
        }
        log.warn(poolName + " finished");
    }

    public interface ThreadJob extends Runnable {
        String getThreadPoolSign();
    }
}
