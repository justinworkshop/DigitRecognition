package com.ubtech.digitrecognition;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.divyanshu.draw.widget.DrawView;

/**
 * Copyright (C), 2016-2020
 * FileName: SecondActivity
 * Author: wei.zheng
 * Date: 2020/4/13 16:15
 * Description: $
 */
public class SecondActivity extends AppCompatActivity {
    private DrawView drawView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity_layout);
        drawView = findViewById(R.id.draw_view);
        drawView.setStrokeWidth(70.0f);
        drawView.setColor(Color.WHITE);
        drawView.setBackgroundColor(Color.BLACK);
        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                drawView.onTouchEvent(event);
                System.out.println("output " + event.toString());
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
