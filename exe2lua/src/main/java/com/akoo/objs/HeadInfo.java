package com.akoo.objs;

import java.util.EnumMap;
import java.util.Map;

import com.akoo.enums.ProcType;

/**
 * Created by Aly on 2015-08-05.
 * 表头信息
 */
public class HeadInfo {

    private Map<ProcType, TypedHeadInfo> allHeads = new EnumMap<>(ProcType.class);

    public void addItem(ProcType procType, int ix, String type, String fieldName, String cellVal) {

        if (fieldName == null && type == null) {
            throw new RuntimeException("type and vale is null val:" + cellVal + "\t ix:" + ix);
        }
        TypedHeadInfo headInfo = allHeads.get(procType);
        if (null == headInfo) {
            allHeads.put(procType, headInfo = new TypedHeadInfo());
        }

        if (headInfo.fieldSet.contains(fieldName)) {
            throw new RuntimeException("fieldName 重复 val:" + cellVal + "\t ix:" + ix);
        }
        type = type.toLowerCase();
        if (fieldName == null && headInfo.fieldSet.contains(type)) {
            throw new RuntimeException("type  重复 val:" + cellVal + "\t ix:" + ix);
        }

        HeadItem headItem = new HeadItem();
        headItem.filedName = null != fieldName ? fieldName.trim() : null;
        headItem.ix = ix;
        headItem.type = type;
        headItem.cellVal = cellVal;
        headInfo.items.add(headItem);
    }

    @Override
    public String toString() {
        return "HeadInfo{" +
                "allHeads=" + allHeads +
                '}';
    }

    public TypedHeadInfo get(ProcType procType) {
        return allHeads.get(procType);
    }
}
