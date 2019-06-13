package com.akoo.orm.demo;

import com.akoo.orm.enhanced.annotation.Save;
import com.akoo.orm.DBVO;


public class DemoVO extends DBVO {


    private int id = 0;


    private String pid = "testPid";
	
	@Save(ix = 0)
	private int i;
	
	@Save(ix = 1)
	private short s;
	
	@Save(ix = 2)
	private long l;

	
	@Save(ix = 3)
	private byte b;
	
	@Save(ix = 4)
	private char c;
	
	@Save(ix = 5)
	private boolean z;
	
	
	@Save(ix = 6)
	private double d;

	@Save(ix = 7)
	private float f;
	
	@Save(ix = 8)
	private String str;
	
	
	@Save(ix = 10)
	private int[] is;
	
	@Save(ix = 11)
	private short[] ss;
	
	@Save(ix = 12)
	private long[] ls;

	
	@Save(ix = 13)
	private byte[] bs;
	
	@Save(ix = 14)
	private char[] cs;
	
	@Save(ix = 15)
	private boolean[] zs;
	
	
	@Save(ix = 16)
	private double[] ds;

	@Save(ix = 17)
	private float[] fs;
	
	@Save(ix = 18)
	private String[] strs;

	
	
	@Override
	protected Object initPrimaryKey() {
		return id;
	}

	@Override
	protected Object getPrimaryKey() {
		return id;
	}

	@Override
	protected void setPrimaryKey(Object pk) {
        id = (Integer) pk;
    }

	@Override
	protected Object[] getRelationKeys() {
		return new Object[]{pid};
	}

	@Override
	protected void setRelationKeys(Object[] relationKeys) {
		pid = (String) relationKeys[0];
	}

	@Override
	protected void init() {
		is = new int[]{};
		ss = new short[]{};
		ls = new long[]{};
		bs = new byte[]{};
		cs = new char[]{};
		zs = new boolean[]{};
		ds = new double[]{};
		fs = new float[]{};
		strs = new String[]{};
		str = "";
		
	}

	@Override
	public Integer getCid() {
		return null;
	}

	@Override
	public void setCid(Integer cid) {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public short getS() {
		return s;
	}

	public void setS(short s) {
		this.s = s;
	}

	public long getL() {
		return l;
	}

	public void setL(long l) {
		this.l = l;
	}

	public byte getB() {
		return b;
	}

	public void setB(byte b) {
		System.out.println("------------------setB");
		this.b = b;
	}

	public char getC() {
		return c;
	}

	public void setC(char c) {
		this.c = c;
	}

	public boolean isZ() {
		return z;
	}

	public void setZ(boolean z) {
		this.z = z;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public float getF() {
		return f;
	}

	public void setF(float f) {
		this.f = f;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public int[] getIs() {
		return is;
	}

	public void setIs(int[] is) {
		this.is = is;
	}

	public short[] getSs() {
		return ss;
	}

	public void setSs(short[] ss) {
		this.ss = ss;
	}

	public long[] getLs() {
		return ls;
	}

	public void setLs(long[] ls) {
		this.ls = ls;
	}

	public byte[] getBs() {
		return bs;
	}

	public void setBs(byte[] bs) {
		this.bs = bs;
	}

	public char[] getCs() {
		return cs;
	}

	public void setCs(char[] cs) {
		this.cs = cs;
	}

	public boolean[] getZs() {
		return zs;
	}

	public void setZs(boolean[] zs) {
		this.zs = zs;
	}

	public double[] getDs() {
		return ds;
	}

	public void setDs(double[] ds) {
		this.ds = ds;
	}

	public float[] getFs() {
		return fs;
	}

	public void setFs(float[] fs) {
		this.fs = fs;
	}

	public String[] getStrs() {
		return strs;
	}

	public void setStrs(String[] strs) {
		this.strs = strs;
	}

	
	
}
