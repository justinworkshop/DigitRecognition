package com.ubtech.digitrecognition;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ubtech.digitrecognition.databinding.ActivityMainBinding;
import com.ubtech.digitrecognition.vm.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding activityMainBinding;
    private MainActivityViewModel classifierViewModel;

    private static final int PERMISSIONS_REQUEST = 1;
    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        classifierViewModel = new MainActivityViewModel(this, activityMainBinding);
        activityMainBinding.setViewModel(classifierViewModel);

        activityMainBinding.fingerPaintView.setVisibility(View.INVISIBLE);
        activityMainBinding.drawView.setStrokeWidth(70.0f);
        activityMainBinding.drawView.setColor(Color.WHITE);
        activityMainBinding.drawView.setBackgroundColor(Color.BLACK);
        activityMainBinding.drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                activityMainBinding.drawView.onTouchEvent(event);
                System.out.println("output "+event.toString());
                return true;
            }
        });

        if (hasPermission()) {
            makeButtonVisible(true);
        } else {
            makeButtonVisible(false);
            requestPermission();
        }
    }

    private void makeButtonVisible(boolean visible) {
        int showView = visible ? View.VISIBLE : View.INVISIBLE;
        activityMainBinding.btnDetect.setVisibility(showView);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
            if (allPermissionsGranted(grantResults)) {
                makeButtonVisible(true);
            } else {
                requestPermission();
            }
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean read = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean write = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            return read && write;
        } else {
            return true;
        }
    }

    private static boolean allPermissionsGranted(final int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSIONS_REQUEST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
