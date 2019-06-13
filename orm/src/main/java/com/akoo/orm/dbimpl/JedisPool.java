package com.akoo.orm.dbimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

public class JedisPool {
    private static Logger log = LoggerFactory.getLogger(JedisPool.class);
    private ShardedJedisPool jedisPool = null;


    public JedisPool(String host, int port) {
        log.warn("redis ip:" + host + "  port: " + port);
        _Init(host, port, null, null);
    }

    public JedisPool(String host, int port, String password) {
        log.warn("redis ip:" + host + "  port: " + port + " password:" + password);
        if (password != null && password.trim().length() == 0)
            password = null;
        _Init(host, port, password, null);
    }

    public JedisPool(String host, int port, String pwd, JedisPoolConfig config) {
        _Init(host, port, pwd, config);
    }

    private void _Init(String host, int port, String pwd, JedisPoolConfig config) {
        List<JedisShardInfo> jedisShardInfos = new ArrayList<>();
        JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port);//"122.112.4.157"
        jedisShardInfo.setConnectionTimeout(10000);
        jedisShardInfo.setPassword(pwd);
        jedisShardInfos.add(jedisShardInfo);
        jedisPool = new ShardedJedisPool(config == null ? createDefJedisConfig() : config, jedisShardInfos);
    }

    private JedisPoolConfig createDefJedisConfig() {
        JedisPoolConfig jedisConfig = new JedisPoolConfig();
        jedisConfig.setMaxTotal(500);
        jedisConfig.setMaxIdle(500);
        jedisConfig.setMaxWaitMillis(5000);
        jedisConfig.setTestOnBorrow(true);
        return jedisConfig;
    }

    public ShardedJedis getSharedJedis() {
        return jedisPool.getResource();
    }
}

