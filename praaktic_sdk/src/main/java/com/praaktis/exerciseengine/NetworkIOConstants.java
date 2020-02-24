package com.praaktis.exerciseengine;

interface NetworkIOConstants {
    int HEADER_SIZE = 13;
    int POS_DATA_SIZE = 1;
    int POS_DATA_CRC32 = 5;
    int POS_HEADER_CRC32 = 9;

    int MSG_OK = 0;
    int MSG_ERROR = 1;
    int MSG_LOGIN_PASSWD = 2;
    int MSG_FRAME_DATA = 3;
    int MSG_FRAME_POINTS = 4;
    int MSG_CLOSE_CONNECTION = 5;
}