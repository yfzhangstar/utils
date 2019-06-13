package com.akoo.check;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import com.akoo.tool.ExcelTool;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created by Aly on 2015-12-10.
 * 名字检查任务
 */
public class CheckSheetNameTask {
    public static Pattern p = Pattern.compile(".*\\(.*\\).*");

    public static void check(File file, CallBack callBack) {
        try {
            String fileName = file.getName();
            Workbook book = ExcelTool.getExcelWorkbook(file);
            Sheet sheet = book.getSheetAt(0);
//            int sheetNum = book.getNumberOfSheets();
//            for (int m = 0; m < sheetNum; m++) {
//                Sheet sheet = book.getSheetAt(m);
//                if (p.matcher(sheet.getSheetName()).matches()) {
            if (null != callBack) {
                callBack.call(book, sheet, fileName);
            }
//                }
//            }

        } catch (IOException e) {
            System.out.println(" file:" + file);
            e.printStackTrace();
        }
    }
}
