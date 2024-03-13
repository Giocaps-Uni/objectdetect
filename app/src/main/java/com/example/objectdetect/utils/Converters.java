package com.example.objectdetect.utils;

import android.net.Uri;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.label.ImageLabel;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Type converters used by the image database. Converts uri to String and List to Gson
 */
public class Converters {

    @TypeConverter
    public static String uriToString(Uri uri) {
        return uri.toString();
    }

    @TypeConverter
    public static Uri stringToUri(String uriString) {
        return Uri.parse(uriString);
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
