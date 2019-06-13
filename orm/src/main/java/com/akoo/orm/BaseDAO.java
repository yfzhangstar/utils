package com.akoo.orm;

import com.akoo.orm.enhanced.asm.Cache;
import com.akoo.orm.enhanced.asm.DBObjChainLoader;
import com.akoo.conc.thread.ESManager;
import com.akoo.conc.thread.ESManager.ThreadJob;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class BaseDAO<V extends DBVO> {
    private static Logger log = LoggerFactory.getLogger(BaseDAO.class);

    private DBStrategy DBStrategy;

    private String threadPoolSign;

    private String tableName;

    private Boolean isNumberPK;
    
    private Byte numPKType  = DBObj.KEY_TYPE_INTEGER;//兼容旧版本处理

    private Map<Integer, String> relKeyPreName;

    private Class<V> voClass;

    private Class<? extends DBObj> poClass;

    private Constructor<? extends DBObj> poByteArrayConstructor;

    private ESManager threadManager;

    private Map<Object, DBObj> cacheUpdate = new ConcurrentHashMap<>();

    private Map<Object, DBObj> cacheDelete = new ConcurrentHashMap<>();

    public void save(V dbvo) {
        final DBObj dbObj = dbvo.writeToDBObj();
        saveDBObj(dbObj);
    }
    
    private List<DBObj> dbvos2DBObjs(Collection<? extends V> vos){
    	List<DBObj> ret = new ArrayList<DBObj>();
    	for (V	vo : vos) {
			ret.add(vo.writeToDBObj());
		}
    	return ret;
    }
    

    private void saveDBObj(final DBObj dbObj) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                saveInCurrentThread(dbObj);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }

    private void saveInCurrentThread(DBObj dbObj) {
        Object primaryKey = dbObj.getPrimaryKey();
        if (getCacheDelete().containsKey(primaryKey))
            return;
        getCacheUpdate().put(primaryKey, dbObj);
    }

    public void delete(V dbvo) {
        final DBObj dbObj = dbvo.writeToDBObj();
        deleteDBObj(dbObj);
    }

    private void deleteDBObj(final DBObj dbObj) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                deleteInCurrentThread(dbObj);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }

    private void deleteInCurrentThread(DBObj dbObj) {
        Object primaryKey = dbObj.getPrimaryKey();
        getCacheDelete().put(primaryKey, dbObj);
        getCacheUpdate().remove(primaryKey);
    }


    public void save(Collection<V> dbvos) {
        final List<DBObj> saves = dbvos2DBObjs(dbvos);
        saveDBObj(saves);
    }

    private void saveDBObj(final Collection<DBObj> saves) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                saveInCurrentThread(saves);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }


    private void saveInCurrentThread(Collection<DBObj> saves) {
        for (DBObj dbObj : saves) {
            final Object primaryKey = dbObj.getPrimaryKey();
            if (getCacheDelete().containsKey(primaryKey))
                return;
            getCacheUpdate().put(primaryKey, dbObj);
        }
    }


    public void delete(Collection<? extends V> dbvos) {
        final Collection<DBObj> deletes =  dbvos2DBObjs(dbvos);
        deleteDBObj(deletes);
    }

    public void deleteDBObj(final Collection<DBObj> deletes) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                deleteInCurrentThread(deletes);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }

    public void deleteInCurrentThread(final Collection<DBObj> deletes) {
        for (DBObj dbObj : deletes) {
            final Object primaryKey = dbObj.getPrimaryKey();
            getCacheDelete().put(primaryKey, dbObj);
            getCacheUpdate().remove(primaryKey);
        }
    }

    public Number getMaxKey() {
        if (!getIsNumberPK())
            return null;
        return getDBStrategy().getMaxKey(tableName);
    }


    public void directSave(final V dbvo) {
        final DBObj dbObj = dbvo.writeToDBObj();
        directSaveDBObj(dbObj);
    }

    private void directSaveDBObj(final DBObj dbObj) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                directSaveInCurrentThread(dbObj);
            }


            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }

    public void directSave(final Collection<V> dbvos) {

        final Collection<DBObj> dbObjs =  dbvos2DBObjs(dbvos);;
        directSaveDBObj(dbObjs);
    }

    private void directSaveDBObj(final Collection<DBObj> dbObjs) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                directSaveInCurrentThread(dbObjs);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }

    private void directSaveInCurrentThread(DBObj dbObj) {
        if (cacheDelete.containsKey(dbObj.getPrimaryKey()))
            return;
        if (getDBStrategy().save(tableName, relKeyPreName, dbObj))
            cacheUpdate.remove(dbObj.getPrimaryKey());
    }


    private void directSaveInCurrentThread(Collection<DBObj> dbObjs) {
        for (Iterator<DBObj> i = dbObjs.iterator(); i.hasNext(); ) {
            DBObj dbObj = i.next();
            if (cacheDelete.containsKey(dbObj.getPrimaryKey()))
                i.remove();
        }
        if (dbObjs.size() == 0)
            return;
        if (getDBStrategy().save(tableName, relKeyPreName, dbObjs)) {
            for (DBObj dbObj : dbObjs) {
                cacheUpdate.remove(dbObj.getPrimaryKey());
            }
        }

    }


    public void directDelete(final V dbvo) {
        final DBObj dbObj = dbvo.writeToDBObj();
        directDeleteDBObj(dbObj);
    }

    private void directDeleteDBObj(final DBObj dbObj) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                directDeleteInCurrentThread(dbObj);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }

    public void directDelete(final Collection<V> dbvos) {
        final Collection<DBObj> dbObjs = dbvos2DBObjs(dbvos);;
        directDeleteDBObj(dbObjs);
    }

    private void directDeleteDBObj(final Collection<DBObj> dbObjs) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                directDeleteInCurrentThread(dbObjs);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }

    private void directDeleteInCurrentThread(final DBObj dbObj) {
        cacheUpdate.remove(dbObj.getPrimaryKey());
        if (getDBStrategy().delete(tableName, relKeyPreName, dbObj))
            cacheDelete.remove(dbObj.getPrimaryKey());
    }


    private void directDeleteInCurrentThread(final Collection<DBObj> dbObjs) {
        for (DBObj dbObj : dbObjs) {
            cacheUpdate.remove(dbObj.getPrimaryKey());
        }
        if (getDBStrategy().delete(tableName, relKeyPreName, dbObjs)) {
            for (DBObj dbObj : dbObjs) {
                cacheDelete.remove(dbObj.getPrimaryKey());
            }
        }
    }


    public DBObj getByPrimaryKey(Object primaryKey) {
        //与内存中对象比对,由内存中对象进行删除或替换
        if (cacheDelete.containsKey(primaryKey))
            return null;
        DBObj obj = cacheUpdate.get(primaryKey);
        if (obj != null)
            return obj;
        return getDBStrategy().getByPrimaryKey(tableName, primaryKey, poByteArrayConstructor);
    }
    
    public List<DBObj> getByPrimaryKeys(List<Object> primaryKeys) {
    	List<DBObj> ret = new ArrayList<>();
        //与内存中对象比对,由内存中对象进行删除或替换
    	for (Iterator<Object> i = primaryKeys.iterator(); i.hasNext();) {
    		Object pk = i.next();
			if(cacheDelete.containsKey(pk))
				i.remove();
			else if(cacheUpdate.containsKey(pk)){
				ret.add(cacheUpdate.get(pk));
				i.remove();
			}
		}
    	if(primaryKeys.isEmpty())
    		return ret;
    	List<DBObj> dbRet = getDBStrategy().getByPrimaryKeys(tableName, primaryKeys, poByteArrayConstructor);
    	ret.addAll(dbRet);
    	return ret;
    }
    
    public void getByPrimaryKeys(final List<Object> primaryKeys, final LoadDBObjsJob<DBObj> loadDBObjJob) {
    	getThreadManager().exec(new ThreadJob() {
    		@Override
    		public void run() {
    			List<DBObj> dbObjs = getByPrimaryKeys(primaryKeys);
    			loadDBObjJob.exe(dbObjs);
    		}

    		@Override
    		public String getThreadPoolSign() {
    			return threadPoolSign;
    		}
    	});
    }
    
    

    public void getByPrimaryKey(final Object primaryKey, final LoadDBObjJob<DBObj> loadDBObjJob) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                DBObj dbObj = getByPrimaryKey(primaryKey);
                loadDBObjJob.exe(dbObj);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });


    }
    
    public void getVOByPrimaryKeys(final List<Object> pks, final LoadVOJob<V> loadVOJob) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                List<DBObj> dbObjs = getByPrimaryKeys(pks);
                loadVOJob.exe(dbObjs, voClass);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });


    }
    

    public void getVOByPrimaryKey(final Object primaryKey, final LoadVOJob<V> loadVOJob) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                DBObj dbObj = getByPrimaryKey(primaryKey);
                loadVOJob.exe(dbObj, voClass);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }

    public List<DBObj> getByRelationKey(int relKeyIX, Object relationKey) {
        List<DBObj> rets = getDBStrategy().getByRelationKey(tableName, relKeyPreName.get(relKeyIX), relationKey, poByteArrayConstructor);
        return updateByCacheData(rets);
    }

    public void getByRelationKey(final int relKeyIX, final Object relationKey, final LoadDBObjsJob<DBObj> loadDBObjJob) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                List<DBObj> rets = getByRelationKey(relKeyIX, relationKey);
                loadDBObjJob.exe(rets);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });

    }


    public void getVOsByRelationKey(final int relKeyIX, final Object relationKey, final LoadVOJob<V> loadVOJob) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                List<DBObj> dbObjs = getByRelationKey(relKeyIX, relationKey);
                loadVOJob.exe(dbObjs, voClass);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }

    public List<DBObj> getAll() {
        List<DBObj> rets = getDBStrategy().getAll(tableName, poByteArrayConstructor);
        return updateByCacheData(rets);
    }

    public void getAllVOs(final LoadVOJob<V> loadVOJob) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
            	try{
            	
            		List<DBObj> dbObjs = getAll();
                    loadVOJob.exe(dbObjs, voClass);
            	}catch(Throwable t){
            		t.printStackTrace();
            	}                
            	
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }


    private List<DBObj> updateByCacheData(List<DBObj> rets) {
        List<DBObj> updates = new ArrayList<>();
        for (Iterator<DBObj> i = rets.iterator(); i.hasNext(); ) {
            DBObj dbObj = i.next();
            Object primaryKey = dbObj.getPrimaryKey();
            DBObj cacheDBObj = cacheUpdate.get(primaryKey);
            if (cacheDelete.containsKey(primaryKey) || cacheDBObj != null) {//与内存中对象比对,由内存中对象进行删除或替换
                i.remove();
                if (cacheDBObj != null)
                    updates.add(cacheDBObj);
            }
        }
        if (updates.size() > 0)
            rets.addAll(updates);
        return rets;
    }

    public void saveAndRemoveCache() {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                saveAndRemoveCacheInCurrentThread();
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }

    public void saveAndRemoveCacheInCurrentThread() {
        try {
            if (getDBStrategy().save(tableName, relKeyPreName, cacheUpdate.values()))
                cacheUpdate.clear();
        } catch (Exception e) {
            log.error("{}", getDBStrategy(), e);
        }

    }

    public void deleteAndRemoveCache() {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                deleteAndRemoveCacheInCurrentThread();
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }

    public void deleteAndRemoveCacheInCurrentThread() {
        if (getDBStrategy().delete(tableName, relKeyPreName, cacheDelete.values()))
            cacheDelete.clear();
    }

    
    public void directChangeRelKey(final int relKeyIx, final Object oldRelKey, V dbvo) {
    	final DBObj write = dbvo.writeToDBObj();
    	directChangeRelKey(relKeyIx, oldRelKey, write);
    }

    

    public void directChangeRelKey(final int relKeyIx, final Object oldRelKey, final DBObj dbobj) {
        getThreadManager().exec(new ThreadJob() {
            @Override
            public void run() {
                if (cacheDelete.containsKey(dbobj.getPrimaryKey()))//如果缓存删除集中包含需要删除外键的对象.取消操作,等待删除执行时外键自然删除
                    return;
                cacheUpdate.remove(dbobj.getPrimaryKey());//如果缓存更新集中包含需要删除外键的对象.将其移除缓存,直接保存新的dbobj以及新外键关联
                getDBStrategy().changeRelKey(tableName, relKeyPreName, relKeyIx, oldRelKey, dbobj);
            }

            @Override
            public String getThreadPoolSign() {
                return threadPoolSign;
            }
        });
    }


    public ExecutorService getExecutorService() {
        return threadManager.getExecutorService(threadPoolSign);
    }

    public DBStrategy getSaveStrategy() {
        return getDBStrategy();
    }

    public void setSaveStrategy(DBStrategy DBStrategy) {
        this.setDBStrategy(DBStrategy);
    }

    public String getThreadPoolSign() {
        return threadPoolSign;
    }

    public void setThreadPoolSign(String threadPoolSign) {
        this.threadPoolSign = threadPoolSign;
    }

    protected Map<Object, DBObj> getCacheUpdate() {
        return cacheUpdate;
    }

    protected Map<Object, DBObj> getCacheDelete() {
        return cacheDelete;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Class<? extends V> getVoClass() {
        return voClass;
    }

    public void setVoClass(Class<V> voClass) {
        this.voClass = voClass;

        if (voClass == null)
            throw new RuntimeException("AdvBaseDAO not setVOClass");
        if (poClass == null) {
            String voInternalName = Type.getInternalName(getVoClass());
            Class<? extends DBObj> dbobjClass = Cache.getMappingDBObjClass(voInternalName);
            if (dbobjClass == null) {
                try {
                    DBObjChainLoader loader = new DBObjChainLoader();
                    ClassReader cr = new ClassReader(voClass.getName());
                    cr.accept(loader, 0);
                    dbobjClass = Cache.getMappingDBObjClass(voInternalName);
                } catch (IOException e) {
                    log.error("", e);
                }
            }
            setPoClass(dbobjClass);
        }
    }

    private void setPoClass(Class<? extends DBObj> poClass) {
        this.poClass = poClass;
        try {
            this.poByteArrayConstructor = poClass.getConstructor(byte[].class);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public Map<Integer, String> getRelKeyPreName() {
        return relKeyPreName;
    }

    public void setRelKeyPreName(Map<Integer, String> relKeyPreName) {
        this.relKeyPreName = relKeyPreName;
    }

    public Boolean getIsNumberPK() {
        return isNumberPK;
    }

    public void setIsNumberPK(Boolean isNumberPK) {
        this.isNumberPK = isNumberPK;
        if(!isNumberPK)
        	setNumPKType(DBObj.KEY_TYPE_UNKNOWN);
    }

    public ESManager getThreadManager() {
        return threadManager;
    }

    public void setThreadManager(ESManager threadManager) {
        this.threadManager = threadManager;
    }

    public DBStrategy getDBStrategy() {
        return DBStrategy;
    }

    public void setDBStrategy(DBStrategy dBStrategy) {
        DBStrategy = dBStrategy;
    }

	public Byte getNumPKType() {
		return numPKType;
	}

	public void setNumPKType(Byte numPKType) {
		this.numPKType = numPKType;
	}

}