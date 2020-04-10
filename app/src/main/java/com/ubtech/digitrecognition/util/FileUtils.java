package com.ubtech.digitrecognition.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.System.out;

/**
 * Copyright (C), 2016-2020
 * FileName: FileUtils
 * Author: wei.zheng
 * Date: 2020/4/10 11:26
 * Description: 将Bitmap图片保存到sd卡
 */
public class FileUtils {
    protected void saveBitmapToSD(Bitmap bt) {
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path, System.currentTimeMillis() + ".jpg");
        out.println(Environment.getExternalStorageState() + "/Cool/" + "000000000000000000000000000");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bt.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        out.flush();
        out.close();
    }

    /**
     * 保存bitmap到SD卡
     *
     * @param fileName 保存的名字
     * @param mBitmap  图片对像
     *                 return 生成压缩图片后的图片路径
     */
    public static String saveMyBitmap(String fileName, Bitmap mBitmap) {
        File file = new File("/sdcard/" + fileName + ".png");
        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            out.println("在保存图片时出错：" + e.toString());
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "/sdcard/" + fileName + ".png";
    }

    /**
     * 保存bitmap到SD卡
     *
     * @param bitmap
     * @param imageName
     */
    public static String saveBitmapToSDCard(Bitmap bitmap, String imageName) {
        String path = "/sdcard/aa/" + "img-" + imageName + ".jpg";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            }
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
