package com.akoo.orm.enhanced.asm;

import com.akoo.orm.DBObj;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {

    public static final Map<String, Class<? extends DBObj>> voNameDBObjClassMapping = new ConcurrentHashMap<>();

    public static boolean containsDBObjClass(String voName) {
        return voNameDBObjClassMapping.containsKey(voName);
    }

    public static void addDBObjClass(String voName, Class<? extends DBObj> dbobjClass) {
        voNameDBObjClassMapping.put(voName, dbobjClass);
    }


    public static Class<? extends DBObj> getMappingDBObjClass(String voClassInternalName) {
        return voNameDBObjClassMapping.get(voClassInternalName);
    }

}
