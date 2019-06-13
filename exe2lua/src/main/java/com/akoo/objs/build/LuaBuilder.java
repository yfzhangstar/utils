package com.akoo.objs.build;

import java.util.Map;

import com.akoo.Main;
import com.akoo.enums.BuildType;
import com.akoo.objs.HeadItem;
import com.akoo.objs.LineObj;
import com.akoo.objs.TypedHeadInfo;
import com.akoo.tool.JSON;
import com.akoo.tool.Tool;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * aly @ 16-11-12.
 */
public class LuaBuilder implements ExportBuilder {
    static final String LuaIdxName = "idx";

    private static void buildStart(StringBuilder lineSb, BuildType buildType) {
        if (buildType == BuildType.many) {
            //start
            lineSb.append('{');
        }
    }

    /**
     * 只用在文字表
     */
    public static void buildLineItem(StringBuilder lineSb, HeadItem headItem, LineObj line, boolean end) throws Exception {
        // 有多少列
        BuildType buildType = BuildType.one;
        if (LuaIdxName.equals(headItem.type)) {
            lineSb.append('\t');
            lineSb.append('[').append(line.getValue(headItem)).append("] = ");
            buildStart(lineSb, buildType);
        } else {
            if (end) {
                int oldLen = lineSb.length();
                buildLienItem(lineSb, line, headItem, buildType);
                if (lineSb.length() == oldLen) {
                    lineSb.append("\"\"");
                }
                buildEnd(lineSb, buildType);
            }
        }
    }

    private static void buildEnd(StringBuilder lineSb, BuildType buildType) {
        int i = lineSb.lastIndexOf(", ");
        if ((i + 2) == lineSb.length()) {
            lineSb.deleteCharAt(lineSb.lastIndexOf(","));
            lineSb.deleteCharAt(lineSb.lastIndexOf(" "));
        }
        if (buildType == BuildType.many) {
            lineSb.append('}');
        }
        lineSb.append(";\n");
    }

    private static void buildLienItem(StringBuilder lineSb, LineObj line, HeadItem item, BuildType buildType) {
        String funType = item.type;
        String cellVal = line.getValue(item);
        boolean buildMany = buildType == BuildType.many;
        if (cellVal.length() > 0) {
            if (funType.contains("json")) {
                if (buildMany) {
                    lineSb.append(item.filedName).append(" = ");
                }
                try {
                    Object m = JSON.parse(cellVal);
                    Tool.readObjec(lineSb, m);
                    lineSb.append(", ");
                } catch (Exception e) {
                    System.out.println("error json:" + cellVal);
                    throw e;
                }
            } else {
                if (buildMany) {
                    lineSb.append(item.filedName).append(" = ");
                }
                boolean isString = (funType.contains("str"));
                if (isString) {
                    lineSb.append("\"").append(cellVal).append("\"");
                } else {
                    lineSb.append(cellVal);
                }
                lineSb.append(", ");
            }
        }
    }

    @Override
    public void buildHeadLine(StringBuilder builder, String saveFileName, TypedHeadInfo info) {
        int i = saveFileName.lastIndexOf('.');
        if (i > 0) {
            saveFileName = saveFileName.substring(i);
        }
        builder.append("local " + saveFileName).append(" = {\n");
    }

    @Override
    public void buildLine(StringBuilder builder, TypedHeadInfo headInfo, String id, LineObj line, boolean buildOne, Map<HeadItem, String> replaceVal) {
        if (headInfo.size() == 0) {
            return;
        }
        builder.append("\t");
        if (null != id) {
            builder.append("[").append(id).append("] = ");
        }
        BuildType type;
        if (headInfo.size() == 2 && buildOne) {
            type = BuildType.one;
        } else {
            type = BuildType.many;
        }
        buildStart(builder, type);
        int oldLen = builder.length();
        for (HeadItem item : headInfo) {
            if (item.type.equals(LuaBuilder.LuaIdxName)) continue;
            buildLienItem(builder, line, item, type);
        }
        if (type == BuildType.one && builder.length() == oldLen) {
            // 长度不变
            builder.append("\"\"");
        }
        buildEnd(builder, type);

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
        builder.append("};\n");
        builder.append("return ").append(fileName);
    }

    @Override
    public HeadItem getIDHeadItem(TypedHeadInfo headInfo) {
        return headInfo.getItemByType(LuaIdxName);
    }
}
