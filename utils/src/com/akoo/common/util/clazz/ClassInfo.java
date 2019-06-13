/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.akoo.common.util.clazz;
import java.io.IOException;
import java.io.InputStream;

/**
 * A Java class parser to make a {ClassVisitor} visit an existing class.
 * This class parses a byte array conforming to the Java class file format and
 * calls the appropriate visit methods of a given class visitor for each field,
 * method and bytecode instruction encountered.
 *
 * @author Eric Bruneton
 * @author Eugene Kuleshov
 */
public class ClassInfo {

    /**
     * Flag to skip method code. If this class is set <code>CODE</code>
     * attribute won't be visited. This can be used, for example, to retrieve
     * annotations for methods and method parameters.
     */
    public static final int SKIP_CODE = 1;
    /**
     * Flag to skip the debug information in the class. If this flag is set the
     * debug information of the class is not visited, i.e. the
     * {MethodVisitor#visitLocalVariable visitLocalVariable} and
     * {MethodVisitor#visitLineNumber visitLineNumber} methods will not be
     * called.
     */
    public static final int SKIP_DEBUG = 2;
    /**
     * Flag to skip the stack map frames in the class. If this flag is set the
     * stack map frames of the class is not visited, i.e. the
     * {MethodVisitor#visitFrame visitFrame} method will not be called.
     * This flag is useful when the {@link Types#COMPUTE_FRAMES} option is
     * used: it avoids visiting frames that will be ignored and recomputed from
     * scratch in the class writer.
     */
    public static final int SKIP_FRAMES = 4;
    /**
     * Flag to expand the stack map frames. By default stack map frames are
     * visited in their original format (i.e. "expanded" for classes whose
     * version is less than V1_6, and "compressed" for the other classes). If
     * this flag is set, stack map frames are always visited in expanded format
     * (this option adds a decompression/recompression step in ClassInfo and
     * Types which degrades performances quite a lot).
     */
    public static final int EXPAND_FRAMES = 8;
    /**
     * True to enable signatures support.
     */
    static final boolean SIGNATURES = true;
    /**
     * True to enable annotations support.
     */
    static final boolean ANNOTATIONS = true;
    /**
     * True to enable stack map frames support.
     */
    static final boolean FRAMES = true;
    /**
     * True to enable bytecode writing support.
     */
    static final boolean WRITER = true;
    /**
     * True to enable JSR_W and GOTO_W support.
     */
    static final boolean RESIZE = true;
    /**
     * The class to be parsed. <i>The content of this array must not be
     * modified. This field is intended for {Attribute} sub classes, and
     * is normally not needed by class generators or adapters.</i>
     */
    public final byte[] b;
    /**
     * Start index of the class header information (access, name...) in
     * {@link #b b}.
     */
    public final int header;
    /**
     * The start index of each constant pool item in {@link #b b}, plus one. The
     * one byte offset skips the constant pool item tag that indicates its type.
     */
    private final int[] items;
    /**
     * The String objects corresponding to the CONSTANT_Utf8 items. This cache
     * avoids multiple parsing of a given CONSTANT_Utf8 constant pool item,
     * which GREATLY improves performances (by a factor 2 to 3). This caching
     * strategy could be extended to all constant pool items, but its benefit
     * would not be so great for these items (because they are much less
     * expensive to parse than CONSTANT_Utf8 items).
     */
    private final String[] strings;
    /**
     * Maximum length of the strings contained in the constant pool of the
     * class.
     */
    private final int maxStringLength;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs a new {@link ClassInfo} object.
     *
     * @param b the bytecode of the class to be read.
     */
    public ClassInfo(final byte[] b) {
        this(b, 0, b.length);
    }

    /**
     * Constructs a new {@link ClassInfo} object.
     *
     * @param b   the bytecode of the class to be read.
     * @param off the start offset of the class center.
     * @param len the length of the class center.
     */
    public ClassInfo(final byte[] b, final int off, final int len) {
        this.b = b;
        // checks the class version
        /* SPRING PATCH: REMOVED FOR FORWARD COMPATIBILITY WITH JDK 9
        if (readShort(off + 6) > Opcodes.V1_8) {
            throw new IllegalArgumentException();
        }
		*/
        // parses the constant pool
        items = new int[readUnsignedShort(off + 8)];
        int n = items.length;
        strings = new String[n];
        int max = 0;
        int index = off + 10;
        for (int i = 1; i < n; ++i) {
            items[i] = index + 1;
            int size;
            switch (b[index]) {
                case Types.FIELD:
                case Types.METH:
                case Types.IMETH:
                case Types.INT:
                case Types.FLOAT:
                case Types.NAME_TYPE:
                case Types.INDY:
                    size = 5;
                    break;
                case Types.LONG:
                case Types.DOUBLE:
                    size = 9;
                    ++i;
                    break;
                case Types.UTF8:
                    size = 3 + readUnsignedShort(index + 1);
                    if (size > max) {
                        max = size;
                    }
                    break;
                case Types.HANDLE:
                    size = 4;
                    break;
                // case Types.CLASS:
                // case Types.STR:
                // case Types.MTYPE
                default:
                    size = 3;
                    break;
            }
            index += size;
        }
        maxStringLength = max;
        // the class header information starts just after the constant pool
        header = index;
    }

