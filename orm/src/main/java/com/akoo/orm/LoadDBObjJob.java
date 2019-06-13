package com.akoo.orm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public abstract class LoadDBObjJob<T extends DBObj> implements ExecuteInThread {
    private static Logger log = LoggerFactory.getLogger(LoadDBObjJob.class);
    private volatile T dbObj;

    void exe(T dbObj) {
        setDbObj(dbObj);
        execute();
    }

    @Override
    public void run() {
        try {
            if (dbObj != null) {
                processLoad(dbObj);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }


    public T getDbObj() {
        return dbObj;
    }

    public void setDbObj(T dbObj) {
        this.dbObj = dbObj;
    }

    public abstract void processLoad(T dbObj);

    public abstract ExecutorService getThreadPool();


}
