package com.akoo.common.util.convert.builder.impl.ext;

import com.akoo.common.util.convert.Util;
import com.akoo.common.util.convert.builder.DTObjFieldBuilder;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

@SuppressWarnings("rawtypes")
public class MapBuilder implements DTObjFieldBuilder {


	private Class kdbobjClass;

	private Class vdbobjClass;

	public Class getKdbobjClass() {
		return kdbobjClass;
	}

	public void setKdbobjClass(Class kdbobjClass) {
		this.kdbobjClass = kdbobjClass;
	}

	public Class getVdbobjClass() {
		return vdbobjClass;
	}

	public void setVdbobjClass(Class vdbobjClass) {
		this.vdbobjClass = vdbobjClass;
	}

	
	
	@Override
	public void buildFromBytesHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 3);
		mv.visitVarInsn(ALOAD, 4);
		mv.visitMethodInsn(INVOKESTATIC, UTIL_PATH, "getMapFromHolder", "(L"+DATAHOLDER_PATH+";)Ljava/util/Map;", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getSetterName(fieldName), "(Ljava/util/Map;)V", false);
	}
	
	
	
	@Override
	public void buildToBytesHolderMContent(MethodVisitor mv, String fieldName, String objName) {
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEVIRTUAL, objName, Util.getGetterName(fieldName, false), "()Ljava/util/Map;", false);
		mv.visitVarInsn(ALOAD, 4);
		mv.visitMethodInsn(INVOKESTATIC, UTIL_PATH, "fillMapToHolder", "(Ljava/util/Map;L"+DATAHOLDER_PATH+";)V", false);
	}
	

}
