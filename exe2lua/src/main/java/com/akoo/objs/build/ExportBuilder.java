package com.akoo.objs.build;

import java.util.HashMap;
import java.util.Map;

import com.akoo.objs.HeadItem;
import com.akoo.objs.LineObj;
import com.akoo.objs.Pair;
import com.akoo.objs.TypedHeadInfo;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * aly @ 16-11-12.
 */
public interface ExportBuilder {
    /**
     * @param info 头信息 可能不存在
     */
    void buildHeadLine(StringBuilder builder, String saveFileName, TypedHeadInfo info);

    /**
     * @param id null 没有表头
     */
    void buildLine(StringBuilder builder, TypedHeadInfo headInfo, String id, LineObj line, boolean buildOne, Map<HeadItem, String> replaceVal);

    String getSaveFileName(Sheet sheet, String fileName);

    void buildEnd(StringBuilder builder, String fileName, TypedHeadInfo headInfo);


    default Pair<String, Map<HeadItem, String>> getValue(TypedHeadInfo headInfo, LineObj line) {
        HeadItem luaIdxHead = getIDHeadItem(headInfo);
        String ID = null;
        if (null != luaIdxHead) {
            ID = line.getValue(luaIdxHead);
        }
        Map<HeadItem, String> lineMap = new HashMap<>();
        for (HeadItem item : headInfo) {
            String cellVal = line.getValue(item);
            if (cellVal.length() == 0) continue;
            if (luaIdxHead == item) continue;
            lineMap.put(item, cellVal);
        }
        return Pair.valueOf(ID, lineMap);
    }

    HeadItem getIDHeadItem(TypedHeadInfo headInfo);

    default String getID(LineObj lineObj, TypedHeadInfo headInfo) {
        HeadItem idHeadItem = getIDHeadItem(headInfo);
        if (null == idHeadItem) return null;
        return lineObj.getValue(idHeadItem);
    }
}
