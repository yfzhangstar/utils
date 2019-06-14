package com.cellsgame.mq.zmq;

public interface ZMQMsgHandler{
	public void handleEvent(ZMQMsg msg);
}