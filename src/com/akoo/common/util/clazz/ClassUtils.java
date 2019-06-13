package com.akoo.common.util.clazz;

import java.io.IOException;
import java.util.List;

import com.akoo.common.util.GameUtil;
import com.akoo.common.util.SpringBeanFactory;
import org.springframework.core.io.Resource;

/**
 * @author Aly on  2016-07-14.
 * 查找Spring
 */
public class ClassUtils {
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     * 只能获取目录下的class jar包中的不能查找
     *
     * @param packageName The base package
     * @return The classes
     */
    public static List<Class<?>> getClasses(String packageName)  throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = "classpath*:" + packageName.replace('.', '/') + "/**/*.class";
        Resource[] resources = SpringBeanFactory.getResources(path);
        List<Class<?>> classes = GameUtil.createList();
        for (Resource res : resources) {
            classes.addAll(findClasses(res, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private static List<Class<?>> findClasses(Resource res, String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = GameUtil.createList();
        String filePath = res.getURL().getFile().replaceAll("/", ".");
        if (filePath.endsWith(".class") && !filePath.contains("$")) {
            classes.add(Class.forName(filePath.substring(filePath.indexOf(packageName), filePath.length() - 6)));
        }
        return classes;
    }
}
