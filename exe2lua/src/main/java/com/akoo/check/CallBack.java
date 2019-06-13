package com.akoo.check;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created by Aly on 2015-12-10.
 */
public interface CallBack {
    void call(Workbook book, Sheet sheet, String fileName);
}
