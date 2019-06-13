package com.akoo.orm.enhanced.asm.builder.impl.ext;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.akoo.orm.enhanced.Util;
import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;

@SuppressWarnings("rawtypes")
public class MapBuilder implements DBObjFieldBuilder{


	private Class kdbvoClass;
	
	private Class kdbobjClass;
	
	private Class vdbvoClass;

	private Class vdbobjClass;

	public Class getKdbobjClass() {
		return kdbobjClass;
	}

	public void setKdbobjClass(Class kdbobjClass) {
		this.kdbobjClass = kdbobjClass;
	}

	public Class getKdbvoClass() {
		return kdbvoClass;
	}

	public void setKdbvoClass(Class kdbvoClass) {
		this.kdbvoClass = kdbvoClass;
	}

	public Class getVdbobjClass() {
		return vdbobjClass;
	}

	public void setVdbobjClass(Class vdbobjClass) {
		this.vdbobjClass = vdbobjClass;
	}

	public Class getVdbvoClass() {
		return vdbvoClass;
	}

	public void setVdbvoClass(Class vdbvoClass) {
		this.vdbvoClass = vdbvoClass;
	}

	@Override
	public void buildDBObjField(ClassWriter cw, String name) {
		cw.visitField(ACC_PRIVATE, name, "Ljava/util/Map;", null, null).visitEnd();
	}
	

	@Override
	public void buildInitMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitTypeInsn(NEW, "java/util/HashMap");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "Ljava/util/Map;");

	};
	
	/*
	 *不可变对象不需要clone 
	 */
	@Override
	public void buildCloneMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "Ljava/util/Map;");
		mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "cloneMap", "(Ljava/util/Map;)Ljava/util/Map;", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "Ljava/util/Map;");
	}
	
	
	@Override
	public void buildReadVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean unboxing) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()Ljava/util/Map;", false);
		mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "transVOMapToObjMap", "(Ljava/util/Map;)Ljava/util/Map;", false);
		mv.visitFieldInsn(PUTFIELD, objname, fieldName, "Ljava/util/Map;");
	}
	
	@Override
	public void buildWriteVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean boxing) {
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "Ljava/util/Map;");
		mv.visitLdcInsn(Type.getType(kdbvoClass));
		mv.visitLdcInsn(Type.getType(vdbvoClass));
		mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "transObjMapToVOMap", "(Ljava/util/Map;Ljava/lang/Class;Ljava/lang/Class;)Ljava/util/Map;", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "(Ljava/util/Map;)V", false);
	}
	
	
	
	
	@Override
	public void buildReadHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitLdcInsn(Type.getType(kdbobjClass));
		mv.visitLdcInsn(Type.getType(vdbobjClass));
		mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "getMapFromDBHolder", "(Lcom/akoo/orm/DataHolder;Ljava/lang/Class;Ljava/lang/Class;)Ljava/util/Map;", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "Ljava/util/Map;");
	}
	
	
	
	@Override
	public void buildWriteHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "Ljava/util/Map;");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "fillMapToDBHolder", "(Ljava/util/Map;Lcom/akoo/orm/DataHolder;)V", false);
	}
	

}
