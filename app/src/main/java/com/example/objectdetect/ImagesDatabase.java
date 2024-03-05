package com.example.objectdetect;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {LabeledImage.class}, version = 1)
@TypeConverters({BitmapConverters.class})
public abstract class ImagesDatabase extends RoomDatabase {
    private static volatile ImagesDatabase INSTANCE;

    public abstract ImagesDao imagesDao();

    public static ImagesDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ImagesDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ImagesDatabase.class, "Images.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

