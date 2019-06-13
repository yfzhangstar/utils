package com.akoo.common.util.cmd;

public interface Command {
    Object execute(Object src, Object cmd, Object[] parm) throws Exception;

    Object getName();

    default boolean canUse() {
        return true;
    }
}
