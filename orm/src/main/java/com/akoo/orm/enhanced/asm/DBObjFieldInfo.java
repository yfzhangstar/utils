package com.akoo.orm.enhanced.asm;

import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;

public class DBObjFieldInfo {
	
	private DBObjFieldBuilder builder;
	
	private String name;
	
	private String desc;
	
	private String sig;
	
	private boolean wrapper;
	
	private int ix;
	
	public DBObjFieldInfo(String name, String desc, String sig){
		this.name = name;
		this.setSig(sig);
		this.desc = desc;
		wrapper = RWConstant.isNumberWapper(desc);
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


	public DBObjFieldBuilder getBuilder() {
		return builder;
	}


	public void setBuilder(DBObjFieldBuilder trans) {
		this.builder = trans;
	}


	public boolean isWrapper() {
		return wrapper;
	}


	public void setWrapper(boolean wrapper) {
		this.wrapper = wrapper;
	}

}
