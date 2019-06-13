package com.akoo.tool;

import com.akoo.Main;
import com.akoo.common.util.csv.UnicodeTool;
import com.akoo.enums.LineType;
import com.akoo.enums.ProcType;
import com.akoo.objs.*;
import com.akoo.objs.build.ExportBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Aly on 2015-08-05.
 * 工具类
 */
public class Tool {

    private static Pattern numPattern = Pattern.compile("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");

    private static void exportFile(final String destPath, final String fileName, final StringBuilder content, String charset, boolean witeBom) {
        File file = new File(destPath);
        if (!file.exists()) {
            boolean suc = file.mkdirs();
            if (!suc) {
                System.out.println(file.getAbsoluteFile() + " create error");
            }
        }
        file = new File(String.valueOf(destPath) + File.separator + fileName);

        if (file.exists()) {
            boolean suc = file.delete();
            if (!suc) {
                System.out.println(file.getAbsoluteFile() + " delete error");
            }
        }


        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            try (final FileOutputStream fo = new FileOutputStream(file)) {
                if (witeBom) {
                    byte[] bom = UnicodeTool.getBom(charset);
                    fo.write(bom);
                }
                fo.write(content.toString().getBytes(charset));
                fo.flush();
                fo.close();
            }
        } catch (Exception e) {
            System.out.println(" 保存文件报错:" + destPath + "//" + fileName);
            e.printStackTrace();
        }
    }

    private static boolean isNum(String str) {
        return numPattern.matcher(str).matches();
    }

    public static LineObj parseLine(String fileName, Row row, TypedHeadInfo headInfo, Map<Integer, String> lineValueCache) {
        if (headInfo == null) return null;
        Workbook book = row.getSheet().getWorkbook();
        LineObj lineObj = new LineObj(LineType.SHEET);
        for (HeadItem item : headInfo) {
            if (null != item) {
                Cell cell = row.getCell(item.ix);
                String cached = lineValueCache.get(item.ix);
                String cellVal;
                if (cached == null) {
                    cellVal = (cell == null) ? "" : ExcelTool.getCellStringVal(fileName, book, cell);
                    lineValueCache.put(item.ix, cellVal);
                } else {
                    cellVal = cached;
                }
                cellVal = cellVal.replaceAll("\n|\r|\n\r", "");
                lineObj.addItem(item, cellVal);
            }
        }

        if (lineObj.isEmpty())
            return null;
        else {
            boolean allnull = true;
            for (LineItem item : lineObj.items) {
                if (item.val != null && item.val.length() != 0) {
                    allnull = false;
                }
            }
            if (allnull)
                return null;
        }
        return lineObj;
    }


    public static void buildLine(StringBuilder lines, HeadInfo allHead, LineObj line, ProcType procType, boolean buildOne, Map<String, Map<HeadItem, String>> lanVale) {
        TypedHeadInfo headInfo = allHead.get(procType);
        if (line.isEmpty() || null == headInfo || headInfo.size() == 0) {
            return;
        }

        ExportBuilder builder = procType.getBuilder();
        String id = builder.getID(line, headInfo);

        builder.buildLine(lines, headInfo, id, line, buildOne, lanVale != null ? lanVale.get(id) : null);

    }


    @SuppressWarnings("unchecked")
    private static void readList(StringBuilder sb, List<Object> list) {
        Iterator<Object> it1 = list.iterator();
        while (it1.hasNext()) {
            Object obj = it1.next();
            readObjec(sb, obj);
            if (it1.hasNext()) {
                sb.append(", ");
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void readObjec(StringBuilder sb, Object obj) {
        if (obj instanceof List) {
            sb.append("{");
            List<Object> listObj = (List<Object>) obj;
            readList(sb, listObj);
            sb.append("}");
        } else if (obj instanceof Map) {
            Iterator<Map.Entry<String, Object>> it = ((Map<String, Object>) obj).entrySet().iterator();
            sb.append("{");
            while (it.hasNext()) {
                Map.Entry<String, Object> en = it.next();
                if (isNum(en.getKey())) {
                    sb.append("[").append(en.getKey()).append("]");
                } else {
                    sb.append(en.getKey());
                }
                sb.append(" = ");
                if (en.getValue() instanceof String) {
                    sb.append("'").append(en.getValue()).append("'");
                } else if (en.getValue() instanceof Boolean) {
                    sb.append(en.getValue());
                } else if (en.getValue() instanceof Number) {
                    sb.append(((Number) en.getValue()).intValue());
                } else {
                    readObjec(sb, en.getValue());
                }
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append("}");
        } else if (obj instanceof Number) {
            Number n = (Number) obj;
            if (n.intValue() == n.doubleValue()) {
                sb.append(n.intValue());
            }
        } else {
            sb.append(obj);
        }
    }

    private static String parseHeadCellVal(final String val, final ProcType type) {
        if (val == null || "".equals(val)) {
            return null;
        }
        final int ind = val.indexOf(type.getHeadFix()[0]);
        final int ind2 = val.indexOf(type.getHeadFix()[1]);
        if (ind == -1 || ind2 == -1) {
            return null;
        }
        return val.substring(ind + 1, ind2).trim();
    }

    public static HeadInfo parseHeadLine(Row row) {
        HeadInfo headInfo = new HeadInfo();
        Iterator<Cell> cells = row.cellIterator();
        while (cells.hasNext()) {
            Cell cell = cells.next();
            int columnIndex = cell.getColumnIndex();
            String stringCellValue = cell.getStringCellValue();
            ProcType.forEach(flag -> {
                String cellVal = parseHeadCellVal(stringCellValue, flag);
                String type;
                String name;
                switch (flag) {
                    case CSV: {
                        if (cellVal != null) {
                            String[] typeAndName = cellVal.split(",");
                            type = typeAndName[1];
                            name = typeAndName[0];

                        } else {
                            // csv 全转化
                            type = stringCellValue;
                            name = stringCellValue;
                        }
                        headInfo.addItem(flag, columnIndex, type, name, stringCellValue);

                    }
                    break;
                    case JSON:
                    case LUA: {
                        if (cellVal != null) {
                            String[] typeAndName = cellVal.split(",");
                            type = typeAndName[0];
                            if (Main.luaFileNameFix.equals(type)) {
                                name = type;
                            } else
                                name = typeAndName.length > 1 ? typeAndName[1] : null;
                            headInfo.addItem(flag, columnIndex, type, name, stringCellValue);
                        }
                    }
                    break;
                    default:
                        break;
                }
            });

        }
        return headInfo;
    }


    public static void SaveFile(String destPath, String fileName, ProcType type, StringBuilder content) {
        String realFileName = fileName;
        String path = destPath;
        if (fileName.contains(File.separator)) {
            realFileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.length());
            path += fileName.substring(0, fileName.lastIndexOf(File.separator));
        }
        exportFile(path, realFileName + type.getFileFix(), content, type.getEncodeing(), type.isWiteBom());

    }

    public static int getLastRow(String fileName, Workbook book, Sheet sheet, int lastRowNum, int startRow) {
        int lastRow = startRow;
        for (; lastRow <= lastRowNum; lastRow++) {
            Row row1 = sheet.getRow(lastRow);
            if (row1 == null) {
                continue;
            }
            String fistVal = ExcelTool.getCellVal(fileName, book, row1, 0);
            if ("END".equalsIgnoreCase(fistVal)) {
                lastRow--;
                break;
            }
        }
        return lastRow;
    }

}
