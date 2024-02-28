package com.example.objectdetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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