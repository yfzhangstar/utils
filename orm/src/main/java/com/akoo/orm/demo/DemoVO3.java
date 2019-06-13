package com.akoo.orm.demo;

import com.akoo.orm.enhanced.annotation.Save;
import com.akoo.orm.DBVO;

import java.util.Map;

public class DemoVO3 extends DBVO {

    @Save(ix = 1)
    private int id = 0;
    private String pid = "testPid";
    private Map<Integer, Integer> map;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }

    public void setMap(Map<Integer, Integer> map) {
        this.map = map;
    }

    @Override
    protected Object initPrimaryKey() {
        return id;
    }

    @Override
    protected Object getPrimaryKey() {
        return id;
    }

    @Override
    protected void setPrimaryKey(Object pk) {
        id = (Integer) pk;
    }

    @Override
    protected Object[] getRelationKeys() {
        return new Object[]{pid};
    }

    @Override
    protected void setRelationKeys(Object[] relationKeys) {
        pid = (String) relationKeys[0];
    }

    @Override
    protected void init() {

    }

    @Override
    public Integer getCid() {
        return null;
    }

    @Override
    public void setCid(Integer cid) {
    }


}
