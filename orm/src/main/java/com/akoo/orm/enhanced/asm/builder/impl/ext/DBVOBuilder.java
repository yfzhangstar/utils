package com.akoo.orm.enhanced.asm.builder.impl.ext;

import com.akoo.orm.enhanced.Util;
import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;


@SuppressWarnings("rawtypes")
public class DBVOBuilder implements DBObjFieldBuilder{

	private String dbobjDesc;
	
	private String dbvoDesc;
	

	public DBVOBuilder(Class voClass,Class poClass) {
		dbobjDesc = Type.getDescriptor(poClass);
		dbvoDesc = Type.getDescriptor(voClass);
	}

	@Override
	public void buildDBObjField(ClassWriter cw, String name) {
		cw.visitField(ACC_PRIVATE, name, dbobjDesc, null, null).visitEnd();
	}
	

	@Override
	public void buildInitMContent(MethodVisitor cw, String fieldName, String objName) {
	}
	
	/*
	 *不可变对象不需要clone 
	 */
	@Override
	public void buildCloneMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, dbobjDesc);
		Label l4 = new Label();
		mv.visitJumpInsn(IFNULL, l4);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, dbobjDesc);
		String poname = Type.getType(dbobjDesc).getInternalName();
		mv.visitMethodInsn(INVOKEVIRTUAL, poname, "clone", "()Lcom/akoo/orm/DBObj;", false);
		mv.visitTypeInsn(CHECKCAST, poname);
		Label l5 = new Label();
		mv.visitJumpInsn(GOTO, l5);
		mv.visitLabel(l4);
		mv.visitInsn(ACONST_NULL);
		mv.visitLabel(l5);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, dbobjDesc);
	}
	
	
	@Override
	public void buildReadVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean unboxing) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()"+dbvoDesc+"", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, Type.getType(dbvoDesc).getInternalName(), "writeToDBObj", "()Lcom/akoo/orm/DBObj;", false);
		mv.visitTypeInsn(CHECKCAST, Type.getType(dbobjDesc).getInternalName());
		mv.visitFieldInsn(PUTFIELD, objname, fieldName, dbobjDesc);
	}
	
	@Override
	public void buildWriteVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean boxing) {
		String vo = Type.getType(dbvoDesc).getInternalName();
		mv.visitVarInsn(ALOAD, 2);
		mv.visitTypeInsn(NEW, vo);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, vo, "<init>", "()V", false);
		mv.visitVarInsn(ASTORE, 3);

		mv.visitVarInsn(ALOAD, 3);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, dbobjDesc);
		mv.visitMethodInsn(INVOKEVIRTUAL, vo, "readFromDBObj", "(Lcom/akoo/orm/DBObj;)V", false);

		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ALOAD, 3);
//		mv.visitMethodInsn(INVOKESPECIAL, vo, "<init>", "(Lcom/akoo/orm/DBObj;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "("+dbvoDesc+")V", false);
	}
	
	
	
	
	@Override
	public void buildReadHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		String po = Type.getType(dbobjDesc).getInternalName();
		mv.visitTypeInsn(NEW, po);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, po, "<init>", "()V", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, dbobjDesc);

		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, dbobjDesc);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "getBytes", "()[B", true);
		mv.visitMethodInsn(INVOKEVIRTUAL, po, "readFromBytes", "([B)V", false);

//		mv.visitInsn(DUP);
//		mv.visitVarInsn(ALOAD, 1);
//		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "getBytes", "()[B", true);
//		mv.visitMethodInsn(INVOKEVIRTUAL, po, "readFromBytes", "([B)V", false);
//		mv.visitMethodInsn(INVOKESPECIAL, po, "<init>", "([B)V", false);

	}
	
	
	
	@Override
	public void buildWriteHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, dbobjDesc);
		mv.visitMethodInsn(INVOKEINTERFACE, "com/akoo/orm/DataHolder", "putDBObj", "(Lcom/akoo/orm/DBObj;)V", true);
	}
	


}
