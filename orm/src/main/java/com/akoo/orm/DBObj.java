package com.akoo.orm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DBObj implements Cloneable, Serializable {
    public static final byte KEY_TYPE_STRING = 0;
    public static final byte KEY_TYPE_LONG = 1;
    public static final byte KEY_TYPE_INTEGER = 2;
    public static final byte KEY_TYPE_SHORT = 3;
    public static final byte KEY_TYPE_UNKNOWN = 4;
    /**
     *
     */
    private static final long serialVersionUID = 1659142957051847366L;
    private static final Object[] EMPTY_REL_KEYS = new Object[0];
    private static Logger log = LoggerFactory.getLogger(DBObj.class);
    private Object primaryKey;
    private Object[] relationKeys;
    private Integer cid;
    private Date createDate;
    private Date modifyDate;

    public DBObj() {
        init();
    }

    public DBObj(DataHolder buffer) {
        init();
        this.primaryKey = getBaseType(buffer);
        int relKeyLen = buffer.getByte();
        this.relationKeys = new Object[relKeyLen];
        for (int i = 0; i < relKeyLen; i++) {
            relationKeys[i] = getBaseType(buffer);
        }
        boolean haveCid = buffer.getBoolean();
        if (haveCid)
            cid = buffer.getInt();
        createDate = buffer.getDate();
        modifyDate = buffer.getDate();
        readBizData(buffer);
    }

    public DBObj(byte[] data) {
        readFromBytes(data);
    }

    public void readFromBytes(byte[] data) {
        init();
        DBBuffer buffer = DBBuffer.warp(data);
        this.primaryKey = getBaseType(buffer);
        int relKeyLen = buffer.getByte();
        this.relationKeys = new Object[relKeyLen];
        for (int i = 0; i < relKeyLen; i++) {
            relationKeys[i] = getBaseType(buffer);
        }
        boolean haveCid = buffer.getBoolean();
        if (haveCid)
            cid = buffer.getInt();
        createDate = buffer.getDate();
        modifyDate = buffer.getDate();
        readBizData(buffer);
        buffer.free();
    }

    @SuppressWarnings("unchecked")
    public DBObj clone() {
        try {
            DBObj newobj = (DBObj) super.clone();
            if (relationKeys == null || relationKeys.length == 0) {
                newobj.setRelationKeys(EMPTY_REL_KEYS);
            } else
                newobj.setRelationKeys(Arrays.copyOf(relationKeys, relationKeys.length));
            newobj.cloneBizData();
            return newobj;
        } catch (CloneNotSupportedException e) {
            log.error("", e);
        }
        return null;
    }

    public void putBaseType(Object in, DataHolder buffer) {
        if (in instanceof String) {
            buffer.putByte(KEY_TYPE_STRING);
            buffer.putString(in.toString());
        } else if (in instanceof Long) {
            buffer.putByte(KEY_TYPE_LONG);
            buffer.putLong((Long) in);
        } else if (in instanceof Integer) {
            buffer.putByte(KEY_TYPE_INTEGER);
            buffer.putInt((Integer) in);
        } else if (in instanceof Short) {
            buffer.putByte(KEY_TYPE_SHORT);
            buffer.putShort((Short) in);
        } else
            buffer.putByte(KEY_TYPE_UNKNOWN);
    }

    public Object getBaseType(DataHolder buffer) {
        byte pktype = buffer.getByte();
        switch (pktype) {
            case KEY_TYPE_STRING:
                return buffer.getString();
            case KEY_TYPE_LONG:
                return buffer.getLong();
            case KEY_TYPE_INTEGER:
                return buffer.getInt();
            case KEY_TYPE_SHORT:
                return buffer.getShort();
        }
        return null;
    }


    public void writeToDBHolder(DataHolder buffer) {
        putBaseType(primaryKey, buffer);
        buffer.putByte((byte) relationKeys.length);
        for (int i = 0; i < relationKeys.length; i++) {
            putBaseType(relationKeys[i], buffer);
        }
        boolean haveCid = cid != null;
        buffer.putBoolean(haveCid);
        if (haveCid)
            buffer.putInt(cid);
        if (createDate == null)
            createDate = new Date();
        buffer.putDate(createDate);
        if (modifyDate == null)
            modifyDate = new Date();
        buffer.putDate(modifyDate);
        writeBizData(buffer);
    }


    public byte[] getData() {
        DBBuffer buffer = DBBuffer.allocate();
        putBaseType(primaryKey, buffer);
        buffer.putByte((byte) relationKeys.length);
        for (int i = 0; i < relationKeys.length; i++) {
            putBaseType(relationKeys[i], buffer);
        }
        boolean haveCid = cid != null;
        buffer.putBoolean(haveCid);
        if (haveCid)
            buffer.putInt(cid);
        if (createDate == null)
            createDate = new Date();
        buffer.putDate(createDate);
        if (modifyDate == null)
            modifyDate = new Date();
        buffer.putDate(modifyDate);
        writeBizData(buffer);
        return buffer.toBytes();
    }

    public Object getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Object primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Object[] getRelationKeys() {
        return relationKeys;
    }

    public void setRelationKeys(Object[] relationKeys) {
        this.relationKeys = relationKeys;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
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

    public abstract void writeBizData(DataHolder buffer);

    public abstract void readBizData(DataHolder buffer);

    public abstract void cloneBizData();

    public abstract void init();

    public abstract void writeVO(DBVO dbvo);

    public abstract void readVO(DBVO dbvo);

}
