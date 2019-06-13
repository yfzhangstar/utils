package com.akoo.tool;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * aly @ 16-11-12.
 */
public class ExcelTool {
    public static Workbook getExcelWorkbook(File file) throws IOException {
        Workbook book;
        FileInputStream fis = null;
        try {
            if (!file.exists()) {
                throw new RuntimeException("文件不存在");
            }
            fis = new FileInputStream(file);
            book = WorkbookFactory.create(fis);
        } catch (Exception e) {
            System.out.println(file.getAbsolutePath());
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        fis.close();
        return book;
    }

    public static String getCellVal(String fileName, Workbook book, Row row, int ix) {
        Cell cell = row.getCell(ix);
        if (null == cell)
            return null;
        return getCellStringVal(fileName, book, cell).trim();
    }

    public static String getCellStringVal(String fileName, final Workbook book, final Cell cell) {
        final int type = cell.getCellType();
        switch (type) {
            case Cell.CELL_TYPE_NUMERIC: {
                final double num = cell.getNumericCellValue();
                if (num - (int) num == 0) {
                    return Integer.toString((int) num);
                } else if(num - (long)num == 0){
                	return Long.toString((long)num);
                }else{
                    return Double.toString(num);
                }
            }
            case Cell.CELL_TYPE_STRING: {
                return cell.getStringCellValue();
            }
            case Cell.CELL_TYPE_BOOLEAN: {
                return String.valueOf(cell.getBooleanCellValue());
            }
            case Cell.CELL_TYPE_FORMULA: {
                final FormulaEvaluator fe = book.getCreationHelper().createFormulaEvaluator();
                try {
                    final CellValue cv = fe.evaluate(cell);
                    String s;
                    if (cv.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                        s = String.valueOf(cv.getBooleanValue());
                    } else if (cv.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        s = String.valueOf((int) cv.getNumberValue());
                    } else {
                        s = String.valueOf(cv.getStringValue());
                    }
                    return s;
                } catch (Exception e) {
                    System.out.println(" Cell value ref error:FileName:" + fileName + " ,cell->" + cell);
                    return "#REF";
                }
            }
            case Cell.CELL_TYPE_BLANK: {
                break;
            }
        }
        try {
            return cell.getStringCellValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static void main(String[] args) {
		System.out.println(2.0D - (long)2.0D);
		System.out.println(Long.MAX_VALUE);
	}
}
