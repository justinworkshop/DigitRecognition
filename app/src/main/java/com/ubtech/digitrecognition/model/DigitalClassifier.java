package com.ubtech.digitrecognition.model;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (C), 2016-2020
 * FileName: DigitalClassifier
 * Author: wei.zheng
 * Date: 2020/4/10 21:34
 * Description: $
 */
public class DigitalClassifier {
    private static final String TAG = "DigitalClassifier";
    private static final String MODEL_FILE = "keras_mnist_model.tflite";
    private static final int FLOAT_TYPE_SIZE = 4;
    private static final int PIXEL_SIZE = 1;
    private static final int OUTPUT_CLASSES_COUNT = 10;

    private Context context;
    private Interpreter interpreter;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private int inputImageWidth;
    private int inputImageHeight;
    private int modelInputSize;
    private boolean isInitialized = false;

    public DigitalClassifier(Context context) {
        this.context = context;
        initialize();
    }

    private void initialize() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    initializeInterpreter();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeInterpreter() throws IOException {
        AssetManager assetManager = context.getResources().getAssets();
        ByteBuffer model = loadModelFile(assetManager);

        Interpreter.Options options = new Interpreter.Options();
        options.setUseNNAPI(true);
        Interpreter interpreter = new Interpreter(model, options);

        int[] inputShape = interpreter.getInputTensor(0).shape();
        inputImageWidth = inputShape[1];
        inputImageHeight = inputShape[2];
        modelInputSize = FLOAT_TYPE_SIZE * inputImageWidth * inputImageHeight * PIXEL_SIZE;

        this.interpreter = interpreter;
        this.isInitialized = true;
    }

    private ByteBuffer loadModelFile(AssetManager assetManager) throws IOException {
        AssetFileDescriptor assetFileDescriptor = assetManager.openFd(MODEL_FILE);
        FileInputStream fis = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel channel = fis.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long declaredLength = assetFileDescriptor.getDeclaredLength();
        return channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public String classify(Bitmap bitmap) {
        if (!isInitialized) {
            throw new IllegalStateException("TF Lite Interpreter is not initialized yet.");
        }

        long startTime;
        long elapsedTime;

        startTime = System.nanoTime();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputImageWidth, inputImageHeight, true);
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(resizedBitmap);
        elapsedTime = (System.nanoTime() - startTime) / 1000000;
        Log.d(TAG, "Preprocessing time = " + elapsedTime + "ms");

        startTime = System.nanoTime();
        float[][] result = new float[1][OUTPUT_CLASSES_COUNT];
        System.out.println("intput byteBuffer " + byteBuffer.toString());
        interpreter.run(byteBuffer, result);
        elapsedTime = (System.nanoTime() - startTime) / 1000000;
        Log.d(TAG, "Inference time = " + elapsedTime + "ms");

        for (int i = 0; i < result[0].length; i++) {
            System.out.println("output: " + i + " - " + result[0][i]);
        }

        return getOutputString(result[0]);

    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(modelInputSize);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[inputImageWidth * inputImageHeight];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < pixels.length; i++) {
            int pixelValue = pixels[i];

            int r = pixelValue >> 16 & 0xFF;
            int g = pixelValue >> 8 & 0xFF;
            int b = pixelValue & 0xFF;

            // Convert RGB to grayscale and normalize pixel value to [0..1]
            float normalizedPixelValue = (r + g + b) / 3.0f / 255.0f;
            byteBuffer.putFloat(normalizedPixelValue);
        }

        return byteBuffer;
    }

    private String getOutputString(float[] output) {
        float max = output[0];
        int maxIndex = 0;
        for (int i = 0; i < output.length; i++) {
            if (max < output[i]) {
                max = output[i];
                maxIndex = i;
            }
        }

        return String.format("Prediction Result: %d\nConfidence: %2f", maxIndex, output[maxIndex]);
    }
}
