package com.akoo.objs;

import org.apache.poi.ss.usermodel.Sheet;

public interface SubTaskFunction<T> {
    T call(int startRow, int lastRow, Sheet sheet) throws Exception;

    T newRet();
}