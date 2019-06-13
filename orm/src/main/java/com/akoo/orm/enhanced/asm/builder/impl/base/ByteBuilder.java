package com.akoo.orm.enhanced.asm.builder.impl.base;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import com.akoo.orm.enhanced.Util;
import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;

public class ByteBuilder implements DBObjFieldBuilder{
	private static final DBObjFieldBuilder t = new ByteBuilder();
	public static DBObjFieldBuilder getInstance(){return t;}
	
	
	@Override
	public void buildDBObjField(ClassWriter cw, String name) {
		cw.visitField(ACC_PRIVATE, name, "B", null, null).visitEnd();
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
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()Ljava/lang/Byte;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
			mv.visitFieldInsn(PUTFIELD, objname, fieldName, "B");
		}else{
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()B", false);	
			mv.visitFieldInsn(PUTFIELD, objname, fieldName, "B");
		}
	}
	@Override
	public void buildWriteVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean boxing) {
		mv.visitVarInsn(ALOAD, 2);//变量3 为转型完成的 vo对象引用
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "B");
		if(boxing){//装箱
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "(Ljava/lang/Byte;)V", false);
		}else{//
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "(B)V", false);	
		}
		
	}
	@Override
	public void buildReadHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "getByte", "()B", true);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "B");
	}
	
	
	
	@Override
	public void buildWriteHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "B");
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "putByte", "(B)V", true);
	}
	

}
