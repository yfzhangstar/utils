package com.akoo.common.util.convert.builder.impl.ext;

import com.akoo.common.util.convert.Util;
import com.akoo.common.util.convert.builder.DTObjFieldBuilder;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

@SuppressWarnings("rawtypes")
public class ListBuilder implements DTObjFieldBuilder {


	private Class objClass;

	public void setDbobjClass(Class dbobjClass) {
		objClass = dbobjClass;
	}

	
	@Override
	public void buildFromBytesHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 3);
		mv.visitVarInsn(ALOAD, 4);
		mv.visitLdcInsn(Type.getType(objClass));
		mv.visitMethodInsn(INVOKESTATIC, UTIL_PATH, "getLstFromHolder", "(L"+DATAHOLDER_PATH+";Ljava/lang/Class;)Ljava/util/List;", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getSetterName(fieldName), "(Ljava/util/List;)V", false);
	}
	
	
	
	@Override
	public void buildToBytesHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getGetterName(fieldName, false), "()Ljava/util/List;", false);
		mv.visitVarInsn(ALOAD, 4);
		mv.visitMethodInsn(INVOKESTATIC, UTIL_PATH, "fillLstToHolder", "(Ljava/util/List;L"+DATAHOLDER_PATH+";)V", false);
	}



	
}
