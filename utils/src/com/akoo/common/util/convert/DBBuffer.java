package com.akoo.common.util.convert;

import org.apache.mina.core.buffer.IoBuffer;

import java.nio.charset.Charset;
import java.util.Date;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class DBBuffer implements DataHolder {
    private static final int ARRAY_MAX_SIZE = 1000;
    private static Charset charset = Charset.forName("UTF-8");
    private IoBuffer b;


    private DBBuffer() {
        b = IoBuffer.allocate(256).setAutoExpand(true);
    }

    private DBBuffer(int len) {
        b = IoBuffer.allocate(len).setAutoExpand(true);
    }


    public static DBBuffer warp(byte[] src) {
        DBBuffer db = new DBBuffer();
        db.b = IoBuffer.wrap(src);
        return db;
    }

    public static DBBuffer allocate() {
        return new DBBuffer();
    }

    public static DataHolder allocate(int len) {
        return new DBBuffer(len);
    }

    public void position(int pos) {
        b.position(pos);
    }

    public boolean hasRemaining() {
        return b.hasRemaining();
    }

    public int remaining() {
        return b.remaining();
    }

    public int position() {
        return b.position();
    }


    public void free() {
        b.free();
    }

    public byte[] toBytes() {
        int pos = b.position();
        byte[] ret = new byte[pos];
        System.arraycopy(b.array(), 0, ret, 0, pos);
        return ret;
    }

    public byte[] toBytesDirect() {
        b.shrink();
        return b.array();
    }

	
	/*-------------------------getter-------------------*/


    @Override
    public int getInt() {
        if (b.remaining() >= 4)
            return b.getInt();
        else
            return 0;
    }

    @Override
    public short getShort() {
        if (b.remaining() >= 2)
            return b.getShort();
        else
            return 0;
    }

    @Override
    public long getLong() {
        if (b.remaining() >= 8)
            return b.getLong();
        else
            return 0;
    }


    @Override
    public byte getByte() {
        if (b.hasRemaining())
            return b.get();
        else
            return 0;
    }

    @Override
    public char getChar() {
        return b.getChar();
    }


    @Override
    public boolean getBoolean() {
        return b.hasRemaining() && (b.get() == 1);
    }

    @Override
    public float getFloat() {
        return b.getFloat();
    }


    @Override
    public double getDouble() {
        return b.getDouble();
    }

    @Override
    public String getString() {
        if (b.remaining() >= 4) {
            byte[] temp = new byte[b.getInt()];
            b.get(temp);
            return new String(temp, charset);
        } else
            return "";

    }

    @Override
    public Date getDate() {
        if (b.hasRemaining()) {
            if (b.remaining() >= 8)
                return new Date(b.getLong());
            else {
                return null;
            }
        } else
            return null;

    }


    @Override
    public int[] getInts() {
        if (b.remaining() >= 4) {
            int len = Math.max(0, Math.min(b.getInt(), ARRAY_MAX_SIZE));
            int[] ret = new int[len];
            for (int i = 0; i < len; i++) {
                if (b.hasRemaining()) {
                    ret[i] = b.getInt();
                } else
                    ret[i] = 0;
            }
            return ret;
        } else {
            return new int[]{};
        }
    }

    @Override
    public short[] getShorts() {
        if (b.remaining() >= 4) {
            int len = Math.max(0, Math.min(b.getInt(), ARRAY_MAX_SIZE));
            short[] ret = new short[len];
            for (int i = 0; i < len; i++) {
                if (b.hasRemaining()) {
                    ret[i] = b.getShort();
                } else
                    ret[i] = 0;
            }
            return ret;
        } else {
            return new short[]{};
        }
    }

    @Override
    public char[] getChars() {
        if (b.remaining() >= 4) {
            int len = Math.max(0, Math.min(b.getInt(), ARRAY_MAX_SIZE));
            char[] ret = new char[len];
            for (int i = 0; i < len; i++) {
                if (b.hasRemaining()) {
                    ret[i] = b.getChar();
                } else
                    ret[i] = 0;
            }
            return ret;
        } else {
            return new char[]{};
        }
    }

    @Override
    public byte[] getBytes() {
        if (b.remaining() >= 4) {
            byte[] temp = new byte[b.getInt()];
            b.get(temp);
            return temp;
        } else
            return null;
    }

    @Override
    public long[] getLongs() {
        if (b.remaining() >= 4) {
            int len = Math.max(0, Math.min(b.getInt(), ARRAY_MAX_SIZE));
            long[] ret = new long[len];
            for (int i = 0; i < len; i++) {
                if (b.hasRemaining()) {
                    ret[i] = b.getLong();
                } else
                    ret[i] = 0;
            }
            return ret;
        } else {
            return new long[]{};
        }


    }


    @Override
    public boolean[] getBooleans() {
        if (b.remaining() >= 4) {
            int len = Math.max(0, Math.min(b.getInt(), ARRAY_MAX_SIZE));
            boolean[] ret = new boolean[len];
            for (int i = 0; i < len; i++) {
                ret[i] = b.hasRemaining() && getBoolean();
            }
            return ret;
        } else {
            return new boolean[]{};
        }
    }


    @Override
    public float[] getFloats() {
        if (b.remaining() >= 4) {
            int len = Math.max(0, Math.min(b.getInt(), ARRAY_MAX_SIZE));
            float[] ret = new float[len];
            for (int i = 0; i < len; i++) {
                if (b.hasRemaining()) {
                    ret[i] = b.getFloat();
                } else
                    ret[i] = 0;
            }
            return ret;
        } else {
            return new float[]{};
        }
    }

    @Override
    public double[] getDoubles() {
        if (b.remaining() >= 4) {
            int len = Math.max(0, Math.min(b.getInt(), ARRAY_MAX_SIZE));
            double[] ret = new double[len];
            for (int i = 0; i < len; i++) {
                if (b.hasRemaining()) {
                    ret[i] = b.getDouble();
                } else
                    ret[i] = 0;
            }
            return ret;
        } else {
            return new double[]{};
        }
    }

    @Override
    public String[] getStrs() {
        if (b.remaining() >= 4) {
            int len = Math.max(0, Math.min(b.getInt(), ARRAY_MAX_SIZE));
            String[] ret = new String[len];
            for (int i = 0; i < len; i++) {
                if (b.hasRemaining()) {
                    ret[i] = getString();
                } else
                    ret[i] = null;
            }
            return ret;
        } else {
            return new String[]{};
        }
    }


    /*---------------------------------putter----------------------------------*/
    @Override
    public void putInt(int in) {
        b.putInt(in);
    }


    @Override
    public void putShort(short in) {
        b.putShort(in);
    }


    @Override
    public void putLong(long in) {
        b.putLong(in);
    }

    @Override
    public void putByte(byte in) {
        b.put(in);
    }

    @Override
    public void putChar(char in) {
        b.putChar(in);
    }


    @Override
    public void putBoolean(boolean in) {
        b.put((byte) (in ? 1 : 0));
    }

    @Override
    public void putFloat(float in) {
        b.putFloat(in);
    }

    @Override
    public void putDouble(double in) {
        b.putDouble(in);
    }

    @Override
    public void putString(String in) {
        byte[] inarray = in.getBytes(charset);
        b.putInt(inarray.length);
        b.put(inarray);
    }

    @Override
    public void putDate(Date d) {
        b.putLong(d.getTime());
    }


    @Override
    public void putInts(int[] ins) {
        int len = Math.min(ins.length, ARRAY_MAX_SIZE);
        b.putInt(len);
        for (int i = 0; i < len; i++) {
            b.putInt(ins[i]);
        }
    }

    @Override
    public void putShorts(short[] shorts) {
        int len = Math.min(shorts.length, ARRAY_MAX_SIZE);
        b.putInt(len);
        for (int i = 0; i < len; i++) {
            b.putShort(shorts[i]);
        }
    }

    @Override
    public void putLongs(long[] longs) {
        int len = Math.min(longs.length, ARRAY_MAX_SIZE);
        b.putInt(len);
        for (int i = 0; i < len; i++) {
            b.putLong(longs[i]);
        }
    }


    @Override
    public void putChars(char[] chars) {
        int len = Math.min(chars.length, ARRAY_MAX_SIZE);
        b.putInt(len);
        for (int i = 0; i < len; i++) {
            b.putChar(chars[i]);
        }
    }

    @Override
    public void putBytes(byte[] bytes) {
        b.putInt(bytes.length);
        b.put(bytes);
    }


    @Override
    public void putBooleans(boolean[] bools) {
        int len = Math.min(bools.length, ARRAY_MAX_SIZE);
        b.putInt(len);
        for (int i = 0; i < len; i++) {
            putBoolean(bools[i]);
        }
    }

    @Override
    public void putFloats(float[] floats) {
        int len = Math.min(floats.length, ARRAY_MAX_SIZE);
        b.putInt(len);
        for (int i = 0; i < len; i++) {
            b.putFloat(floats[i]);
        }
    }

    @Override
    public void putDoubles(double[] doubles) {
        int len = Math.min(doubles.length, ARRAY_MAX_SIZE);
        b.putInt(len);
        for (int i = 0; i < len; i++) {
            b.putDouble(doubles[i]);
        }

    }

    @Override
    public void putStrs(String[] strs) {
        int len = Math.min(strs.length, ARRAY_MAX_SIZE);
        b.putInt(len);
        for (int i = 0; i < len; i++) {
            putString(strs[i]);
        }
    }

}
