package com.akoo.objs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akoo.enums.LineType;

/**
 * Created by Aly on 2015-08-05.
 * Line
 */
public class LineObj {
    public List<LineItem> items = new ArrayList<LineItem>();
    private LineType lineType;
    // 字段名称， 对应的值
    private Map<String, LineItem> line = new HashMap<String, LineItem>();

    public LineObj(LineType lineType) {
        this.lineType = lineType;
    }

    public void addItem(HeadItem item, String val) {
        LineItem lineItem = new LineItem();
        lineItem.headVal = item.cellVal;
        lineItem.fieldName = item.filedName;
        lineItem.val = val == null ? null : val.trim();

        items.add(lineItem);
        if (null == item.filedName) {
            line.put(item.cellVal, lineItem);
        } else {
            line.put(item.filedName, lineItem);
        }
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public String getValue(HeadItem item) {
        LineItem val = line.get(item.filedName);
        if (null == val) {
            val = line.get(item.cellVal);
        }
        return null == val ? "" : val.val;
    }

    public LineType getLineType() {
        return lineType;
    }
}
