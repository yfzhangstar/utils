package com.akoo.common.util.convert;

import com.akoo.common.util.convert.annotation.AConvert;
import com.akoo.common.util.convert.annotation.AIndex;

@AConvert
public class DemoParent {

    @AIndex(ix = 1)
    private int parent1;
    @AIndex(ix = 2)
    private int parent2;

    public int getParent1() {
        return parent1;
    }

    public void setParent1(int parent1) {
        this.parent1 = parent1;
    }

    public int getParent2() {
        return parent2;
    }

    public void setParent2(int parent2) {
        this.parent2 = parent2;
    }
}
