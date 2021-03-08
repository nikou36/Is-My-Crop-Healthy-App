package com.example.ismycrophealthy.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ismycrophealthy.daos.PlantDao;
import com.example.ismycrophealthy.entities.Plant;

@Database(entities = {Plant.class},version = 1)
public abstract class PlantDatabase extends RoomDatabase {
    private static PlantDatabase instance;

    public static synchronized  PlantDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),PlantDatabase.class,"plant_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
    public abstract PlantDao plantDao();
}
