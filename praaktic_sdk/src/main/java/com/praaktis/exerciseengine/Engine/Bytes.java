package com.praaktis.exerciseengine.Engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This code duplicates ByteBuffer functionality
 * but is faster
 */
final class Bytes {

    /**
     * <p>This method used to get integer located at [pos, pos + 3] of a byte array</p>
     * @param buf byte array
     * @param pos int pos
     * @return int
     */
    static int getIntAt(byte[] buf, int pos) {
        return (buf[pos] & 255)
               | ((buf[pos + 1] & 255) << 8)
               | ((buf[pos + 2] & 255) << 16)
               | ((buf[pos + 3] & 255) << 24);
    }

    /**
     * <p>This method used to get unsigned integer located at [pos, pos + 3] of a byte array</p>
     * @param buf byte array
     * @param pos int pos
     * @return long
     */
    static long getUInt32At(byte[] buf, int pos) {
        return (buf[pos] & 255)
                | ((buf[pos + 1] & 255) << 8)
                | ((buf[pos + 2] & 255) << 16)
                | ((buf[pos + 3] & 255) << 24);
    }

    /**
     * <p>This method used to set integer to be located at [pos, pos + 3] of a byte array</p>
     * @param buf: byte array
     * @param pos: int pos
     */
    static void setIntAt(byte[] buf, int pos, int n) {
        buf[pos] = (byte) (n & 255);
        buf[pos + 1] = (byte) ((n >> 8) & 255);
        buf[pos + 2] = (byte) ((n >> 16) & 255);
        buf[pos + 3] = (byte) ((n >> 24) & 255);
    }
    /**
     * <p>This method used to set unsigned integer to be located at [pos, pos + 3] of a byte array</p>
     * @param buf byte array
     * @param pos int pos
     */
    static void setUInt32At(byte[] buf, int pos, long n) {
        buf[pos] = (byte) (n & 255);
        buf[pos + 1] = (byte) ((n >> 8) & 255);
        buf[pos + 2] = (byte) ((n >> 16) & 255);
        buf[pos + 3] = (byte) ((n >> 24) & 255);
    }

    /**
     * <p>This method used to get float located from pos of a byte array</p>
     * @param buf byte array
     * @param pos int pos
     * @return float
     */
    static float getFloatAt(byte[] buf, int pos) {
        if (buf.length < pos + 4) return 0;
        return ByteBuffer.wrap(buf, pos, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

}

