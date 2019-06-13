package com.akoo.common.util.csv;

/**
 * @author Aly on  2016-10-25.
 */
class TypeUtil {
    /**
     * 返回段字符串 {} 中的内容
     * 不支持嵌
     */
    static String getContentFromBrace(String input) {
        String val;
        int star = input.indexOf('{');
        int end = input.lastIndexOf('}');
        if (star >= 0) {
            if (end >= 0) val = input.substring(star + 1, end);
            else val = input.substring(star + 1);
        } else {
            if (end >= 0) val = input.substring(1, end);
            else val = input;
        }
        return val;
    }

    static Object arrayStrToTgtArray(String input, Class<?> tgtClz) {
        String[] values = input.split(",");
        if (tgtClz.equals(String[].class)) {
            if (input.length() == 0)
                return new String[0];
            return values;
        } else if (tgtClz.equals(int[].class)) {
            if (input.length() == 0)
                return new int[0];
            int[] ret = new int[values.length];
            for (int i = 0; i < ret.length; i++) {
                try {
                    ret[i] = Integer.parseInt(values[i]);
                } catch (NumberFormatException e) {
                    ret[i] = 0;
                }
            }
            return ret;
        } else if (tgtClz.equals(long[].class)) {
            if (input.length() == 0)
                return new long[0];
            long[] ret = new long[values.length];
            for (int i = 0; i < ret.length; i++) {
                try {
                    ret[i] = Long.parseLong(values[i]);
                } catch (NumberFormatException e) {
                    ret[i] = 0L;
                }
            }
            return ret;
        } else if (tgtClz.equals(boolean[].class)) {
            if (input.length() == 0)
                return new boolean[0];
            boolean[] ret = new boolean[values.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = Boolean.valueOf(values[i]) || "1".equals(values[i]);
            }
            return ret;
        }
        return null;
    }

    static Object strToTgtType(String value, Class<?> tgtClz) {
        if (tgtClz.equals(String.class)) {
            return value;
        } else if (tgtClz.equals(int.class)) {
            if (value == null || value.trim().length() == 0)
                return 0;
            return Integer.valueOf(value);
        } else if (tgtClz.equals(long.class)) {
            if (value == null || value.trim().length() == 0)
                return 0L;
            return Long.valueOf(value);
        } else if (tgtClz.equals(boolean.class)) {
            if (value == null || value.trim().length() == 0)
                return Boolean.FALSE;
            return Boolean.valueOf(value) || "1".equals(value);
        }
        return null;
    }
}
