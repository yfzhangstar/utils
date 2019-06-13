package com.akoo.enums;

import com.akoo.objs.build.CSVBuilder;
import com.akoo.objs.build.ExportBuilder;
import com.akoo.objs.build.JSONBuilder;
import com.akoo.objs.build.LuaBuilder;

import java.util.function.Consumer;

public enum ProcType {
    CSV(".csv", new CSVBuilder(), true, '{', '}'),            //
    LUA(".lua", new LuaBuilder(), false, '(', ')'),            //
    JSON(".json", new JSONBuilder(), false, '(', ')');          //

    private boolean witeBom;
    private char[] headFix;
    private String savePath;
    private boolean enable;
    private ExportBuilder builder;
    private String fileFix;

    ProcType(String fileFix, ExportBuilder builder, boolean writeBom, char... headFix) {
        this.fileFix = fileFix;
        this.builder = builder;
        this.headFix = headFix;
        this.witeBom = writeBom;
    }

    public static void setExportType(String exportType) {
        if (exportType == null) exportType = "";
        for (ProcType type : ProcType.values()) {
            boolean contains = exportType.contains(type.name().toLowerCase());
            type.setEnable(contains);
        }
    }

    public static void forEach(Consumer<ProcType> consumer) {
        for (ProcType type : ProcType.values()) {
            if (type.enable) {
                consumer.accept(type);
            }
        }
    }

    public char[] getHeadFix() {
        return headFix;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getEncodeing() {
        return "UTF-8";
    }

    public boolean isWiteBom() {
        return witeBom;
    }

    public ExportBuilder getBuilder() {
        return builder;
    }

    public String getFileFix() {
        return fileFix;
    }

    public void setFileFix(String fileFix) {
        this.fileFix = fileFix;
    }
}