    /**
     * Constructs a new {@link ClassInfo} object.
     *
     * @param is an input stream from which to read the class.
     * @throws IOException if a problem occurs during reading.
     */
    public ClassInfo(final InputStream is) throws IOException {
        this(readClass(is, false));
    }


    /**
     * Constructs a new {@link ClassInfo} object.
     *
     * @param name the binary qualified name of the class to be read.
     * @throws IOException if an exception occurs during reading.
     */
    public ClassInfo(final String name) throws IOException {
        this(readClass(
                ClassLoader.getSystemResourceAsStream(name.replace('.', '/')
                        + ".class"), true));
    }

    /**
     * Reads the bytecode of a class.
     *
     * @param is    an input stream from which to read the class.
     * @param close true to close the input stream after reading.
     * @return the bytecode read from the given input stream.
     * @throws IOException if a problem occurs during reading.
     */
    public static byte[] readClass(final InputStream is, boolean close)
            throws IOException {
        if (is == null) {
            throw new IOException("Class not found");
        }
        try {
            byte[] b = new byte[is.available()];
            int len = 0;
            while (true) {
                int n = is.read(b, len, b.length - len);
                if (n == -1) {
                    if (len < b.length) {
                        byte[] c = new byte[len];
                        System.arraycopy(b, 0, c, 0, len);
                        b = c;
                    }
                    return b;
                }
                len += n;
                if (len == b.length) {
                    int last = is.read();
                    if (last < 0) {
                        return b;
                    }
                    byte[] c = new byte[b.length + 1000];
                    System.arraycopy(b, 0, c, 0, len);
                    c[len++] = (byte) last;
                    b = c;
                }
            }
        } finally {
            if (close) {
                is.close();
            }
        }
    }

    /**
     * Returns the class's access flags (see {Opcodes}). This value may
     * not reflect Deprecated and Synthetic flags when bytecode is before 1.5
     * and those flags are represented by attributes.
     *
     * @return the class access flags
     */
    public int getAccess() {
        return readUnsignedShort(header);
    }

    public String getClassName() {
        return readClass(header + 2, new char[maxStringLength]);
    }

    public String getSuperName() {
        return readClass(header + 4, new char[maxStringLength]);
    }

    public String[] getInterfaces() {
        int index = header + 6;
        int n = readUnsignedShort(index);
        String[] interfaces = new String[n];
        if (n > 0) {
            char[] buf = new char[maxStringLength];
            for (int i = 0; i < n; ++i) {
                index += 2;
                interfaces[i] = readClass(index, buf);
            }
        }
        return interfaces;
    }

    /**
     * Returns the start index of the attribute_info structure of this class.
     *
     * @return the start index of the attribute_info structure of this class.
     */
    private int getAttributes() {
        // skips the header
        int u = header + 8 + readUnsignedShort(header + 6) * 2;
        // skips fields and methods
        for (int i = readUnsignedShort(u); i > 0; --i) {
            for (int j = readUnsignedShort(u + 8); j > 0; --j) {
                u += 6 + readInt(u + 12);
            }
            u += 8;
        }
        u += 2;
        for (int i = readUnsignedShort(u); i > 0; --i) {
            for (int j = readUnsignedShort(u + 8); j > 0; --j) {
                u += 6 + readInt(u + 12);
            }
            u += 8;
        }
        // the attribute_info structure starts just after the methods
        return u + 2;
    }

    // ------------------------------------------------------------------------
    // Utility methods: low level parsing
    // ------------------------------------------------------------------------

    /**
     * Returns the number of constant pool items in {@link #b b}.
     *
     * @return the number of constant pool items in {@link #b b}.
     */
    public int getItemCount() {
        return items.length;
    }

