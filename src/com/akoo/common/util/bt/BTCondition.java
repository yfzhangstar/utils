package com.akoo.common.util.bt;

public abstract class BTCondition extends BTNode {

    // Override to provide the condition check.
    public abstract boolean check ();

    // Functions as a node
    public  BTResult tick () {
        boolean success = check();
        if (success) {
            return BTResult.Success;
        }
        else {
            return BTResult.Fail;
        }
    }
}
