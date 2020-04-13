package com.ubtech.digitrecognition.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.ubtech.digitrecognition.util.Logger;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2016-2020
 * FileName: KerasTFLite
 * Author: wei.zheng
 * Date: 2020/4/9 17:29
 * Description: $
 */
public class KerasTFLite {
    private static final String TAG = "KerasTFLite";
    private Logger mLogger = new Logger();
    private static final String MODEL_FILE = "keras_mnist_model.tflite";
    private Interpreter mInterpreter;

    public KerasTFLite(Context context) {
        File file = loadModelFile(context);
        mInterpreter = new Interpreter(file);
        try {
//            FileInputStream fis = new FileInputStream(new File("file:///android_asset/keras_mnist_model.tflite"));
//            FileChannel f_channel = fis.getChannel();
//            MappedByteBuffer tflite_model = f_channel.map(FileChannel.MapMode.READ_ONLY, 0, f_channel.size());
//            mInterpreter = new Interpreter(tflite_model);
        } catch (Exception e) {
            e.printStackTrace();
            mLogger.d("Keras: " + e.getMessage());
        }
    }

    public String run(float[] input) {
        //result will be number between 0~9
        float[][] labelProbArray = new float[1][10];
        mInterpreter.run(input, labelProbArray);
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            labels.add(String.valueOf(i));
        }

        return labels.get(getMax(labelProbArray[0]));
    }

    private File loadModelFile(Context context) {
        String filePath = Environment.getExternalStorageDirectory() + File.separator + MODEL_FILE;
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                AssetManager assetManager = context.getAssets();
                InputStream stream = assetManager.open(MODEL_FILE);
                OutputStream output = new BufferedOutputStream(new FileOutputStream(filePath));
                byte[] buffer = new byte[1024];
                int read;
                while ((read = stream.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                stream.close();
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            mLogger.e("Keras:" + e.getMessage());
        }
        return file;
    }

    private int getMax(float[] results) {
        mLogger.d("xxx-> getMax");
        int maxID = 0;
        float maxValue = results[maxID];
        for (int i = 0; i < results.length; i++) {
            mLogger.d("xxx-> " + i + " " + results[i]);
            if (results[i] > maxValue) {
                maxID = i;
                maxValue = results[maxID];
            }
        }
        return maxID;
    }

    public void release() {
        mInterpreter.close();
    }
}
