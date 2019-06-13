package com.akoo.common.util.convert;

import com.akoo.common.util.GameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;


@SuppressWarnings({"unchecked", "rawtypes"})
public class Util {
    private static Logger log = LoggerFactory.getLogger(Util.class);

    public static final Map<String, Class> StrMappingClz = GameUtil.createSimpleMap();
    public static final Map<Class, String> ClzMappingStr = GameUtil.createSimpleMap();

    public static String getGetterName(String fieldName, boolean isBoolean) {
        if (fieldName.length() < 1)
            throw new RuntimeException("fieldName illegal");
        if (isBoolean)
            return "is" + trans(fieldName);
        return "get" + trans(fieldName);
    }

    public static String getSetterName(String fieldName) {
        if (fieldName.length() < 1)
            throw new RuntimeException("fieldName illegal");
        return "set" + trans(fieldName);
    }


    private static String trans(String fieldName) {
        char[] charArray = fieldName.toCharArray();
        if (charArray.length > 1) {
            if (Character.isUpperCase(charArray[1]) || Character.isUpperCase(charArray[0]))
                return fieldName;
            else {
                charArray[0] = Character.toUpperCase(charArray[0]);
                return new String(charArray);
            }
        } else {
            if (Character.isUpperCase(charArray[0]))
                return fieldName;
            else {
                charArray[0] = Character.toUpperCase(charArray[0]);
                return new String(charArray);

            }
        }
    }


    public static List getLstFromHolder(DataHolder holder, Class clazz) {
        int size = holder.getInt();
        List l = new ArrayList();
        if (size == 0)
            return l;
        for (int i = 0; i < size; i++) {
            Object classStr = getObjectFrmHolder(holder, String.class);
            Class clz = classString2Object((String) classStr);
            Object value = getObjectFrmHolder(holder, clz);
            l.add(value);
        }
        return l;
    }

    public static Set getSetFromHolder(DataHolder holder, Class clazz) {
        int size = holder.getInt();
        Set s = new HashSet();
        if (size == 0)
            return s;
        for (int i = 0; i < size; i++) {
            Object classStr = getObjectFrmHolder(holder, String.class);
            Class clz = classString2Object((String) classStr);
            Object value = getObjectFrmHolder(holder, clz);
            s.add(value);
        }
        return s;
    }

    public static Map getMapFromHolder(DataHolder holder) {
        int size = holder.getInt();
        Map r = new HashMap();
        if (size == 0)
            return r;
        for (int i = 0; i < size; i++) {
            Object classStr = getObjectFrmHolder(holder, String.class);
            Class clz = classString2Object((String) classStr);
            Object key = getObjectFrmHolder(holder, clz);
            classStr = getObjectFrmHolder(holder, String.class);
            clz = classString2Object((String) classStr);
            Object value = getObjectFrmHolder(holder, clz);
            r.put(key, value);
        }
        return r;
    }

