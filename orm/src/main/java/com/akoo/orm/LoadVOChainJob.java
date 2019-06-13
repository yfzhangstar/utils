package com.akoo.orm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;


public class LoadVOChainJob<V extends DBVO> extends LoadVOJob<V> {

    private volatile BaseDAO<V> dao;

    private volatile Object primaryKey;

    private volatile Integer relKeyIx;

    private volatile Object relKey;

    private volatile LoadVOChainJob next;

    private volatile ConcurrentHashMap<String, Object> dataCache;

    private volatile ExecutorService threadPool;

    private volatile ChainLoadFinisher finisher;

    private volatile String dataSign;

    public LoadVOChainJob() {
    }

    @Override
    public void run() {
        super.run();
        if (finisher != null)
            finisher.finishLoad(dataCache);
        if (next != null)
            next.load(dataCache);
    }


    public void load(ConcurrentHashMap<String, Object> dataCache) {
        this.dataCache = dataCache;
        loadDataInDAOThread();
    }

    public void loadDataInDAOThread() {
        if (getPrimaryKey() != null)
            dao.getVOByPrimaryKey(getPrimaryKey(), this);
        if (getRelKeyIx() != null && getRelKey() != null)
            dao.getVOsByRelationKey(getRelKeyIx(), getRelKey(), this);
    }


    @Override
    public void processLoad(V dbvo) {
        cache(dbvo);
    }

    @Override
    public void processLoad(List<V> dbvos) {
        cache(dbvos);
    }

//	@Override
//	public void processLoad(List<DBVO> dbvos) {
//		cache(dbvos);	
//	}

    private void cache(V dbvo) {
        dataCache.put(getDataSign(), dbvo);
    }


    private void cache(List<V> dbvos) {
        dataCache.put(getDataSign(), dbvos);
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

    public void setDao(BaseDAO<V> dao) {
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

    public LoadVOChainJob getNext() {
        return next;
    }

    public void setNext(LoadVOChainJob next) {
        this.next = next;
    }

    public Map getDataCache() {
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
