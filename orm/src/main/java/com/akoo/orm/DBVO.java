package com.akoo.orm;

import com.akoo.orm.enhanced.asm.Cache;
import org.objectweb.asm.Type;

import java.util.Date;

public abstract class DBVO {
    private Class<? extends DBObj> dbobjClass;

    private DBObj dbobj;

    private Date createDate;

    private Date modifyDate;


    public DBVO() {
        init();
        createDate = new Date();
    }

    public DBVO(DBObj dbObj) {
        init();
        readFromDBObj(dbObj);
    }

    public void readFromDBObj(DBObj dbObj) {
        this.setDbobj(dbObj);
        setPrimaryKey(dbObj.getPrimaryKey());
        setRelationKeys(dbObj.getRelationKeys());
        setCid(dbObj.getCid());
        setCreateDate(dbObj.getCreateDate());
        setModifyDate(dbObj.getModifyDate());
        readBizData(dbObj);
    }


    public DBObj writeToDBObj() {
        if (dbobj == null)
            dbobj = initDBObj();
        else
            writeAllData(dbobj);
        return dbobj.clone();

    }


    public DBObj initDBObj() {
        DBObj d = newDBObj();
        d.setPrimaryKey(defaultInitPrimaryKey());
        setPrimaryKey(d.getPrimaryKey());//防止initPrimaryKey时忘记给DBVO的主键赋值
        d.setRelationKeys(getRelationKeys());
        d.setCid(getCid());
        d.setCreateDate(createDate);
        d.setModifyDate(new Date());
        writeBizData(d);
        return d;
    }

    public void writeAllData(DBObj d) {
        d.setPrimaryKey(getPrimaryKey());
        d.setRelationKeys(getRelationKeys());
        d.setCid(getCid());
        d.setModifyDate(new Date());
        writeBizData(d);
    }

    public DBObj getDbobj() {
        return dbobj;
    }

    public void setDbobj(DBObj dbobj) {
        this.dbobj = dbobj;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    protected Object defaultInitPrimaryKey(){
    	Number numberPk = NumberDBIDManager.initDBID(getCacheDBIDKey());
    	if(numberPk != null)
    		return numberPk;
    	else
    		return initPrimaryKey();
    }

    protected Class<? extends DBVO> getCacheDBIDKey(){
        return getClass();
    }
    
    protected Object initPrimaryKey(){
    	return null;
    }

    protected abstract Object getPrimaryKey();

    protected abstract void setPrimaryKey(Object pk);

    protected abstract Object[] getRelationKeys();

    protected abstract void setRelationKeys(Object[] relationKeys);

    protected abstract void init();

    public abstract Integer getCid();

    public abstract void setCid(Integer cid);

    private DBObj newDBObj() {
        if (dbobjClass == null) {
            dbobjClass = Cache.getMappingDBObjClass(Type.getInternalName(getClass()));
        }
        try {
            return dbobjClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void readBizData(DBObj dbobj) {
        dbobj.writeVO(this);
    }

    private void writeBizData(DBObj dbobj) {
        dbobj.readVO(this);
    }

}



