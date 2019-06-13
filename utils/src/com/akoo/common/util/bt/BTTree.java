package com.akoo.common.util.bt;

public class BTTree {

    protected BTNode _root = null;

    public BTTree(BTNode root){
        this._root = root;
    }

    public void update() {
        _root.tick();
    }
}
