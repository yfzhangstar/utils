package com.akoo.orm.enhanced.asm.builder.impl.base;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import com.akoo.orm.enhanced.Util;
import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;

public class AryLongBuilder implements DBObjFieldBuilder{
	private static final DBObjFieldBuilder t = new AryLongBuilder();
	public static DBObjFieldBuilder getInstance(){return t;}
	
	
	
	@Override
	public void buildDBObjField(ClassWriter cw, String name) {
		cw.visitField(ACC_PRIVATE, name, "[J", null, null).visitEnd();
	}
	

	@Override
	public void buildInitMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ICONST_0);
		mv.visitIntInsn(NEWARRAY, T_LONG);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "[J");
	};
	
	/*
	 *不可变对象不需要clone 
	 */
	@Override
	public void buildCloneMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "[J");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "[J");
		mv.visitInsn(ARRAYLENGTH);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "copyOf", "([JI)[J", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "[J");

	}
	
	
	@Override
	public void buildReadVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean unboxing) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()[J", false);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()[J", false);
		mv.visitInsn(ARRAYLENGTH);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "copyOf", "([JI)[J", false);
		mv.visitFieldInsn(PUTFIELD, objname, fieldName, "[J");
	}
	
	
	@Override
	public void buildWriteVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean boxing) {
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "[J");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "[J");
		mv.visitInsn(ARRAYLENGTH);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "copyOf", "([JI)[J", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "([J)V", false);
	}
	
	@Override
	public void buildReadHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "getLongs", "()[J", true);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "[J");
	}
	
	
	
	@Override
	public void buildWriteHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "[J");
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "putLongs", "([J)V", true);
	}


}
