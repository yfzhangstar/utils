package com.akoo.tasks;

public interface Mergable<T extends Mergable> {
    void merge(T rightR);
}
