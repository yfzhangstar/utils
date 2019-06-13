package com.akoo.common.util.cmd;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.akoo.common.util.cmd.console.ConsoleCmdResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Aly @2016-12-22.
 */
public class ConsoleCmdRunner implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ConsoleCmdRunner.class);
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // CMD 执行文件 的checkTime
    private long lastCmdFileCheckTime = System.currentTimeMillis();
    private AtomicBoolean runningStatus;
    private ConsoleCmdResolver consoleCMD;
    private Runnable stopMethod;

    public ConsoleCmdRunner(AtomicBoolean runningStatus, String cmdPkg, Runnable stopMethod) {
        this.runningStatus = runningStatus;
        consoleCMD = new ConsoleCmdResolver(cmdPkg);
        this.stopMethod = stopMethod;

        insertCMD("help", param -> log.warn("已知命令列表为：" + consoleCMD.getCmdList()));
        if (null != stopMethod) {
            insertCMD("stop", param -> stop());
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        }
    }

    public void stop() {
        if (runningStatus.compareAndSet(true, false)) {
            if (null != stopMethod) stopMethod.run();
        }
    }

    public void insertCMD(String cmdName, Consumer<String[]> function) {
        consoleCMD.addCMD(new Command() {
            @Override
            public Object execute(Object src, Object cmd, Object[] parm) throws Exception {
                function.accept(((String[]) parm));
                return null;
            }

            @Override
            public Object getName() {
                return cmdName;
            }
        });
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String getCMDFromFile() {
        try {
            File cmdFile = new File("cmd");

            log.warn("check back CMD file:[" + cmdFile.getAbsolutePath() + "]@" + format.format(System.currentTimeMillis()));
            if (!cmdFile.exists()) return null;
            if (!cmdFile.isFile()) {
                cmdFile.delete();
                return null;
            } else {
                long lastModified = cmdFile.lastModified();
                if (lastCmdFileCheckTime >= lastModified) {
                    log.warn("cmd File is executed @" + format.format(lastCmdFileCheckTime));
                } else {
                    lastCmdFileCheckTime = lastModified;
                    BufferedReader fileReader = new BufferedReader(new FileReader(cmdFile));
                    String cmd = fileReader.readLine();
                    fileReader.close();
                    return cmd;
                }
            }
        } catch (Throwable e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            // 防止上次执行命令被重复执行
            lastCmdFileCheckTime = System.currentTimeMillis();
            reader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            boolean consoleMod = true;
            while (runningStatus.get()) {
                try {
                    String cmd;
                    if (consoleMod) {
                        System.out.print(">>>");
                        cmd = reader.readLine();
                        if (null == cmd) {
                            // EOF
                            consoleMod = false;
                            log.warn(" switch to CMD file mod");
                            reader.close();
                            continue;
                        }
                    } else {
                        Thread.sleep(30000L);
                        cmd = getCMDFromFile();
                        if (null == cmd) continue;
                    }
                    processCMD(cmd);
                } catch (Throwable e) {
                    // EOF
                    consoleMod = false;
                    log.warn(" switch to CMD file mod");
                    reader.close();

                    log.error("", e);
                }
            }
        } catch (Throwable e) {
            log.error("", e);
        } finally {
            if (null != reader) try {
                reader.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void processCMD(String cmd) {
        cmd = cmd.trim();
        if (cmd.length() == 0) return;
        log.warn(" exe cmd :[{}]", cmd);
        String[] split = cmd.split(" ");
        try {
            consoleCMD.exeCmd(null, split[0], split);
        } catch (Throwable e) {
            log.warn(" 执行命令出错 ", e);
        }
    }
}
