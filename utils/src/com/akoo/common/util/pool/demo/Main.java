package com.akoo.common.util.pool.demo;

import com.akoo.common.util.pool.Pool;
import com.akoo.common.util.pool.PoolFactory;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Pool<Connection> pool =
                PoolFactory.newBoundedBlockingPool(
                        10,
                        new JDBCConnectionFactory("", "", "", ""),
                        new JDBCConnectionValidator());
        //do whatever you like
    }
}
