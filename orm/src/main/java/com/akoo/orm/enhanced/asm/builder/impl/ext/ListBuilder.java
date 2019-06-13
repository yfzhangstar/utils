package com.akoo.orm.enhanced.asm.builder.impl.ext;

import com.akoo.orm.enhanced.Util;
import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

@SuppressWarnings("rawtypes")
public class ListBuilder implements DBObjFieldBuilder{


	private Class dbvoClass;
	private Class objClass;
	

	public void setDbobjClass(Class dbobjClass) {
		objClass = dbobjClass;
	}

	public void setDbvoClass(Class dbvoClasss) {
		this.dbvoClass = dbvoClasss;
	}

	@Override
	public void buildDBObjField(ClassWriter cw, String name) {
		cw.visitField(ACC_PRIVATE, name, "Ljava/util/List;", null, null).visitEnd();
	}
	

	@Override
	public void buildInitMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitTypeInsn(NEW, "java/util/ArrayList");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "Ljava/util/List;");

    }

    /*
     *不可变对象不需要clone
     */
    @Override
    public void buildCloneMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "Ljava/util/List;");
		mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "cloneList", "(Ljava/util/List;)Ljava/util/List;", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "Ljava/util/List;");
	}
	
	
	@Override
	public void buildReadVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean unboxing) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getGetterName(fieldName, false), "()Ljava/util/List;", false);
		if(Util.checkDBVO(dbvoClass)){
			mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "transVOLstToObjLst", "(Ljava/util/List;)Ljava/util/List;", false);
		}else{
			mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "cloneList", "(Ljava/util/List;)Ljava/util/List;", false);
		}
		mv.visitFieldInsn(PUTFIELD, objname, fieldName, "Ljava/util/List;");
	}
	
	@Override
	public void buildWriteVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean boxing) {
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objname, fieldName, "Ljava/util/List;");
		if(Util.checkDBVO(dbvoClass)){
			mv.visitLdcInsn(Type.getType(dbvoClass));
			mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "transObjLstToVOLst", "(Ljava/util/List;Ljava/lang/Class;)Ljava/util/List;", false);
		}else{
			mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "cloneList", "(Ljava/util/List;)Ljava/util/List;", false);
		}
		mv.visitMethodInsn(INVOKEVIRTUAL, voname, Util.getSetterName(fieldName), "(Ljava/util/List;)V", false);
	}
	
	
	
	
	@Override
	public void buildReadHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitLdcInsn(Type.getType(objClass));
		mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "getLstFromDBHolder", "(Lcom/akoo/orm/DataHolder;Ljava/lang/Class;)Ljava/util/List;", false);
		mv.visitFieldInsn(PUTFIELD, objName, fieldName, "Ljava/util/List;");
	}
	
	
	
	@Override
	public void buildWriteHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objName, fieldName, "Ljava/util/List;");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKESTATIC, "com/akoo/orm/enhanced/Util", "fillLstToDBHolder", "(Ljava/util/List;Lcom/akoo/orm/DataHolder;)V", false);
	}



	
}
