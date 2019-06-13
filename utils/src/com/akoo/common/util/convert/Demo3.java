package com.akoo.common.util.convert;

import com.akoo.common.util.convert.annotation.AConvert;
import com.akoo.common.util.convert.annotation.AIndex;

import java.util.HashMap;
import java.util.Map;

@AConvert
public class Demo3 extends DemoParent {

    @AIndex(ix = 1)
    private int demo3A;

    @AIndex(ix = 2)
    private int demo3B;

    @AIndex(ix = 3)
    private Map<Integer, Integer> demo3Map = new HashMap<>();

    public int getDemo3A() {
        return demo3A;
    }

    public void setDemo3A(int demo3A) {
        this.demo3A = demo3A;
    }

    public int getDemo3B() {
        return demo3B;
    }

    public void setDemo3B(int demo3B) {
        this.demo3B = demo3B;
    }

    public Map<Integer, Integer> getDemo3Map() {
        return demo3Map;
    }

    public void setDemo3Map(Map<Integer, Integer> demo3Map) {
        this.demo3Map = demo3Map;
    }
}
