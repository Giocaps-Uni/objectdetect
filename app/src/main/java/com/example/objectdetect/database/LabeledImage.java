package com.example.objectdetect.database;

import android.net.Uri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.mlkit.vision.label.ImageLabel;

import java.util.List;

/**
 * Database entity. Contains the uri of the image, the list of labels found and the confidence
 * Threshold set for the database. An automatically incremented id is used for unique identification
 */
@Entity
public class LabeledImage {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public Uri uri;

    public List<ImageLabel> labels;

    public float confidence;

    public LabeledImage (Uri uri, List<ImageLabel> labels, float confidence) {
        this.uri = uri;
        this.labels = labels;
        this.confidence = confidence;
    }
}
