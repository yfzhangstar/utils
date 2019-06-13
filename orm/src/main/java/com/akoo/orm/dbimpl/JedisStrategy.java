package com.akoo.orm.dbimpl;

import com.akoo.orm.DBBuffer;
import com.akoo.orm.DBObj;
import com.akoo.orm.DBStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;


public class JedisStrategy implements DBStrategy {
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static Logger log = LoggerFactory.getLogger(JedisStrategy.class);
    private JedisPool jedisPool;


    public void dealMaxKey(String tableName, ShardedJedis sj, DBObj dbo) {
        Object pk = dbo.getPrimaryKey();
        if (!isNumPK(dbo))
            return;
        String st = sj.hget("maxKey", tableName);
        byte type = -1;
        if (pk instanceof Long) {
            type = DBObj.KEY_TYPE_LONG;
        } else if (pk instanceof Integer) {
            type = DBObj.KEY_TYPE_INTEGER;
        } else if (pk instanceof Short) {
            type = DBObj.KEY_TYPE_SHORT;
        }
        if (st == null || st.length() <= 1) {
            sj.hset("maxKey".getBytes(CHARSET), tableName.getBytes(CHARSET), (type + pk.toString()).getBytes(CHARSET));
        } else {
            if (pk instanceof Long) {
                Long max = Long.parseLong(st.substring(1));
                max = Math.max(max, (Long) pk);
                sj.hset("maxKey".getBytes(CHARSET), tableName.getBytes(CHARSET), (type + max.toString()).getBytes(CHARSET));
            } else if (pk instanceof Integer) {
                Integer max = Integer.parseInt(st.substring(1));
                max = Math.max(max, (Integer) pk);
                sj.hset("maxKey".getBytes(CHARSET), tableName.getBytes(CHARSET), (type + max.toString()).getBytes(CHARSET));
            } else if (pk instanceof Short) {
                Short max = Short.parseShort(st.substring(1));
                max = (short) Math.max(max, (Short) pk);
                sj.hset("maxKey".getBytes(CHARSET), tableName.getBytes(CHARSET), (type + max.toString()).getBytes(CHARSET));
            }
        }
    }


    public Number getMaxKey(String tableName) {
        try (ShardedJedis sj = jedisPool.getSharedJedis()) {

            byte[] maxKey = sj.hget("maxKey".getBytes(CHARSET), tableName.getBytes(CHARSET));

            if (maxKey == null) {
                return new Number() {

                    private static final long serialVersionUID = -7381941392606803986L;

                    @Override
                    public long longValue() {
                        return 0L;
                    }

                    @Override
                    public int intValue() {
                        return 0;
                    }

                    @Override
                    public float floatValue() {
                        return 0F;
                    }

                    @Override
                    public double doubleValue() {
                        return 0;
                    }
                };

            }
            String maxStr = new String(maxKey, CHARSET);
            byte type = Byte.parseByte(String.valueOf(maxStr.charAt(0)));
            switch (type) {
                case DBObj.KEY_TYPE_INTEGER:
                    return Integer.parseInt(maxStr.substring(1));
                case DBObj.KEY_TYPE_LONG:
                    return Long.parseLong(maxStr.substring(1));
                case DBObj.KEY_TYPE_SHORT:
                    return Short.parseShort(maxStr.substring(1));
                default:
                    break;
            }
            return 0;
        } catch (Exception e) {
            log.warn("", e);
            return null;
        }
    }


