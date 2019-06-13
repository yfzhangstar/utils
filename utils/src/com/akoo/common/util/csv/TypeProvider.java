package com.akoo.common.util.csv;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.akoo.common.util.GameUtil;
import com.google.gson.reflect.TypeToken;

/**
 * @author Aly on  2016-10-24.
 */
public class TypeProvider {
    private static final Map<String, Class> sysTypeMapping = GameUtil.createSimpleMap();
    static int defaultSetterFindDeep = 5;

    static {
        sysTypeMapping.put("int", int.class);
        sysTypeMapping.put("ints", int[].class);
        sysTypeMapping.put("intsAry", int[][].class);
        sysTypeMapping.put("str", String.class);
        sysTypeMapping.put("strs", String[].class);
        sysTypeMapping.put("strsAry", String[][].class);
        sysTypeMapping.put("long", long.class);
        sysTypeMapping.put("longs", long[].class);
        sysTypeMapping.put("bool", boolean.class);
        sysTypeMapping.put("bools", boolean[].class);
    }

    private Map<String, Class> typeMapping;
    private Map<String, Type> cacheCusType;

    public Class getSetMethodTypeByTypeName(String typeName) {
        Class setType = sysTypeMapping.get(typeName);
        if (null == setType && null != typeMapping) {
            return typeMapping.get(typeName);
        }
        return setType;
    }

    public Type getJsonTypeByTypeName(String typeName) {
        if (null != cacheCusType) return cacheCusType.get(typeName);
        return null;
    }

    /**
     * 注册自定义数据结构类型
     * 为了方便区分 防止初始类型被 所有类型的名字开头均增加 “json” 字符串
     *
     * @param setParamClassType set方法 的参数类型
     * @param type              set方法的值转换类
     */
    public void regCusJsonType(String typeName, Class<?> setParamClassType, TypeToken<?> type) {
        String nName = "json" + typeName;
        if (!setParamClassType.isAssignableFrom(type.getRawType())) {
            throw new RuntimeException("参数类型不匹配" + setParamClassType + "-->" + type);
        }
        if (null == typeMapping) typeMapping = new HashMap<>();
        typeMapping.put(nName, setParamClassType);

        if (null == cacheCusType) cacheCusType = new HashMap<>();
        cacheCusType.put(nName, type.getType());
    }
}
