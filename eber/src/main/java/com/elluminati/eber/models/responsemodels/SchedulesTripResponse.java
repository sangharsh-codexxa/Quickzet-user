package com.elluminati.eber.models.responsemodels;

import com.elluminati.eber.models.datamodels.Trip;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SchedulesTripResponse {

    @SerializedName("success")
    private boolean success;
    @SerializedName("scheduledtrip")
    private List<Trip> scheduledTrips;
    @SerializedName("message")
    private String message;
    @SerializedName("error_code")
    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<Trip> getScheduledTrips() {
        return scheduledTrips;
    }

    public void setScheduledTrips(List<Trip> scheduledTrips) {
        this.scheduledTrips = scheduledTrips;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SchedulesTripResponse{" + "success = '" + success + '\'' + ",scheduledtrip = '" + scheduledTrips + '\'' + ",message = '" + message + '\'' + "}";
    }
}