package com.akoo.orm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseCache<V extends DBVO> {

    private static Logger log = LoggerFactory.getLogger(BaseCache.class);

    private Map<Object, DBObj> cacheUpdate = new ConcurrentHashMap<>();

    private Map<Object, DBObj> cacheDelete = new ConcurrentHashMap<>();


    private void save(Collection<DBObj> saves) {
        for (DBObj dbObj : saves) {
            final Object primaryKey = dbObj.getPrimaryKey();
            if (cacheDelete.containsKey(primaryKey))
                return;
            cacheUpdate.put(primaryKey, dbObj);
        }
    }

    public void delete(final Collection<DBObj> deletes) {
        for (DBObj dbObj : deletes) {
            final Object primaryKey = dbObj.getPrimaryKey();
            cacheDelete.put(primaryKey, dbObj);
            cacheUpdate.remove(primaryKey);
        }
    }

    private void delete(DBObj dbObj) {
        Object primaryKey = dbObj.getPrimaryKey();
        cacheDelete.put(primaryKey, dbObj);
        cacheUpdate.remove(primaryKey);
    }

    private void save(DBObj dbObj) {
        Object primaryKey = dbObj.getPrimaryKey();
        if (cacheDelete.containsKey(primaryKey))
            return;
        cacheUpdate.put(primaryKey, dbObj);
    }

}
