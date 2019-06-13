package com.akoo;

import com.akoo.check.CheckSheetNameTask;
import com.akoo.common.util.csv.UnicodeInputStream;
import com.akoo.enums.ParseType;
import com.akoo.enums.ProcType;
import com.akoo.tasks.ExcelTask;
import com.akoo.tool.FileTool;

import java.io.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;

/**
 * aly @ 16-11-12.
 */
public class Main {
    public static final AtomicLong countTime = new AtomicLong();
    public static final String luaFileNameFix = "Data";
    public static Map<String, File> lanFile = new HashMap<>();
    // 语言替换
    private static String LAN_FIX;
    private static String excelDirPath;
    private static ParseType parseType;
    private static String[] fileNames;
    private static String curLang = "";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws Exception {
        File file = new File("out.log");
        if (file.exists()) {
            file.delete();
        }
        PrintStream out = System.out;
        FileOutputStream stream = new FileOutputStream(file);
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                out.write(b);
                stream.write(b);
            }
        }));
        run(args);
    }

    private static void run(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("没有指定parseConfig.properties的路径");
            return;
        }
        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                String arg = args[i];
                String prefix = "-lan=";
                if (arg.startsWith(prefix)) {
                    curLang = arg.substring(prefix.length());
                }
            }
        }
        System.out.println("----> 当前 配置语言为:[" + curLang + "]");

        String configPath = args[0];
        loadConfig(configPath);

        //清理文件夹中的数据
        ProcType.forEach(procType -> FileTool.cleanDir(procType.getSavePath()));

        List<File> files = getParseFiles();
        long al = System.currentTimeMillis();

        List<File> othterFile = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) continue;
            // 如果有配置文字表
            if (LAN_FIX.length() > 0) {
                String fn2 = file.getName().substring(0, file.getName().lastIndexOf("."));
                int ix = fn2.indexOf(LAN_FIX);
                if (ix >= 0) {
                    //如果是文件表
                    String fileName = fn2.substring(0, ix);
                    String lan = fn2.substring(ix + LAN_FIX.length());
                    if (curLang.equals(lan.trim())) {
                        System.out.println("__load 文字配置:" + fn2);
                        lanFile.put(fileName, file);
                    }
                } else {
                    othterFile.add(file);
                }
            } else
                othterFile.add(file);

        }

        final ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        saveOtherFile(othterFile, pool);

        while (pool.getActiveThreadCount() != 0) {
            Thread.sleep(100);
        }
        long use = System.currentTimeMillis() - al;
        long threadUse = countTime.get();
        System.out.println("\nDone! " + use + "ms ..thread use:" + threadUse + "   sub:" + (threadUse - use));
        pool.shutdown();
    }


    private static void saveOtherFile(List<File> othterFile, final ForkJoinPool pool) {
        othterFile.sort((o1, o2) -> (int) (o1.length() - o2.length()));
        // 导出其他CSV
        for (File file : othterFile) {
            CheckSheetNameTask.check(file, (book, sheet, fileName) -> pool.execute(new ExcelTask(book, sheet, fileName)));
        }
    }


    private static void loadConfig(String configPath) throws IOException {
        File file = new File(configPath);
        System.out.println(file.getAbsolutePath());
        Properties pro = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            UnicodeInputStream in = new UnicodeInputStream(fileInputStream, "GBK");
            Reader fileRader = new BufferedReader(new InputStreamReader(in, in.getEncoding()), 4096);
            pro.load(fileRader);

            excelDirPath = getProperty(pro, "xlsxSourcePath");
            ProcType.LUA.setSavePath(getProperty(pro, "exportLuaPath"));
            ProcType.CSV.setSavePath(getProperty(pro, "exportCsvPath"));
            ProcType.JSON.setSavePath(getProperty(pro, "exportJsonPath"));
            ProcType.LUA.setFileFix(getProperty(pro, "luaFileSuffix"));
            ProcType.setExportType(getProperty(pro, "exportType").toLowerCase());

            parseType = ParseType.getType(getProperty(pro, "parseType"));
            if (null == parseType) parseType = ParseType.ALL;
            if (ParseType.EXCLUDES == parseType) {
                fileNames = getProperty(pro, "excludes").split(",");
            } else if (ParseType.INCLUDES == parseType) {
                fileNames = getProperty(pro, "includes").split(",");
            } else {
                fileNames = null;
            }
            // MergeFile () 文件是 相同的表结构，但是在不同都标签下都有.. 这种格式需要每个位置都是一一对应的，否则在数据读取时有问题
            // spFile 特殊处理文件,
            System.out.println("ParseType:" + parseType + "-->files:" + Arrays.toString(fileNames));
            LAN_FIX = getProperty(pro, "replaceFix");
        }
    }

    private static String getProperty(Properties pro, String parseType) {
        try {
//            return new String(pro.getProperty(parseType).getBytes("iso8859-1"), "UTF-8");
            return pro.getProperty(parseType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static List<File> getParseFiles() {
        String[] path = excelDirPath.split(";");
        List<File> files = new ArrayList<>();
        for (String p : path) {
            if (null == p) continue;
            p = p.trim();
            if (p.length() == 0) continue;

            File file = new File(p);
            System.out.println(p);
            if (!file.exists()) {
                boolean mkdir = file.mkdir();
                if (!mkdir) System.out.println("makedir error:  " + file.getAbsolutePath());
            }
            File[] xlsxes = file.listFiles((dir, name) -> {
                int ind = name.lastIndexOf(".");
                if ((ind == -1) || !name.substring(ind + 1).equals("xlsx") || name.startsWith("~") || name.startsWith(".~"))
                    return false;
                String _na = name.substring(0, ind);
                return parseType.isParseFile(fileNames, _na);
            });
            if (xlsxes != null)
                Collections.addAll(files, xlsxes);
        }

        return files;
    }

    public static String getLanFix() {
        return LAN_FIX;
    }
}
