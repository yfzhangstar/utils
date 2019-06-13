package com.akoo.orm.enhanced.asm.builder.impl.base;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import com.akoo.orm.enhanced.Util;
import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;

public class BooleanBuilder implements DBObjFieldBuilder{
	private static final DBObjFieldBuilder t = new BooleanBuilder();
	public static DBObjFieldBuilder getInstance(){return t;}
	
	
	@Override
	public void buildDBObjField(ClassWriter cw, String name) {
		cw.visitField(ACC_PRIVATE, name, "Z", null, null).visitEnd();
	}
	

	@Override
	public void buildInitMContent(MethodVisitor cw, String fieldName, String objName) {
	};
	
	/*
	 *不可变对象不需要clone 
	 */
	@Override
	public void buildCloneMContent(MethodVisitor mv, String fieldName, String objName) {
	}
	
	
	@Override
	public void buildReadVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean unboxing) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 2);//变量3 为转型完成的 vo对象引用
		if(unboxing){//拆箱
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, true), "()Ljava/lang/Boolean;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
			mv.visitFieldInsn(PUTFIELD, objname, fieldName, "Z");
		}else{
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, true), "()Z", false);	
			mv.visitFieldInsn(PUTFIELD, objname, fieldName, "Z");
		}
	}
	@Override
	public void buildWriteVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean boxing) {
		mv.visitVarInsn(ALOAD, 2);//变量3 为转型完成的 vo对象引用
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "Z");
		if(boxing){//装箱
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "(Ljava/lang/Boolean;)V", false);
		}else{//
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "(Z)V", false);	
		}
		
	}
	@Override
	public void buildReadHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "getBoolean", "()Z", true);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "Z");
	}
	
	
	
	@Override
	public void buildWriteHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "Z");
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "putBoolean", "(Z)V", true);
	}
	


}
