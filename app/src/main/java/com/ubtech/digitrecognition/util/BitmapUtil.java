package com.ubtech.digitrecognition.util;

import android.graphics.Bitmap;

import java.util.Arrays;

/**
 * Copyright (C), 2016-2020
 * FileName: BitmapUtil
 * Author: wei.zheng
 * Date: 2020/4/13 11:01
 * Description: $
 */
public class BitmapUtil {

    /**
     * Get 28x28 pixel data for tensorflow input.
     */
    public static float[] getPixelData(Bitmap bitmap) {
        Logger mLogger = new Logger();
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // Get 28x28 pixel data from bitmap
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        float[] retPixels = new float[pixels.length];
        for (int i = 0; i < pixels.length; ++i) {
            // Set 0 for white and 255 for black pixel
            int pix = pixels[i];
            int b = pix & 0xff;
            retPixels[i] = 0xff - b;
        }
        mLogger.d("pixel Data: " + Arrays.toString(retPixels));
        mLogger.d("pixel Data length: " + retPixels.length);
        return retPixels;
    }

    public Bitmap exportToBitmap(Bitmap rawBitmap, int width, int height) {
        FileUtils.saveBitmapToSDCard(rawBitmap, "_raw_");
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(rawBitmap, width, height, false);
        rawBitmap.recycle();

        FileUtils.saveBitmapToSDCard(scaledBitmap, "_scale_");

        return scaledBitmap;
    }

}
