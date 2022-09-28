package com.elluminati.eber.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateDestinationResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("destinationLocation")
    private List<Double> destinationLocation;

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

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<Double> getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(List<Double> destinationLocation) {
        this.destinationLocation = destinationLocation;
    }
}
