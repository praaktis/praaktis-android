package com.praaktis.exerciseengine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// This code duplicates ByteBuffer functionality
// but is faster
//
final class Bytes {

    public static int getIntAt(byte [] buf, int pos) {
        return (buf[pos] & 255)
               | ((buf[pos + 1] & 255) << 8)
               | ((buf[pos + 2] & 255) << 16)
               | ((buf[pos + 3] & 255) << 24);
    }

    public static long getUInt32At(byte [] buf, int pos) {
        return (buf[pos] & 255)
                | ((buf[pos + 1] & 255) << 8)
                | ((buf[pos + 2] & 255) << 16)
                | ((buf[pos + 3] & 255) << 24);
    }

    public static void setIntAt(byte [] buf, int pos, int n) {
        buf[pos] = (byte) (n & 255);
        buf[pos + 1] = (byte) ((n >> 8) & 255);
        buf[pos + 2] = (byte) ((n >> 16) & 255);
        buf[pos + 3] = (byte) ((n >> 24) & 255);
    }

    public static void setUInt32At(byte [] buf, int pos, long n) {
        buf[pos] = (byte) (n & 255);
        buf[pos + 1] = (byte) ((n >> 8) & 255);
        buf[pos + 2] = (byte) ((n >> 16) & 255);
        buf[pos + 3] = (byte) ((n >> 24) & 255);
    }

    public static float getFloatAt(byte [] buf, int pos) {
        if (buf.length < pos + 4) return 0;
        return ByteBuffer.wrap(buf, pos, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

}

