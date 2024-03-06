package com.example.objectdetect;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.mlkit.vision.label.ImageLabel;

import java.util.List;

@Entity
public class LabeledImage {

    @PrimaryKey(autoGenerate = true)
    public int id;

    //public Bitmap labeledBitmap;
    public Uri uri;

    public List<ImageLabel> labels;

    public float confidence;

    public LabeledImage (Uri uri, List<ImageLabel> labels, float confidence) {
        this.uri = uri;
        this.labels = labels;
        this.confidence = confidence;
    }
}
