package com.akoo.conc.disruptor;

import java.util.concurrent.ThreadFactory;

import com.akoo.conc.thread.CustomThreadFactory;
import com.akoo.conc.util.Util;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Disruptor的单生产者单消费者封装
 * 经测试表明该模式10,000,000次无业务逻辑事件分发耗时500-700ms之间
 *
 * @author peterveron
 */
public class SingleDisruptor {

    private final Disruptor<DpEvt> disruptor;

    private final TranslatorOneArg t1;

    public SingleDisruptor(int bufferSize, String threadName, DpEventHandler handler) {
        this(bufferSize, threadName, null, handler);
    }

    public SingleDisruptor(int bufferSize, String threadName, ThreadGroup group, DpEventHandler handler) {
        this(bufferSize, threadName, group, ProducerType.MULTI, new YieldingWaitStrategy(), handler);
    }

    public SingleDisruptor(int bufferSize, String threadName, ThreadGroup group, ProducerType producerType, WaitStrategy waitStrategy, DpEventHandler handler) {
        this(bufferSize, new CustomThreadFactory(group, threadName), producerType, waitStrategy, handler);
    }

    @SuppressWarnings("unchecked")
	public SingleDisruptor(int bufferSize, ThreadFactory threadFactory, ProducerType producerType, WaitStrategy waitStrategy, DpEventHandler handler) {
        bufferSize = Util.getMinPowerOf2EqualsOrBiggerThan(bufferSize);
        disruptor = new Disruptor<>(new DpEvtFactory(), bufferSize, threadFactory, producerType, waitStrategy);
        disruptor.handleEventsWith(handler);
        t1 = new TranslatorOneArg();
    }
    
    public SingleDisruptor(int bufferSize, String threadName, DpWorkHandler ... handlers) {
        this(bufferSize, threadName, null, handlers);
    }
    
    public SingleDisruptor(int bufferSize, String threadName, ThreadGroup group, DpWorkHandler ... handlers) {
        this(bufferSize, threadName, group, ProducerType.MULTI, new YieldingWaitStrategy(), handlers);
    }

    public SingleDisruptor(int bufferSize, String threadName, ThreadGroup group, ProducerType producerType, WaitStrategy waitStrategy, DpWorkHandler ... handlers) {
        this(bufferSize, new CustomThreadFactory(group, threadName), producerType, waitStrategy, handlers);
    }
    
    public SingleDisruptor(int bufferSize, ThreadFactory threadFactory, ProducerType producerType, WaitStrategy waitStrategy, DpWorkHandler ... handlers) {
        bufferSize = Util.getMinPowerOf2EqualsOrBiggerThan(bufferSize);
        disruptor = new Disruptor<>(new DpEvtFactory(), bufferSize, threadFactory, producerType, waitStrategy);
        disruptor.handleEventsWithWorkerPool(handlers);
        t1 = new TranslatorOneArg();
    }
    

    public void publish(Object arg) {
        disruptor.publishEvent(t1, arg);
    }

    public void publish(Object... args) {
        disruptor.publishEvent(t1, args);
    }

    public void start() {
        disruptor.start();
    }

    public void shutdown() {
        disruptor.shutdown();
    }


}
