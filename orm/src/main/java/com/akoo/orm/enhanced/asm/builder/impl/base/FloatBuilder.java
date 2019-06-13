package com.akoo.orm.enhanced.asm.builder.impl.base;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import com.akoo.orm.enhanced.Util;
import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;

public class FloatBuilder implements DBObjFieldBuilder {
	private static final DBObjFieldBuilder t = new FloatBuilder();
	public static DBObjFieldBuilder getInstance(){return t;}
	@Override
	public void buildDBObjField(ClassWriter cw, String name) {
		cw.visitField(ACC_PRIVATE, name, "F", null, null).visitEnd();
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
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()Ljava/lang/Float;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
			mv.visitFieldInsn(PUTFIELD, objname, fieldName, "F");
		}else{
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()F", false);	
			mv.visitFieldInsn(PUTFIELD, objname, fieldName, "F");
		}
	}
	@Override
	public void buildWriteVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean boxing) {
		mv.visitVarInsn(ALOAD, 2);//变量3 为转型完成的 vo对象引用
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "F");
		if(boxing){//装箱
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "(Ljava/lang/Float;)V", false);
		}else{//
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "(F)V", false);	
		}
		
	}
	@Override
	public void buildReadHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "getFloat", "()F", true);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "F");
	}
	
	
	
	@Override
	public void buildWriteHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "F");
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "putFloat", "(F)V", true);
	}
	

}
