package com.akoo.common.util.bt;

import java.util.ArrayList;
import java.util.List;

public abstract class BTNode {

    protected List<BTNode> _children;

    public abstract BTResult tick ();

    public abstract void clear ();

    public void add (BTNode aNode) {
        if (_children == null) {
            _children = new ArrayList<>();
        }
        if (aNode != null) {
            _children.add(aNode);
        }
    }

    public boolean remove (BTNode aNode) {
        if (_children != null && aNode != null) {
            return _children.remove(aNode);
        }
        return false;
    }
}
