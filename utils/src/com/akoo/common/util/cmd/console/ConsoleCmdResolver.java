package com.akoo.common.util.cmd.console;

import com.akoo.common.util.SpringBeanFactory;
import com.akoo.common.util.cmd.CMDResolver;
import com.akoo.common.util.cmd.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.List;

public class ConsoleCmdResolver extends CMDResolver {
    private static final Logger log = LoggerFactory.getLogger(ConsoleCmdResolver.class);

    private static final Class[] defaultCMDNames = new Class[]{
            ExeClass.class, LoadJar.class, ReLoad.class
    };

    public ConsoleCmdResolver(String... cmdPkgName) {
        super(cmdPkgName);
    }

    @Override
    public List<Command> getCmds() {
        List<Command> cmds = super.getCmds();
        for (Class cls : defaultCMDNames) {
            try {
                if (Command.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
                    Command c = (Command) cls.newInstance();
                    SpringBeanFactory.autowireBean(c);
                    cmds.add(c);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("", e);
            }

        }
        return cmds;
    }
}
