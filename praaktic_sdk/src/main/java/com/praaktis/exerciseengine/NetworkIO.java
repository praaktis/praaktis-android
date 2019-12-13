package com.praaktis.exerciseengine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;

import static com.praaktis.exerciseengine.NetworkIOConstants.POS_DATA_CRC32;
import static com.praaktis.exerciseengine.NetworkIOConstants.POS_DATA_SIZE;
import static com.praaktis.exerciseengine.NetworkIOConstants.POS_HEADER_CRC32;

final class NetworkIO {
    private static CRC32 sCrc32In = new CRC32();
    private static CRC32 sCrc32Out = new CRC32();
    private static byte [] sHeaderOut = new byte[NetworkIOConstants.HEADER_SIZE];
    private static byte [] sHeaderIn = new byte[NetworkIOConstants.HEADER_SIZE];

    private static void sendHeader(OutputStream output, byte packetType, int dataSize, long dataCrc32)
            throws IOException {
        sCrc32Out.reset();
        sHeaderOut[0] = packetType;
        Bytes.setIntAt(sHeaderOut, POS_DATA_SIZE, dataSize);
        Bytes.setUInt32At(sHeaderOut, POS_DATA_CRC32, dataCrc32);
        sCrc32Out.update(sHeaderOut, 0, sHeaderOut.length - 4);
        long hdrCrc32 = sCrc32Out.getValue();
        Bytes.setUInt32At(sHeaderOut, POS_HEADER_CRC32, hdrCrc32);
        output.write(sHeaderOut);
        output.flush();
    }

    public static void sendPacket(OutputStream output, byte packetType, byte [] data)
        throws IOException {
        sCrc32Out.reset();
        sCrc32Out.update(data);
        long dataCrc32 = sCrc32Out.getValue();
        sendHeader(output, packetType, data.length, dataCrc32);
        output.write(data);
        output.flush();
    }

    public static boolean readAll(InputStream input, byte [] buf) {
        int pos = 0;
        int toRead = buf.length;
        while (toRead > 0) {
           int bytesRead;
           try {
               bytesRead = input.read(buf, pos, toRead);
               if (bytesRead == -1)
                   return false;
               toRead -= bytesRead;
               pos += bytesRead;
           } catch (IOException ex ) {
               return false;
           }
        }
        return true;
    }

    public static class ReceivePacketResult {
        byte packetType;
        byte [] packetData;
    }

    public static boolean receivePacket(InputStream input, ReceivePacketResult rpResult) {
        sCrc32In.reset();
        if (!readAll(input, sHeaderIn))
            return false;
        sCrc32In.update(sHeaderIn, 0, sHeaderIn.length - 4);
        long hdrCrc32 = Bytes.getUInt32At(sHeaderIn, POS_HEADER_CRC32);
        if (false && hdrCrc32 != sCrc32In.getValue())
            return false;
        rpResult.packetType = sHeaderIn[0];
        int dataSize = Bytes.getIntAt(sHeaderIn, 1);
        long dataCrc32 = Bytes.getUInt32At(sHeaderIn, POS_DATA_CRC32);
        byte [] buf = new byte[dataSize];
        if (!readAll(input, buf))
            return false;
        sCrc32In.reset();
        sCrc32In.update(buf);
        if (false && sCrc32In.getValue() != dataCrc32)
            return false;
        rpResult.packetData = buf;
        return  true;
    }
}
