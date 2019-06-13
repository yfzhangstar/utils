package com.akoo.common.util.clazz;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import com.akoo.common.util.StringUtil;

/**
 * Class类工具 查找文件
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ClassFileUtils {
    public static final String FILE_URL_PREFIX = "file:";
    public static final String JAR_URL_SEPARATOR = "!/";

    /**
     * 从包package中获取所有的Class
     *
     * @param pkgName   包名
     * @param recursive 是否递归
     * @return a {@link java.util.Set} object.
     */
    public static void getClasses(String pkgName, boolean recursive, Consumer<String> clazzNameConsumer) {
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs = null;
        try {
            String replace = pkgName.replace('.', '/');
            dirs = Thread.currentThread().getContextClassLoader().getResources(replace);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (dirs == null) {
            return;
        }
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            // 得到协议的名称
            String protocol = url.getProtocol();
            switch (protocol) {
                case "file": {
                    // 获取包的物理路径
                    try {
                        String filePath;
                        filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                        // 以文件的方式扫描整个包下的文件 并添加到集合中
                        File dir = new File(filePath);
                        if (dir.exists()) {
                            if (dir.isDirectory()) {
                                findInPkgByDir(pkgName, dir, recursive, clazzNameConsumer);
                            }
                            if (dir.isFile()) {
                                readJarFile(recursive, pkgName, url, clazzNameConsumer);
                            }

                        }
                    } catch (UnsupportedEncodingException ignored) {
                    }
                }
                break;
                case "jar":
                    readJarFile(recursive, pkgName, url, clazzNameConsumer);
                    break;
                default: {
                }
            }
        }
    }

    public static URI toURI(String location) throws URISyntaxException {
        String lo = location.replaceAll(" ", "%20");
        return new URI(lo);
    }

    private static JarFile getJarFile(String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith(FILE_URL_PREFIX)) try {
            return new JarFile(toURI(jarFileUrl).getSchemeSpecificPart());
        } catch (URISyntaxException ex) {
            return new JarFile(jarFileUrl.substring(FILE_URL_PREFIX.length()));
        }
        else {
            return new JarFile(jarFileUrl);
        }
    }

    private static void readJarFile(boolean recursive, String packageName, URL url, Consumer<String> clazzNameConsumer) {
        JarFile jarFile;
        String jarFileUrl;
        String rootEntryPath;
        JarEntry jarEntry;
        boolean closeJarFile;
        try {
            URLConnection urlConnection = url.openConnection();
            if (urlConnection instanceof JarURLConnection) {
                JarURLConnection jarCon = (JarURLConnection) urlConnection;

                jarCon.setUseCaches(jarCon.getClass().getSimpleName().startsWith("JNLP"));

                jarFile = jarCon.getJarFile();
//                jarFileUrl = jarCon.getJarFileURL().toExternalForm();
                jarEntry = jarCon.getJarEntry();
                rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
                closeJarFile = !jarCon.getUseCaches();
            } else {
                String urlFile = url.getFile();
                try {
                    int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
                    if (separatorIndex != -1) {
                        jarFileUrl = urlFile.substring(0, separatorIndex);
                        rootEntryPath = urlFile.substring(separatorIndex + JAR_URL_SEPARATOR.length());
                        jarFile = getJarFile(jarFileUrl);
                    } else {
                        jarFile = new JarFile(urlFile);
//                        jarFileUrl = urlFile;
                        rootEntryPath = "";
                    }
                    closeJarFile = true;
                } catch (ZipException ignored) {
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                // Root entry path must end with slash to allow for proper matching.
                // The Sun JRE does not return a slash here, but BEA JRockit does.
                rootEntryPath = rootEntryPath + "/";
            }
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
//                    String relativePath = entryPath.substring(rootEntryPath.length());
                    if (entry.isDirectory()) {
                        continue;
                    }
                    String name = entry.getName();
                    int idx = name.lastIndexOf('/');
                    // 如果可以迭代下去 并且是一个包
                    if (recursive || ((idx + 1) == rootEntryPath.length())) {
                        // 如果是一个.class文件 而且不是目录
                        if (isClazzFile(name)) {
                            // 去掉后面的".class" 获取真正的类名
                            String className = name.substring(0, name.length() - 6).replace('/', '.');
                            clazzNameConsumer.accept(className);
                        }
                    }
                }
            }
        } finally {
            if (closeJarFile) try {
                jarFile.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static boolean isClazzFile(String name) {
        return name.endsWith(".class");
    }

    /**
     * 以文件的形式来获取包下的所有Class
     */
    public static void findInPkgByDir(String packageName,
                                      String dir, final boolean recursive, Consumer<String> clazzNameConsumer) {
        findInPkgByDir(packageName, new File(dir), recursive, clazzNameConsumer);
    }

    /**
     * 以文件的形式来获取包下的所有Class
     */
    public static void findInPkgByDir(String packageName,
                                      File dir, final boolean recursive, Consumer<String> clazzNameConsumer) {

        // 如果不存在或者 也不是目录就直接返回
        if (null == dir || !dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirfiles;
        if (recursive) {
            dirfiles = dir.listFiles(file -> file.isDirectory() || isClazzFile(file.getName()));
        } else {
            dirfiles = dir.listFiles(file -> isClazzFile(file.getName()));
        }
        if (null == dirfiles) return;
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            String fileName = file.getName();
            if (file.isDirectory()) {
                findInPkgByDir(packageName + "." + fileName, file, recursive, clazzNameConsumer);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String clazzName = packageName + '.' + fileName.substring(0, fileName.length() - 6);
                clazzNameConsumer.accept(clazzName);
            }
        }
    }

    /**
     * <p>
     * Description：给一个接口，返回这个接口同一个包下的所有实现类
     * </p>
     */
    public static List<Class<?>> getAllClassByInterface(Class<?> c, String searchPkg) {
        // 如果不是一个接口，则不做处理
        if (!c.isInterface() || StringUtil.isEmpty(searchPkg)) {
            return Collections.emptyList();
        }
        List<Class<?>> returnClassList = new ArrayList<>(); // 返回结果
        getClasses(searchPkg, true, s -> {
            try {
                Class<?> clazz = c.getClassLoader().loadClass(s);
                if (c.isAssignableFrom(clazz) && !c.equals(clazz)) {
                    returnClassList.add(clazz);
                }
            } catch (ClassNotFoundException ignored) {
            }

        });
        return returnClassList;
    }
}