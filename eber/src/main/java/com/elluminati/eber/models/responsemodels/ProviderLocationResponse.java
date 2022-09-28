package com.elluminati.eber.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderLocationResponse {

    @SerializedName("providerLocation")
    private List<Double> providerLocation;

    @SerializedName("success")
    private boolean success;

    @SerializedName("bearing")
    private double bearing;

    @SerializedName("total_distance")
    private double totalDistance;

    @SerializedName("message")
    private String message;

    @SerializedName("total_time")
    private int totalTime;

    public List<Double> getProviderLocation() {
        return providerLocation;
    }

    public void setProviderLocation(List<Double> providerLocation) {
        this.providerLocation = providerLocation;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return "ProviderLocationResponse{" + "providerLocation = '" + providerLocation + '\'' + ",success = '" + success + '\'' + ",bearing = '" + bearing + '\'' + ",total_distance = '" + totalDistance + '\'' + ",message = '" + message + '\'' + ",total_time = '" + totalTime + '\'' + "}";
    }
}