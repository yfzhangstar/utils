package com.akoo.common.util.cmd.rmi;


import com.akoo.common.util.cmd.Command;

public abstract class ARMICmd implements Command {
    @Override
    public final Object execute(Object src, Object cmd, Object[] parm) {
        if (null != cmd && cmd instanceof Integer) {
            return exe(null == src ? null : src.toString(), (Integer) cmd, parm);
        }
        return null;
    }

    public abstract Object exe(String src, int cmd, Object[] parm);

    @Override
    public final Object getName() {
        return getCName() / 100;
    }

    public abstract int getCName();
}
