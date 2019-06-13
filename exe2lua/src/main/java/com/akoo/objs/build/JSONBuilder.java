package com.akoo.objs.build;

import java.util.Arrays;
import java.util.Map;

import com.akoo.Main;
import com.akoo.objs.HeadItem;
import com.akoo.objs.LineObj;
import com.akoo.objs.TypedHeadInfo;
import com.akoo.tool.JSON;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * aly @ 16-11-12.
 */
public class JSONBuilder implements ExportBuilder {
    private static final char[] keyWord = "[]{}=:".toCharArray();

    static {
        Arrays.sort(keyWord);
    }

    @Override
    public void buildHeadLine(StringBuilder builder, String saveFileName, TypedHeadInfo headInfo) {
        HeadItem luaIdxHead = headInfo.getItemByType(LuaBuilder.LuaIdxName);
        if (null != luaIdxHead)
            builder.append('{');
        else {
            builder.append('[');
        }
    }

    @Override
    public void buildLine(StringBuilder builder, TypedHeadInfo headInfo, String ID, LineObj line, boolean buildOne, Map<HeadItem, String> replaceVal) {
        builder.append('\n');
        if (null != ID) {
            builder.append('\"').append(ID).append("\":{");
        } else {
            builder.append('{');
        }

        boolean first = true;
        for (HeadItem item : headInfo) {
            if (item.type.equals(LuaBuilder.LuaIdxName)) continue;


            String cellVal = replaceVal == null ? line.getValue(item) : replaceVal.get(item);
            if (cellVal == null) {
                cellVal = line.getValue(item);
            }
            if (cellVal.length() == 0) continue;

            if (first) {
                first = false;
            } else {
                builder.append(',');
            }
            builder.append('\"').append(item.filedName).append("\":");
            String funType = item.type;
            switch (funType) {
                case "json":
                    // 如果存在有字段的情况
//                    if (cellVal.contains("=")) {
                        builder.append(JSON.JSONString(JSON.parse(cellVal)));
//                    } else {
//                        builder.append(cellVal);
//                    }
                    break;
                case "fun":         // 不支持func 按照字符串转化
                case "str": {
                    builder.append("\"").append(cellVal).append("\"");
                    break;
                }
                case "num": {
                    builder.append(cellVal);
                    break;
                }
                case "nums":
                    builder.append("[").append(cellVal.substring(1, cellVal.length() - 1)).append("]");
                    break;
                case "strs": {
                    String content = cellVal.substring(1, cellVal.length() - 1);
                    String[] split = content.split(",");
                    builder.append("[");

                    builder.append('\"').append(split[0]).append('\"');
                    for (int i = 1; i < split.length; i++) {
                        String in = split[i];
                        builder.append(',').append('\"').append(in).append('\"');
                    }
                    builder.append("]");
                    break;
                }
                default: {
                    builder.append(cellVal);
                }
            }
        }
        builder.append("},");
    }

    @Override
    public String getSaveFileName(Sheet sheet, String fileName) {
        if (!fileName.endsWith(Main.luaFileNameFix)) {
            return fileName + Main.luaFileNameFix;
        }
        return fileName;
    }

    @Override
    public void buildEnd(StringBuilder builder, String fileName, TypedHeadInfo headInfo) {
        if (builder.length() > 0 && builder.lastIndexOf(",") + 1 == builder.length()) {
            builder.deleteCharAt(builder.length() - 1);
        }

        HeadItem luaIdxHead = headInfo.getItemByType(LuaBuilder.LuaIdxName);
        builder.append('\n');
        if (null != luaIdxHead) {
            builder.append('}');

        } else {
            builder.append(']');
        }
    }

    @Override
    public HeadItem getIDHeadItem(TypedHeadInfo headInfo) {
        return headInfo.getItemByType(LuaBuilder.LuaIdxName);
    }
}
