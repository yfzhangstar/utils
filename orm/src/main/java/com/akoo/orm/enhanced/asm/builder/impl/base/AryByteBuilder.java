package com.akoo.orm.enhanced.asm.builder.impl.base;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import com.akoo.orm.enhanced.Util;
import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;

public class AryByteBuilder implements DBObjFieldBuilder{
	private static final DBObjFieldBuilder t = new AryByteBuilder();
	public static DBObjFieldBuilder getInstance(){return t;}
	
	
	@Override
	public void buildDBObjField(ClassWriter cw, String name) {
		cw.visitField(ACC_PRIVATE, name, "[B", null, null).visitEnd();
	}
	

	@Override
	public void buildInitMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ICONST_0);
		mv.visitIntInsn(NEWARRAY, T_BYTE);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "[B");
	};
	
	/*
	 *不可变对象不需要clone 
	 */
	@Override
	public void buildCloneMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "[B");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "[B");
		mv.visitInsn(ARRAYLENGTH);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "copyOf", "([BI)[B", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "[B");

	}
	
	
	@Override
	public void buildReadVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean unboxing) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()[B", false);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()[B", false);
		mv.visitInsn(ARRAYLENGTH);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "copyOf", "([BI)[B", false);
		mv.visitFieldInsn(PUTFIELD, objname, fieldName, "[B");
	}
	
	
	@Override
	public void buildWriteVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean boxing) {
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "[B");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "[B");
		mv.visitInsn(ARRAYLENGTH);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "copyOf", "([BI)[B", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "([B)V", false);
	}
	
	@Override
	public void buildReadHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "getBytes", "()[B", true);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "[B");
	}
	
	
	
	@Override
	public void buildWriteHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "[B");
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "putBytes", "([B)V", true);
	}


}