    /**
     * Returns the start index of the constant pool item in {@link #b b}, plus
     * one. <i>This method is intended for { Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     *
     * @param item the index a constant pool item.
     * @return the start index of the constant pool item in {@link #b b}, plus
     * one.
     */
    public int getItem(final int item) {
        return items[item];
    }

    /**
     * Returns the maximum length of the strings contained in the constant pool
     * of the class.
     *
     * @return the maximum length of the strings contained in the constant pool
     * of the class.
     */
    public int getMaxStringLength() {
        return maxStringLength;
    }

    /**
     * Reads an unsigned short value in {@link #b b}. <i>This method is intended
     * for { Attribute} sub classes, and is normally not needed by class
     * generators or adapters.</i>
     *
     * @param index the start index of the value to be read in {@link #b b}.
     * @return the read value.
     */
    public int readUnsignedShort(final int index) {
        byte[] b = this.b;
        return ((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF);
    }

    /**
     * Reads a signed int value in {@link #b b}. <i>This method is intended for
     * { Attribute} sub classes, and is normally not needed by class
     * generators or adapters.</i>
     *
     * @param index the start index of the value to be read in {@link #b b}.
     * @return the read value.
     */
    public int readInt(final int index) {
        byte[] b = this.b;
        return ((b[index] & 0xFF) << 24) | ((b[index + 1] & 0xFF) << 16)
                | ((b[index + 2] & 0xFF) << 8) | (b[index + 3] & 0xFF);
    }

    /**
     * Reads an UTF8 string constant pool item in {@link #b b}. <i>This method
     * is intended for { Attribute} sub classes, and is normally not needed
     * by class generators or adapters.</i>
     *
     * @param index the start index of an unsigned short value in {@link #b b},
     *              whose value is the index of an UTF8 constant pool item.
     * @param buf   buffer to be used to read the item. This buffer must be
     *              sufficiently large. It is not automatically resized.
     * @return the String corresponding to the specified UTF8 item.
     */
    public String readUTF8(int index, final char[] buf) {
        int item = readUnsignedShort(index);
        if (index == 0 || item == 0) {
            return null;
        }
        String s = strings[item];
        if (s != null) {
            return s;
        }
        index = items[item];
        return strings[item] = readUTF(index + 2, readUnsignedShort(index), buf);
    }

    /**
     * Reads UTF8 string in {@link #b b}.
     *
     * @param index  start offset of the UTF8 string to be read.
     * @param utfLen length of the UTF8 string to be read.
     * @param buf    buffer to be used to read the string. This buffer must be
     *               sufficiently large. It is not automatically resized.
     * @return the String corresponding to the specified UTF8 string.
     */
    private String readUTF(int index, final int utfLen, final char[] buf) {
        int endIndex = index + utfLen;
        byte[] b = this.b;
        int strLen = 0;
        int c;
        int st = 0;
        char cc = 0;
        while (index < endIndex) {
            c = b[index++];
            switch (st) {
                case 0:
                    c = c & 0xFF;
                    if (c < 0x80) { // 0xxxxxxx
                        buf[strLen++] = (char) c;
                    } else if (c < 0xE0 && c > 0xBF) { // 110x xxxx 10xx xxxx
                        cc = (char) (c & 0x1F);
                        st = 1;
                    } else { // 1110 xxxx 10xx xxxx 10xx xxxx
                        cc = (char) (c & 0x0F);
                        st = 2;
                    }
                    break;

                case 1: // byte 2 of 2-byte char or byte 3 of 3-byte char
                    buf[strLen++] = (char) ((cc << 6) | (c & 0x3F));
                    st = 0;
                    break;

                case 2: // byte 2 of 3-byte char
                    cc = (char) ((cc << 6) | (c & 0x3F));
                    st = 1;
                    break;
            }
        }
        return new String(buf, 0, strLen);
    }

    /**
     * Reads a class constant pool item in {@link #b b}. <i>This method is
     * intended for {Attribute} sub classes, and is normally not needed by
     * class generators or adapters.</i>
     *
     * @param index the start index of an unsigned short value in {@link #b b},
     *              whose value is the index of a class constant pool item.
     * @param buf   buffer to be used to read the item. This buffer must be
     *              sufficiently large. It is not automatically resized.
     * @return the String corresponding to the specified class item.
     */
    public String readClass(final int index, final char[] buf) {
        // computes the start index of the CONSTANT_Class item in b
        // and reads the CONSTANT_Utf8 item designated by
        // the first two bytes of this CONSTANT_Class item
        return readUTF8(items[readUnsignedShort(index)], buf);
    }
}
