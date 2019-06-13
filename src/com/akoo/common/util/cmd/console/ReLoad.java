package com.akoo.common.util.cmd.console;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.akoo.common.util.FileEx;
import com.akoo.common.util.clazz.ClassInfo;

/**
 * @author Aly on  2016-07-20.
 *         !important 匿名类 类似$1 的那种， 重新编译Class后可能出现 名字一样 而类型不一样的
 *         然而这样是无法进行替换的
 */
public class ReLoad extends AConsoleCmd {
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReLoad.class);

    private static Instrumentation ins;
    private boolean canUse = false;

    public ReLoad() {
        try {
            Class<?> agentClass = Class.forName("com.akoo.agent.AgentLib");
            Method getIns = agentClass.getDeclaredMethod("getIns");
            ins = (Instrumentation) getIns.invoke(agentClass);
            if (null != ins) {
                canUse = true;
            }
            log.debug("Instrumentation  load Finish  value is {}  isRedefineClassesSupported :[{}]", ins, null != ins && ins.isRedefineClassesSupported());
        } catch (ClassNotFoundException e) {
            log.warn("-->AgentLib not load Console CMD [{}] is invalidate", getName());
        } catch (NoSuchMethodException e) {
            log.warn("-->AgentLib Method [getIns] not found");
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.warn("-->AgentLib Method [getIns] access error");
        }

    }

    @Override
    public boolean canUse() {
        return canUse;
    }

    @Override
    public Object getName() {
        return "reload";
    }

    @Override
    public Object exe(String cmd, String[] param) throws Exception {
        if (param.length < 1) {
            log.warn("{}  参数数量错误~ - ", getName());
            return null;
        }
        String fileName = param[1];

        List<byte[]> allFiles = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) {
            log.warn("文件不存在:" + file.getAbsolutePath());
        }
        if (fileName.endsWith(".zip") || fileName.endsWith(".jar")) {
            // zipFile
            ZipFile zipFile = new ZipFile(fileName);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.getName().endsWith(".class")) {
                    allFiles.add(ClassInfo.readClass(zipFile.getInputStream(zipEntry), true));
                }
            }
        } else if (fileName.endsWith(".class")) {
            byte[] bytes = ClassInfo.readClass(new FileInputStream(file), true);
            allFiles.add(bytes);
        } else {
            log.warn(" 不支持的文件类型");
            return null;
        }

        if (allFiles.size() == 0) {
            log.warn("nothing to load for class" + fileName);
            return null;
        }
        Map<String, byte[]> newClazz = new HashMap<>();
        Map<Class<?>, byte[]> oldClazz = new HashMap<>();
        for (byte[] bytes : allFiles) {
            ClassInfo reader = new ClassInfo(bytes);
            String className = reader.getClassName().replace('/', '.');
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException ignored) {
            }
            if (null == clazz) {
                // 这个是新增的需要单独处理
                newClazz.put(className, bytes);
                log.warn(" new clazz: {}", className);
            } else {
                // 这个是老的 直接直接替换
                oldClazz.put(clazz, bytes);
                log.warn(" old clazz: {}", className);
            }
        }
        // 优先考虑 新增
        if (newClazz.size() > 0) {
            FileEx.makeDir("bin");
            String newName = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()) + ".zip";
            File newFile = FileEx.makeFile("bin", newName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(newFile)));
            for (Map.Entry<String, byte[]> en : newClazz.entrySet()) {
                String key = en.getKey();
                log.warn("addClass:" + en.getKey());
                out.putNextEntry(new ZipEntry(key.substring(key.lastIndexOf('.')) + ".class"));
                out.write(en.getValue());
            }

            out.finish();
            out.close();
            URLClassLoader classLoader = ((URLClassLoader) this.getClass().getClassLoader());
            URL url = newFile.toURI().toURL();
            LoadJar.AddUrl(classLoader, url);
            log.warn("addNewFile to ClassLoader :" + url);
        }
        if (oldClazz.size() > 0) {
            List<ClassDefinition> classDefinitions = new ArrayList<>();
            for (Map.Entry<Class<?>, byte[]> entry : oldClazz.entrySet()) {
                ClassDefinition definition = new ClassDefinition(entry.getKey(), entry.getValue());
                classDefinitions.add(definition);
                log.warn("redefine Old class:" + entry.getKey());
            }
            ins.redefineClasses(classDefinitions.toArray(new ClassDefinition[classDefinitions.size()]));
        }
        return null;
    }
}
