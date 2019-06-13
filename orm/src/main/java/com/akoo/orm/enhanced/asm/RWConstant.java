package com.akoo.orm.enhanced.asm;

import com.akoo.orm.enhanced.annotation.Save;
import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;
import com.akoo.orm.enhanced.asm.builder.impl.base.*;
import com.akoo.orm.DBObj;
import com.akoo.orm.DBVO;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RWConstant {
	public static final String DBOBJ_INTERNAL_NAME = Type.getInternalName(DBObj.class);
	public static final String DBVO_INTERNAL_NAME = Type.getInternalName(DBVO.class);
	public static final String SAVE_ANNO_DESC = Type.getDescriptor(Save.class);
	public static final String SAVE_INDEX = "ix";
	
	private static final Map<String, DBObjFieldBuilder> BASE_DESC_BUILDER_MAPPING = new HashMap<>();
	private static final Set<String> NUMBER_WAPPER_CLASSES = new HashSet<>();
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
	
	public static DBObjFieldBuilder getBaseBuilder(String desc){
		return BASE_DESC_BUILDER_MAPPING.get(desc);
	}
	
	
	public static boolean containsBaseBuilder(String desc){
		return BASE_DESC_BUILDER_MAPPING.containsKey(desc);
	}
	
	public static void main(String[] args) {
		
	}
}
