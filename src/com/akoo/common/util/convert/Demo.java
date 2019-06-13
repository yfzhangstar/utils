package com.akoo.common.util.convert;


import com.akoo.common.util.convert.annotation.AConvert;
import com.akoo.common.util.convert.annotation.AIndex;

import java.util.*;

@AConvert
public class Demo {

    @AIndex(ix = 1)
    private int aaa;

    @AIndex(ix = 2)
    private long bbb;

    @AIndex(ix = 3)
    private DemoParent demo2;

    @AIndex(ix = 4)
    private Map<Integer, DemoParent> demoParentMap;

    @AIndex(ix = 5)
    private String name = "test";

    @AIndex(ix = 6)
    private List<DemoParent> demoParentList;

    @AIndex(ix = 7)
    private Set<DemoParent> demoParentSet;

    public List<DemoParent> getDemoParentList() {
        return demoParentList;
    }

    public void setDemoParentList(List<DemoParent> demoParentList) {
        this.demoParentList = demoParentList;
    }

    public Set<DemoParent> getDemoParentSet() {
        return demoParentSet;
    }

    public void setDemoParentSet(Set<DemoParent> demoParentSet) {
        this.demoParentSet = demoParentSet;
    }

    public int getAaa() {
        return aaa;
    }

    public void setAaa(int aaa) {
        this.aaa = aaa;
    }

    public long getBbb() {
        return bbb;
    }

    public void setBbb(long bbb) {
        this.bbb = bbb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DemoParent getDemo2() {
        return demo2;
    }

    public void setDemo2(DemoParent demo2) {
        this.demo2 = demo2;
    }

    public Map<Integer, DemoParent> getDemoParentMap() {
        return demoParentMap;
    }

    public void setDemoParentMap(Map<Integer, DemoParent> demoParentMap) {
        this.demoParentMap = demoParentMap;
    }

    public static void main(String[] args) {
        ConvertManager.load("com.akoo");
        IConvertProcess process = ConvertManager.getProcessMap().get(Demo.class);

        Demo demo = new Demo();
        demo.setAaa(1);
        demo.setBbb(222);
        Map demos = new HashMap();
        demos.put(1, new Demo2());
        demos.put(2, new Demo3());
        demo.setDemoParentMap(demos);

        List lst = new ArrayList();
        lst.add(new Demo3());
        demo.setDemoParentList(lst);

        HashSet set = new HashSet();
        set.add(new Demo2());
        demo.setDemoParentSet(set);

        Demo2 demo2 = new Demo2();
        demo.setDemo2(demo2);
        demo2.setDemo2A(111);
        demo2.setDemo2B(3333);
        Map demo2Map = new HashMap<Integer, Integer>();
        demo2Map.put(1, 1);
        demo2.setDemo2Map(demo2Map);
        demo2.setParent1(1);
        demo2.setParent2(2);



        Demo3 demo3 = new Demo3();
        demo2.setDemo3(demo3);
        demo3.setDemo3A(55555);
        demo3.setDemo3B(66666);
        Map demo3Map = new HashMap<Integer, Integer>();
        demo3Map.put(1, 11);
        demo3.setDemo3Map(demo3Map);
        demo3.setParent1(11);
        demo3.setParent2(22);

        DBBuffer buffer = DBBuffer.allocate();
        process.toBytes(demo, buffer);
        demo = new Demo();

        byte[] bytes = buffer.toBytes();
        System.out.println(">>> bytes len : " + bytes.length);
        try {
            process.fromBytes(DBBuffer.warp(bytes), demo);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("aaa : " + demo.getAaa());
        System.out.println("bbb : " + demo.getBbb());
        System.out.println("map : " + demo.getDemoParentMap());
        System.out.println("name : " + demo.getName());
        Demo2 demo21 = (Demo2) demo.getDemo2();
        System.out.println("demo2 : " + demo.getDemo2());
        System.out.println("demo3 : " + demo21.getDemo3());
//        System.out.println("demo2 : " + demo.getDemo2().getDemo2A() + ", "+ demo.getDemo2().getDemo2B());
        System.out.println("demo2 : " + demo.getDemo2().getParent1() + ", "+ demo.getDemo2().getParent2());
//        System.out.println("demo2 : " + demo.getDemo2().getDemo2Map());
//        System.out.println("demo3 : " + demo.getDemo2().getDemo3().getDemo3A() + ", "+ demo.getDemo2().getDemo3().getDemo3B());
//        System.out.println("demo3 : " + demo.getDemo2().getDemo3().getParent1() + ", "+ demo.getDemo2().getDemo3().getParent2());
//        System.out.println("demo3 : " + demo.getDemo2().getDemo3().getDemo3Map());
    }
}
