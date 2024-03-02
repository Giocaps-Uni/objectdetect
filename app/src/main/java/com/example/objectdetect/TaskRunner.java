package com.example.objectdetect;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback<Type> {
        void onComplete(Type result);
    }

    public <Type> void executeAsync(Callable<Type> callable, Callback<Type> callback) {
        executor.execute(() -> {
            final Type result;
            try {
                result = callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            handler.post(() -> {
                callback.onComplete(result);
            });
        });
    }
}

