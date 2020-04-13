package com.ubtech.digitrecognition.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Copyright (C), 2016-2020
 * FileName: FingerPaintView
 * Author: wei.zheng
 * Date: 2020/4/9 17:28
 * Description: PainterView
 */
public class PainterView extends View {
    private Path path;
    private Bitmap drawingBitmap;
    private Canvas drawingCanvas;
    private Paint drawingPaint;
    private float curX = 0.0f;
    private float curY = 0.0f;
    private Paint paint;

    public PainterView(Context context) {
        super(context);
        init();
    }

    public PainterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PainterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawingPaint = new Paint(Paint.DITHER_FLAG);
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(70.0f);
    }

    public void setStrokeWidth(float strokeWidth) {
        paint.setStrokeWidth(strokeWidth);
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawingBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawingCanvas = new Canvas(drawingBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {
            canvas.drawBitmap(drawingBitmap, 0f, 0f, drawingPaint);
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null)
            return false;
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.reset();
                path.moveTo(x, y);
                curX = x;
                curY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                path.quadTo(curX, curY, (x + curX) / 2, (y + curY) / 2);
                curX = x;
                curY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(curX, curY);
                drawingCanvas.drawPath(path, paint);
                path.reset();
                performClick();
                invalidate();
                break;
        }
        super.onTouchEvent(event);
        return true;
    }

    public void clear() {
        path.reset();
        drawingBitmap = Bitmap.createBitmap(drawingBitmap.getWidth(), drawingBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        drawingCanvas = new Canvas(drawingBitmap);
        invalidate();
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        draw(canvas);

        return bitmap;
    }
}
