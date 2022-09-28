package com.elluminati.eber.models.datamodels;

import com.google.gson.annotations.SerializedName;

public class Polyline{

    @SerializedName("points")
    private String points;

    public String getPoints(){
        return points;
    }
}