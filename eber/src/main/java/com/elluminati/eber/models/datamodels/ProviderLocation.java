package com.elluminati.eber.models.datamodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderLocation {

    @SerializedName("bearing")
    private double bearing;

    @SerializedName("provider_id")
    private String providerId;

    @SerializedName("location")
    private List<Double> location;

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ProviderLocation{" + "bearing = '" + bearing + '\'' + ",provider_id = '" + providerId + '\'' + ",location = '" + location + '\'' + "}";
    }
}