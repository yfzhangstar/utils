package com.akoo.orm.enhanced;

import com.akoo.orm.DBObj;
import com.akoo.orm.DBVO;
import com.akoo.orm.DataHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;


@SuppressWarnings({"unchecked", "rawtypes"})
public class Util {
    private static Logger log = LoggerFactory.getLogger(Util.class);

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


    public static Map cloneMap(Map tgt) {
        Set<Entry> es = tgt.entrySet();
        Map ret = new HashMap();
        for (Entry e : es) {
            Object key = e.getKey();
            Object value = e.getValue();
            ret.put(cloneObject(key), cloneObject(value));
        }
        return ret;
    }

    public static Object cloneObject(Object tgt) {
        if (tgt instanceof Number) {// 不可变对象直接返回
            return tgt;
        } else if (tgt instanceof Boolean) {// 不可变对象直接返回
            return tgt;
        } else if (tgt instanceof String) {// 不可变对象直接返回
            return tgt;
        } else if (tgt instanceof Character) {
            return tgt;
        } else if (tgt instanceof Map) {
            return cloneMap((Map) tgt);
        } else if (tgt instanceof DBObj) {
            DBObj dbObj = (DBObj) tgt;
            return dbObj.clone();
        } else if (tgt instanceof List) {
            return cloneList((List) tgt);
        } else if (tgt instanceof Set) {
            return cloneSet((Set) tgt);
        } else if (tgt instanceof int[]) {
            return cloneIntAry((int[]) tgt);
        } else if (tgt instanceof long[]) {
            return cloneLongAry((long[]) tgt);
        } else if (tgt instanceof short[]) {
            return cloneShortAry((short[]) tgt);
        } else if (tgt instanceof byte[]) {
            return cloneByteAry((byte[]) tgt);
        } else if (tgt instanceof char[]) {
            return cloneCharAry((char[]) tgt);
        } else if (tgt instanceof boolean[]) {
            return cloneBooleanAry((boolean[]) tgt);
        } else if (tgt instanceof float[]) {
            return cloneFloatAry((float[]) tgt);
        } else if (tgt instanceof double[]) {
            return cloneDoubleAry((double[]) tgt);
        } else if (tgt instanceof String[]) {
            return cloneStringAry((String[]) tgt);
        }
        throw new RuntimeException("cannt support " + tgt.getClass() + " clone");
    }


    private static Object cloneStringAry(String[] tgt) {
        return Arrays.copyOf(tgt, tgt.length);
    }

    private static Object cloneDoubleAry(double[] tgt) {
        return Arrays.copyOf(tgt, tgt.length);
    }

    private static Object cloneFloatAry(float[] tgt) {
        return Arrays.copyOf(tgt, tgt.length);
    }

    private static Object cloneBooleanAry(boolean[] tgt) {
        return Arrays.copyOf(tgt, tgt.length);
    }

    private static Object cloneCharAry(char[] tgt) {
        return Arrays.copyOf(tgt, tgt.length);
    }

    private static Object cloneByteAry(byte[] tgt) {
        return Arrays.copyOf(tgt, tgt.length);
    }

    private static Object cloneShortAry(short[] tgt) {
        return Arrays.copyOf(tgt, tgt.length);
    }

    private static Object cloneLongAry(long[] tgt) {
        return Arrays.copyOf(tgt, tgt.length);
    }

    private static Object cloneIntAry(int[] tgt) {
        return Arrays.copyOf(tgt, tgt.length);
    }


    public static List cloneList(List tgt) {
        List ret = new ArrayList();
        for (Object obj : tgt) {
            ret.add(cloneObject(obj));
        }
        return ret;
    }

    public static Set cloneSet(Set tgt) {
        Set ret = new HashSet();
        for (Object obj : tgt) {
            ret.add(cloneObject(obj));
        }
        return ret;
    }

    public static List transVOLstToObjLst(List<DBVO> in) {
        List<DBObj> ret = new ArrayList();
        for (DBVO vo : in) {
            ret.add(vo.writeToDBObj());
        }
        return ret;
    }

    public static Set transVOSetToObjSet(Set<DBVO> in) {
        Set<DBObj> ret = new HashSet();
        for (DBVO vo : in) {
            ret.add(vo.writeToDBObj());
        }
        return ret;
    }

    public static Map transVOMapToObjMap(Map in) {
        if (in.size() == 0)
            return new HashMap();
        Entry i = (Entry) in.entrySet().iterator().next();
        boolean keyDBVO = checkDBVO(i.getKey().getClass());
        boolean valueDBVO = checkDBVO(i.getValue().getClass());
        Set<Entry> es = in.entrySet();
        Map ret = new HashMap<>();
        for (Entry e : es) {
            Object key = e.getKey();
            if (keyDBVO) {
                key = ((DBVO) key).writeToDBObj();
            } else {
                key = cloneObject(key);
            }

            Object value = e.getValue();
            if (valueDBVO) {
                value = ((DBVO) value).writeToDBObj();
            } else {
                value = cloneObject(value);
            }
            ret.put(key, value);
        }
        return ret;
    }


