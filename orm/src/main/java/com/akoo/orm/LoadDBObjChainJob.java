package com.akoo.orm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class LoadDBObjChainJob<T extends DBObj> extends LoadDBObjJob<T> {


    private volatile BaseDAO dao;

    private volatile Object primaryKey;

    private volatile Integer relKeyIx;

    private volatile Object relKey;

    private volatile LoadDBObjChainJob next;

    private volatile ConcurrentHashMap<String, Object> dataCache;

    private volatile ExecutorService threadPool;

    private volatile ChainLoadFinisher finisher;

    private volatile String dataSign;

    public LoadDBObjChainJob() {
    }

    @Override
    public void run() {
        super.run();
        toNext();
    }

    private void toNext() {
        if (finisher != null)
            finisher.finishLoad(dataCache);
        if (next != null)
            next.load(dataCache);
    }

    public void load(ConcurrentHashMap<String, Object> dataCache) {
        this.dataCache = dataCache;
        loadDataInDAOThread();
    }


    private void loadDataInDAOThread() {
        if (getPrimaryKey() != null)
            dao.getByPrimaryKey(getPrimaryKey(), this);
        if (getRelKeyIx() != null && getRelKey() != null)
            dao.getByRelationKey(getRelKeyIx(), getRelKey(), new LoadDBObjsJob<DBObj>() {
                @Override
                public void run() {
                    super.run();
                    toNext();
                }

                @Override
                public void processLoad(List<DBObj> dbObjs) {
                    cache(dbObjs);
                }

                @Override
                public ExecutorService getThreadPool() {
                    return LoadDBObjChainJob.this.getThreadPool();
                }
            });
    }


    @Override
    public void processLoad(T dbobj) {
        cache(dbobj);
    }

    private void cache(T dbobj) {
        dataCache.put(getDataSign(), dbobj);
    }

    private void cache(List<DBObj> dbobjs) {
        dataCache.put(getDataSign(), dbobjs);
    }

    @Override
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public BaseDAO getDao() {
        return dao;
    }

    public void setDao(BaseDAO dao) {
        this.dao = dao;
    }

    public Object getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Object primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Integer getRelKeyIx() {
        return relKeyIx;
    }

    public void setRelKeyIx(Integer relKeyIx) {
        this.relKeyIx = relKeyIx;
    }

    public Object getRelKey() {
        return relKey;
    }

    public void setRelKey(Object relKey) {
        this.relKey = relKey;
    }

    public LoadDBObjChainJob getNext() {
        return next;
    }

    public void setNext(LoadDBObjChainJob next) {
        this.next = next;
    }

    public Map<String, Object> getDataCache() {
        return dataCache;
    }

    public void setDataCache(ConcurrentHashMap<String, Object> dataCache) {
        this.dataCache = dataCache;
    }

    public ChainLoadFinisher getFinisher() {
        return finisher;
    }


    public void setFinisher(ChainLoadFinisher finisher) {
        this.finisher = finisher;
    }


    public String getDataSign() {
        return dataSign;
    }


    public void setDataSign(String dataSign) {
        this.dataSign = dataSign;
    }


}
