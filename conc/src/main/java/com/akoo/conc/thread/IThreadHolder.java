package com.akoo.conc.thread;

import java.util.concurrent.ExecutorService;

public interface IThreadHolder {

    public ExecutorService getService();

    void bind(ExecutorService service);

    void unBind();
}
