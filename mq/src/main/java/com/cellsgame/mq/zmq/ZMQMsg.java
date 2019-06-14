package com.cellsgame.mq.zmq;

public class ZMQMsg{


	private byte[] server;
	
	private byte[] group;
	
	private ZMQData data;
	
	public ZMQMsg(byte[] server, byte[] group, ZMQData data){
		this.server = server;
		this.group = group;
		this.data = data;
	}

	public ZMQData getData() {
		return data;
	}

	public void setData(ZMQData data) {
		this.data = data;
	}

	public byte[] getGroup() {
		return group;
	}

	public void setGroup(byte[] group) {
		this.group = group;
	}

	public byte[] getServer() {
		return server;
	}

	public void setServer(byte[] server) {
		this.server = server;
	}

	
}
