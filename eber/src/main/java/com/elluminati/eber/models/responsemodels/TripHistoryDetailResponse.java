package com.elluminati.eber.models.responsemodels;

import com.elluminati.eber.models.datamodels.CityType;
import com.elluminati.eber.models.datamodels.Provider;
import com.elluminati.eber.models.datamodels.Trip;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TripHistoryDetailResponse {

    @SerializedName("trip")
    private Trip trip;

    @SerializedName("provider")
    private Provider provider;

    @SerializedName("success")
    private boolean success;

    @SerializedName("startTripToEndTripLocations")
    private List<List<Double>> startTripToEndTripLocations;

    @SerializedName("message")
    private String message;

    @SerializedName("tripservice")
    private CityType tripService;

    @SerializedName("error_code")
    private int errorCode;

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<List<Double>> getStartTripToEndTripLocations() {
        return startTripToEndTripLocations;
    }

    public void setStartTripToEndTripLocations(List<List<Double>> startTripToEndTripLocations) {
        this.startTripToEndTripLocations = startTripToEndTripLocations;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CityType getTripService() {
        return tripService;
    }

    public void setTripService(CityType tripService) {
        this.tripService = tripService;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "TripHistoryDetailResponse{" + "trip = '" + trip + '\'' + ",provider = '" + provider + '\'' + ",success = '" + success + '\'' + ",startTripToEndTripLocations = '" + startTripToEndTripLocations + '\'' + ",message = '" + message + '\'' + ",tripservice = '" + tripService + '\'' + "}";
    }
}