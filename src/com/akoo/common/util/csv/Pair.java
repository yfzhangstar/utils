package com.akoo.common.util.csv;

/**
 * @author Aly on  2016-07-18.
 */
public class Pair<L, R> {
    private L left;
    private R right;

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    private Pair() {
    }

    public static <L, R>Pair<L, R> valueOf(L left, R right) {
        Pair<L,R> pair = new Pair<>();
        pair.left = left;
        pair.right = right;
        return pair;
    }
}
