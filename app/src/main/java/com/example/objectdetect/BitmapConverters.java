package com.example.objectdetect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.label.ImageLabel;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.List;

public class BitmapConverters {

    @TypeConverter
    public static byte[] bitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
        return stream.toByteArray();
    }
    @TypeConverter
    public static Bitmap byteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    @TypeConverter
    public static String listToGsonString(List<ImageLabel> imageLabelList) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ImageLabel>>() {}.getType();
        return gson.toJson(imageLabelList, type);
    }

    @TypeConverter
    public static List<ImageLabel> gsonStringToList(String gsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ImageLabel>>() {}.getType();
        return gson.fromJson(gsonString, type);
    }
}
