package com.ubtech.digitrecognition;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ubtech.digitrecognition.util.Logger;
import com.ubtech.digitrecognition.view.FingerPaintView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private TextView mResultText;
    private FingerPaintView fingerPaintView;
    private View detectButton;
    private KerasTFLite mTFLite;
    private DigitalClassifier digitalClassifier;

    private Logger mLogger = new Logger();
    private static final int PERMISSIONS_REQUEST = 1;
    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fingerPaintView = findViewById(R.id.finger_paint_view);
        detectButton = findViewById(R.id.buttonDetect);
        detectButton.setOnClickListener(v -> onDetectClicked());
        findViewById(R.id.buttonClear).setOnClickListener(v -> onClearClicked());
        mResultText = findViewById(R.id.textResult);


        mTFLite = new KerasTFLite(MainActivity.this);
        digitalClassifier = new DigitalClassifier(this);
        digitalClassifier.initialize();

        if (hasPermission()) {
            makeButtonVisible(true);
        } else {
            makeButtonVisible(false);
            requestPermission();
        }
    }

    private void makeButtonVisible(boolean visible) {
        if (visible) {
            detectButton.setVisibility(View.VISIBLE);
        } else {
            detectButton.setVisibility(View.INVISIBLE);
        }
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
        if (mTFLite != null) {
            mTFLite.release();
            mTFLite = null;
        }
        super.onPause();
    }

    private void onDetectClicked() {
        if (fingerPaintView.isEmpty()) {
            Toast.makeText(this, "请写上一个数字", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap rawBitmap = fingerPaintView.getRawBitmap();
        String classifyResult = digitalClassifier.classify(rawBitmap);
        Toast.makeText(this, classifyResult, Toast.LENGTH_SHORT).show();

        final int PIXEL_SIZE = 28;
        Bitmap bitmap = fingerPaintView.exportToBitmap(rawBitmap, PIXEL_SIZE, PIXEL_SIZE);
        float pixels[] = getPixelData(bitmap);
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
        String result = mTFLite.run(pixels);
        String value = "数字是: " + result;
        mResultText.setText(value);
    }

    private void onClearClicked() {
        fingerPaintView.clear();
        mResultText.setText("");
    }

    /**
     * Get 28x28 pixel data for tensorflow input.
     */
    public float[] getPixelData(Bitmap bitmap) {
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


}
