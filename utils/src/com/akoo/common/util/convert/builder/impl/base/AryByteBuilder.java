package com.akoo.common.util.convert.builder.impl.base;

import com.akoo.common.util.convert.Util;
import com.akoo.common.util.convert.builder.DTObjFieldBuilder;
import org.objectweb.asm.MethodVisitor;

public class AryByteBuilder implements DTObjFieldBuilder {
	private static final DTObjFieldBuilder t = new AryByteBuilder();
	public static DTObjFieldBuilder getInstance(){return t;}
	



	@Override
	public void buildFromBytesHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 3);
		mv.visitVarInsn(ALOAD, 4);
		mv.visitMethodInsn(INVOKEINTERFACE, DATAHOLDER_PATH, "getBytes", "()[B", true);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getSetterName(fieldName), "([B)V", false);
	}
	
	
	
	@Override
	public void buildToBytesHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 4);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getGetterName(fieldName, false), "()[B", false);
		mv.visitMethodInsn(INVOKEINTERFACE, DATAHOLDER_PATH, "putBytes", "([B)V", true);
	}


}
