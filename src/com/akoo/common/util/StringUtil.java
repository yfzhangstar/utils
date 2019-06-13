package com.akoo.common.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Administrator
 */
public class StringUtil {

    /**
     * 将String转换为Integer
     */
    public static Integer parseInt(String s) {
        Integer n = 0;
        try {
            n = Integer.parseInt(s);
        } catch (Exception ignored) {

        }
        return n;
    }

    /**
     * 将String转换为Float
     */
    public static Float parseFloat(String s) {
        Float f = 0.0f;
        try {
            f = Float.parseFloat(s);
        } catch (Exception ignored) {

        }
        return f;
    }

    /**
     * 将String转换为Double
     */
    public static double parseDouble(String s) {
        Double d = 0.0;
        try {
            d = Double.parseDouble(s);
        } catch (Exception ignored) {

        }
        return d;
    }

    /**
     * 字符串转换为日期 更多日期处理的请参�?�：DateUtil.java
     */
    public static Date strToDate(String s) {
        Date date = new Date();
        ParsePosition pos = new ParsePosition(0);
        try {
            if (s.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                date = sdf.parse(s, pos);
            }
        } catch (Exception ignored) {

        }
        return date;
    }

    public static String join(Object... os) {
        StringBuilder sb = new StringBuilder();
        for (Object o : os) {
            sb.append(o);
        }
        return sb.toString();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.equals("");
    }

    public static boolean isNotEmpty(String str) {
        return !(str == null || str.equals(""));
    }

    public static boolean verifyEmailFormat(String email) {
        if (email == null)
            return false;
        if (email.length() > 64)
            return false;
        String regex = "[a-z0-9_-]{0,}@(([a-z0-9_-]){1,}\\.){1,3}[a-z0-9_-]{1,}";
        return match(regex, email);
    }

    public static boolean verifyPhoneNumberFormat(String phoneNumber) {
        if (phoneNumber == null)
            return false;
        String regex = "[0-9]{1,30}";
        return match(regex, phoneNumber);
    }

    public static boolean verifyAccountNameFormat(String accountName) {
        if (accountName == null)
            return false;
        String regex = "[a-zA-Z]{1}[a-zA-Z0-9_]{5,30}";
        return match(regex, accountName);
    }

    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static boolean verifyPasswordFormat(String password) {
        if (password == null)
            return false;
        String regex = "[a-zA-Z0-9]{6,15}";
        return match(regex, password);
    }
    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String toSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            c[i] = toSBC(c[i]);
        }
        return new String(c);
    }

    /**
     * 半角转全角
     *
     * @return 全角字符串.
     */
    public static char toSBC(char c) {
        if (c == ' ') {
            c = '\u3000';
        } else if (c < '\177') {
            c = (char) (c + 65248);
        }
        return c;
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @return 半角字符
     */
    public static char toDBC(char c) {
        if (c == '\u3000') {
            c = ' ';
        } else if (c > '\uFF00' && c < '\uFF5F') {
            c = (char) (c - 65248);
        }
        return c;
    }
}
