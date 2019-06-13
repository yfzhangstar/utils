package com.akoo.common.util.convert.builder.impl.base;

import com.akoo.common.util.convert.Util;
import com.akoo.common.util.convert.builder.DTObjFieldBuilder;
import org.objectweb.asm.MethodVisitor;

public class AryCharBuilder implements DTObjFieldBuilder {
	private static final DTObjFieldBuilder t = new AryCharBuilder();
	public static DTObjFieldBuilder getInstance(){return t;}



	
	@Override
	public void buildFromBytesHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 3);
		mv.visitVarInsn(ALOAD, 4);
		mv.visitMethodInsn(INVOKEINTERFACE, DATAHOLDER_PATH, "getChars", "()[C", true);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getSetterName(fieldName), "([C)V", false);
	}
	
	
	
	@Override
	public void buildToBytesHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 4);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getGetterName(fieldName, false), "()[C", false);
		mv.visitMethodInsn(INVOKEINTERFACE, DATAHOLDER_PATH, "putChars", "([C)V", true);
	}



}
