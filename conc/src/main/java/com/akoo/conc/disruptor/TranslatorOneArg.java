package com.akoo.conc.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;

public class TranslatorOneArg implements EventTranslatorOneArg<DpEvt, Object>{

	public void translateTo(DpEvt event, long sequence, Object data) {
		event.setData(data);
	}

}