    public static Object getObjectFrmHolder(DataHolder holder, Class clazz) {
        if (clazz == Integer.class) {// 不可变对象直接返回
            return holder.getInt();
        } else if (clazz == Short.class) {
            return holder.getShort();
        } else if (clazz == Long.class) {
            return holder.getLong();
        } else if (clazz == Byte.class) {
            return holder.getByte();
        } else if (clazz == Double.class) {
            return holder.getDouble();
        } else if (clazz == Float.class) {
            return holder.getFloat();
        } else if (clazz == Boolean.class) {// 不可变对象直接返回
            return holder.getBoolean();
        } else if (clazz == String.class) {// 不可变对象直接返回
            return holder.getString();
        } else if (clazz == Character.class) {
            return holder.getChar();
        } else if (clazz == int[].class) {
            return holder.getInts();
        } else if (clazz == long[].class) {
            return holder.getLongs();
        } else if (clazz == short[].class) {
            return holder.getShorts();
        } else if (clazz == byte[].class) {
            return holder.getBytes();
        } else if (clazz == char[].class) {
            return holder.getChars();
        } else if (clazz == boolean[].class) {
            return holder.getBooleans();
        } else if (clazz == float[].class) {
            return holder.getFloats();
        } else if (clazz == double[].class) {
            return holder.getDoubles();
        } else if (clazz == String[].class) {
            return holder.getStrs();
        } else if (clazz == Date.class) {
            return holder.getDate();
        } else {
            IConvertProcess process = ConvertManager.getProcessMap().get(clazz);
            if(process == null)
                throw new RuntimeException("cannt support " + clazz + " read");
            else {
                try {
                    Object obj = clazz.newInstance();
                    process.fromBytes(DBBuffer.warp(holder.getBytes()), obj);
                    return obj;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }

    public static void fillMapToHolder(Map map, DataHolder holder) {
        if(map == null || map.isEmpty()){
            holder.putInt(0);
            return;
        }
        int size = map.size();
        holder.putInt(size);
        if (size < 1)
            return;
        Set<Entry> es = map.entrySet();
        for (Entry e : es) {
            fillObjectToHolder(classObject2String(e.getKey()), holder);
            fillObjectToHolder(e.getKey(), holder);
            fillObjectToHolder(classObject2String(e.getValue()), holder);
            fillObjectToHolder(e.getValue(), holder);
        }

    }

    public static void fillLstToHolder(List list, DataHolder holder) {
        if(list == null || list.isEmpty()){
            holder.putInt(0);
            return;
        }
        holder.putInt(list.size());
        if (list.isEmpty())
            return;
        for (Object o : list) {
            fillObjectToHolder(classObject2String(o), holder);
            fillObjectToHolder(o, holder);
        }
    }

    public static void fillSetToHolder(Set set, DataHolder holder) {
        if(set == null || set.isEmpty()){
            holder.putInt(0);
            return;
        }
        holder.putInt(set.size());
        if (set.isEmpty())
            return;
        for (Object o : set) {
            fillObjectToHolder(classObject2String(o), holder);
            fillObjectToHolder(o, holder);
        }
    }

    public static void fillObjectToHolder(Date o, DataHolder holder){
        holder.putDate(o);
    }

    public static void fillObjectToHolder(Object o, DataHolder holder) {
        if (o instanceof Integer) {// 不可变对象直接返回
            holder.putInt((Integer) o);
        } else if (o instanceof Short) {
            holder.putShort((Short) o);
        } else if (o instanceof Long) {
            holder.putLong((Long) o);
        } else if (o instanceof Byte) {
            holder.putByte((Byte) o);
        } else if (o instanceof Double) {
            holder.putDouble((Double) o);
        } else if (o instanceof Float) {
            holder.putFloat((Float) o);
        } else if (o instanceof Boolean) {// 不可变对象直接返回
            holder.putBoolean((Boolean) o);
        } else if (o instanceof String) {// 不可变对象直接返回
            holder.putString((String) o);
        } else if (o instanceof Character) {
            holder.putChar((Character) o);
        }  else if (o instanceof int[]) {
            holder.putInts((int[]) o);
        } else if (o instanceof long[]) {
            holder.putLongs((long[]) o);
        } else if (o instanceof short[]) {
            holder.putShorts((short[]) o);
        } else if (o instanceof byte[]) {
            holder.putBytes((byte[]) o);
        } else if (o instanceof char[]) {
            holder.putChars((char[]) o);
        } else if (o instanceof boolean[]) {
            holder.putBooleans((boolean[]) o);
        } else if (o instanceof float[]) {
            holder.putFloats((float[]) o);
        } else if (o instanceof double[]) {
            holder.putDoubles((double[]) o);
        } else if (o instanceof String[]) {
            holder.putStrs((String[]) o);
        } else if (o instanceof Date) {
            holder.putDate((Date)o);
        }else {
            IConvertProcess process = ConvertManager.getProcessMap().get(o.getClass());
            if(process == null)
                throw new RuntimeException("cannt support " + o.getClass() + " fill");
            else {
                DBBuffer buffer = DBBuffer.allocate();
                process.toBytes(o, buffer);
                holder.putBytes(buffer.toBytes());
            }
        }
    }

    public static String classObject2String(Object o){
        String str = Util.ClzMappingStr.get(o.getClass());
        if(str == null) {
            return o.getClass().getName();
        }else{
            return str;
        }
    }

    public static Class classString2Object(String classStr){
        Class clz = Util.StrMappingClz.get(classStr);
        if(clz != null) return clz;
        try {
            return Class.forName(classStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
