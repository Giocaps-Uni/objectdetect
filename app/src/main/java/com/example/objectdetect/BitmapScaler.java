package com.example.objectdetect;

import android.graphics.Bitmap;

public class BitmapScaler {
    // Scale and maintain aspect ratio given a desired width and height
    public static Bitmap scaleToFitWidthHeight(Bitmap b, int width, int height) {
        float factor_width = width / (float) b.getWidth();
        float factor_height = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getHeight() * factor_width), (int) (b.getHeight() * factor_height), true);
    }
}