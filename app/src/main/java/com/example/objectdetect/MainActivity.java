package com.example.objectdetect;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;

import com.example.objectdetect.database.ImagesDatabase;
import com.example.objectdetect.utils.TaskRunner;

import java.util.concurrent.Callable;

/**
 * Single activity architecture, main activity constains the fragment container view in fullscreen
 * and holds the navigation resource
 * ImagesDB -> an instance of the database to be used by every fragment
 * Lrucache -> a runtime cache to store bitmap for a more efficient loading into imageviews
 *  implemented as a dictionary with resource uri as key and bitmap of the image as value
 */

public class MainActivity extends AppCompatActivity {

    public ImagesDatabase imagesDB;
    private LruCache<String, Bitmap> memoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagesDB = ImagesDatabase.getInstance(this);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

    class BitmapLoaderTask implements Callable<Void> {

        private final String urikey;
        private final Bitmap bitmap;

        public BitmapLoaderTask(String input, Bitmap bitmapInput) {
            this.urikey = input;
            this.bitmap = bitmapInput;
        }

        @Override
        public Void call(){
            addBitmapToMemoryCache(urikey, bitmap);
            return null;
        }
    }

    public void loadBitmap(Uri uri, Bitmap image) {
        final String imageKey = uri.toString();
        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(new BitmapLoaderTask(imageKey, image), (Null) -> {
            Log.d("CACHING", String.valueOf(memoryCache.size()));
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d("ACTIVITY","onSaveInstanceState");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ACTIVITY","onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();

        Log.d("ACTIVITY","onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ACTIVITY","onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ACTIVITY","onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ACTIVITY","onDestroy");
    }
}