package com.akoo.objs;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.akoo.enums.ProcType;
import com.akoo.tasks.Mergable;

public class TableObj implements Mergable<TableObj> {
    private final String fileName;
    //Type ID ,Head Vale
    private Map<ProcType, Map<String, Map<HeadItem, String>>> val = new EnumMap<>(ProcType.class);

    public TableObj(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void merge(TableObj rightR) {
        rightR.val.forEach((procType, stringMapMap) -> {
            Map<String, Map<HeadItem, String>> map = val.computeIfAbsent(procType, t -> new HashMap<>());
            stringMapMap.forEach((s, headItemStringMap) -> {
                Map<HeadItem, String> old = map.put(s, headItemStringMap);
                if (null != old)
                    System.out.println("重复的ID:--->" + fileName + " id:" + s + "==" + old);
            });
        });
    }

    public void put(ProcType procType, Pair<String, Map<HeadItem, String>> value) {
        if (null == value) return;
        Map<String, Map<HeadItem, String>> v = val.computeIfAbsent(procType, t -> new HashMap<>());
        String id = value.getLeft();
        if (null != id && id.length() > 0)
            v.put(id, value.getRight());
    }

    public Map<String, Map<HeadItem, String>> get(ProcType procType) {
        return val.computeIfAbsent(procType, k -> new HashMap<>());
    }
}
