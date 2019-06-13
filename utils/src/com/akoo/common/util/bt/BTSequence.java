package com.akoo.common.util.bt;

import com.akoo.common.util.bt.BTNode;

public class BTSequence extends BTNode {

    private BTNode _activeChild;
    private int _activeIndex = -1;

    @Override
    public BTResult tick() {
        // first time
        if (_activeChild == null) {
            _activeChild = _children.get(0);
            _activeIndex = 0;
        }

        BTResult result = _activeChild.tick();
        if (result == BTResult.Success) {	// Current active node over
            _activeIndex++;
            if (_activeIndex >= _children.size()) {	// sequence is over
                _activeChild.clear();
                _activeChild = null;
                _activeIndex = -1;
            }
            else {	// next node
                _activeChild.clear();
                _activeChild = _children.get(_activeIndex);
                result = BTResult.Running;
            }
        }
        return result;
    }

    @Override
    public void clear() {
        if (_activeChild != null) {
            _activeChild = null;
            _activeIndex = -1;
        }
        for (BTNode child : _children) {
            child.clear();
        }
    }
}
