package com.elluminati.eber.models.datamodels;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RoutesItem{

    @SerializedName("legs")
    private List<LegsItem> legs;

    public List<LegsItem> getLegs(){
        return legs;
    }
}