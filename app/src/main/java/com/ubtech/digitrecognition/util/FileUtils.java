package com.ubtech.digitrecognition.util;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Copyright (C), 2016-2020
 * FileName: FileUtils
 * Author: wei.zheng
 * Date: 2020/4/10 11:26
 * Description: 文件工具类
 */
public class FileUtils {
    /**
     * 保存bitmap到SD卡
     *
     * @param bitmap
     * @param imageName
     */
    public static String saveBitmapToSDCard(Bitmap bitmap, String imageName) {
        String path = "/sdcard/aa/";
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName = path + imageName + ".jpg";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {

            }
        }
        return null;
    }
}