    @Override
    public boolean delete(String tableName, Map<Integer, String> relKeyPreNames, DBObj dbObj) {
        try (ShardedJedis sj = jedisPool.getSharedJedis()) {
            sj.hdel(tableName.getBytes(CHARSET), dbObj.getPrimaryKey().toString().getBytes(CHARSET));
            Object[] relationKeys = dbObj.getRelationKeys();
            if (relationKeys != null && relationKeys.length > 0) {
                for (int i = 0; i < relationKeys.length; i++) {
                    String relKeyPreName = relKeyPreNames.get(i);
                    if (relKeyPreName == null)
                        continue;
                    if (relationKeys[i] == null) {
                        log.error("table " + tableName + "'s orm's relKey " + relKeyPreName + "'s orm is null, orm pk:" + dbObj.getPrimaryKey());
                        continue;
                    }
                    sj.hdel((relKeyPreName + relationKeys[i].toString()).getBytes(CHARSET), dbObj.getPrimaryKey().toString().getBytes(CHARSET));
                }
            }
        } catch (Exception e) {
            log.warn("", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(String tableName, Map<Integer, String> relKeyPreNames, Collection<DBObj> deletes) {

        try (ShardedJedis sj = jedisPool.getSharedJedis()) {
            for (DBObj dbObj : deletes) {
                sj.hdel(tableName.getBytes(CHARSET), dbObj.getPrimaryKey().toString().getBytes(CHARSET));
                Object[] relationKeys = dbObj.getRelationKeys();
                if (relationKeys != null && relationKeys.length > 0) {
                    for (int i = 0; i < relationKeys.length; i++) {
                        String relKeyPreName = relKeyPreNames.get(i);
                        if (relKeyPreName == null)
                            continue;
                        if (relationKeys[i] == null) {
                            log.error("table " + tableName + "'s orm's relKey " + relKeyPreName + "'s orm is null, orm pk:" + dbObj.getPrimaryKey());
                            continue;
                        }
                        sj.hdel((relKeyPreName + relationKeys[i].toString()).getBytes(CHARSET), dbObj.getPrimaryKey().toString().getBytes(CHARSET));
                    }
                }
            }
        } catch (Exception e) {
            log.warn("", e);
        }
        return true;
    }
    


    @Override
    public boolean changeRelKey(String tableName, Map<Integer, String> relKeyPreNames, Integer relKeyIx, Object oldRelKey, DBObj dbObj) {
        try (ShardedJedis sj = jedisPool.getSharedJedis()) {
            if (oldRelKey != null) {
                sj.hdel((relKeyPreNames.get(relKeyIx) + oldRelKey.toString()).getBytes(CHARSET), dbObj.getPrimaryKey().toString().getBytes(CHARSET));
            }


            Map<byte[], byte[]> dataMap = new HashMap<>();
            Map<Object, Map<byte[], byte[]>> relDataMap = new HashMap<>();
            byte[] dump = new byte[0];

            Object pk = dbObj.getPrimaryKey();
            Object[] relationKeys = dbObj.getRelationKeys();

            if (isNumPK(dbObj))
                dealMaxKey(tableName, sj, dbObj);

            byte[] pkBytes = pk.toString().getBytes(CHARSET);
            DBBuffer dbBuffer = DBBuffer.allocate();
            dataMap.put(pkBytes, dbBuffer.putAndGetData(dbObj));

            if (relationKeys != null && relationKeys.length > 0) {
                for (int i = 0; i < relationKeys.length; i++) {
                    String relKeyPreName = relKeyPreNames.get(i);
                    if (relKeyPreName == null)
                        continue;
                    if (relationKeys[i] == null) {
                        log.error("table " + tableName + "'s orm's relKey " + relKeyPreName + "'s orm is null, orm pk:" + dbObj.getPrimaryKey());
                        continue;
                    }
                    String relKey = relKeyPreName + relationKeys[i].toString();
                    Map<byte[], byte[]> relMap = relDataMap.get(relKey);
                    if (relMap == null)
                        relDataMap.put(relKey, relMap = new HashMap<>());
                    relMap.put(pkBytes, dump);
                }
            }

            if (dataMap.size() > 0)
                sj.hmset(tableName.getBytes(CHARSET), dataMap);
            Set<Entry<Object, Map<byte[], byte[]>>> relSaveSet = relDataMap.entrySet();
            for (Entry<Object, Map<byte[], byte[]>> rel : relSaveSet) {
                Object key = rel.getKey();
                Map<byte[], byte[]> data = rel.getValue();
                if (data.size() > 0)
                    sj.hmset(key.toString().getBytes(CHARSET), data);
            }
        } catch (Exception e) {
            log.warn("", e);
            return false;
        }
        return true;
    }


    @Override
    public boolean save(String tableName, Map<Integer, String> relKeyPreNames, Collection<DBObj> updates) {
        try (ShardedJedis sj = jedisPool.getSharedJedis()) {

//			Set<Entry<Object, DBObj>> entrySet = cacheUpdate.entrySet();
            Map<byte[], byte[]> dataMap = new HashMap<>();
            Map<Object, Map<byte[], byte[]>> relDataMap = new HashMap<>();
            byte[] dump = new byte[0];
            DBObj maxKeyDBObj = null;
            for (DBObj dbObj : updates) {
                Object pk = dbObj.getPrimaryKey();
                maxKeyDBObj = getMaxPKDBObj(maxKeyDBObj, dbObj);
                Object[] relationKeys = dbObj.getRelationKeys();
                byte[] pkBytes = pk.toString().getBytes(CHARSET);
                DBBuffer dbBuffer = DBBuffer.allocate();
                dataMap.put(pkBytes, dbBuffer.putAndGetData(dbObj));

                if (relationKeys != null && relationKeys.length > 0) {
                    for (int i = 0; i < relationKeys.length; i++) {
                        String relKeyPreName = relKeyPreNames.get(i);
                        if (relKeyPreName == null)
                            continue;
                        if (relationKeys[i] == null) {
                            log.error("table " + tableName + "'s orm's relKey " + relKeyPreName + "'s orm is null, orm pk:" + dbObj.getPrimaryKey());
                            continue;
                        }
                        String relKey = relKeyPreName + relationKeys[i].toString();
                        Map<byte[], byte[]> relMap = relDataMap.get(relKey);
                        if (relMap == null)
                            relDataMap.put(relKey, relMap = new HashMap<>());
                        relMap.put(pkBytes, dump);
                    }
                }
            }
            if (maxKeyDBObj != null)
                dealMaxKey(tableName, sj, maxKeyDBObj);

            if (dataMap.size() > 0)
                sj.hmset(tableName.getBytes(CHARSET), dataMap);
            Set<Entry<Object, Map<byte[], byte[]>>> relSaveSet = relDataMap.entrySet();
            for (Entry<Object, Map<byte[], byte[]>> rel : relSaveSet) {
                Object key = rel.getKey();
                Map<byte[], byte[]> data = rel.getValue();
                if (data.size() > 0)
                    sj.hmset(key.toString().getBytes(CHARSET), data);
            }
        } catch (Exception e) {
            log.warn("", e);
            return false;
        }
        return true;
    }


    public boolean isNumPK(DBObj dbObj) {
        return dbObj != null && dbObj.getPrimaryKey() instanceof Number;
    }


    public DBObj getMaxPKDBObj(DBObj maxKeyDBObj, DBObj dbObj) {
        Object pk = dbObj.getPrimaryKey();
        if (isNumPK(dbObj)) {
            if (maxKeyDBObj == null)
                maxKeyDBObj = dbObj;
            else {
                Object pkObj = maxKeyDBObj.getPrimaryKey();
                if (pkObj instanceof Long) {
                    Long max = (Long) pkObj;
                    Long now = (Long) pk;
                    if (now > max)
                        maxKeyDBObj = dbObj;
                } else if (pkObj instanceof Integer) {
                    Integer max = (Integer) pkObj;
                    Integer now = (Integer) pk;
                    if (now > max)
                        maxKeyDBObj = dbObj;
                } else if (pkObj instanceof Short) {
                    Short max = (Short) pkObj;
                    Short now = (Short) pk;
                    if (now > max)
                        maxKeyDBObj = dbObj;
                }
            }
        }
        return maxKeyDBObj;
    }

    @Override
    public boolean save(String tableName, Map<Integer, String> relKeyPreNames, DBObj dbObj) {
        try (ShardedJedis sj = jedisPool.getSharedJedis()) {
            Map<byte[], byte[]> dataMap = new HashMap<>();
            Map<Object, Map<byte[], byte[]>> relDataMap = new HashMap<>();
            byte[] dump = new byte[0];

            Object pk = dbObj.getPrimaryKey();
            Object[] relationKeys = dbObj.getRelationKeys();

            if (isNumPK(dbObj))
                dealMaxKey(tableName, sj, dbObj);

            byte[] pkBytes = pk.toString().getBytes(CHARSET);
            DBBuffer buffer = DBBuffer.allocate();
            dataMap.put(pkBytes, buffer.putAndGetData(dbObj));

            if (relationKeys != null && relationKeys.length > 0) {
                for (int i = 0; i < relationKeys.length; i++) {
                    String relKeyPreName = relKeyPreNames.get(i);
                    if (relKeyPreName == null)
                        continue;
                    if (relationKeys[i] == null) {
                        log.error("table " + tableName + "'s orm's relKey " + relKeyPreName + "'s orm is null, orm pk:" + dbObj.getPrimaryKey());
                        continue;
                    }
                    String relKey = relKeyPreName + relationKeys[i].toString();
                    Map<byte[], byte[]> relMap = relDataMap.get(relKey);
                    if (relMap == null)
                        relDataMap.put(relKey, relMap = new HashMap<>());
                    relMap.put(pkBytes, dump);
                }
            }

            if (dataMap.size() > 0)
                sj.hmset(tableName.getBytes(CHARSET), dataMap);
            Set<Entry<Object, Map<byte[], byte[]>>> relSaveSet = relDataMap.entrySet();
            for (Entry<Object, Map<byte[], byte[]>> rel : relSaveSet) {
                Object key = rel.getKey();
                Map<byte[], byte[]> data = rel.getValue();
                if (data.size() > 0)
                    sj.hmset(key.toString().getBytes(CHARSET), data);
            }
        } catch (Exception e) {
            log.warn("", e);
            return false;
        }
        return true;
    }


    @Override
    public List<DBObj> getByRelationKey(String tableName, String relationKeyName, Object relationKey, Constructor<? extends DBObj> poConstructor) {
        List<DBObj> ret = new ArrayList<>();
        String rk = relationKeyName + relationKey;

        try (ShardedJedis sj = jedisPool.getSharedJedis()) {
            Set<byte[]> pks = sj.hkeys(rk.getBytes(CHARSET));
            byte[][] pkAry = new byte[pks.size()][];
            pkAry = pks.toArray(pkAry);
            if (pkAry.length > 0) {
                List<byte[]> datas = sj.hmget(tableName.getBytes(CHARSET), pkAry);
                int len = datas.size();
                for (int i = 0; i < len; i++) {
                    byte[] data = datas.get(i);
                    try {
                        if (data != null) {
                            DBObj dbObj = poConstructor.newInstance(data);
                            ret.add(dbObj);
                        } else {
                            log.error("rk {}'s orm lost, pk:{}", rk, pkAry[i]);
                        }
                    } catch (Exception e) {
                        log.warn("{}", poConstructor, e);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("", e);
            return null;
        }
        return ret;
    }

    @Override
    public DBObj getByPrimaryKey(String tableName, Object primaryKey, Constructor<? extends DBObj> poConstructor) {
        DBObj ret = null;
        try (ShardedJedis sj = jedisPool.getSharedJedis()) {
            byte[] bytes = sj.hget(tableName.getBytes(CHARSET), primaryKey.toString().getBytes(CHARSET));
            if (bytes != null) {
                ret = poConstructor.newInstance(bytes);
            }
        } catch (Exception e) {
            log.warn("", e);
            return null;
        }
        return ret;
    }
    
    @Override
	public List<DBObj> getByPrimaryKeys(String tableName, List<Object> primaryKey,Constructor<? extends DBObj> poConstructor) {
    	List<DBObj> ret = new ArrayList<>(primaryKey.size());
    	try (ShardedJedis sj = jedisPool.getSharedJedis()) {
    		byte[][] pks = new byte[primaryKey.size()][];
    		for (int i = 0; i < pks.length; i++) {
    			pks[i] = primaryKey.get(i).toString().getBytes(CHARSET);
    		}
    		List<byte[]> hmget = sj.hmget(tableName.getBytes(CHARSET), pks);
    		if(hmget!=null&&hmget.size()>0){
    			for (byte[] bs : hmget) {
    				if(bs == null)
    					continue;
    				ret.add(poConstructor.newInstance(bs));
    			}
    		}
    	} catch (Exception e) {
    		log.warn("", e);
    		return null;
    	}
    	return ret;
	}


    @Override
    public List<DBObj> getAll(String tableName, Constructor<? extends DBObj> poConstructor) {
        List<DBObj> ret = new ArrayList<>();
        try (ShardedJedis sj = jedisPool.getSharedJedis()) {
            Collection<byte[]> datas = sj.hvals(tableName.getBytes(CHARSET));
            for (byte[] data : datas) {
                if (data == null)
                    continue;
                DBObj dbObj = poConstructor.newInstance(data);
                ret.add(dbObj);
            }
        } catch (Exception e) {
            log.warn("", e);
            return null;
        }
        return ret;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }


	


}
