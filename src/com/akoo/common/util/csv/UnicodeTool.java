package com.akoo.common.util.csv;

/*
 * http://www.unicode.org/unicode/faq/utf_bom.html
 * BOMs in byte length ordering:
 * 00 00 FE FF    = UTF-32, big-endian
 * FF FE 00 00    = UTF-32, little-endian
 * EF BB BF       = UTF-8,
 * FE FF          = UTF-16, big-endian
 * FF FE          = UTF-16, little-endian
 */
public final class UnicodeTool {
    public static byte[] getBom(String charset) {
        switch (charset.toUpperCase()) {
            case "UTF-32BE":
                return new byte[]{0x00, 0x00, (byte) 0xFE, (byte) 0xFF};
            case "UTF-32LE":
                return new byte[]{(byte) 0xFF, (byte) 0xFE, 0x00, 0x00};
            case "UTF-8":
                return new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            case "UTF-16BE":
                return new byte[]{(byte) 0xFE, (byte) 0xFF};
            case "UTF-16LE":
                return new byte[]{(byte) 0xFF, (byte) 0xFE};
        }
        return new byte[0];
    }

}
