package com.akoo.common.util.cmd.console;

//import FileEx;
//import MD5;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.*;
//import java.net.URLClassLoader;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.regex.Pattern;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;
//import java.util.zip.ZipOutputStream;
//
///**
// * <p>For Java 1.4,<br>
// * <pre>java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000</pre>
// * <p>For Java 5,<br>
// * <pre>java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000</pre>
// * tool.jar 必须加到classpath
// *
// * 修改方法逻辑             OK
// * 修改方法              X
// * 添加方法              X
// * 新增类                   OK   --> 解压到其他地方 使用loadJar方式添加到class路径
// * 增加字段              X
// */
//public class LoadBin extends AConsoleCmd {
//    private static final Logger log = LoggerFactory.getLogger(LoadBin.class);
//
//    private static Pattern fileP = Pattern.compile("/");
//    private static String binFileName = "bin.zip";
//
//    private static volatile HotSwapper swapper;
//    private static Map<String, String> clazzMd5CodeCache = new ConcurrentHashMap<>();
//
//    static {
//        try {
//            File binFile = new File(binFileName);
//            if (!binFile.isFile() || !binFile.exists()) {
//                log.error(" bin.zip 读取失败..  可能无法进行");
//            } else {
//                ZipFile zipFile = new ZipFile(binFile);
//                Enumeration<? extends ZipEntry> entries = zipFile.entries();
//                while (entries.hasMoreElements()) {
//                    ZipEntry zipEntry = entries.nextElement();
//
//                    if (!zipEntry.isDirectory()) {
//                        boolean isClazzFile = zipEntry.getName().endsWith(".class");
//                        if (isClazzFile) {
//                            String md5 = new MD5().getMD5ofStr((getByte(zipFile, zipEntry)));
//                            clazzMd5CodeCache.put(zipEntry.getName(), md5);
//                        }
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static byte[] getByte(ZipFile zipFile, ZipEntry zipEntry) throws IOException {
//        InputStream stream = zipFile.getInputStream(zipEntry);
//
//        ByteArrayOutputStream bufferos = new ByteArrayOutputStream();
//
//        int buffSize = 2048;
//        byte[] buffer = new byte[buffSize];
//        int read;
//        while ((read = stream.read(buffer)) > 0) {
//            bufferos.write(buffer, 0, read);
//        }
//        return bufferos.toByteArray();
//    }
//
//    @Override
//    public Object exe(String cmd, String[] param) {
//        int port;
//        try {
//            port = Integer.parseInt(param[1]);
//        } catch (Exception e) {
//            log.error("参数错误 需要填端口");
//            return null;
//        }
//        try {
//            File binFile = new File(binFileName);
//            if (!binFile.isFile() || !binFile.exists()) {
//                log.error("文件不存在:{}", binFile.getAbsolutePath());
//            }
//
//            Map<String, byte[]> hotswapClass = new HashMap<>();
//            Map<String, byte[]> newClazz = new HashMap<>();
//
//            //
//            // 查找Class 变更
//            ZipFile zipFile = new ZipFile(binFile);
//            Enumeration<? extends ZipEntry> entries = zipFile.entries();
//
//            while (entries.hasMoreElements()) {
//                ZipEntry zipEntry = entries.nextElement();
//                String zipName = zipEntry.getName();
//                if (!zipEntry.isDirectory() && zipName.endsWith(".class")) {
//                    String clazzMd5 = clazzMd5CodeCache.get(zipName);
//                    if (null != clazzMd5) {
//                        byte[] clazzByte = getByte(zipFile, zipEntry);
//                        if (!clazzMd5.equals(new MD5().getMD5ofStr(clazzByte))) {
//                            String clazzName = fileP.matcher(zipName.substring(0, zipName.length() - 6)).replaceAll(".");
//                            hotswapClass.put(clazzName, clazzByte);
//                        }
//                    } else {
//                        // 新增Class bin.zip 依然在class路径下  可以不管
//                        newClazz.put(zipName, getByte(zipFile, zipEntry));
//
//                    }
//                }
//            }
//
//            log.warn("新增的Class为:{}\n ,需要替换的Clazz为:{}", join(newClazz.keySet())
//                    , join(hotswapClass.keySet()));
//
//
//            if (newClazz.size() > 0) {
//                FileEx.makeDir("bin");
//                String newName = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()) + ".zip";
//                File newFile = FileEx.makeFile("bin", newName);
//                ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(newFile)));
//                for (Map.Entry<String, byte[]> en : newClazz.entrySet()) {
//                    out.putNextEntry(new ZipEntry(en.getKey()));
//                    out.write(en.getValue());
//                }
//
//                out.finish();
//                out.close();
//                URLClassLoader classLoader = ((URLClassLoader) this.getClass().getClassLoader());
//                LoadJar.AddUrl(classLoader, newFile.toURI().toURL());
//            }
//
//            // 开始重
//            if (null == swapper) {
//                swapper = new HotSwapper(port);
//            }
//            swapper.reload(hotswapClass);
//            log.info(" 重新加载成功！");
//        } catch (Exception e) {
//            log.error("", e);
//        }
//        return null;
//    }
//
//    private String join(Set<String> strings) {
//        StringBuilder sb = new StringBuilder();
//        Iterator<String> iterator = strings.iterator();
//        if (iterator.hasNext()) {
//            sb.append(iterator.next());
//            while (iterator.hasNext()) {
//                sb.append(",\n");
//                sb.append(iterator.next());
//            }
//        }
//        return sb.toString();
//    }
//
//    @Override
//    public Object getName() {
//        return "loadbin";
//    }
//
//}
