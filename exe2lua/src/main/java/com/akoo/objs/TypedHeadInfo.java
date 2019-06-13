package com.akoo.objs;

import java.util.*;

/**
 * aly @ 16-11-12.
 */
public class TypedHeadInfo implements Iterable<HeadItem> {
    List<HeadItem> items = new ArrayList<HeadItem>();

    Set<String> fieldSet = new HashSet<String>();

    @Override
    public Iterator<HeadItem> iterator() {
        return items.iterator();
    }

    public int size() {
        return items.size();
    }

    public HeadItem getItemByFieldName(String fildName) {
        if (null == fildName) {
            return null;
        }
        for (HeadItem item : items) {
            if (fildName.equals(item.filedName)) {
                return item;
            }
        }
        return null;
    }

    public HeadItem getItemByType(String type) {
        if (null == type) {
            return null;
        }
        for (HeadItem item : items) {
            if (type.equals(item.type)) {
                return item;
            }
        }
        return null;
    }

    public HeadItem getItemByCellVal(String cellVal) {
        for (HeadItem item : items) {
            if (cellVal.equals(item.cellVal)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "TypedHeadInfo{" +
                "items=" + items +
                ", fieldSet=" + fieldSet +
                '}';
    }
}
