package com.akoo.tasks;

import com.akoo.enums.ProcType;
import com.akoo.objs.*;
import com.akoo.tool.Log;
import com.akoo.tool.Tool;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class LanFileTask extends RecursiveTask<TableObj> {
    private static final ThreadLocal<Map<Integer, String>> LocalMapCache = ThreadLocal.withInitial(HashMap::new);
    private final Sheet sheet;
    private final Workbook book;
    private final String fileName;

    public LanFileTask(Workbook book, Sheet sheet, String fileName) {
        this.book = book;
        this.sheet = sheet;
        this.fileName = fileName;
    }

    @Override
    protected TableObj compute() {

        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        for (int i = firstRowNum; i <= lastRowNum; ++i) {
            Row row = sheet.getRow(i);
            if (null == row) {
                continue;
            }
            Row nexRow = sheet.getRow(row.getRowNum());
            try {
                HeadInfo headInfo = Tool.parseHeadLine(nexRow);

                int startRow = nexRow.getRowNum() + 1;
                int lastRow = Tool.getLastRow(fileName, book, sheet, lastRowNum, startRow);
                i = lastRow + 1;
                return new SubTask<>(startRow, lastRow, sheet, new SubTaskFunction<TableObj>() {
                    @Override
                    public TableObj call(int startRow1, int lastRow1, Sheet sheet1) throws Exception {
                        TableObj obj = newRet();
                        for (; startRow1 <= lastRow1; startRow1++)
                            try {
                                Row nexRow1 = sheet1.getRow(startRow1);
                                if (null == nexRow1) continue;
                                Map<Integer, String> LineCache = LocalMapCache.get();
                                ProcType.forEach(procType -> {
                                    TypedHeadInfo info = headInfo.get(procType);
                                    if (null == info || info.size() == 0) return;
                                    LineObj lineObj = Tool.parseLine(fileName, nexRow1, info, LineCache);
                                    if (null != lineObj && !lineObj.isEmpty()) {
                                        Pair<String, Map<HeadItem, String>> value = procType.getBuilder().getValue(info, lineObj);
                                        String ID = value.getLeft();
                                        if (ID != null) {
                                            Map<String, Map<HeadItem, String>> map = obj.get(procType);
                                            Map<HeadItem, String> put = map.put(ID, value.getRight());
                                            if (null != put) {
                                                System.out.println("重ID:+" + ID);
                                            }
                                        }
                                    }
                                });
                                LineCache.clear();
                            } catch (Exception e) {
                                Log.outError(fileName, startRow1, e);
                            }
                        return obj;
                    }

                    @Override
                    public TableObj newRet() {
                        return new TableObj(fileName);
                    }
                }).compute();
            } catch (Exception e) {
                Log.outError(fileName, -1, e);
                e.printStackTrace();
            }

        }
        return null;
    }
}
