package com.akoo.common.util.convert;

import com.akoo.common.util.convert.annotation.AConvert;
import com.akoo.common.util.convert.annotation.AIndex;
import com.akoo.common.util.convert.builder.DTObjFieldBuilder;
import com.akoo.common.util.convert.builder.impl.base.*;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RWConstant {
	public static final String INDEX_ANNO_DESC = Type.getDescriptor(AIndex.class);
	public static final String INDEX_IX = "ix";

	public static final String OBJ_INTERNAL_NAME = Type.getInternalName(Object.class);
	
	private static final Map<String, DTObjFieldBuilder> BASE_DESC_BUILDER_MAPPING = new HashMap<>();
	private static final Set<String> NUMBER_WAPPER_CLASSES = new HashSet<>();


	public static String ACONVERT_ANNOTATION_NAME = Type.getDescriptor(AConvert.class);
	static{
		BASE_DESC_BUILDER_MAPPING.put(Type.INT_TYPE.getDescriptor(), IntBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.BOOLEAN_TYPE.getDescriptor(), BooleanBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.CHAR_TYPE.getDescriptor(), CharBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.BYTE_TYPE.getDescriptor(), ByteBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.SHORT_TYPE.getDescriptor(), ShortBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.LONG_TYPE.getDescriptor(), LongBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.DOUBLE_TYPE.getDescriptor(), DoubleBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.FLOAT_TYPE.getDescriptor(), FloatBuilder.getInstance());
		
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(Integer.class), IntBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(Boolean.class), BooleanBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(Byte.class), ByteBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(Character.class), CharBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(Short.class), ShortBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(Long.class), LongBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(Double.class), DoubleBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(Float.class), FloatBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(String.class), StrBuilder.getInstance());
		
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(int[].class), AryIntBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(boolean[].class), AryBooleanBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(byte[].class), AryByteBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(char[].class), AryCharBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(short[].class), AryShortBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(long[].class), AryLongBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(double[].class), AryDoubleBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(float[].class), AryFloatBuilder.getInstance());
		BASE_DESC_BUILDER_MAPPING.put(Type.getDescriptor(String[].class), AryStrBuilder.getInstance());
		
		
		NUMBER_WAPPER_CLASSES.add(Type.getDescriptor(Integer.class));
		NUMBER_WAPPER_CLASSES.add(Type.getDescriptor(Boolean.class));
		NUMBER_WAPPER_CLASSES.add(Type.getDescriptor(Byte.class));
		NUMBER_WAPPER_CLASSES.add(Type.getDescriptor(Character.class));
		NUMBER_WAPPER_CLASSES.add(Type.getDescriptor(Short.class));
		NUMBER_WAPPER_CLASSES.add(Type.getDescriptor(Long.class));
		NUMBER_WAPPER_CLASSES.add(Type.getDescriptor(Double.class));
		NUMBER_WAPPER_CLASSES.add(Type.getDescriptor(Float.class));
	}
	
	public static boolean isNumberWapper(String desc){
		return NUMBER_WAPPER_CLASSES.contains(desc);
	}
	
	public static DTObjFieldBuilder getBaseBuilder(String desc){
		return BASE_DESC_BUILDER_MAPPING.get(desc);
	}
	
	
	public static boolean containsBaseBuilder(String desc){
		return BASE_DESC_BUILDER_MAPPING.containsKey(desc);
	}

}
