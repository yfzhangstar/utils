package com.akoo.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings({"WeakerAccess", "unused"})
public class NDateUtil {
    private static final int START_YEAR = 1970;
    private static final int[][][] DAY_ARRAY = new int[60][][];
    private static final int[][][] WEEK_ARRAY = new int[60][][];
    public static ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmss"));
    public static ThreadLocal<SimpleDateFormat> sdf2 = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    static {
        for (int year = 0; year < DAY_ARRAY.length; year++) {
            DAY_ARRAY[year] = new int[12][];
            WEEK_ARRAY[year] = new int[12][];
            for (int month = 0; month < DAY_ARRAY[year].length; month++) {
                switch (month) {
                    case 0:
                    case 2:
                    case 4:
                    case 6:
                    case 7:
                    case 9:
                    case 11:
                        DAY_ARRAY[year][month] = new int[32];
                        WEEK_ARRAY[year][month] = new int[32];
                        break;
                    case 1:
                        int tempY = year + START_YEAR;
                        if (tempY % 400 == 0 || (tempY % 100 != 0 && tempY % 4 == 0)) {
                            DAY_ARRAY[year][month] = new int[30];
                            WEEK_ARRAY[year][month] = new int[30];
                        } else {
                            DAY_ARRAY[year][month] = new int[29];
                            WEEK_ARRAY[year][month] = new int[29];
                        }
                        break;
                    default: {
                        DAY_ARRAY[year][month] = new int[31];
                        WEEK_ARRAY[year][month] = new int[31];
                    }

                    break;
                }
            }
        }
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, START_YEAR);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        int ix = 0;

        int wdix = c.get(Calendar.DAY_OF_WEEK);
        int wix = 0;
        for (int year = 0; year < DAY_ARRAY.length; year++) {
            for (int month = 0; month < DAY_ARRAY[year].length; month++) {
                for (int date = 1; date < DAY_ARRAY[year][month].length; date++) {
                    DAY_ARRAY[year][month][date] = ix++;
                    WEEK_ARRAY[year][month][date] = wix;
                    wdix++;
                    if (wdix > 7) {
                        wdix = 0;
                        wix++;
                    }
                }
            }
        }
    }


    public static int getWeek(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return WEEK_ARRAY[c.get(Calendar.YEAR) - START_YEAR][c.get(Calendar.MONTH)][c.get(Calendar.DATE)];
    }

    public static int getYear(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c.get(Calendar.YEAR);
    }

    public static int compareTwoDate(Date d1, Date d2) {
    	int day1 = getDay(d1);
    	int day2 = getDay(d2);
    	return day2 - day1;
    }
    
    public static int getDay(Date d){
    	try{
    		Calendar c = Calendar.getInstance();
            c.setTime(d);
            int year  = c.get(Calendar.YEAR) - START_YEAR;
            if(year < 0 )
            	return 0;
            return DAY_ARRAY[year][c.get(Calendar.MONTH)][c.get(Calendar.DATE)];
    	}catch(Exception e){
    		e.printStackTrace();
    		return 0;
    	}
    }
}
