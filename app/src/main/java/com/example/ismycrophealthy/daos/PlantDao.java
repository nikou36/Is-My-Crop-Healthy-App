package com.example.ismycrophealthy.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ismycrophealthy.entities.Plant;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
/*
Data Access Object for our plant data
 */

@Dao
public interface PlantDao {

    @Query("SELECT * FROM plant_table")
    List<Plant> getAll();


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Plant... users);

    @Delete
    void delete(Plant plant);

    @Query("DELETE FROM plant_table")
    void nukeTable();
}
