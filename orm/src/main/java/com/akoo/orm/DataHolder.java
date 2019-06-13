package com.akoo.orm;

import java.util.Date;

public interface DataHolder {
	
	/*------------------putter----------------------*/
	void putInt(int in);

	void putShort(short in);
	
	void putByte(byte in);
	
	void putLong(long in);
	
	void putChar(char in);
	
	void putBoolean(boolean in);
	
	void putFloat(float in);
	
	void putDouble(double in);
	
	void putString(String in);
	
	void putDate(Date d);

	void putInts(int[] ins);

	void putShorts(short[] shorts);
	
	void putBytes(byte[] bytes);
	
	void putLongs(long[] longs);
	
	void putChars(char[] chars);

	void putBooleans(boolean[] bools);
	
	void putFloats(float[] floats);
	
	void putDoubles(double[] doubles);

	void putStrs(String[] strs);
	
	void putDBObj(DBObj dbobj);


	/*------------------getter----------------------*/	
	int getInt();
	
	short getShort();
	
	byte getByte();
	
	
	long getLong();
	

	char getChar();
	
	boolean getBoolean();
	
	float getFloat();
	
	double getDouble();
	
	String getString();
	
	Date getDate();
	
	
	int[] getInts();
	
	short[] getShorts();
	
	byte[] getBytes();
	
	long[] getLongs();
	
	char[] getChars();
	
	boolean[] getBooleans();

	float[] getFloats();
	
	double[] getDoubles();
	
	String[] getStrs();

    byte[] putAndGetData(DBObj dbobj);

}