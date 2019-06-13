package com.akoo.common.util.convert;


import com.akoo.common.util.convert.builder.DTObjFieldBuilder;

public class DTObjFieldInfo {

	private DTObjFieldBuilder builder;

	private String name;

	private String desc;

	private String sig;


	private int ix;

	public DTObjFieldInfo(String name, String desc, String sig){
		this.name = name;
		this.setSig(sig);
		this.desc = desc;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIx() {
		return ix;
	}

	public void setIx(int ix) {
		this.ix = ix;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public String getSig() {
		return sig;
	}


	public void setSig(String sig) {
		this.sig = sig;
	}


	public DTObjFieldBuilder getBuilder() {
		return builder;
	}


	public void setBuilder(DTObjFieldBuilder trans) {
		this.builder = trans;
	}


}
