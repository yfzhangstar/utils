package com.akoo.conc.disruptor;

import com.lmax.disruptor.EventHandler;

public interface DpEventHandler extends EventHandler<DpEvt> {

    void onEvent(DpEvt event, long sequence, boolean endOfBatch) throws Exception;

}
