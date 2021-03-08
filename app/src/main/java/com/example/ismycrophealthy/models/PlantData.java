package com.example.ismycrophealthy.models;

import android.graphics.Bitmap;

public class PlantData {
    private Bitmap plantBitmap;
    private String Date;
    private String Dianosis;

    public PlantData(Bitmap plantBitmap, String date, String dianosis) {
        this.plantBitmap = plantBitmap;
        Date = date;
        Dianosis = dianosis;
    }
    public PlantData(){}

    public Bitmap getPlantBitmap() {
        return plantBitmap;
    }

    public void setPlantBitmap(Bitmap plantBitmap) {
        this.plantBitmap = plantBitmap;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDianosis() {
        return Dianosis;
    }

    public void setDianosis(String dianosis) {
        Dianosis = dianosis;
    }
}
