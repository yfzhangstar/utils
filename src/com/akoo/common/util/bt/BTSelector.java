package com.akoo.common.util.bt;

public class BTSelector extends BTNode {
    @Override
    public BTResult tick() {
        for (BTNode child : _children) {
            BTResult result = child.tick();
            if(result == BTResult.Running || result == BTResult.Success){
                return result;
            }
        }
        return BTResult.Fail;
    }

    @Override
    public void clear() {
        for (BTNode child : _children) {
            child.clear();
        }
    }
}
