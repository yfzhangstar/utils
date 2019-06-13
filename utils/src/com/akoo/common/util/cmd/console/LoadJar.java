package com.akoo.common.util.cmd.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * 每次只能加载不同的文件
 */
public class LoadJar extends AConsoleCmd {
    private static final Logger log = LoggerFactory.getLogger(LoadJar.class);

    private static Method addURL = null;

    static {
        if (null == addURL) {
            addURL = initAddMethod();
        }
    }

    public LoadJar() {
    }

    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void AddUrl(ClassLoader classLoader, URL url) throws IllegalAccessException, InvocationTargetException, MalformedURLException {
        addURL.invoke(classLoader, url);
    }

    @Override
    public Object getName() {
        return "loadjar";
    }

    @Override
    public Object exe(String cmd, String[] param) throws Exception {
        if (param.length < 2) {
            log.warn("parm error");
        } else {
            String fileName = param[1];
            File f = new File("jar", fileName);
            if (!f.isFile() || !f.exists()) {
                log.error("jar [{}] is not exists  or is not file path:[{}]", new Object[]{fileName, f.getAbsolutePath()});
            } else {

                try {
                    AddUrl(this.getClass().getClassLoader(), f.toURI().toURL());
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                        | MalformedURLException e) {
                    log.error("load jar error parm:[{}]", new Object[]{Arrays.toString(param)});
                }
            }
        }
        return null;
    }

}
