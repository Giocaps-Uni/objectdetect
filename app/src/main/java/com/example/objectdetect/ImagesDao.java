package com.example.objectdetect;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ImagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insertImage(LabeledImage image);

    @Query("SELECT * FROM LabeledImage")
    List<LabeledImage> getAll();


}
