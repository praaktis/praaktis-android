package com.praaktis.exerciseengine.Engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;

import static com.praaktis.exerciseengine.Engine.NetworkIOConstants.POS_DATA_CRC32;
import static com.praaktis.exerciseengine.Engine.NetworkIOConstants.POS_DATA_SIZE;
import static com.praaktis.exerciseengine.Engine.NetworkIOConstants.POS_HEADER_CRC32;

/**
 * Networking instructions and protocols are implemented here
 */
final class NetworkIO {
    private static CRC32 sCrc32In = new CRC32();
    private static CRC32 sCrc32Out = new CRC32();
    private static byte[] sHeaderOut = new byte[NetworkIOConstants.HEADER_SIZE];
    private static byte[] sHeaderIn = new byte[NetworkIOConstants.HEADER_SIZE];

    /**
     * A method for sending header of a package
     * @param output output stream for the header to be sent
     * @param packetType type of the package, can take values defined at {@link NetworkIOConstants}
     * @param dataSize size of the package to be sent
     * @param dataCrc32 Crc32 of the package
     * @throws IOException
     */
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

    /**
     * A method for sending a package
     * @param output {@link OutputStream} for the package to be sent
     * @param packetType type of the package, can take values defined at {@link NetworkIOConstants}
     * @param data byte array to be sent
     * @throws IOException
     */
    static void sendPacket(OutputStream output, byte packetType, byte[] data)
            throws IOException {
//        if (Globals.state == EngineState.EXERCISE) {
//            packetType = 12;
//        }
        sCrc32Out.reset();
        sCrc32Out.update(data);
        long dataCrc32 = sCrc32Out.getValue();
        sendHeader(output, packetType, data.length, dataCrc32);

        output.write(data);
        output.flush();
    }

    /**
     * A method to fully read a buffer from input
     * @param input {@link InputStream} for the data to be read
     * @param buf buffer for data
     * @return true if the date is read fully, false otherwise
     */
    private static boolean readAll(InputStream input, byte[] buf) {
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
            } catch (IOException ex) {
                return false;
            }
        }
        return true;
    }

    /**
     * Data structure describing a packet received from pose-estimation server,
     * contains type of the packet and the packet itself
     */
    public static class ReceivePacketResult {
        byte packetType;
        byte[] packetData;
    }

    /**
     * A method to receive bytes from pose-estimation server
     * @param input {@link InputStream}
     * @param rpResult {@link ReceivePacketResult} instance
     * @return true if data is read successfully, false otherwise
     */
    static boolean receivePacket(InputStream input, ReceivePacketResult rpResult) {
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
        byte[] buf = new byte[dataSize];
        if (!readAll(input, buf))
            return false;
        sCrc32In.reset();
        sCrc32In.update(buf);
        if (false && sCrc32In.getValue() != dataCrc32)
            return false;
        rpResult.packetData = buf;
        return true;
    }
}
