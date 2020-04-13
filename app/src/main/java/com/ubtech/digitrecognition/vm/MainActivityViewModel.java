package com.ubtech.digitrecognition.vm;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.BaseObservable;

import com.ubtech.digitrecognition.databinding.ActivityMainBinding;
import com.ubtech.digitrecognition.model.DigitalClassifier;
import com.ubtech.digitrecognition.model.KerasTFLite;
import com.ubtech.digitrecognition.util.BitmapUtil;
import com.ubtech.digitrecognition.util.Logger;

import java.util.Arrays;

/**
 * Copyright (C), 2016-2020
 * FileName: MainActivityViewModel
 * Author: wei.zheng
 * Date: 2020/4/13 10:02
 * Description: MainActivityViewModel
 */
public class MainActivityViewModel extends BaseObservable {
    private static final String TAG = "MainActivityViewModel";

    private Activity activity;
    private ActivityMainBinding viewDataBinding;
    private String classifyResult;
    private KerasTFLite kerasTFLite;
    private DigitalClassifier digitalClassifier;
    private Logger mLogger = new Logger();

    public MainActivityViewModel(Activity activity, ActivityMainBinding viewDataBinding) {
        this.activity = activity;
        this.viewDataBinding = viewDataBinding;
        init();
    }

    private void init() {
        kerasTFLite = new KerasTFLite(activity);
        digitalClassifier = new DigitalClassifier(activity);
    }

    public String getClassifyResult() {
        return classifyResult;
    }

    public void setClassifyResult(String classifyResult) {
        this.classifyResult = classifyResult;
        notifyPropertyChanged(com.ubtech.digitrecognition.BR._all);
    }

    public void clearView(View v) {
        viewDataBinding.fingerPaintView.clear();
        viewDataBinding.drawView.clearCanvas();
        viewDataBinding.textResult.setText("");
    }

    public void detectView(View v) {
        if (viewDataBinding.fingerPaintView.isEmpty()) {
            Toast.makeText(activity, "Bitmap view is Empty", Toast.LENGTH_SHORT).show();
//            return;
        }

//        Bitmap rawBitmap = viewDataBinding.fingerPaintView.getRawBitmap();
        Bitmap rawBitmap = viewDataBinding.drawView.getBitmap();

        try {
            System.out.println("w:" + rawBitmap.getWidth() + ", h:" + rawBitmap.getHeight());
            String res = digitalClassifier.classify(rawBitmap);
            Toast.makeText(activity, res, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int PIXEL_SIZE = 28;
        Bitmap bitmap = viewDataBinding.fingerPaintView.exportToBitmap(rawBitmap, PIXEL_SIZE, PIXEL_SIZE);
        float pixels[] = BitmapUtil.getPixelData(bitmap);
        //should be same format with train
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = pixels[i] / 255;
        }
//        for (int i = 0; i < PIXEL_SIZE; i++) {
//            float[] a = Arrays.copyOfRange(pixels, i * PIXEL_SIZE, i * PIXEL_SIZE + PIXEL_SIZE);
//            mLogger.d("pixel - " + i + "  " + Arrays.toString(a));
//        }
        mLogger.d("Pixel Data: " + Arrays.toString(pixels));
        mLogger.d("Start run");
        String result = kerasTFLite.run(pixels);
        classifyResult = "数字是: " + result;

        setClassifyResult(classifyResult);
    }
}
