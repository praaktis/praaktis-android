package com.praaktis.exerciseengine.RawPlayer;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

class H264Stream {

    private static final int BUFFER_SIZE = 512 * 1024;

//    private MarkableFileInputStream mInputStream;
    private RandomAccessFile mInputFile;
    private int mNumFrames;
    private byte[] mBuffer = new byte[BUFFER_SIZE * 3];
    private ArrayList<BlockInfo> mBlocksInfo;
    private ArrayList<Integer> mFramesToBlocks;
    private int mCurrentBlock;
    private int mCurrentFrame;

    private class BlockInfo {
        public BlockInfo(int type, int pos, int size) {
            this.pos = pos;
            this.size = size;
            this.type = type;
        }

        public int type;
        public int pos;
        public int size;
    }

    public H264Stream() {

    }

    private static void sendByteArrayTcp(byte[] arr, String host, int port) {
        Socket skt = null;
        try {
            skt = new Socket(host, port);
            OutputStream os = skt.getOutputStream();
            int sent = 0;
            //do {
            os.write(arr, sent, arr.length - sent);
            //} while (sent < arr.length);
        } catch (IOException ex) {
            Log.e("SENDBYTEARRAYTCP", "Cannot connect.");
        } finally {
            if (skt != null)
                try {
                    skt.close();
                } catch (IOException ex2) {
                }
            ;
        }
    }

    public void openStream(String fileName) throws IOException {

//        mInputStream = new MarkableFileInputStream(new FileInputStream(fileName));
        mInputFile = new RandomAccessFile(fileName, "r");

//        mInputReader = new FileReader(fileName);

        mBlocksInfo = new ArrayList<>();
        long fileSize = mInputFile.length();
        int frameStart = 0;
        int totalRead = 0;
        int cntr = 3;
        mCurrentBlock = 0;
        int frameType = -1;
        do {
            int bytesRead = mInputFile.read(mBuffer, 0, mBuffer.length);
            Log.d("BYTESREAD", bytesRead + " ");
            if (totalRead == 0)
                frameType = mBuffer[4] & 31;
            int frameEnd = 0;
            for (int i = 0; i < bytesRead; i++) {
                if (mBuffer[i] != 0) {
                    if (mBuffer[i] == 1 && cntr == 0 && i != 3) {
                        frameEnd = totalRead + i - 3;
                        BlockInfo frameInfo = new BlockInfo(frameType, frameStart, frameEnd - frameStart);
                        if (i != bytesRead - 1)
                            frameType = mBuffer[i + 1] & 31;
                        else {
                            frameType = mInputFile.read();
                            totalRead++;
                        }
                        frameStart = frameEnd;
                        Log.d("BUFSIZE", "#" + mBlocksInfo.size() + " buf size:" + frameInfo.size + " type: " + frameInfo.type);
                        mBlocksInfo.add(frameInfo);
                    }
                    cntr = 3;
                } else {
                    cntr--;
                }
            }
            totalRead += bytesRead;
            if (totalRead >= fileSize) {
                //int frameType = mBuffer[totalRead - frameStart + 4] & 31;
                BlockInfo frameInfo = new BlockInfo(frameType, frameStart, (int)fileSize - frameStart);
                mBlocksInfo.add(frameInfo);
            }
        } while (totalRead < fileSize);
//        sendByteArrayTcp(mBuffer, "gauss", 9999);
        mFramesToBlocks = new ArrayList<>();
        boolean combining = false;
        int firstBlockIdx = 0;
        for (int i = 0; i < mBlocksInfo.size(); i++) {
            BlockInfo bi = mBlocksInfo.get(i);
            if (bi.type != 1 && bi.type != 5) {
                if (!combining) {
                    combining = true;
                    firstBlockIdx = i;
                }
            } else {
                mFramesToBlocks.add(firstBlockIdx);
                combining = false;
            }
        }
        setCurrentFrame(0);
    }

    public void closeStream() {
        try {
            mInputFile.close();
        } catch (IOException ex) {
        }
    }


    public int setCurrentFrame(int frame) throws IOException {

        mInputFile.seek(0);

        int i;
        for (i = frame; i >= 0; i--)
            if (mBlocksInfo.get(mFramesToBlocks.get(i)).type == 5)
                break;
        if (i < 0)
            i = 0;
        mInputFile.seek(mBlocksInfo.get(i).pos);
        mCurrentFrame = i;
        mCurrentBlock = mFramesToBlocks.get(i);
        return i;
    }

    public int getNumberOfFrames() {
        return mFramesToBlocks.size();
    }

    public int getNumberOfBlocks() {
        return mBlocksInfo.size();
    }

    public int getCurrentFrame() {
        return mCurrentFrame;
    }

    public int getCurrentBlock() {
        return mCurrentBlock;
    }

    public ByteBuffer getNextBuffer() throws IOException {
        int bufSize = mBlocksInfo.get(mCurrentBlock).size;
        int totalRead = 0;

        do {
            Log.d("CURRENTFRAME", " " + mCurrentFrame);
            Log.d("NUMBUFFERS", mBlocksInfo.size() + " " + (bufSize) + " " + totalRead + " " + mBuffer.length);

            int bytesRead = mInputFile.read(mBuffer, totalRead, bufSize - totalRead);
            totalRead += bytesRead;
        } while (totalRead < bufSize);
        mCurrentBlock++;
        if (mCurrentFrame < mFramesToBlocks.size() - 1) {
            if (mCurrentBlock >= mFramesToBlocks.get(mCurrentFrame + 1))
                mCurrentFrame++;
        }
        return ByteBuffer.wrap(mBuffer, 0, bufSize);
    }
}