    public static List transObjLstToVOLst(List<DBObj> in, Class voClazz) {
        List<DBVO> ret = new ArrayList();
        for (DBObj obj : in) {
            try {
                DBVO vo = (DBVO) voClazz.newInstance();
                vo.readFromDBObj(obj);
                ret.add(vo);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("", e);
            }
        }
        return ret;
    }

    public static Set transObjSetToVOSet(Set<DBObj> in, Class voClazz) {
        Set<DBVO> ret = new HashSet();
        for (DBObj obj : in) {
            try {
                DBVO vo = (DBVO) voClazz.newInstance();
                vo.readFromDBObj(obj);
                ret.add(vo);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("", e);
            }
        }
        return ret;
    }

    public static Map transObjMapToVOMap(Map in, Class keyVOClazz, Class valueVOClazz) {
        Map ret = new HashMap();
        boolean keyDBVO = checkDBVO(keyVOClazz);
        boolean valueDBVO = checkDBVO(valueVOClazz);
        Set<Entry> es = in.entrySet();
        for (Entry e : es) {
            Object key = e.getKey();
            if (keyDBVO) {
                try {
                    DBVO vo = (DBVO) keyVOClazz.newInstance();
                    vo.readFromDBObj((DBObj) key);
                    key = vo;
                } catch (InstantiationException | IllegalAccessException e1) {
                    log.error("", e1);
                }

            } else {
                key = cloneObject(key);
            }

            Object value = e.getValue();
            if (valueDBVO) {
                try {
                    DBVO vo = (DBVO) valueVOClazz.newInstance();
                    vo.readFromDBObj((DBObj) value);
                    value = vo;
                } catch (InstantiationException | IllegalAccessException e1) {
                    log.error("", e1);
                }
            } else {
                value = cloneObject(value);
            }
            ret.put(key, value);
        }
        return ret;
    }


    public static boolean checkDBVO(Class<?> clazz) {
        return checkSuperClass(clazz, DBVO.class);
    }

    private static boolean checkSuperClass(Class<?> clazz, Class<?> superClazz) {
        boolean isDBVO = false;
        while (clazz != Object.class && clazz != null) {
            clazz = clazz.getSuperclass();
            if (clazz == superClazz) {
                isDBVO = true;
                break;
            }
        }
        return isDBVO;
    }

    public static void fillLstToDBHolder(List list, DataHolder holder) {
        holder.putInt(list.size());
        if (list.isEmpty())
            return;
        Object obj = list.get(0);
        fillCollectionToDBHolder(list, holder, obj);
    }

    public static void fillSetToDBHolder(Set set, DataHolder holder) {
        holder.putInt(set.size());
        if (set.isEmpty())
            return;
        Object obj = set.iterator().next();
        fillCollectionToDBHolder(set, holder, obj);
    }

    private static void fillCollectionToDBHolder(Collection c, DataHolder holder, Object obj) {
        if (obj instanceof Integer) {// 不可变对象直接返回
            for (Object o : c) {
                holder.putInt((Integer) o);
            }
        } else if (obj instanceof Short) {
            for (Object o : c) {
                holder.putShort((Short) o);
            }
        } else if (obj instanceof Long) {
            for (Object o : c) {
                holder.putLong((Long) o);
            }
        } else if (obj instanceof Byte) {
            for (Object o : c) {
                holder.putByte((Byte) o);
            }
        } else if (obj instanceof Double) {
            for (Object o : c) {
                holder.putDouble((Double) o);
            }
        } else if (obj instanceof Float) {
            for (Object o : c) {
                holder.putFloat((Float) o);
            }
        } else if (obj instanceof Boolean) {// 不可变对象直接返回
            for (Object o : c) {
                holder.putBoolean((Boolean) o);
            }
        } else if (obj instanceof String) {// 不可变对象直接返回
            for (Object o : c) {
                holder.putString((String) o);
            }
        } else if (obj instanceof Character) {
            for (Object o : c) {
                holder.putChar((Character) o);
            }
        } else if (obj instanceof DBObj) {
            for (Object o : c) {
                holder.putDBObj((DBObj) o);
            }
        } else if (obj instanceof int[]) {
            for (Object o : c) {
                holder.putInts((int[]) o);
            }
        } else if (obj instanceof long[]) {
            for (Object o : c) {
                holder.putLongs((long[]) o);
            }
        } else if (obj instanceof short[]) {
            for (Object o : c) {
                holder.putShorts((short[]) o);
            }
        } else if (obj instanceof byte[]) {
            for (Object o : c) {
                holder.putBytes((byte[]) o);
            }
        } else if (obj instanceof char[]) {
            for (Object o : c) {
                holder.putChars((char[]) o);
            }
        } else if (obj instanceof boolean[]) {
            for (Object o : c) {
                holder.putBooleans((boolean[]) o);
            }
        } else if (obj instanceof float[]) {
            for (Object o : c) {
                holder.putFloats((float[]) o);
            }
        } else if (obj instanceof double[]) {
            for (Object o : c) {
                holder.putDoubles((double[]) o);
            }
        } else if (obj instanceof String[]) {
            for (Object o : c) {
                holder.putStrs((String[]) o);
            }
        } else
            throw new RuntimeException("cannt support " + obj.getClass() + " fill");
    }

