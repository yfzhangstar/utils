package com.akoo.orm.enhanced.asm;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DBObjInfo {
	
	private String name;//名字
	
	private String superName;//父类名字
	
	private String mapVOName;//对应的vo名
	
	private Set<DBObjFieldInfo> fieldsInfoSet = new HashSet<DBObjFieldInfo>();
	
	private Map<Integer,DBObjFieldInfo> fieldsInfos = new TreeMap<>((Integer o1, Integer o2)->{
		if(o1>o2)
			return 1;
		else if(o1<o2)
			return -1;
		else
			return 0;
	});

	public DBObjInfo(String name, String superName, String voName) {
		this.name = name;
		this.superName = superName;
		this.mapVOName = voName;
	}
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSuperName() {
		return superName;
	}

	public void setSuperName(String superName) {
		this.superName = superName;
	}

	public String getMapVOName() {
		return mapVOName;
	}

	public void setMapVOName(String mapVOName) {
		this.mapVOName = mapVOName;
	}

	public void putFieldInfo(Integer ix, DBObjFieldInfo fieldInfo){
		fieldsInfos.put(ix, fieldInfo);
		fieldsInfoSet.add(fieldInfo);
	}
	
	public boolean containsFieldInfo(DBObjFieldInfo fieldInfo){
		return fieldsInfoSet.contains(fieldInfo);
	}
	
	
	public Map<Integer,DBObjFieldInfo> getFieldsInfos() {
		return fieldsInfos;
	}

	public void setFieldsInfos(Map<Integer,DBObjFieldInfo> fieldsInfos) {
		this.fieldsInfos = fieldsInfos;
	}
	
	
	
	
}
