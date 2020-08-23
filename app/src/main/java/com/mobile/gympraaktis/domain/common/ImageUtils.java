package com.mobile.gympraaktis.domain.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import com.mobile.gympraaktis.PraaktisApp;

import java.io.*;

public class ImageUtils {

    public static File convertToBitmap2(final File originalFile, int dstWidth, int dstHeight) {
        int rotation = 0;
        int rotationInDegrees = 0;
        try {
            ExifInterface exif = new ExifInterface(originalFile.getAbsolutePath());
            rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            rotationInDegrees = exifToDegrees(rotation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File resizedFile = resize(originalFile, dstWidth, dstHeight);
        return rotate(resizedFile, rotation, rotationInDegrees);
    }

    public static File resize(final File file, int dstWidth, int dstHeight) {
        try {
            int inWidth;
            int inHeight;

            InputStream in = new FileInputStream(file);

            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;

            options = new BitmapFactory.Options();
            // calc rought re-size (this is no exact resize)
            options.inSampleSize = Math.max(inWidth / dstWidth, inHeight / dstHeight);
            if (options.inSampleSize < 2) {
                return file;
            }

            // decode full image
            // decode full image pre-resized

            in = new FileInputStream(file);
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

            // calc exact destination size
            Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0, 0, dstWidth, dstHeight);
            m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
            float[] values = new float[9];
            m.getValues(values);

            // resize bitmap
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);
            if (roughBitmap != resizedBitmap) {
                roughBitmap.recycle();
            }

            // save image
            try {
                File outputDir = PraaktisApp.getApplication().getCacheDir(); // context being the Activity pointer
                File outputFile = File.createTempFile(file.getName().substring(0, file.getName().lastIndexOf(".")), ".jpeg", outputDir);
                FileOutputStream out = new FileOutputStream(outputFile, false);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                return outputFile;
            } catch (Exception e) {
            }
        } catch (IOException e) {
        }
        return file;
    }

    private static File rotate(File file, int rotation, int rotationInDegrees) {
        try {
            if (rotation != 0f) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inJustDecodeBounds = true;
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                Matrix matrix = new Matrix();
                matrix.preRotate(rotationInDegrees);
                myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);

                FileOutputStream out = new FileOutputStream(file);
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
}
