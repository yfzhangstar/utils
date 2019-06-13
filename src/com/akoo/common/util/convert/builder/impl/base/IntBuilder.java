package com.akoo.common.util.convert.builder.impl.base;

import com.akoo.common.util.convert.Util;
import com.akoo.common.util.convert.builder.DTObjFieldBuilder;
import org.objectweb.asm.MethodVisitor;

public class IntBuilder implements DTObjFieldBuilder {
	private static final DTObjFieldBuilder t = new IntBuilder();
	public static DTObjFieldBuilder getInstance(){return t;}


	@Override
	public void buildFromBytesHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 3);
		mv.visitVarInsn(ALOAD, 4);
		mv.visitMethodInsn(INVOKEINTERFACE, DATAHOLDER_PATH, "getInt", "()I", true);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getSetterName(fieldName), "(I)V", false);
}

	@Override
	public void buildToBytesHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 4);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getGetterName(fieldName, false), "()I", false);
		mv.visitMethodInsn(INVOKEINTERFACE, DATAHOLDER_PATH, "putInt", "(I)V", true);
	}




}