package com.cellsgame.common.newbuffer;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.mina.core.buffer.IoBuffer;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class WriteBuffer {

	// byte
	public static final byte BYTE_MIN_VALUE = -128;
	public static final byte BYTE_MAX_VALUE = 127;
	// short
	public static final short SHORT_MIN_VALUE = -32768;
	public static final short SHORT_MAX_VALUE = 32767;
	// int
	public static final int INT_MIN_VALUE = 0x80000000;
	public static final int INT_MAX_VALUE = 0x7fffffff;
	// long
	public static final long LONG_MIN_VALUE = 0x8000000000000000L;
	public static final long LONG_MAX_VALUE = 0x7fffffffffffffffL;
	// float
	public static final float FLOAT_MIN_VALUE = 0x0.000002P-126f; // 1.4e-45f
	public static final float FLOAT_MAX_VALUE = 0x1.fffffeP+127f; // 3.4028235e+38f
	// double
	public static final double DOUBLE_MIN_VALUE = 0x0.0000000000001P-1022; // 4.9e-324
	public static final double DOUBLE_MAX_VALUE = 0x1.fffffffffffffP+1023; // 1.7976931348623157e+308

	
	private IoBuffer b = IoBuffer.allocate(512).setAutoExpand(true);

	private WriteBuffer() {
	}

	public static WriteBuffer allocate() {
		WriteBuffer db = new WriteBuffer();
		return db;
	}

	private void _writeByte(byte v) throws IOException {
		this.b.put(v);
	}

	private void _writeBytes(byte[] v) throws IOException {
		this.b.put(v);
	}

	public final void writeNull() throws IOException {
		_writeByte(B2Type.NULL);
	}

	public final void writeBoolean(boolean v) throws IOException {
		if (v)
			_writeByte(B2Type.BOOLEAN_TRUE);
		else
			_writeByte(B2Type.BOOLEAN_FALSE);
	}

	public final void writeByte(int v) throws IOException {
		if (v == 0)
			_writeByte(B2Type.BYTE_0);
		else {
			_writeByte(B2Type.BYTE);
			_writeByte((byte)v);
		}
	}

	public final void writeShort(int v) throws IOException {
		if (v == 0)
			_writeByte(B2Type.SHORT_0);
		else if (v >= BYTE_MIN_VALUE && v <= BYTE_MAX_VALUE) {
			_writeByte(B2Type.SHORT_8B);
			_writeByte((byte)v);
		} else {
			_writeByte(B2Type.SHORT_16B);
			_writeByte((byte) ((v >> 8) & 0xff));
			_writeByte((byte) ((v >> 0) & 0xff));
		}
	}

	public final void writeInt(int v) throws IOException {
		switch (v) {
		case -1:
			_writeByte(B2Type.INT_N1);
			break;
		case 0:
			_writeByte(B2Type.INT_0);
			break;
		case 1:
			_writeByte(B2Type.INT_1);
			break;
		case 2:
			_writeByte(B2Type.INT_2);
			break;
		case 3:
			_writeByte(B2Type.INT_3);
			break;
		case 4:
			_writeByte(B2Type.INT_4);
			break;
		case 5:
			_writeByte(B2Type.INT_5);
			break;
		case 6:
			_writeByte(B2Type.INT_6);
			break;
		case 7:
			_writeByte(B2Type.INT_7);
			break;
		case 8:
			_writeByte(B2Type.INT_8);
			break;
		case 9:
			_writeByte(B2Type.INT_9);
			break;
		case 10:
			_writeByte(B2Type.INT_10);
			break;
		case 11:
			_writeByte(B2Type.INT_11);
			break;
		case 12:
			_writeByte(B2Type.INT_12);
			break;
		case 13:
			_writeByte(B2Type.INT_13);
			break;
		case 14:
			_writeByte(B2Type.INT_14);
			break;
		case 15:
			_writeByte(B2Type.INT_15);
			break;
		case 16:
			_writeByte(B2Type.INT_16);
			break;
		case 17:
			_writeByte(B2Type.INT_17);
			break;
		case 18:
			_writeByte(B2Type.INT_18);
			break;
		case 19:
			_writeByte(B2Type.INT_19);
			break;
		case 20:
			_writeByte(B2Type.INT_20);
			break;
		case 21:
			_writeByte(B2Type.INT_21);
			break;
		case 22:
			_writeByte(B2Type.INT_22);
			break;
		case 23:
			_writeByte(B2Type.INT_23);
			break;
		case 24:
			_writeByte(B2Type.INT_24);
			break;
		case 25:
			_writeByte(B2Type.INT_25);
			break;
		case 26:
			_writeByte(B2Type.INT_26);
			break;
		case 27:
			_writeByte(B2Type.INT_27);
			break;
		case 28:
			_writeByte(B2Type.INT_28);
			break;
		case 29:
			_writeByte(B2Type.INT_29);
			break;
		case 30:
			_writeByte(B2Type.INT_30);
			break;
		case 31:
			_writeByte(B2Type.INT_31);
			break;
		case 32:
			_writeByte(B2Type.INT_32);
			break;
		default:
			if (v >= BYTE_MIN_VALUE && v <= BYTE_MAX_VALUE) {
				_writeByte(B2Type.INT_8B);
				_writeByte((byte)v);
			} else if (v >= SHORT_MIN_VALUE && v <= SHORT_MAX_VALUE) {
				_writeByte(B2Type.INT_16B);
				_writeByte((byte) ((v >> 8) & 0xff));
				_writeByte((byte) ((v >> 0) & 0xff));
			} else {
				_writeByte(B2Type.INT_32B);
				_writeByte((byte) ((v >> 24) & 0xff));
				_writeByte((byte) ((v >> 16) & 0xff));
				_writeByte((byte) ((v >> 8) & 0xff));
				_writeByte((byte) ((v >> 0) & 0xff));
			}
			break;
		}
	}

	public final void writeIntArray(int[] v) throws IOException {
		int len = v.length;
		switch (len) {
		case 0:
			_writeByte(B2Type.INT_ARRAY_0);
			break;
		case 1:
			_writeByte(B2Type.INT_ARRAY_1);
			break;
		case 2:
			_writeByte(B2Type.INT_ARRAY_2);
			break;
		case 3:
			_writeByte(B2Type.INT_ARRAY_3);
			break;
		case 4:
			_writeByte(B2Type.INT_ARRAY_4);
			break;
		case 5:
			_writeByte(B2Type.INT_ARRAY_5);
			break;
		case 6:
			_writeByte(B2Type.INT_ARRAY_6);
			break;
		case 7:
			_writeByte(B2Type.INT_ARRAY_7);
			break;
		case 8:
			_writeByte(B2Type.INT_ARRAY_8);
			break;
		case 9:
			_writeByte(B2Type.INT_ARRAY_9);
			break;
		case 10:
			_writeByte(B2Type.INT_ARRAY_10);
			break;
		case 11:
			_writeByte(B2Type.INT_ARRAY_11);
			break;
		case 12:
			_writeByte(B2Type.INT_ARRAY_12);
			break;
		case 13:
			_writeByte(B2Type.INT_ARRAY_13);
			break;
		case 14:
			_writeByte(B2Type.INT_ARRAY_14);
			break;
		case 15:
			_writeByte(B2Type.INT_ARRAY_15);
			break;
		case 16:
			_writeByte(B2Type.INT_ARRAY_16);
			break;
		default:
			_writeByte(B2Type.INT_ARRAY);
			writeInt(len);
			break;
		}
		for (int i = 0; i < len; i++) {
			writeInt(v[i]);
		}
	}

	public final void writeInt2DArray(int[][] v) throws IOException {
		int len = v.length;
		if (len <= 0) {
			_writeByte(B2Type.INT_2D_ARRAY_0);
			return;
		}
		_writeByte(B2Type.INT_2D_ARRAY);
		writeInt(len);
		for (int i = 0; i < len; i++) {
			writeIntArray(v[i]);
		}
	}

	public final void writeLong(long v) throws IOException {
		if (v == 0) {
			_writeByte(B2Type.LONG_0);
		} else if (v >= BYTE_MIN_VALUE && v <= BYTE_MAX_VALUE) {
			_writeByte(B2Type.LONG_8B);
			_writeByte((byte)v);
		} else if (v >= SHORT_MIN_VALUE && v <= SHORT_MAX_VALUE) {
			_writeByte(B2Type.LONG_16B);
			_writeByte((byte) ((v >> 8) & 0xff));
			_writeByte((byte) ((v >> 0) & 0xff));
		} else if (v >= INT_MIN_VALUE && v <= INT_MAX_VALUE) {
			_writeByte(B2Type.LONG_32B);
			_writeByte((byte) ((v >> 24) & 0xff));
			_writeByte((byte) ((v >> 16) & 0xff));
			_writeByte((byte) ((v >> 8) & 0xff));
			_writeByte((byte) ((v >> 0) & 0xff));
		} else {
			_writeByte(B2Type.LONG_64B);
			_writeByte((byte) ((v >> 56) & 0xff));
			_writeByte((byte) ((v >> 48) & 0xff));
			_writeByte((byte) ((v >> 40) & 0xff));
			_writeByte((byte) ((v >> 32) & 0xff));
			_writeByte((byte) ((v >> 24) & 0xff));
			_writeByte((byte) ((v >> 16) & 0xff));
			_writeByte((byte) ((v >> 8) & 0xff));
			_writeByte((byte) ((v >> 0) & 0xff));
		}
	}

	public final void writeDouble(double var) throws IOException {
		long v = Double.doubleToLongBits(var);
		if (v == 0) {
			_writeByte(B2Type.DOUBLE_0);
			// } else if (v >= NumEx.Byte_MIN_VALUE && v <=
			// NumEx.Byte_MAX_VALUE) {
			// _writeByte(B2Type.DOUBLE_8B);
			// _writeByte((int) v);
			// } else if (v >= NumEx.Short_MIN_VALUE && v <=
			// NumEx.Short_MAX_VALUE) {
			// _writeByte(B2Type.DOUBLE_16B);
			// _writeByte((byte) ((v >> 8) & 0xff));
			// _writeByte((byte) ((v >> 0) & 0xff));
			// } else if (v >= NumEx.Integer_MIN_VALUE && v <=
			// NumEx.Integer_MAX_VALUE) {
			// _writeByte(B2Type.DOUBLE_32B);
			// _writeByte((byte) ((v >> 24) & 0xff));
			// _writeByte((byte) ((v >> 16) & 0xff));
			// _writeByte((byte) ((v >> 8) & 0xff));
			// _writeByte((byte) ((v >> 0) & 0xff));
		} else {
			_writeByte(B2Type.DOUBLE_64B);
			_writeByte((byte) ((v >> 56) & 0xff));
			_writeByte((byte) ((v >> 48) & 0xff));
			_writeByte((byte) ((v >> 40) & 0xff));
			_writeByte((byte) ((v >> 32) & 0xff));
			_writeByte((byte) ((v >> 24) & 0xff));
			_writeByte((byte) ((v >> 16) & 0xff));
			_writeByte((byte) ((v >> 8) & 0xff));
			_writeByte((byte) ((v >> 0) & 0xff));
		}
	}

	public final void writeString(String v) throws IOException {
		if (v == null) {
			writeNull();
		} else {
			byte[] b = v.getBytes(B2Type.UTF8);
			int len = b.length;
			switch (len) {
			case 0:
				_writeByte(B2Type.STR_0);
				break;
			case 1:
				_writeByte(B2Type.STR_1);
				printString(b);
				break;
			case 2:
				_writeByte(B2Type.STR_2);
				printString(b);
				break;
			case 3:
				_writeByte(B2Type.STR_3);
				printString(b);
				break;
			case 4:
				_writeByte(B2Type.STR_4);
				printString(b);
				break;
			case 5:
				_writeByte(B2Type.STR_5);
				printString(b);
				break;
			case 6:
				_writeByte(B2Type.STR_6);
				printString(b);
				break;
			case 7:
				_writeByte(B2Type.STR_7);
				printString(b);
				break;
			case 8:
				_writeByte(B2Type.STR_8);
				printString(b);
				break;
			case 9:
				_writeByte(B2Type.STR_9);
				printString(b);
				break;
			case 10:
				_writeByte(B2Type.STR_10);
				printString(b);
				break;
			case 11:
				_writeByte(B2Type.STR_11);
				printString(b);
				break;
			case 12:
				_writeByte(B2Type.STR_12);
				printString(b);
				break;
			case 13:
				_writeByte(B2Type.STR_13);
				printString(b);
				break;
			case 14:
				_writeByte(B2Type.STR_14);
				printString(b);
				break;
			case 15:
				_writeByte(B2Type.STR_15);
				printString(b);
				break;
			case 16:
				_writeByte(B2Type.STR_16);
				printString(b);
				break;
			case 17:
				_writeByte(B2Type.STR_17);
				printString(b);
				break;
			case 18:
				_writeByte(B2Type.STR_18);
				printString(b);
				break;
			case 19:
				_writeByte(B2Type.STR_19);
				printString(b);
				break;
			case 20:
				_writeByte(B2Type.STR_20);
				printString(b);
				break;
			case 21:
				_writeByte(B2Type.STR_21);
				printString(b);
				break;
			case 22:
				_writeByte(B2Type.STR_22);
				printString(b);
				break;
			case 23:
				_writeByte(B2Type.STR_23);
				printString(b);
				break;
			case 24:
				_writeByte(B2Type.STR_24);
				printString(b);
				break;
			case 25:
				_writeByte(B2Type.STR_25);
				printString(b);
				break;
			case 32:
				_writeByte(B2Type.STR_32);
				printString(b);
				break;
			default:
				_writeByte(B2Type.STR);
				writeInt(len);
				printString(b);
				break;
			}
		}
	}

	public final void writeDate(java.util.Date dat) throws IOException {
		long v = dat.getTime();
		_writeByte(B2Type.JAVA_DATE);
		_writeByte((byte) ((v >> 56) & 0xff));
		_writeByte((byte) ((v >> 48) & 0xff));
		_writeByte((byte) ((v >> 40) & 0xff));
		_writeByte((byte) ((v >> 32) & 0xff));
		_writeByte((byte) ((v >> 24) & 0xff));
		_writeByte((byte) ((v >> 16) & 0xff));
		_writeByte((byte) ((v >> 8) & 0xff));
		_writeByte((byte) ((v >> 0) & 0xff));
	}

	public final void writeBytes(byte[] v) throws IOException {
		if (v == null) {
			writeNull();
		} else {
			int len = v.length;
			if (len == 0) {
				_writeByte(B2Type.BYTES_0);
			} else {
				_writeByte(B2Type.BYTES);
				writeInt(len);
				_writeBytes(v);
			}
		}
	}

	public final void writeVector(List v) throws Exception {
		if (v == null) {
			writeNull();
		} else {
			int len = v.size();
			switch (len) {
			case 0:
				_writeByte(B2Type.VECTOR_0);
				break;
			case 1:
				_writeByte(B2Type.VECTOR_1);
				break;
			case 2:
				_writeByte(B2Type.VECTOR_2);
				break;
			case 3:
				_writeByte(B2Type.VECTOR_3);
				break;
			case 4:
				_writeByte(B2Type.VECTOR_4);
				break;
			case 5:
				_writeByte(B2Type.VECTOR_5);
				break;
			case 6:
				_writeByte(B2Type.VECTOR_6);
				break;
			case 7:
				_writeByte(B2Type.VECTOR_7);
				break;
			case 8:
				_writeByte(B2Type.VECTOR_8);
				break;
			case 9:
				_writeByte(B2Type.VECTOR_9);
				break;
			case 10:
				_writeByte(B2Type.VECTOR_10);
				break;
			case 11:
				_writeByte(B2Type.VECTOR_11);
				break;
			case 12:
				_writeByte(B2Type.VECTOR_12);
				break;
			case 13:
				_writeByte(B2Type.VECTOR_13);
				break;
			case 14:
				_writeByte(B2Type.VECTOR_14);
				break;
			case 15:
				_writeByte(B2Type.VECTOR_15);
				break;
			case 16:
				_writeByte(B2Type.VECTOR_16);
				break;
			case 17:
				_writeByte(B2Type.VECTOR_17);
				break;
			case 18:
				_writeByte(B2Type.VECTOR_18);
				break;
			case 19:
				_writeByte(B2Type.VECTOR_19);
				break;
			case 20:
				_writeByte(B2Type.VECTOR_20);
				break;
			case 21:
				_writeByte(B2Type.VECTOR_21);
				break;
			case 22:
				_writeByte(B2Type.VECTOR_22);
				break;
			case 23:
				_writeByte(B2Type.VECTOR_23);
				break;
			case 24:
				_writeByte(B2Type.VECTOR_24);
				break;
			default:
				_writeByte(B2Type.VECTOR);
				writeInt(len);
				break;
			}

			for (int i = 0; i < len; i++) {
				// Object object = v.elementAt(i);
				Object object = v.get(i);
				writeObject(object);
			}
		}
	}

	public final void writeMap(Map v) throws Exception {
		if (v == null) {
			writeNull();
		} else {
			int len = v.size();
			switch (len) {
			case 0:
				_writeByte(B2Type.HASHTABLE_0);
				break;
			case 1: {
				_writeByte(B2Type.HASHTABLE_1);
				break;
			}
			case 2: {
				_writeByte(B2Type.HASHTABLE_2);
				break;
			}
			case 3: {
				_writeByte(B2Type.HASHTABLE_3);
				break;
			}
			case 4: {
				_writeByte(B2Type.HASHTABLE_4);
				break;
			}
			case 5: {
				_writeByte(B2Type.HASHTABLE_5);
				break;
			}
			case 6: {
				_writeByte(B2Type.HASHTABLE_6);
				break;
			}
			case 7: {
				_writeByte(B2Type.HASHTABLE_7);
				break;
			}
			case 8: {
				_writeByte(B2Type.HASHTABLE_8);
				break;
			}
			case 9: {
				_writeByte(B2Type.HASHTABLE_9);
				break;
			}
			case 10: {
				_writeByte(B2Type.HASHTABLE_10);
				break;
			}
			case 11: {
				_writeByte(B2Type.HASHTABLE_11);
				break;
			}
			case 12: {
				_writeByte(B2Type.HASHTABLE_12);
				break;
			}
			case 13: {
				_writeByte(B2Type.HASHTABLE_13);
				break;
			}
			case 14: {
				_writeByte(B2Type.HASHTABLE_14);
				break;
			}
			case 15: {
				_writeByte(B2Type.HASHTABLE_15);
				break;
			}
			default:
				_writeByte(B2Type.HASHTABLE);
				writeInt(len);
				break;
			}

			Set<Entry> entrys = v.entrySet();
			for (Entry e : entrys) {
				Object key = e.getKey();
				Object var = e.getValue();
				writeObject(key);
				writeObject(var);
			}

			// Enumeration keys = v.keys();
			// Iterator keys = v.keySet().iterator();
			// while (keys.hasNext()) {
			// Object key = keys.next();
			// Object var = v.get(key);
			// writeObject(key);
			// writeObject(var);
			// }
		}
	}

	public final void writeObject(Object object) throws Exception {
		if (object == null) {
			writeNull();
		} else if (object instanceof Map) {
			Map v = (Map) object;
			writeMap(v);
		} else if (object instanceof Integer) {
			int v = ((Integer) object).intValue();
			writeInt(v);
		} else if (object instanceof String) {
			String v = (String) object;
			writeString(v);
		} else if (object instanceof Boolean) {
			boolean v = ((Boolean) object).booleanValue();
			writeBoolean(v);
		} else if (object instanceof Byte) {
			int v = ((Byte) object).byteValue();
			writeByte(v);
		} else if (object instanceof byte[]) {
			byte[] v = (byte[]) object;
			writeBytes(v);
		} else if (object instanceof List) {
			List v = (List) object;
			writeVector(v);
		} else if (object instanceof Short) {
			int v = ((Short) object).shortValue();
			writeShort(v);
		} else if (object instanceof Long) {
			long v = ((Long) object).longValue();
			writeLong(v);
		} else if (object instanceof Double) {
			double v = ((Double) object).doubleValue();
			writeDouble(v);
		} else if (object instanceof java.util.Date) {
			java.util.Date v = (java.util.Date) object;
			writeDate(v);
		} else if (object instanceof java.sql.Date) {
			java.sql.Date v = (java.sql.Date) object;
			writeDate(new java.util.Date(v.getTime()));
		} else if (object instanceof java.sql.Timestamp) {
			java.sql.Timestamp v = (java.sql.Timestamp) object;
			writeDate(new java.util.Date(v.getTime()));
		} else if (object instanceof java.sql.Time) {
			java.sql.Time v = (java.sql.Time) object;
			writeDate(new java.util.Date(v.getTime()));
		} else if (object instanceof int[]) {
			int[] v = (int[]) object;
			writeIntArray(v);
		} else if (object instanceof int[][]) {
			int[][] v = (int[][]) object;
			writeInt2DArray(v);
		} else {
			throw new IOException("unsupported object:" + object);
		}
	}

	// ////////////////////////////////
	protected final void printString(byte[] v) throws IOException {
		_writeBytes(v);
	}

	public byte[] toBytes(){
		int pos = b.position();
		byte[] ret = new byte[pos];
		System.arraycopy(b.array(), 0, ret,0,pos);
		return ret;
	}
	
	
	public static void main(String[] args) throws Exception {
		WriteBuffer wb = WriteBuffer.allocate();
		wb.writeString("1");
		wb.writeString("2");
		wb.writeString("3");
		wb.writeString("4");
		wb.writeInt(4);
		wb.writeDate(new Date());
		byte[] bytes = wb.toBytes();
		
		
		ReadBuffer eb = ReadBuffer.warp(bytes);
		System.out.println(eb.readString());
		System.out.println(eb.readString());
		System.out.println(eb.readString());
		System.out.println(eb.readString());
		System.out.println(eb.readInt());
		System.out.println(eb.readDate());
		
		
		byte[] b = UUID.randomUUID().toString().replaceAll("-", "").getBytes(B2Type.UTF8);
		int len = b.length;
		System.out.println("len : "  + len);
		
	}
}