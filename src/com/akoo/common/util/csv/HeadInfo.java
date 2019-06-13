package com.akoo.common.util.csv;

/**
 * @author Aly on  2016-07-18.
 *         CSV 头信息
 */
class HeadInfo {
    boolean[] readCols;
    String[] names;
    String[] defines;

    HeadInfo(int len) {
        readCols = new boolean[len];
        names = new String[len];
        defines = new String[len];
    }

}
