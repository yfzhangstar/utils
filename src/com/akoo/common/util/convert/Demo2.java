package com.akoo.common.util.convert;

import com.akoo.common.util.convert.annotation.AConvert;
import com.akoo.common.util.convert.annotation.AIndex;

import java.util.HashMap;
import java.util.Map;

@AConvert
public class Demo2 extends DemoParent {

    @AIndex(ix = 1)
    private int demo2A;

    @AIndex(ix = 2)
    private int demo2B;

    @AIndex(ix = 3)
    private DemoParent demo3 = new Demo3();

    @AIndex(ix = 4)
    private Map<Integer, Integer> demo2Map = new HashMap<>();

    public int getDemo2A() {
        return demo2A;
    }

    public void setDemo2A(int demo2A) {
        this.demo2A = demo2A;
    }

    public int getDemo2B() {
        return demo2B;
    }

    public void setDemo2B(int demo2B) {
        this.demo2B = demo2B;
    }

    public DemoParent getDemo3() {
        return demo3;
    }

    public void setDemo3(DemoParent demo3) {
        this.demo3 = demo3;
    }

    public Map<Integer, Integer> getDemo2Map() {
        return demo2Map;
    }

    public void setDemo2Map(Map<Integer, Integer> demo2Map) {
        this.demo2Map = demo2Map;
    }
}
