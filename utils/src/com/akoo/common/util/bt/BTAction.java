package com.akoo.common.util.bt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BTAction extends BTNode {

    private static Logger Logger = LoggerFactory.getLogger(BTAction.class);

    private BTActionStatus _status = BTActionStatus.Ready;

    protected abstract void enter();

    protected abstract void exit();

    protected abstract BTResult execute ();

    @Override
    public void clear () {
        if (_status != BTActionStatus.Ready) {	// not cleared yet
            exit();
            _status = BTActionStatus.Ready;
        }
    }

    @Override
    public BTResult tick () {
        BTResult result = BTResult.Fail;
        if (_status == BTActionStatus.Ready) {
            enter();
            _status = BTActionStatus.Running;
        }
        if (_status == BTActionStatus.Running) {		// not using else so that the status changes reflect instantly
            result = execute();
            if (result != BTResult.Running) {
                exit();
                _status = BTActionStatus.Ready;
            }
        }
        return result;
    }

    @Override
    public void add (BTNode aNode) {
        Logger.error("BTAction: Cannot add a node into BTAction.");
    }

    @Override
    public boolean remove (BTNode aNode) {
        Logger.error("BTAction: Cannot remove a node into BTAction.");
        return false;
    }
}
