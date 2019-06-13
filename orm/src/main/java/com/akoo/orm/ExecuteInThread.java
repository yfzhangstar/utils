package com.akoo.orm;

import java.util.concurrent.ExecutorService;

/**
 * @author Aly on  2016-08-10.
 */
public interface ExecuteInThread extends Runnable {

    default void execute() {
        try {
            if (getThreadPool() != null) {
                getThreadPool().execute(this);
            } else
                run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ExecutorService getThreadPool();
}
