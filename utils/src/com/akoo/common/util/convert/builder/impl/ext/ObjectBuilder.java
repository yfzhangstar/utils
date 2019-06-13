package com.akoo.common.util.convert.builder.impl.ext;

import com.akoo.common.util.convert.RWConstant;
import com.akoo.common.util.convert.Util;
import com.akoo.common.util.convert.builder.DTObjFieldBuilder;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;


@SuppressWarnings("rawtypes")
public class ObjectBuilder implements DTObjFieldBuilder {

	private String objClass;


	public ObjectBuilder(Class clz) {
		objClass = Type.getDescriptor(clz);
	}


	@Override
	public void buildFromBytesHolderMContent(MethodVisitor mv, String fieldName, String objName) {
//		mv.visitVarInsn(ALOAD, 3);
//		mv.visitVarInsn(ALOAD, 1);
//		mv.visitLdcInsn(Type.getType(objClass));
//		mv.visitMethodInsn(INVOKESTATIC, UTIL_PATH, "getObjectFrmHolder", "(L"+DATAHOLDER_PATH+";Ljava/lang/Class;)L"+ RWConstant.OBJ_INTERNAL_NAME+";", false);
//		mv.visitTypeInsn(CHECKCAST, Type.getType(objClass).getInternalName());
//		mv.visitVarInsn(ASTORE, 4);
//		mv.visitVarInsn(ALOAD, 3);
//		mv.visitVarInsn(ALOAD, 4);
//		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getSetterName(fieldName), "("+objClass+")V", false);

		mv.visitVarInsn(ALOAD, 4);
		mv.visitMethodInsn(INVOKEINTERFACE, DATAHOLDER_PATH, "getString", "()Ljava/lang/String;", true);
		mv.visitMethodInsn(INVOKESTATIC, UTIL_PATH, "classString2Object", "(Ljava/lang/String;)Ljava/lang/Class;", false);
		mv.visitVarInsn(ASTORE, 5);
		mv.visitVarInsn(ALOAD, 4);
		mv.visitVarInsn(ALOAD, 5);
		mv.visitMethodInsn(INVOKESTATIC, UTIL_PATH, "getObjectFrmHolder", "(L"+DATAHOLDER_PATH+";Ljava/lang/Class;)L"+ RWConstant.OBJ_INTERNAL_NAME+";", false);
		mv.visitTypeInsn(CHECKCAST, Type.getType(objClass).getInternalName());
		mv.visitVarInsn(ASTORE, 6);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitVarInsn(ALOAD, 6);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getSetterName(fieldName), "("+objClass+")V", false);
	}



	@Override
	public void buildToBytesHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 4);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getGetterName(fieldName, false), "()"+objClass, false);
		mv.visitMethodInsn(INVOKESTATIC, UTIL_PATH, "classObject2String", "(Ljava/lang/Object;)Ljava/lang/String;", false);
		mv.visitMethodInsn(INVOKEINTERFACE, DATAHOLDER_PATH, "putString", "(Ljava/lang/String;)V", true);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getGetterName(fieldName, false), "()"+objClass, false);
		mv.visitVarInsn(ALOAD, 4);
		mv.visitMethodInsn(INVOKESTATIC, UTIL_PATH, "fillObjectToHolder", "(L"+ RWConstant.OBJ_INTERNAL_NAME +";L"+DATAHOLDER_PATH+";)V", false);
	}



}
