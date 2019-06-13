package com.akoo.tool;

import java.io.File;

/**
 * aly @ 16-11-12.
 */
public class FileTool {
    private static void cleanDir(File dirFile) {
        boolean exists = dirFile.exists();
        if (exists) {
            if (dirFile.isDirectory()) {
                File[] files = dirFile.listFiles();
                if (null != files) {
                    for (File file : files) {
                        if (file.isFile()) {
                            file.delete();
                        } else
                            cleanDir(file);
                    }
                    dirFile.delete();
                }
            }
        }
    }

    public static void cleanDir(String csvDestDirPath) {
        File file = new File(csvDestDirPath);
        boolean exists = file.exists();
        if (exists) {
            if (file.isFile()) {
                file.delete();
            }
            cleanDir(file);
        }
        file.mkdirs();
        System.out.println("clean dir[" + csvDestDirPath + "] suc");
    }
}
