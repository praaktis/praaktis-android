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
                                                                 jint uv_row_stride,
                                                                 jint pixel_stride
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
            uvoffset += (x & 1) ? pixel_stride : 0;
        }
        idx_row_y += y_row_stride;
    }

    env->ReleaseByteArrayElements(bytes_y, ybytes, 0);
    env->ReleaseByteArrayElements(bytes_b, bbytes, 0);
    env->ReleaseByteArrayElements(bytes_r, rbytes, 0);

    AndroidBitmap_unlockPixels(env, bmp);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_praaktis_exerciseengine_ExerciseEngineActivity_yuvPlanarToRGB(JNIEnv *env, jobject thiz,
                                                                       jbyteArray bytes_y,
                                                                       jbyteArray bytes_b,
                                                                       jbyteArray bytes_r,
                                                                       jobject bmp, jint width,
                                                                       jint height,
                                                                       jint y_row_stride,
                                                                       jint uv_rowstride) {
    // TODO: implement yuvPlanarToRGB()


}
#define Y_HEIGHT 1080
#define Y_WIDTH  1920
#define UV_HEIGHT (Y_HEIGHT / 2)
#define UV_WIDTH  (Y_WIDTH / 2)

extern "C"
JNIEXPORT void JNICALL
Java_com_praaktis_exerciseengine_ExerciseEngineActivity_resizeYUV3(JNIEnv *env, jobject thiz,
                                                                   jbyteArray out_arr,
                                                                   jbyteArray bytes_y,
                                                                   jbyteArray bytes_b,
                                                                   jbyteArray bytes_r,
                                                                   jint y_row_stride,
                                                                   jint uv_row_stride,
                                                                   jint pixel_stride) {
    // TODO: implement resizeYUV3()
    jbyte *yBytes0 = env->GetByteArrayElements(bytes_y, 0);
    jbyte *bBytes0 = env->GetByteArrayElements(bytes_b, 0);
    jbyte *rBytes0 = env->GetByteArrayElements(bytes_r, 0);
    jbyte *outBytes0 = env->GetByteArrayElements(out_arr, 0);

    uint8_t *yBytes = (uint8_t *)yBytes0;
    uint8_t *bBytes = (uint8_t *)bBytes0;
    uint8_t *rBytes = (uint8_t *)rBytes0;
    uint8_t *outBytes = (uint8_t *)outBytes0;

    int inPos = 0;
    int outPos = 0;

    // Y channel
    for (int y = 0; y < Y_HEIGHT; y += 3) {
        for (int x = 0; x < Y_WIDTH; x += 3) {
            int s = yBytes[inPos] + yBytes[inPos + 1] + yBytes[inPos + 2] +
                    yBytes[Y_WIDTH + inPos] + yBytes[Y_WIDTH + inPos + 1] + yBytes[Y_WIDTH + inPos + 2] +
                    yBytes[Y_WIDTH * 2 + inPos] + yBytes[Y_WIDTH * 2 + inPos + 1] + yBytes[Y_WIDTH * 2 + inPos + 2];
            inPos += 3;
            outBytes[outPos] = s / 9;
            outPos++;
        }
        inPos = y * y_row_stride;
    }
    // U channel1
    inPos = 0;
    int rowDelta = uv_row_stride;//UV_WIDTH * pixel_stride;
    for (int y = 0; y < UV_HEIGHT; y += 3) {
        for (int x = 0; x < UV_WIDTH; x += 3) {
            int s = rBytes[inPos] + rBytes[inPos + pixel_stride] + rBytes[inPos + 2 * pixel_stride] +
                    rBytes[rowDelta + inPos] + rBytes[rowDelta + inPos + pixel_stride] + rBytes[rowDelta + inPos + 2 * pixel_stride] +
                    rBytes[rowDelta * 2 + inPos] + rBytes[rowDelta * 2 + inPos + pixel_stride] + rBytes[rowDelta * 2 + inPos + pixel_stride * 2];
            inPos += 3 * pixel_stride;
            outBytes[outPos] = s / 9;
            outPos++;
        }
        inPos = y * uv_row_stride;
    }
    inPos = 0;
    for (int y = 0; y < UV_HEIGHT; y += 3) {
        for (int x = 0; x < UV_WIDTH; x += 3) {
            int s = bBytes[inPos] + bBytes[inPos + pixel_stride] + bBytes[inPos + 2 * pixel_stride] +
                    bBytes[rowDelta + inPos] + bBytes[rowDelta + inPos + pixel_stride] + bBytes[rowDelta + inPos + 2 * pixel_stride] +
                    bBytes[rowDelta * 2 + inPos] + bBytes[rowDelta * 2 + inPos + pixel_stride] + bBytes[rowDelta * 2 + inPos + pixel_stride * 2];
            inPos += 3 * pixel_stride;
            outBytes[outPos] = s / 9;
            outPos++;
        }
        inPos = y * uv_row_stride;
    }
    env->ReleaseByteArrayElements(bytes_y, yBytes0, 0);
    env->ReleaseByteArrayElements(bytes_b, bBytes0, 0);
    env->ReleaseByteArrayElements(bytes_r, rBytes0, 0);
    env->ReleaseByteArrayElements(out_arr, outBytes0, 0);
}