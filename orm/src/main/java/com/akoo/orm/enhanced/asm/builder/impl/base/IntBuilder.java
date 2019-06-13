package com.akoo.orm.enhanced.asm.builder.impl.base;

import com.akoo.orm.enhanced.Util;
import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class IntBuilder implements DBObjFieldBuilder{
	private static final DBObjFieldBuilder t = new IntBuilder();
	public static DBObjFieldBuilder getInstance(){return t;}

	@Override
	public void buildDBObjField(ClassWriter cw, String name) {
		cw.visitField(ACC_PRIVATE, name, "I", null, null).visitEnd();
	}
	

	@Override
	public void buildInitMContent(MethodVisitor cw, String fieldName, String objName) {
	}

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
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()Ljava/lang/Integer;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
			mv.visitFieldInsn(PUTFIELD, objname, fieldName, "I");
		}else{
//			System.out.println("voname "+voname+" methodName "+ Util.getGetterName(fieldName, false));
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()I", false);
			mv.visitFieldInsn(PUTFIELD, objname, fieldName, "I");
		}
	}
	@Override
	public void buildWriteVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean boxing) {
		mv.visitVarInsn(ALOAD, 2);//变量3 为转型完成的 vo对象引用
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "I");
		if(boxing){//装箱
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "(Ljava/lang/Integer;)V", false);
		}else{//
			mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "(I)V", false);	
		}
		
	}
	@Override
	public void buildReadHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "getInt", "()I", true);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "I");
	}

	@Override
	public void buildWriteHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "I");
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "putInt", "(I)V", true);
	}
	



}
