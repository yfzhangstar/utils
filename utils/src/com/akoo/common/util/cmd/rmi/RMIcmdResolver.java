package com.akoo.common.util.cmd.rmi;


import com.akoo.common.util.cmd.CMDResolver;
import com.akoo.common.util.cmd.Command;


public class RMIcmdResolver extends CMDResolver {
    public RMIcmdResolver(String... cmdPkgName) {
        super(cmdPkgName);
    }

    @Override
    protected Command getCommand(Object cmd) {
        return cmdCache.get((Integer) cmd / 100);
    }
}
