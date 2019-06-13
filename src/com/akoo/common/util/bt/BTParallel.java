package com.akoo.common.util.bt;

public class BTParallel extends BTNode {

    protected BTParallelFunction _func;

    public BTParallel (BTParallelFunction func) {
        this._func = func;
    }

    @Override
    public BTResult tick() {
        if (_func == BTParallelFunction.And) {
            for (int i=0; i<_children.size(); i++) {
                BTResult result = _children.get(i).tick();
                if (result == BTResult.Fail) {
                    return result;
                }
            }
            return BTResult.Success;
        }
        else {
            for (int i=0; i<_children.size(); i++) {
                BTResult result = _children.get(i).tick();
                if (result == BTResult.Success || result == BTResult.Running) {
                    return result;
                }
            }
            return BTResult.Fail;
        }
    }

    public void clear () {
        for (BTNode child : _children) {
            child.clear();
        }
    }
}
