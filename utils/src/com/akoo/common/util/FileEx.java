package com.akoo.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class FileEx {
    private static Logger log = LoggerFactory.getLogger(FileEx.class);

    public static String getAppRoot() {
        return System.getProperty("user.dir");
    }

    public static File makeFile(String path, String fileName) {
        File file = new File(path, fileName);
        // 文件如果存在则直接读取
        if (file.isDirectory()) {
            delFile(file);
        }
        if (!file.exists()) {
            try {
                boolean suc = file.createNewFile();
                if (!suc) {
                    log.error(file.getAbsolutePath() + " create error");
                }
            } catch (IOException e) {
                log.error("", e);
            }
        }
        return file;
    }

    public static void makeDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        } else {
            if (dir.isFile()) {
                delFile(dir);
            }
            dir.mkdirs();

        }
    }

    public static void delFile(File myDelFile) {
        try {
            boolean suc = myDelFile.delete();
            if (!suc) {
                log.error(myDelFile.getAbsolutePath() + "delete fiald");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
