package com.akoo.common.util.cmd.console;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;

import com.akoo.common.util.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExeClass extends AConsoleCmd {
    private static final Logger log = LoggerFactory.getLogger(ExeClass.class);

    @Override
    public Object getName() {
        return "exeClass";
    }

    @Override
    public Object exe(String cmd, String[] param) throws Exception {
        if (param.length < 3) {
            log.warn("exeClass params error");
        } else {
            String fname = param[1];
            String className = param[2];
            final File file = new File("bin", fname);
            URLClassLoader loader = AccessController.doPrivileged((PrivilegedAction<URLClassLoader>) () -> {
                try {
                    return new URLClassLoader(new URL[]{file.toURI().toURL()},
                            Thread.currentThread().getContextClassLoader());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            });
            try {
                Class<?> clazz = loader.loadClass(className);
                Method m = clazz.getMethod("execute");
                Object in = clazz.newInstance();
                SpringBeanFactory.autowireBean(in);
                m.invoke(in);
            } catch (Exception e) {
                log.error("exeClass error  parm[{}],filePath:[{}]", Arrays.toString(param), file.getAbsolutePath(), e);
            }
        }
        return null;
    }

}
