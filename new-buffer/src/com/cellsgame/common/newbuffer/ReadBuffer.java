package com.cellsgame.common.newbuffer;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;


// å¯¹æ¯”äº? readMap(new ByteArrayInputStream(byte[] buf)) å’? readMap(byte[] buff) 
// ä¸¤è?…åœ¨æ€§èƒ½ä¸Šæ²¡ä»?ä¹ˆå·®å¼‚ã??

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ReadBuffer {

	
	private IoBuffer b = IoBuffer.allocate(512).setAutoExpand(true);

	private ReadBuffer() {
	}

	public static ReadBuffer warp(byte[] src) {
		ReadBuffer db = new ReadBuffer();
		db.b = IoBuffer.wrap(src);
		return db;
	}
	
	
	public final byte _readByte() throws IOException {
		int ch = b.get();
		return (byte) (ch);
	}

	public final int readInt() throws IOException {
		byte tag = _readByte();
		switch (tag) {
		case B2Type.INT_N1:
			return -1;
		case B2Type.INT_0:
			return 0;
		case B2Type.INT_1:
			return 1;
		case B2Type.INT_2:
			return 2;
		case B2Type.INT_3:
			return 3;
		case B2Type.INT_4:
			return 4;
		case B2Type.INT_5:
			return 5;
		case B2Type.INT_6:
			return 6;
		case B2Type.INT_7:
			return 7;
		case B2Type.INT_8:
			return 8;
		case B2Type.INT_9:
			return 9;
		case B2Type.INT_10:
			return 10;
		case B2Type.INT_11:
			return 11;
		case B2Type.INT_12:
			return 12;
		case B2Type.INT_13:
			return 13;
		case B2Type.INT_14:
			return 14;
		case B2Type.INT_15:
			return 15;
		case B2Type.INT_16:
			return 16;
		case B2Type.INT_17:
			return 17;
		case B2Type.INT_18:
			return 18;
		case B2Type.INT_19:
			return 19;
		case B2Type.INT_20:
			return 20;
		case B2Type.INT_21:
			return 21;
		case B2Type.INT_22:
			return 22;
		case B2Type.INT_23:
			return 23;
		case B2Type.INT_24:
			return 24;
		case B2Type.INT_25:
			return 25;
		case B2Type.INT_26:
			return 26;
		case B2Type.INT_27:
			return 27;
		case B2Type.INT_28:
			return 28;
		case B2Type.INT_29:
			return 29;
		case B2Type.INT_30:
			return 30;
		case B2Type.INT_31:
			return 31;
		case B2Type.INT_32:
			return 32;
		case B2Type.INT_8B: {
			byte v = (byte) _readByte();
			return v;
		}
		case B2Type.INT_16B: {
			short v = (short) (((_readByte() & 0xff) << 8) + ((_readByte() & 0xff) << 0));
			return v;
		}
		case B2Type.INT_32B: {
			int value1 = _readByte();
			int value2 = _readByte();
			int value3 = _readByte();
			int value4 = _readByte();

			int v = ((value1 & 0xff) << 24) + ((value2 & 0xff) << 16)
					+ ((value3 & 0xff) << 8) + ((value4 & 0xff) << 0);
			return v;
		}
		default:
			throw new IOException("read int tag error:" + tag);
		}
	}

	private final int[] readIntArray(int len) throws Exception {
		int[] ret = new int[len];
		for (int i = 0; i < len; i++) {
			int v = readInt();
			ret[i] = v;
		}
		return ret;
	}

	private final int[][] readInt2DArray(int len) throws Exception {
		int[][] ret = new int[len][];
		for (int i = 0; i < len; i++) {
			Object o = readObject();
			int[] v = (int[]) o;
			ret[i] = v;
		}
		return ret;
	}

	private final List readList(int len) throws Exception {
		List ret = new ArrayList();
		for (int i = 0; i < len; i++) {
			Object o = readObject();
			// ret.addElement(o);
			ret.add(o);
		}
		return ret;
	}

	private final Map readMap(int len) throws Exception {
		Map ret = new HashMap();
		for (int i = 0; i < len; i++) {
			Object key = readObject();
			Object var = readObject();
			ret.put(key, var);
		}
		return ret;

	}

	public final Map readMap() throws Exception {
		return (Map) readObject();
	}
	
	public final String readString() throws Exception {
		byte tag = (byte) _readByte();
		switch (tag) {
		case B2Type.NULL: {
			return null;
		}
		case B2Type.STR_0: {
			return "";
		}
		case B2Type.STR_1: {
			return readStringImpl(1);
		}
		case B2Type.STR_2: {
			return readStringImpl(2);
		}
		case B2Type.STR_3: {
			return readStringImpl(3);
		}
		case B2Type.STR_4: {
			return readStringImpl(4);
		}
		case B2Type.STR_5: {
			return readStringImpl(5);
		}
		case B2Type.STR_6: {
			return readStringImpl(6);
		}
		case B2Type.STR_7: {
			return readStringImpl(7);
		}
		case B2Type.STR_8: {
			return readStringImpl(8);
		}
		case B2Type.STR_9: {
			return readStringImpl(9);
		}
		case B2Type.STR_10: {
			return readStringImpl(10);
		}
		case B2Type.STR_11: {
			return readStringImpl(11);
		}
		case B2Type.STR_12: {
			return readStringImpl(12);
		}
		case B2Type.STR_13: {
			return readStringImpl(13);
		}
		case B2Type.STR_14: {
			return readStringImpl(14);
		}
		case B2Type.STR_15: {
			return readStringImpl(15);
		}
		case B2Type.STR_16: {
			return readStringImpl(16);
		}
		case B2Type.STR_17: {
			return readStringImpl(17);
		}
		case B2Type.STR_18: {
			return readStringImpl(18);
		}
		case B2Type.STR_19: {
			return readStringImpl(19);
		}
		case B2Type.STR_20: {
			return readStringImpl(20);
		}
		case B2Type.STR_21: {
			return readStringImpl(21);
		}
		case B2Type.STR_22: {
			return readStringImpl(22);
		}
		case B2Type.STR_23: {
			return readStringImpl(23);
		}
		case B2Type.STR_24: {
			return readStringImpl(24);
		}
		case B2Type.STR_25: {
			return readStringImpl(25);
		}
		case B2Type.STR_32: {
			return readStringImpl(32);
		}
		case B2Type.STR: {
			int len = readInt();
			return readStringImpl(len);
		}
		default:
			if (tag == -1)
				throw new SocketException("remote close.");
			else
				throw new IOException("unknow tag error:" + tag);
		}
	}
	
	
	public final Boolean readBoolean() throws Exception {
		byte tag = (byte) _readByte();
		switch (tag) {
		case B2Type.NULL: {
			return null;
		}
		case B2Type.BOOLEAN_TRUE: {
			return new Boolean(true);
		}
		case B2Type.BOOLEAN_FALSE: {
			return new Boolean(false);
		}
		default:
			if (tag == -1)
				throw new SocketException("remote close.");
			else
				throw new IOException("unknow tag error:" + tag);
		}
	}
	
	
	public final Date readDate() throws Exception {
		byte tag = (byte) _readByte();
		switch (tag) {
		case B2Type.NULL: {
			return null;
		}
		case B2Type.JAVA_DATE: {
			byte[] b = new byte[8];
			for (int i = 0; i < 8; i++) {
				b[i] = (byte) _readByte();
			}
			long high = ((b[0] & 0xff) << 24) + ((b[1] & 0xff) << 16)
					+ ((b[2] & 0xff) << 8) + ((b[3] & 0xff) << 0);
			long low = ((b[4] & 0xff) << 24) + ((b[5] & 0xff) << 16)
					+ ((b[6] & 0xff) << 8) + ((b[7] & 0xff) << 0);
			long v = (high << 32) + (0xffffffffL & low);
			return new java.util.Date(v);
		}
		default:
			if (tag == -1)
				throw new SocketException("remote close.");
			else
				throw new IOException("unknow tag error:" + tag);
		}
	}
	

	public final Object readObject() throws Exception {
		byte tag = (byte) _readByte();
		switch (tag) {
		case B2Type.NULL: {
			return null;
		}
		case B2Type.HASHTABLE_0: {
			return new HashMap();
		}
		case B2Type.HASHTABLE_1: {
			return readMap(1);
		}
		case B2Type.HASHTABLE_2: {
			return readMap(2);
		}
		case B2Type.HASHTABLE_3: {
			return readMap(3);
		}
		case B2Type.HASHTABLE_4: {
			return readMap(4);
		}
		case B2Type.HASHTABLE_5: {
			return readMap(5);
		}
		case B2Type.HASHTABLE_6: {
			return readMap(6);
		}
		case B2Type.HASHTABLE_7: {
			return readMap(7);
		}
		case B2Type.HASHTABLE_8: {
			return readMap(8);
		}
		case B2Type.HASHTABLE_9: {
			return readMap(9);
		}
		case B2Type.HASHTABLE_10: {
			return readMap(10);
		}
		case B2Type.HASHTABLE_11: {
			return readMap(11);
		}
		case B2Type.HASHTABLE_12: {
			return readMap(12);
		}
		case B2Type.HASHTABLE_13: {
			return readMap(13);
		}
		case B2Type.HASHTABLE_14: {
			return readMap(14);
		}
		case B2Type.HASHTABLE_15: {
			return readMap(15);
		}
		case B2Type.HASHTABLE: {
			int len = readInt();
			return readMap(len);
		}
		case B2Type.INT_N1: {
			return new Integer(-1);
		}
		case B2Type.INT_0: {
			return new Integer(0);
		}
		case B2Type.INT_1: {
			return new Integer(1);
		}
		case B2Type.INT_2: {
			return new Integer(2);
		}
		case B2Type.INT_3: {
			return new Integer(3);
		}
		case B2Type.INT_4: {
			return new Integer(4);
		}
		case B2Type.INT_5: {
			return new Integer(5);
		}
		case B2Type.INT_6: {
			return new Integer(6);
		}
		case B2Type.INT_7: {
			return new Integer(7);
		}
		case B2Type.INT_8: {
			return new Integer(8);
		}
		case B2Type.INT_9: {
			return new Integer(9);
		}
		case B2Type.INT_10: {
			return new Integer(10);
		}
		case B2Type.INT_11: {
			return new Integer(11);
		}
		case B2Type.INT_12: {
			return new Integer(12);
		}
		case B2Type.INT_13: {
			return new Integer(13);
		}
		case B2Type.INT_14: {
			return new Integer(14);
		}
		case B2Type.INT_15: {
			return new Integer(15);
		}
		case B2Type.INT_16: {
			return new Integer(16);
		}
		case B2Type.INT_17: {
			return new Integer(17);
		}
		case B2Type.INT_18: {
			return new Integer(18);
		}
		case B2Type.INT_19: {
			return new Integer(19);
		}
		case B2Type.INT_20: {
			return new Integer(20);
		}
		case B2Type.INT_21: {
			return new Integer(21);
		}
		case B2Type.INT_22: {
			return new Integer(22);
		}
		case B2Type.INT_23: {
			return new Integer(23);
		}
		case B2Type.INT_24: {
			return new Integer(24);
		}
		case B2Type.INT_25: {
			return new Integer(25);
		}
		case B2Type.INT_26: {
			return new Integer(26);
		}
		case B2Type.INT_27: {
			return new Integer(27);
		}
		case B2Type.INT_28: {
			return new Integer(28);
		}
		case B2Type.INT_29: {
			return new Integer(29);
		}
		case B2Type.INT_30: {
			return new Integer(30);
		}
		case B2Type.INT_31: {
			return new Integer(31);
		}
		case B2Type.INT_32: {
			return new Integer(32);
		}
		case B2Type.INT_8B: {
			byte v = (byte) _readByte();
			return new Integer(v);
		}
		case B2Type.INT_16B: {
			short v = (short) (((_readByte() & 0xff) << 8) + ((_readByte() & 0xff) << 0));
			return new Integer(v);
		}
		case B2Type.INT_32B: {
			int v1 = _readByte();
			int v2 = _readByte();
			int v3 = _readByte();
			int v4 = _readByte();
			int v = ((v1 & 0xff) << 24) + ((v2 & 0xff) << 16)
					+ ((v3 & 0xff) << 8) + ((v4 & 0xff) << 0);
			return new Integer(v);
		}
		case B2Type.STR_0: {
			return "";
		}
		case B2Type.STR_1: {
			return readStringImpl(1);
		}
		case B2Type.STR_2: {
			return readStringImpl(2);
		}
		case B2Type.STR_3: {
			return readStringImpl(3);
		}
		case B2Type.STR_4: {
			return readStringImpl(4);
		}
		case B2Type.STR_5: {
			return readStringImpl(5);
		}
		case B2Type.STR_6: {
			return readStringImpl(6);
		}
		case B2Type.STR_7: {
			return readStringImpl(7);
		}
		case B2Type.STR_8: {
			return readStringImpl(8);
		}
		case B2Type.STR_9: {
			return readStringImpl(9);
		}
		case B2Type.STR_10: {
			return readStringImpl(10);
		}
		case B2Type.STR_11: {
			return readStringImpl(11);
		}
		case B2Type.STR_12: {
			return readStringImpl(12);
		}
		case B2Type.STR_13: {
			return readStringImpl(13);
		}
		case B2Type.STR_14: {
			return readStringImpl(14);
		}
		case B2Type.STR_15: {
			return readStringImpl(15);
		}
		case B2Type.STR_16: {
			return readStringImpl(16);
		}
		case B2Type.STR_17: {
			return readStringImpl(17);
		}
		case B2Type.STR_18: {
			return readStringImpl(18);
		}
		case B2Type.STR_19: {
			return readStringImpl(19);
		}
		case B2Type.STR_20: {
			return readStringImpl(20);
		}
		case B2Type.STR_21: {
			return readStringImpl(21);
		}
		case B2Type.STR_22: {
			return readStringImpl(22);
		}
		case B2Type.STR_23: {
			return readStringImpl(23);
		}
		case B2Type.STR_24: {
			return readStringImpl(24);
		}
		case B2Type.STR_25: {
			return readStringImpl(25);
		}
		case B2Type.STR_32: {
			return readStringImpl(32);
		}
		case B2Type.STR: {
			int len = readInt();
			return readStringImpl(len);
		}
		case B2Type.BOOLEAN_TRUE: {
			return new Boolean(true);
		}
		case B2Type.BOOLEAN_FALSE: {
			return new Boolean(false);
		}
		case B2Type.BYTE_0: {
			byte v = 0;
			return new Byte(v);
		}
		case B2Type.BYTE: {
			byte v = (byte) _readByte();
			return new Byte(v);
		}
		case B2Type.BYTES_0: {
			return new byte[0];
		}
		case B2Type.BYTES: {
			int len = readInt();
			byte[] bytes = new byte[len];
			b.get(bytes);
			return bytes;
		}
		case B2Type.VECTOR_0: {
			return new ArrayList();
		}
		case B2Type.VECTOR_1: {
			return readList(1);
		}
		case B2Type.VECTOR_2: {
			return readList(2);
		}
		case B2Type.VECTOR_3: {
			return readList(3);
		}
		case B2Type.VECTOR_4: {
			return readList(4);
		}
		case B2Type.VECTOR_5: {
			return readList(5);
		}
		case B2Type.VECTOR_6: {
			return readList(6);
		}
		case B2Type.VECTOR_7: {
			return readList(7);
		}
		case B2Type.VECTOR_8: {
			return readList(8);
		}
		case B2Type.VECTOR_9: {
			return readList(9);
		}
		case B2Type.VECTOR_10: {
			return readList(10);
		}
		case B2Type.VECTOR_11: {
			return readList(11);
		}
		case B2Type.VECTOR_12: {
			return readList(12);
		}
		case B2Type.VECTOR_13: {
			return readList(13);
		}
		case B2Type.VECTOR_14: {
			return readList(14);
		}
		case B2Type.VECTOR_15: {
			return readList(15);
		}
		case B2Type.VECTOR_16: {
			return readList(16);
		}
		case B2Type.VECTOR_17: {
			return readList(17);
		}
		case B2Type.VECTOR_18: {
			return readList(18);
		}
		case B2Type.VECTOR_19: {
			return readList(19);
		}
		case B2Type.VECTOR_20: {
			return readList(20);
		}
		case B2Type.VECTOR_21: {
			return readList(21);
		}
		case B2Type.VECTOR_22: {
			return readList(22);
		}
		case B2Type.VECTOR_23: {
			return readList(23);
		}
		case B2Type.VECTOR_24: {
			return readList(24);
		}
		case B2Type.VECTOR: {
			int len = readInt();
			return readList(len);
		}
		case B2Type.SHORT_0: {
			short v = 0;
			return new Short(v);
		}
		case B2Type.SHORT_8B: {
			short v = (short) _readByte();
			return new Short(v);
		}
		case B2Type.SHORT_16B: {
			short v = (short) (((_readByte() & 0xff) << 8) + ((_readByte() & 0xff) << 0));
			return new Short(v);
		}
		case B2Type.LONG_0: {
			int v = 0;
			return new Long(v);
		}
		case B2Type.LONG_8B: {
			int v = _readByte();
			return new Long(v);
		}
		case B2Type.LONG_16B: {
			int v = (((_readByte() & 0xff) << 8) + ((_readByte() & 0xff) << 0));
			return new Long(v);
		}
		case B2Type.LONG_32B: {
			int v1 = _readByte();
			int v2 = _readByte();
			int v3 = _readByte();
			int v4 = _readByte();
			int v = ((v1 & 0xff) << 24) + ((v2 & 0xff) << 16)
					+ ((v3 & 0xff) << 8) + ((v4 & 0xff) << 0);
			return new Long(v);
		}
		case B2Type.LONG_64B: {
			byte[] b = new byte[8];
			for (int i = 0; i < 8; i++) {
				b[i] = (byte) _readByte();
			}
			long high = ((b[0] & 0xff) << 24) + ((b[1] & 0xff) << 16)
					+ ((b[2] & 0xff) << 8) + ((b[3] & 0xff) << 0);
			long low = ((b[4] & 0xff) << 24) + ((b[5] & 0xff) << 16)
					+ ((b[6] & 0xff) << 8) + ((b[7] & 0xff) << 0);
			long v = (high << 32) + (0xffffffffL & low);
			return new Long(v);
		}
		case B2Type.JAVA_DATE: {
			byte[] b = new byte[8];
			for (int i = 0; i < 8; i++) {
				b[i] = (byte) _readByte();
			}
			long high = ((b[0] & 0xff) << 24) + ((b[1] & 0xff) << 16)
					+ ((b[2] & 0xff) << 8) + ((b[3] & 0xff) << 0);
			long low = ((b[4] & 0xff) << 24) + ((b[5] & 0xff) << 16)
					+ ((b[6] & 0xff) << 8) + ((b[7] & 0xff) << 0);
			long v = (high << 32) + (0xffffffffL & low);
			return new java.util.Date(v);
		}
		case B2Type.DOUBLE_0: {
			// int v = 0;
			// double ret = Double.longBitsToDouble(v);
			return new Double(0);
			// }case B2Type.DOUBLE_8B: {
			// int v = readByte(is);
			// double ret = Double.longBitsToDouble(v);
			// return new Double(ret);
			// }case B2Type.DOUBLE_16B: {
			// int v = (((readByte(is) & 0xff) << 8) + ((readByte(is) & 0xff) <<
			// 0));
			// double ret = Double.longBitsToDouble(v);
			// return new Double(ret);
			// }case B2Type.DOUBLE_32B: {
			// int v1 = readByte(is);
			// int v2 = readByte(is);
			// int v3 = readByte(is);
			// int v4 = readByte(is);
			//
			// int v = ((v1 & 0xff) << 24) + ((v2 & 0xff) << 16)
			// + ((v3 & 0xff) << 8) + ((v4 & 0xff) << 0);
			// double ret = Double.longBitsToDouble(v);
			// return new Double(ret);
		}
		case B2Type.DOUBLE_64B: {
			byte[] b = new byte[8];
			for (int i = 0; i < 8; i++) {
				b[i] = (byte) _readByte();
			}
			long high = ((b[0] & 0xff) << 24) + ((b[1] & 0xff) << 16)
					+ ((b[2] & 0xff) << 8) + ((b[3] & 0xff) << 0);
			long low = ((b[4] & 0xff) << 24) + ((b[5] & 0xff) << 16)
					+ ((b[6] & 0xff) << 8) + ((b[7] & 0xff) << 0);
			long v = (high << 32) + (0xffffffffL & low);
			double ret = Double.longBitsToDouble(v);
			return new Double(ret);
		}
		case B2Type.INT_ARRAY_0: {
			return new int[0];
		}
		case B2Type.INT_ARRAY_1: {
			return readIntArray(1);
		}
		case B2Type.INT_ARRAY_2: {
			return readIntArray(2);
		}
		case B2Type.INT_ARRAY_3: {
			return readIntArray(3);
		}
		case B2Type.INT_ARRAY_4: {
			return readIntArray(4);
		}
		case B2Type.INT_ARRAY_5: {
			return readIntArray(5);
		}
		case B2Type.INT_ARRAY_6: {
			return readIntArray(6);
		}
		case B2Type.INT_ARRAY_7: {
			return readIntArray(7);
		}
		case B2Type.INT_ARRAY_8: {
			return readIntArray(8);
		}
		case B2Type.INT_ARRAY_9: {
			return readIntArray(9);
		}
		case B2Type.INT_ARRAY_10: {
			return readIntArray(10);
		}
		case B2Type.INT_ARRAY_11: {
			return readIntArray(11);
		}
		case B2Type.INT_ARRAY_12: {
			return readIntArray(12);
		}
		case B2Type.INT_ARRAY_13: {
			return readIntArray(13);
		}
		case B2Type.INT_ARRAY_14: {
			return readIntArray(14);
		}
		case B2Type.INT_ARRAY_15: {
			return readIntArray(15);
		}
		case B2Type.INT_ARRAY_16: {
			return readIntArray(16);
		}
		case B2Type.INT_ARRAY: {
			int len = readInt();
			return readIntArray(len);
		}
		case B2Type.INT_2D_ARRAY_0: {
			return new int[0][0];
		}
		case B2Type.INT_2D_ARRAY: {
			int len = readInt();
			return readInt2DArray(len);
		}
		default:
			if (tag == -1)
				throw new SocketException("remote close.");
			else
				throw new IOException("unknow tag error:" + tag);
		}
	}

	// //////////////////////////////////
	private final String readStringImpl(int length) throws IOException {
		if (length <= 0)
			return "";
		byte[] bytes = new byte[length];
		b.get(bytes);
		return new String(bytes, B2Type.UTF8);
	}
}
