package com.akoo.orm.demo;

import com.akoo.orm.enhanced.annotation.Save;
import com.akoo.orm.enhanced.asm.Cache;
import com.akoo.orm.enhanced.asm.DBObjChainLoader;
import com.akoo.orm.DBBuffer;
import com.akoo.orm.DBObj;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DemoVO2 extends DemoVO {

    @Save(ix = 1)
    private Map<Integer, DemoVO3> demoVOMap;
    @Save(ix = 2)
    private DemoVO3 demoVO;

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        DBObjChainLoader l = new DBObjChainLoader();
        ClassReader cr = new ClassReader(DemoVO2.class.getName());
        cr.accept(l, 0);
        System.out.println(Cache.voNameDBObjClassMapping);
        Collection<Class<? extends DBObj>> classes = Cache.voNameDBObjClassMapping.values();
        for (Class clazz : classes) {
            System.out.println("class:" + clazz.getName());
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                System.out.println("field:" + field.getName() + " " + field.getType());
            }
            Method[] methods = clazz.getDeclaredMethods();
            for (Method m : methods) {
                System.out.println("method:" + m.getName() + " " + m.toString());
            }


            System.out.println();
        }
        DemoVO2 vo2 = new DemoVO2();
        vo2.setIs(new int[]{32323, 32332223, 323323, 3232332});
        DBObj vo2Obj = vo2.writeToDBObj();

        DBBuffer buffer = DBBuffer.allocate();
        vo2Obj.writeToDBHolder(buffer);


        byte[] bytes = buffer.toBytes();
        Class<? extends DBObj> class1 = vo2Obj.getClass();

        Constructor<? extends DBObj> constructor = class1.getConstructor(byte[].class);
        DBObj newInstance = constructor.newInstance(bytes);


        DemoVO2 vo22 = new DemoVO2();
        vo22.readFromDBObj(newInstance);

        System.out.println(Arrays.toString(vo22.getIs()));
        vo22.getDemoVOMap().forEach((integer, demoVO3) -> {
            System.out.println(integer);
            System.out.println(demoVO3.getId());
        });

    }

    @Override
    protected void init() {
        super.init();
        demoVOMap = new HashMap<>();
        DemoVO3 demoVO3 = new DemoVO3();
        demoVO3.setId(3232323);
        demoVOMap.put(2, demoVO3);
    }

    public Map<Integer, DemoVO3> getDemoVOMap() {
        return demoVOMap;
    }

    public void setDemoVOMap(Map<Integer, DemoVO3> demoVOMap) {
        this.demoVOMap = demoVOMap;
    }

    public DemoVO3 getDemoVO() {
        return demoVO;
    }

    public void setDemoVO(DemoVO3 demoVO) {
        this.demoVO = demoVO;
    }
}
