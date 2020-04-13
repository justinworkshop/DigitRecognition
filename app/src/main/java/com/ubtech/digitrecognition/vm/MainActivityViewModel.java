package com.ubtech.digitrecognition.vm;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import androidx.databinding.BaseObservable;

import com.ubtech.digitrecognition.databinding.ActivityMainBinding;
import com.ubtech.digitrecognition.model.DigitalClassifier;
import com.ubtech.digitrecognition.util.FileUtils;
import com.ubtech.digitrecognition.util.Logger;

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
    private String viewName;
    private String classifyResult;
    private DigitalClassifier digitalClassifier;
    private boolean switchFlag = false;
    private Logger mLogger = new Logger();

    public MainActivityViewModel(Activity activity, ActivityMainBinding viewDataBinding) {
        this.activity = activity;
        this.viewDataBinding = viewDataBinding;
        init();
    }

    private void init() {
        digitalClassifier = new DigitalClassifier(activity);
        switchView(null);
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
        notifyPropertyChanged(com.ubtech.digitrecognition.BR._all);
    }

    public String getClassifyResult() {
        return classifyResult;
    }

    public void setClassifyResult(String classifyResult) {
        this.classifyResult = classifyResult;
        notifyPropertyChanged(com.ubtech.digitrecognition.BR._all);
    }

    public void switchView(View v) {
        switchFlag = !switchFlag;
        if (switchFlag) {
            viewDataBinding.painterView.setVisibility(View.VISIBLE);
            viewDataBinding.drawView.setVisibility(View.INVISIBLE);
        } else {
            viewDataBinding.painterView.setVisibility(View.INVISIBLE);
            viewDataBinding.drawView.setVisibility(View.VISIBLE);
        }
        setViewName(switchFlag ? "PainterView" : "DrawView");
    }

    public void clearView(View v) {
        viewDataBinding.textResult.setText("");
        if (viewDataBinding.painterView.getVisibility() == View.VISIBLE) {
            viewDataBinding.painterView.clear();
        } else {
            viewDataBinding.drawView.clearCanvas();
        }
    }

    public void detectView(View v) {
        Bitmap rawBitmap;
        String source;
        if (viewDataBinding.painterView.getVisibility() == View.VISIBLE) {
            rawBitmap = viewDataBinding.painterView.getBitmap();
            source = "PainterView";
        } else {
            rawBitmap = viewDataBinding.drawView.getBitmap();
            source = "DrawView";
        }
        FileUtils.saveBitmapToSDCard(rawBitmap, source);

        System.out.println("source:" + source + " w:" + rawBitmap.getWidth() + ", h:" + rawBitmap.getHeight());
        digitalClassifier.classify(rawBitmap, new DigitalClassifier.ClassifyCallback() {
            @Override
            public void onComplete(String result) {
                setClassifyResult(result);
            }
        });
    }

    public void destroy() {
        digitalClassifier.destroy();
    }
}
