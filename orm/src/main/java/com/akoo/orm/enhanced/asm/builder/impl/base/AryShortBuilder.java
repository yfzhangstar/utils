package com.akoo.orm.enhanced.asm.builder.impl.base;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import com.akoo.orm.enhanced.Util;
import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;

public class AryShortBuilder implements DBObjFieldBuilder{
	private static final DBObjFieldBuilder t = new AryShortBuilder();
	public static DBObjFieldBuilder getInstance(){return t;}

	@Override
	public void buildDBObjField(ClassWriter cw, String name) {
		cw.visitField(ACC_PRIVATE, name, "[S", null, null).visitEnd();
	}
	

	@Override
	public void buildInitMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ICONST_0);
		mv.visitIntInsn(NEWARRAY, T_SHORT);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "[S");
	};
	
	/*
	 *不可变对象不需要clone 
	 */
	@Override
	public void buildCloneMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "[S");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "[S");
		mv.visitInsn(ARRAYLENGTH);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "copyOf", "([SI)[S", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "[S");

	}
	
	
	@Override
	public void buildReadVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean unboxing) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()[S", false);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()[S", false);
		mv.visitInsn(ARRAYLENGTH);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "copyOf", "([SI)[S", false);
		mv.visitFieldInsn(PUTFIELD, objname, fieldName, "[S");
	}
	
	
	@Override
	public void buildWriteVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean boxing) {
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "[S");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "[S");
		mv.visitInsn(ARRAYLENGTH);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "copyOf", "([SI)[S", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "([S)V", false);
	}
	
	@Override
	public void buildReadHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "getShorts", "()[S", true);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "[S");
	}
	
	
	
	@Override
	public void buildWriteHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "[S");
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "putShorts", "([S)V", true);
	}

}
