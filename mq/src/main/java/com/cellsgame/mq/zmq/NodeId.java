package com.cellsgame.mq.zmq;

import java.util.Arrays;

public class NodeId{
	private byte[] id;
	
	private long invalidateTime;
	
	private long interval;
	
	public NodeId(byte[] id){
		this.id = id;
		this.interval = 0;
		invalidateTime = Long.MAX_VALUE;
	}
	
	
	public NodeId(byte[] id, long interval){
		this.id = id;
		this.interval = interval;
		refresh();
	}

	public byte[] getId() {
		return id;
	}

	public void setId(byte[] id) {
		this.id = id;
	}
	
	public boolean validate(){
		return invalidateTime >= System.currentTimeMillis();
	}


	public void refresh() {
		this.invalidateTime = System.currentTimeMillis()+interval;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof byte[]){
			return Arrays.equals(id, (byte[])obj);
		}
		if(obj instanceof NodeId){
			return Arrays.equals(id, ((NodeId)obj).getId());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(id);
	}
	
	@Override
	public String toString() {
		return new String(id);
	}
}
