package com.akoo.tasks;

import com.akoo.Main;
import com.akoo.enums.ProcType;
import com.akoo.objs.*;
import com.akoo.tool.ExcelTool;
import com.akoo.tool.Log;
import com.akoo.tool.Tool;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

/**
 * aly @ 16-11-12.
 */
public class ExcelTask extends RecursiveTask<Object> {
    private static final ThreadLocal<Map<Integer, String>> LocalMapCache = ThreadLocal.withInitial(HashMap::new);
    private Workbook book;
    private String fileName;
    private Sheet sheet;
    private ExportObj exportObj = new ExportObj();
    private Map<ProcType, String> saveFileNames = new EnumMap<>(ProcType.class);

    public ExcelTask(Workbook book, Sheet sheet, String realFileName) {
        this.book = book;
        this.sheet = sheet;
        int endIndex = realFileName.lastIndexOf(".");
        String fileName = realFileName.substring(0, endIndex > 0 ? endIndex : this.fileName.length());
        this.fileName = fileName;
        ProcType.forEach(type -> saveFileNames.put(type, type.getBuilder().getSaveFileName(sheet, fileName)));
    }

    @Override
    protected StringBuilder compute() {
        try {
            long l = System.currentTimeMillis();
            File lanFile = Main.lanFile.get(fileName);
            TableObj lanFileVal = null;
            if (null != lanFile) {
                System.out.println("-->开始解析 文件:" + lanFile.getName());
                Workbook excelWorkbook = ExcelTool.getExcelWorkbook(lanFile);
                LanFileTask task = new LanFileTask(excelWorkbook, excelWorkbook.getSheetAt(0), fileName);
                lanFileVal = task.compute();
            }
            execute(lanFileVal);
            long use = System.currentTimeMillis() - l;
            System.out.println(String.format("[%20s]\t export suc spend:[%3s] ms", fileName, use));
            Main.countTime.addAndGet(use);
        } catch (Exception e) {
            System.out.println(String.format("[%20s]\t export  error", fileName));
            e.printStackTrace();
        }
        return null;
    }

    private void execute(TableObj lanFileVal) {
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

                // 构建头信息
                ProcType.forEach(type -> {
                    TypedHeadInfo info = headInfo.get(type);
                    if (info == null) return;
                    type.getBuilder().buildHeadLine(exportObj.export(type), saveFileNames.get(type), info);
                });

                int startRow = nexRow.getRowNum() + 1;
                int lastRow = Tool.getLastRow(fileName, book, sheet, lastRowNum, startRow);
                i = lastRow + 1;
                ExportObj ret = new SubTask<>(startRow, lastRow, sheet, new SubTaskFunction<ExportObj>() {
                    @Override
                    public ExportObj call(int startRow1, int lastRow1, Sheet sheet1) throws Exception {
                        ExportObj obj = new ExportObj();
                        for (; startRow1 <= lastRow1; startRow1++) {
                            try {
                                Row nexRow1 = sheet1.getRow(startRow1);
                                if (null == nexRow1) {
                                    continue;
                                }

                                Map<Integer, String> LineCache = LocalMapCache.get();
                                ProcType.forEach((ProcType procType) -> {
                                    TypedHeadInfo info = headInfo.get(procType);
                                    if (null == info) return;
                                    LineObj lineObj = Tool.parseLine(fileName, nexRow1, info, LineCache);
                                    if (null != lineObj) {
                                        Map<String, Map<HeadItem, String>> lanVal = null;
                                        if (lanFileVal != null) lanVal = lanFileVal.get(procType);
                                        Tool.buildLine(obj.export(procType), headInfo, lineObj, procType, false, lanVal);
                                    }
                                });
                                LineCache.clear();
                            } catch (Exception e) {
                                Log.outError(fileName, startRow1, e);
                            }
                        }
                        return obj;
                    }

                    @Override
                    public ExportObj newRet() {
                        return new ExportObj();
                    }
                }).compute();
                if (null != ret)
                    exportObj.merge(ret);
                ProcType.forEach(type -> {
                    StringBuilder export = exportObj.export(type);
                    TypedHeadInfo info = headInfo.get(type);
                    if (null == info) return;
                    type.getBuilder().buildEnd(export, saveFileNames.get(type), info);
                    Tool.SaveFile(type.getSavePath(), saveFileNames.get(type), type, export);
                });
            } catch (Exception e) {
                System.out.println(fileName + "\t" + sheet.getSheetName() + "\trow:" + (null == nexRow ? null : nexRow.getRowNum()));
                e.printStackTrace();
            }

        }
    }
}