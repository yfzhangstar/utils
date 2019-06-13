package com.akoo.common.util.cmd;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.akoo.common.util.SpringBeanFactory;
import com.akoo.common.util.StringUtil;
import com.akoo.common.util.clazz.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * cmd解析者 ,根据 构造具体的命令
 *
 * @author Aly
 */
public abstract class CMDResolver {
    private static final Logger log = LoggerFactory.getLogger(CMDResolver.class);
    protected Map<Object, Command> cmdCache = new ConcurrentHashMap<>();
    private String[] cmdPkgName;

    /**
     * 命令初始化
     */
    public CMDResolver(String[] cmdPkgName) {
        boolean havePkg = false;
        for (String s : cmdPkgName) {
            if (StringUtil.isNotEmpty(s)) {
                havePkg = true;
                break;
            }
        }
        this.cmdPkgName = cmdPkgName.clone();
        if (havePkg) {
            try {
                List<Command> allCmds = getCmds();
                if (null != allCmds && allCmds.size() > 0) {
                    for (Command command : allCmds) {
                        if (null != command && command.canUse()) {
                            addCMD(command);
                        }
                    }
                } else {
                    log.error("init cmd faild with 0 cmd:pkg{{}} ", new Object[]{cmdPkgName});
                }
            } catch (Throwable e) {
                log.error("", e);
            }
        }
    }

    public void addCMD(Command command) {
        log.debug("load CMD:[{}]:\t[{}]", new Object[]{command.getName(), command.getClass()});
        Command cm = cmdCache.put(command.getName(), command);
        if (null != cm) {
            log.error("cmd name:[{}] is not unique for class{} and {}", command.getName(),
                    command.getClass().getName(), cm.getClass().getName());
        }
    }

    protected List<Command> getCmds() {
        List<Command> cmd = new ArrayList<>();
        for (String pkgName : cmdPkgName) {
            try {
                if (StringUtil.isEmpty(pkgName)) {
                    continue;
                }
                List<Class<?>> impls = ClassUtils.getClasses(pkgName);
                for (Class<?> cls : impls) {
                    if (Command.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
                        Command c = (Command) cls.newInstance();
                        SpringBeanFactory.autowireBean(c);
                        cmd.add(c);
                    }
                }
            } catch (Throwable e) {
                log.warn("load Cmd error: pkg:{}", new Object[]{cmdPkgName, e});
            }
        }
        return cmd;
    }

    /**
     *
     */
    public Object exeCmd(Object src, Object cmd, Object[] parm) throws Exception {
        Command c = getCommand(cmd);
        if (null != c) {
            try {
                return c.execute(src, cmd, parm);
            } catch (Exception e) {
                log.error("{} exeClass error src:{},cmd:{},parm:{}", this, src, cmd, parm, e);
            }
        } else {
            log.error("can not find cmd :[{}] parm:[{}]", new Object[]{cmd, parm});
        }
        return null;
    }

    protected Command getCommand(Object cmd) {
        return cmdCache.get(cmd);
    }

    public List<Object> getCmdList() {
        return new ArrayList<>(cmdCache.keySet());
    }
}
