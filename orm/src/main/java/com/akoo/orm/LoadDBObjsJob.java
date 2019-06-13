package com.akoo.orm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author Aly on  2016-08-10.
 */
public abstract class LoadDBObjsJob<T extends DBObj> implements ExecuteInThread {

    private static Logger log = LoggerFactory.getLogger(LoadDBObjJob.class);

    private volatile List<T> dbObjs;

    void exe(List<T> dbObjs) {
        setDbObjs(dbObjs);
        execute();
    }

    @Override
    public void run() {
        try {
            if (dbObjs != null && dbObjs.size() > 0) {
                processLoad(dbObjs);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }


    public List<T> getDbObjs() {
        return dbObjs;
    }

    public void setDbObjs(List<T> dbObjs) {
        this.dbObjs = dbObjs;
    }


    public abstract void processLoad(List<T> dbObjs);

    public abstract ExecutorService getThreadPool();

}
