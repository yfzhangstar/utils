package com.akoo.common.util.csv;

import com.akoo.common.util.JSONUtils;
import com.csvreader.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvConfigReader {
    private static Logger log = LoggerFactory.getLogger(CsvConfigReader.class);
    private final Map<Class, Map<String, Method>> methodCache = new HashMap<>();
    private String csvPath;

    private TypeProvider typeProvider;
    private Charset charset;

    private boolean debug = false;

    public CsvConfigReader(String path, Charset charset, TypeProvider typeProvider) {
        this.csvPath = path;
        this.charset = charset;
        this.typeProvider = typeProvider;
    }

    public CsvConfigReader(String path) {
        this.csvPath = path;
        this.charset = Charset.forName("UTF-8");
    }

    private static String upperFirst(String s) {
        int len = s.length();
        if (len <= 0)
            return "";
        return s.substring(0, 1).toUpperCase() +
                s.substring(1, len);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public final <T extends BaseCfg> void loadCfg(final Map<Integer, T> map, Class<T> cfgClass, String csvName) {
        loadCfg(cfgClass, csvName, map::put);
    }

    public final <T extends BaseCfg> void loadCfg(Class<T> cfgClass, String csvName, Iter<T> iter) {
        try {
            Pair<HeadInfo, List<String[]>> cfgLine = getCfgLine(csvName);
            if (null != cfgLine) {
                // 先创建所有的Cfg对象 将数据添加到缓存
                //然后再对其他值进行set
                loadCSVValue(cfgClass, iter, cfgLine.getLeft(), cfgLine.getRight());
            }
        } catch (Throwable e) {
            LogError("加载CSV 出现错误：" + csvName, e);
        }
    }

    private void LogError(String msg, Throwable e) {
        if (debug) {
            log.warn(msg, e);
        } else {
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * 加载CSV的值
     */
    private <T extends BaseCfg> void loadCSVValue(Class<T> cfgClass, Iter<T> iter, HeadInfo headInfo, List<String[]> csvList) throws Throwable {

        for (int j = 0; j < csvList.size(); j++) {
            String[] line = csvList.get(j);
            if (line == null)
                continue;
            T cfg = cfgClass.newInstance();
            for (int i = 0; i < headInfo.readCols.length; i++) {
                if (!headInfo.readCols[i])
                    continue;
                String define = headInfo.defines[i];
                Class clazz = typeProvider.getSetMethodTypeByTypeName(define);
                String value = line[i];
                String name = headInfo.names[i];


                Object param = getValue(cfgClass, clazz, value, define);
                invokeValue(cfgClass, cfg, clazz, name, param);
            }
            if (cfg.getId() == 0) {
                LogError("ID 错误" + cfgClass + " ix:" + (j + 1), null);
            }
            if (null != iter) {
                iter.iter(cfg.getId(), cfg);
            }
        }
    }

    /**
     * 只加载配置的基本数据  ID
     */


    private Pair<HeadInfo, List<String[]>> getCfgLine(String csvName) throws Exception {
        String fileName = csvPath + csvName;
        UnicodeInputStream in = new UnicodeInputStream(new FileInputStream(fileName), charset.name());
        Reader fileRader = new BufferedReader(new InputStreamReader(in, in.getEncoding()), 4096);

        CsvReader reader = new CsvReader(fileRader, ',');
        List<String[]> csvList = new ArrayList<>();
        String[] head = null;
        if (reader.readHeaders())
            head = reader.getHeaders();
        if (head == null)
            return null;
        HeadInfo headInfo = new HeadInfo(head.length);
        for (int i = 0; i < head.length; i++) {
            String headI = head[i];
            String fromBrace = TypeUtil.getContentFromBrace(headI);
            if (!headI.equals(fromBrace)) {
                headInfo.readCols[i] = true;
                String[] define = fromBrace.split(",");
                headInfo.names[i] = define[0].trim();
                String defineType = define[1].trim();

                if (null == typeProvider.getSetMethodTypeByTypeName(defineType)
                        && null == typeProvider.getJsonTypeByTypeName(defineType)) {
                    LogError("csvName:[" + csvName + "] -->不支持的类型:[" + defineType + "]", null);
                }
                headInfo.defines[i] = defineType;
            } else
                headInfo.readCols[i] = false;
        }
        while (reader.readRecord())
            csvList.add(reader.getValues());
        reader.close();
        return Pair.valueOf(headInfo, csvList);
    }

    private Object getValue(Class cfgClass, Class clazz, String value, String define) {
        Object param = null;
        if (define.startsWith("json")) {
            Type jsonType = typeProvider.getJsonTypeByTypeName(define);
            String jsonStr = value.trim();
            try {
                param = JSONUtils.fromJson(jsonStr, jsonType);
            } catch (Exception e) {
                LogError(String.format(" json error:cfgClass:[%s.%s] jsonString:[%s]", cfgClass, define, jsonStr), null);
                param = null;
            }
        } else {
            if (null != clazz) {
                if (clazz.isArray()) {
                    String content = TypeUtil.getContentFromBrace(value).trim();
                    param = TypeUtil.arrayStrToTgtArray(content, clazz);
                } else
                    param = TypeUtil.strToTgtType(value, clazz);
            }
        }
        return param;
    }

    private <T extends BaseCfg> void invokeValue(Class<T> cfgClass, T cfg, Class clazz, String name, Object param) throws Throwable {
        if (param != null) {
            // read from cache
            Map<String, Method> map = methodCache.computeIfAbsent(cfgClass, c -> new HashMap<>());
            Method m = map.get(name);
            // not find
            if (null == m) {
                String meName = "set" + upperFirst(name);
                m = getMethod(cfgClass, meName, clazz);
                if (null == m) {
                    LogError(cfgClass + " 方法未找到:set" + upperFirst(name) + "(" + clazz + ") , 参数类型 : " + param.getClass(), null);
                } else {
                    m.setAccessible(true);
                    map.put(name, m);
                }
            }
            if (null != m)
                m.invoke(cfg, param);
        }
    }


    private <T> Method getMethod(Class<T> cfgClass, String meName, Class opClass) throws NoSuchMethodException {
        Method m = null;
        Class[] interfaces = cfgClass.getInterfaces();
        Class<?> su = cfgClass;
        int deep = TypeProvider.defaultSetterFindDeep;
        while (Object.class != su) {
            try {
                m = su.getDeclaredMethod(meName, opClass);
                break;
            } catch (Exception ignore) {
                su = su.getSuperclass();
                deep--;
            }
            if (deep < 1) {
                break;
            }
        }
        if (null == m) {
            for (Class itf : interfaces) {
                try {
                    m = cfgClass.getDeclaredMethod(meName, itf);
                    break;
                } catch (NoSuchMethodException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return m;
    }
}
