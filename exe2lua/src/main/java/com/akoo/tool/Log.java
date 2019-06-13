package com.akoo.tool;

/**
 * aly @ 16-11-12.
 */
public class Log {
    public static void outError(String fileName, int startRow1, Exception e) {
        System.out.println(String.format("文件 %s 行%d  执行报错", fileName, startRow1));
        if (null != e) e.printStackTrace();
    }
}
