package com.cellsgame.mq.zmq;

public class Util {

	
	public static byte[] getNextNodeId(RingArray<NodeId> rary){
		NodeId ret = null;
		for (int i = 0; i < rary.getSize(); i++) {
			ret = rary.next();
			if(ret == null)
				continue;
			if(ret.validate())
				break;
		}
		if(ret == null)
			return null;
		return ret.getId();
	}
}
