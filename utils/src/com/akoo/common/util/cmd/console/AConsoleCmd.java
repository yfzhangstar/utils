package com.akoo.common.util.cmd.console;


import com.akoo.common.util.cmd.Command;

public abstract class AConsoleCmd implements Command {
    @Override
    public final Object execute(Object src, Object cmd, Object[] param) throws Exception {
            return exe((String) cmd, (String[]) param);
    }

    public abstract Object exe(String cmd, String[] param) throws Exception;
}
