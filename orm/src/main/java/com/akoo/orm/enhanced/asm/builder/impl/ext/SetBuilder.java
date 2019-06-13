package com.akoo.orm.enhanced.asm.builder.impl.ext;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.akoo.orm.enhanced.Util;
import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;

@SuppressWarnings("rawtypes")
public class SetBuilder implements DBObjFieldBuilder{

	private Class dbvoClass;
	private Class objClass;
	

	public void setDbobjClass(Class dbobjClass) {
		objClass = dbobjClass;
	}

	public void setDbvoClass(Class dbvoClass) {
		this.dbvoClass = dbvoClass;
	}

	@Override
	public void buildDBObjField(ClassWriter cw, String name) {
		cw.visitField(ACC_PRIVATE, name, "Ljava/util/Set;", null, null).visitEnd();
	}
	

	@Override
	public void buildInitMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitTypeInsn(NEW, "java/util/HashSet");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>", "()V", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "Ljava/util/Set;");

	};
	
	/*
	 *不可变对象不需要clone 
	 */
	@Override
	public void buildCloneMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "Ljava/util/Set;");
		mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "cloneSet", "(Ljava/util/Set;)Ljava/util/Set;", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "Ljava/util/Set;");
	}
	

	@Override
	public void buildReadVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean unboxing) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()Ljava/util/Set;", false);
		if(Util.checkDBVO(dbvoClass)){
			mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "transVOSetToObjSet", "(Ljava/util/Set;)Ljava/util/Set;", false);
		}else{
			mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "cloneSet", "(Ljava/util/Set;)Ljava/util/Set;", false);
		}
		mv.visitFieldInsn(PUTFIELD, objname, fieldName, "Ljava/util/Set;");
	}
	
	
	

	@Override
	public void buildWriteVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean boxing) {
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "Ljava/util/Set;");
		if(Util.checkDBVO(dbvoClass)){
			mv.visitLdcInsn(Type.getType(dbvoClass));
			mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "transObjSetToVOSet", "(Ljava/util/Set;Ljava/lang/Class;)Ljava/util/Set;", false);
		}else{
			mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "cloneSet", "(Ljava/util/Set;)Ljava/util/Set;", false);
		}
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "(Ljava/util/Set;)V", false);
	}
	
	

	@Override
	public void buildReadHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitLdcInsn(Type.getType(objClass));
		mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "getSetFromDBHolder", "(Lcom/akoo/orm/DataHolder;Ljava/lang/Class;)Ljava/util/Set;", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "Ljava/util/Set;");
	}
	
	
	
	@Override
	public void buildWriteHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "Ljava/util/Set;");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "fillSetToDBHolder", "(Ljava/util/Set;Lcom/akoo/orm/DataHolder;)V", false);
	}

	

}
