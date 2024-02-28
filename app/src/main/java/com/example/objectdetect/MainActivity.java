package com.example.objectdetect;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedDispatcher;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage(R.string.exit_dialog)
                        .setCancelable(true)
                        .setPositiveButton(R.string.yes_string, (dialog, id) -> finish())
                        .setNegativeButton(R.string.no_string, (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.show();
            }
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