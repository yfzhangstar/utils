package com.akoo.orm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class LoadVOJob<V extends DBVO> implements ExecuteInThread {
    private static Logger log = LoggerFactory.getLogger(LoadVOJob.class);

    private volatile DBObj dbObj;

    private volatile List<DBObj> dbObjs;

    private volatile Constructor<V> constructor;

    void exe(List<DBObj> dbObjs, Class<V> voclass) {
        setDbObjs(dbObjs);
        try {
            setConstructor(voclass.getConstructor(DBObj.class));
        } catch (SecurityException | NoSuchMethodException e) {
            log.error("", e);
            return;
        }
        execute();
    }


    void exe(DBObj dbObj, Class<V> voclass) {
        setDbObj(dbObj);
        try {
            setConstructor(voclass.getConstructor(DBObj.class));
        } catch (SecurityException | NoSuchMethodException e) {
            log.error("", e);
        }
        execute();
    }

    @Override
    public void run() {
        try {
            if (dbObj != null) {
                V dbvo = getInstance(dbObj);
                processLoad(dbvo);
            }
            if (dbObjs != null) {
                List<V> dbvos = new ArrayList<>();
                for (DBObj dbObj : dbObjs) {
                    V dbvo = getInstance(dbObj);
                    if (dbvo == null)
                        continue;
                    dbvos.add(dbvo);
                }
                processLoad(dbvos);
            }

        } catch (Exception e) {
            log.error("", e);
        }
    }


    public V getInstance(DBObj dbObj) {
        try {
            return getConstructor().newInstance(dbObj);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public DBObj getDbObj() {
        return dbObj;
    }

    public void setDbObj(DBObj dbObj) {
        this.dbObj = dbObj;
    }

    public List<DBObj> getDbObjs() {
        return dbObjs;
    }

    public void setDbObjs(List<DBObj> dbObjs) {
        this.dbObjs = dbObjs;
    }

    public abstract void processLoad(V dbvo);

    public abstract void processLoad(List<V> dbvo);

    public abstract ExecutorService getThreadPool();

    public Constructor<V> getConstructor() {
        return constructor;
    }

    public void setConstructor(Constructor<V> constructor) {
        this.constructor = constructor;
    }

}
