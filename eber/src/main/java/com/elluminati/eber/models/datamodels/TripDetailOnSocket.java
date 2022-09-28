package com.elluminati.eber.models.datamodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TripDetailOnSocket {
    @SerializedName("is_trip_updated")
    private boolean isTripUpdated;
    @SerializedName("location")
    private List<Double> providerLocations;
    @SerializedName("total_time")
    private double totalTime;
    @SerializedName("total_distance")
    private double totalDistance;
    @SerializedName("bearing")
    private float bearing;

    public List<Double> getProviderLocations() {
        return providerLocations;
    }

    public void setProviderLocations(List<Double> providerLocations) {
        this.providerLocations = providerLocations;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public boolean isTripUpdated() {
        return isTripUpdated;
    }

    public void setTripUpdated(boolean tripUpdated) {
        isTripUpdated = tripUpdated;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }
}
