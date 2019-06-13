package com.akoo.common.util.convert;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DTObjInfo {

	private String name;//名字

	private String convertName;

	private String superName;

	private Set<DTObjFieldInfo> fieldsInfoSet = new HashSet<DTObjFieldInfo>();

	private Map<Integer, DTObjFieldInfo> fieldsInfos = new TreeMap<>((Integer o1, Integer o2)->{
		if(o1>o2)
			return 1;
		else if(o1<o2)
			return -1;
		else
			return 0;
	});

	public DTObjInfo(String name, String superName) {
		this.name = name;
		this.convertName = name + "$Convert";
		this.superName = superName;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public void putFieldInfo(Integer ix, DTObjFieldInfo fieldInfo){
		fieldsInfos.put(ix, fieldInfo);
		fieldsInfoSet.add(fieldInfo);
	}

	public boolean containsFieldInfo(DTObjFieldInfo fieldInfo){
		return fieldsInfoSet.contains(fieldInfo);
	}


	public Map<Integer, DTObjFieldInfo> getFieldsInfos() {
		return fieldsInfos;
	}

	public void setFieldsInfos(Map<Integer, DTObjFieldInfo> fieldsInfos) {
		this.fieldsInfos = fieldsInfos;
	}

	public String getConvertName() {
		return convertName;
	}

	public void setConvertName(String convertName) {
		this.convertName = convertName;
	}

	public String getSuperName() {
		return superName;
	}

	public void setSuperName(String superName) {
		this.superName = superName;
	}

}
