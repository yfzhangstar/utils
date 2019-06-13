package com.akoo.conc.disruptor;

import com.lmax.disruptor.EventFactory;

public class DpEvtFactory implements EventFactory<DpEvt>{

	public DpEvt newInstance() {
		return new DpEvt();
	}
	

}
