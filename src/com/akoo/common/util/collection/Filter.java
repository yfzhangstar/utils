package com.akoo.common.util.collection;

public interface Filter<T> {
    /**
     * @return 要过滤掉这个就返回true
     */
    boolean isFilter(T t);
}
