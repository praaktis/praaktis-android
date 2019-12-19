#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/bitmap.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_praaktis_exerciseengine_ExerciseEngineActivity_yuvToRGBGrayscale(JNIEnv *env, jobject thiz,
                                                                          jbyteArray buf,
                                                                          jintArray pixels,
                                                                          jint n) {

    jbyte *bytes = env->GetByteArrayElements(buf, 0);
    jint *dest = env->GetIntArrayElements(pixels, 0);

    for (int i = 0; i < n; i += 4) {

        uint32_t y = static_cast<uint32_t>(bytes[i] & 255);

        dest[i] = 0xFF000000 | (y << 16) | (y << 8) | y;

        y = bytes[i + 1] & 255;

        dest[i + 1] = 0xFF000000 | (y << 16) | (y << 8) | y;

        y = bytes[i + 2] & 255;

        dest[i + 2] = 0xFF000000 | (y << 16) | (y << 8) | y;

        y = bytes[i + 3] & 255;

        dest[i + 3] = 0xFF000000 | (y << 16) | (y << 8) | y;
    }

    env->ReleaseByteArrayElements(buf, bytes, 0);
    env->ReleaseIntArrayElements(pixels, dest, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_praaktis_exerciseengine_ExerciseEngineActivity_yuvToRGB(JNIEnv *env, jobject thiz,
                                                                 jbyteArray bytes_y,
                                                                 jbyteArray bytes_b,
                                                                 jbyteArray bytes_r,
                                                                 jobject bmp,
                                                                 jint width,
                                                                 jint height,
                                                                 jint y_row_stride,
                                                                 jint uv_row_stride
) {
    // TODO: implement yuvToRGB()

    jbyte *ybytes = env->GetByteArrayElements(bytes_y, 0);
    jbyte *bbytes = env->GetByteArrayElements(bytes_b, 0);
    jbyte *rbytes = env->GetByteArrayElements(bytes_r, 0);

    int* dest_pixels = nullptr;

    AndroidBitmap_lockPixels(env, bmp, reinterpret_cast<void **>(&dest_pixels));

    if(dest_pixels == nullptr) {
        AndroidBitmap_unlockPixels(env, bmp);
        return;
    }

    int idx_row_y = 0;
    int ind = 0;

    for (int y = 0; y < height; y++) {
        int y2 = y >> 1;
        int idxRowRB = y2 * uv_row_stride;

        int uvoffset = idxRowRB;
        for (int x = 0; x < width; x++) {

            int yc = 255 & ybytes[idx_row_y + x];
            int cb = (255 & bbytes[uvoffset]) - 128;
            int cr = (255 & rbytes[uvoffset]) - 128;

            int r = yc + ((45 * cr) >> 5);
            int g = yc - ((11 * cb + 23 * cr) >> 5);
            int b = yc + ((113 * cb) >> 6);

            if (b < 0) b = 0;
            if (b > 255) b = 255;
            if (r < 0) r = 0;
            if (r > 255) r = 255;
            if (g < 0) g = 0;
            if (g > 255) g = 255;

            dest_pixels[ind++] = 0xFF000000 | (b << 16) | (g << 8) | r;
            uvoffset += 2 * (x & 1);
        }
        idx_row_y += y_row_stride;
    }

    env->ReleaseByteArrayElements(bytes_y, ybytes, 0);
    env->ReleaseByteArrayElements(bytes_b, bbytes, 0);
    env->ReleaseByteArrayElements(bytes_r, rbytes, 0);

    AndroidBitmap_unlockPixels(env, bmp);
}