    public static List getLstFromDBHolder(DataHolder holder, Class clazz) {
        int size = holder.getInt();
        List l = new ArrayList();
        if (size == 0)
            return l;
        getCollectionFrmDBHolder(l, size, holder, clazz);
        return l;
    }

    public static Set getSetFromDBHolder(DataHolder holder, Class clazz) {
        int size = holder.getInt();
        Set s = new HashSet();
        if (size == 0)
            return s;
        getCollectionFrmDBHolder(s, size, holder, clazz);
        return s;
    }

    private static void getCollectionFrmDBHolder(Collection c, int size, DataHolder holder, Class clazz) {
        if (clazz == Integer.class) {// 不可变对象直接返回
            for (int i = 0; i < size; i++) {
                c.add(holder.getInt());
            }
        } else if (clazz == Short.class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getShort());
            }
        } else if (clazz == Long.class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getLong());
            }
        } else if (clazz == Byte.class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getByte());
            }
        } else if (clazz == Double.class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getDouble());
            }
        } else if (clazz == Float.class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getFloat());
            }
        } else if (clazz == Boolean.class) {// 不可变对象直接返回
            for (int i = 0; i < size; i++) {
                c.add(holder.getBoolean());
            }
        } else if (clazz == String.class) {// 不可变对象直接返回
            for (int i = 0; i < size; i++) {
                c.add(holder.getString());
            }
        } else if (clazz == Character.class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getChar());
            }
        } else if (clazz == int[].class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getInts());
            }
        } else if (clazz == long[].class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getLongs());
            }
        } else if (clazz == short[].class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getShorts());
            }
        } else if (clazz == byte[].class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getBytes());
            }
        } else if (clazz == char[].class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getChars());
            }
        } else if (clazz == boolean[].class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getBooleans());
            }
        } else if (clazz == float[].class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getFloats());
            }
        } else if (clazz == double[].class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getDoubles());
            }
        } else if (clazz == String[].class) {
            for (int i = 0; i < size; i++) {
                c.add(holder.getStrs());
            }
        } else if (checkSuperClass(clazz, DBObj.class)) {
            for (int i = 0; i < size; i++) {
                try {
                    DBObj obj = (DBObj) clazz.newInstance();
                    obj.readFromBytes(holder.getBytes());
                    c.add(obj);
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("", e);
                }

            }
        } else
            throw new RuntimeException("cannt support " + clazz + " read");
    }


    public static Map getMapFromDBHolder(DataHolder holder, Class kclazz, Class vclazz) {
        int size = holder.getInt();
        Map r = new HashMap();
        if (size == 0)
            return r;
        for (int i = 0; i < size; i++) {
            Object key = getObjectFrmDBHolder(holder, kclazz);
            Object value = getObjectFrmDBHolder(holder, vclazz);
            r.put(key, value);
        }
        return r;
    }

    private static Object getObjectFrmDBHolder(DataHolder holder, Class clazz) {
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
        } else if (checkSuperClass(clazz, DBObj.class)) {
            try {
                DBObj obj = (DBObj) clazz.newInstance();
                obj.readFromBytes(holder.getBytes());
                return obj;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else
            throw new RuntimeException("cannt support " + clazz + " read");
    }

    public static void fillMapToDBHolder(Map map, DataHolder holder) {
        int size = map.size();
        holder.putInt(size);
        if (size < 1)
            return;
        Set<Entry> es = map.entrySet();
        for (Entry e : es) {
            fillObjectToDBHolder(e.getKey(), holder);
            fillObjectToDBHolder(e.getValue(), holder);
        }

    }


    private static void fillObjectToDBHolder(Object o, DataHolder holder) {
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
        } else if (o instanceof DBObj) {
            holder.putDBObj((DBObj) o);
        } else if (o instanceof int[]) {
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
        } else
            throw new RuntimeException("cannt support " + o.getClass() + " fill");
    }
}
