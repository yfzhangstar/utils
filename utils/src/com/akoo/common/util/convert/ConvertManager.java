package com.akoo.common.util.convert;

import com.akoo.common.util.GameUtil;
import com.akoo.common.util.clazz.ClassFileUtils;
import org.objectweb.asm.ClassReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ConvertManager {

    private static Map<Class, IConvertProcess> processMap = GameUtil.createSimpleMap();

    public static void load(String pkg){
        List<String> allPkgClazz = new ArrayList<>();
        ClassFileUtils.getClasses(pkg, true, allPkgClazz::add);

        List<DTObjInfo> lst = GameUtil.createList();

        for (String clazzName : allPkgClazz)
            try {
                ClassReader reader = new ClassReader(clazzName);
                reader.accept(new DTObjChainLoader(new Consumer<DTObjInfo>() {
                    @Override
                    public void accept(DTObjInfo dtoInfo) {
                        lst.add(dtoInfo);
                    }
                }), ClassReader.EXPAND_FRAMES);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        for (DTObjInfo dtObjInfo : lst) {
            ConvertManager.load(dtObjInfo);
        }
    }

    public static void load(DTObjInfo info) {
        ConvertWriter writer = new ConvertWriter();
        try {
            Class<?> dtoClz = ConvertManager.class.getClassLoader().
                    loadClass(info.getName().replace('/', '.'));
            if(!processMap.containsKey(dtoClz)) {
                Class<? extends IConvertProcess> clazz = writer.writeClass(info);
                processMap.put(dtoClz, clazz.newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<Class, IConvertProcess> getProcessMap() {
        return processMap;
    }
}
