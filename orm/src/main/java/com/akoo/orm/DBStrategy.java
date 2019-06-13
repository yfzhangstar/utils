package com.akoo.orm;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DBStrategy {

    Number getMaxKey(String tableName);

    boolean save(String tableName, Map<Integer, String> relKeyPreName, Collection<DBObj> updates);

    boolean save(String tableName, Map<Integer, String> relKeyPreName, DBObj dbObj);

    boolean delete(String tableName, Map<Integer, String> relKeyPreNames, DBObj dbObj);

    boolean delete(String tableName, Map<Integer, String> relKeyPreNames, Collection<DBObj> deletes);


    List<DBObj> getByRelationKey(String tableName, String relationKeyName, Object relationKey, Constructor<? extends DBObj> poClass);

    DBObj getByPrimaryKey(String tableName, Object primaryKey, Constructor<? extends DBObj> poClass);

    List<DBObj> getAll(String tableName, Constructor<? extends DBObj> poClass);


    List<DBObj> getByPrimaryKeys(String tableName, List<Object> primaryKey, Constructor<? extends DBObj> poClass);
    
    boolean changeRelKey(String tableName, Map<Integer, String> relKeyPreNames, Integer relKeyIx, Object oldRelKey, DBObj dbObj);


}