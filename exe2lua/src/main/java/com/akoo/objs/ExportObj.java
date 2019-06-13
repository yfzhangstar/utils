package com.akoo.objs;

import java.util.EnumMap;
import java.util.Map;

import com.akoo.enums.ProcType;
import com.akoo.tasks.Mergable;

/**
 * aly @ 16-11-12.
 * 数据导出对象
 */
public class ExportObj implements Mergable<ExportObj> {
    private Map<ProcType, StringBuilder> export = new EnumMap<>(ProcType.class);


    public void export(ProcType type, StringBuilder sb) {
        StringBuilder builder = export.get(type);
        if (null == builder) {
            export.put(type, builder = new StringBuilder());
        }
        builder.append(sb);
    }

    public StringBuilder export(ProcType type) {
        StringBuilder builder = export.get(type);
        if (null == builder) {
            export.put(type, builder = new StringBuilder());
        }
        return builder;

    }

    public void merge(ExportObj join) {
        join.export.forEach((type, stringBuilder) -> export(type).append(stringBuilder));
    }
}
