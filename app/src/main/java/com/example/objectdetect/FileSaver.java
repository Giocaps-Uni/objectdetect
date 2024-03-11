package com.example.objectdetect;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.FileOutputStream;
import java.util.concurrent.Callable;

public class FileSaver implements Callable<Void> {

    private final String filename;
    private final ContextWrapper contextWrapper;
    private final Bitmap result;
    public FileSaver(String filename, ContextWrapper contextWrapper, Bitmap result) {
        this.filename = filename;
        this.contextWrapper = contextWrapper;
        this.result = result;
    }

    @Override
    public Void call(){
        try (FileOutputStream fileOutputStream = contextWrapper
                .openFileOutput(filename, Context.MODE_PRIVATE)) {
            result.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            Log.d("SAVE_IMAGE", "File saved in " + contextWrapper.getFilesDir());
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }
        return null;
    }
}
