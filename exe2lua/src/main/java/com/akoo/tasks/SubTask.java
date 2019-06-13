package com.akoo.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import com.akoo.objs.SubTaskFunction;
import org.apache.poi.ss.usermodel.Sheet;

public class SubTask<T extends Mergable> extends RecursiveTask<T> {
    private int startRow, lastRow;

    private Sheet sheet;
    private SubTaskFunction<T> function;

    public SubTask(int startRow, int lastRow, Sheet sheet, SubTaskFunction<T> function) {
        this.startRow = startRow;
        this.lastRow = lastRow;
        this.sheet = sheet;
        this.function = function;
    }


    @Override
    protected T compute() {
        try {
//            return getRet1();
            return getRet2();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private T getRet2() throws Exception {
        if (lastRow - startRow > 100) {
            int mid = (startRow + this.lastRow) / 2;
            SubTask<T> left = new SubTask<>(startRow, mid, sheet, function);
            SubTask<T> right = new SubTask<>(mid + 1, lastRow, sheet, function);
            right.fork();
            T leftR = left.compute();
            if (leftR == null) {
                leftR = function.newRet();
            }
            T rightR = right.join();
            if (null != rightR)
                leftR.merge(rightR);
            return leftR;
        } else {
            try {
                return function.call(startRow, lastRow, sheet);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private T getRet1() throws Exception {
        int step = 100;
        int start = startRow;
        List<SubTask<T>> allTask = new ArrayList<>();

        while (true) {
            if (start > lastRow) {
                break;
            }
            int nStart = start + step;
            if (nStart >= lastRow) {
                return function.call(startRow, lastRow, sheet);
            } else {
                SubTask<T> subTask = new SubTask<>(start, nStart - 1, sheet, function);
                subTask.fork();
                allTask.add(subTask);
            }
            start = nStart;
        }
        T allRet = function.newRet();
        for (SubTask<T> task : allTask) {
            T join = task.join();
            allRet.merge(join);
        }

        return allRet;
    }
}