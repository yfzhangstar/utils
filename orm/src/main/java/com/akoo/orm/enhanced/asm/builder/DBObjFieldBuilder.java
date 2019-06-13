package com.akoo.orm.enhanced.asm.builder;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * VO PO 字段翻译器
 * @author peterveron
 */
public interface DBObjFieldBuilder extends Opcodes{
	
	
	void buildDBObjField(ClassWriter cw, String name);
	
	void buildReadVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean unboxing);
	
	void buildWriteVOMContent(MethodVisitor mv, String fieldName, String objname, String voname, boolean boxing);
	
	void buildReadHolderMContent(MethodVisitor mv, String fieldName, String objName);
	
	void buildWriteHolderMContent(MethodVisitor mv, String fieldName, String objName);
	
	void buildCloneMContent(MethodVisitor mv, String fieldName, String objName);
	
	void buildInitMContent(MethodVisitor mv, String fieldName, String objName);
}
