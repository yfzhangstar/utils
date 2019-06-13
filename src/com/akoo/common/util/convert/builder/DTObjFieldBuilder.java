package com.akoo.common.util.convert.builder;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public interface DTObjFieldBuilder extends Opcodes{

	String UTIL_PATH = "com/akoo/common/util/convert/Util";
	String DATAHOLDER_PATH = "com/akoo/common/util/convert/DataHolder";

	void buildFromBytesHolderMContent(MethodVisitor mv, String fieldName, String objName);
	
	void buildToBytesHolderMContent(MethodVisitor mv, String fieldName, String objName);
}
