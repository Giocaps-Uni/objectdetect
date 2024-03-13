package com.example.objectdetect.threads;

import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import com.example.objectdetect.fragments.ImagePreviewFragment;

import java.util.concurrent.Callable;

/**
 * Implements the rotation of an image chosen with the photopicker to show a corrected-oriented
 * thumbnail in {@link ImagePreviewFragment}
 */
public class MatrixCalculator implements Callable<Matrix> {
    private final ExifInterface exif;

    public MatrixCalculator(ExifInterface exif) {
        this.exif = exif;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    @Override
    public Matrix call() {
        // Get rotation from exif interface of the selected image and rotate
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        int rotationInDegrees = exifToDegrees(rotation);
        Matrix matrix = new Matrix();
        if (rotation != 0) {
            matrix.preRotate(rotationInDegrees);
        }
        return matrix;
    }
}