package com.elluminati.eber.models.responsemodels;

import com.elluminati.eber.models.datamodels.EmergencyContact;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllEmergencyContactsResponse {


    @SerializedName("emergency_contact_data")
    private List<EmergencyContact> emergencyContactData;

    @SerializedName("success")
    private boolean success;

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

    public List<EmergencyContact> getEmergencyContactData() {
        return emergencyContactData;
    }

    public void setEmergencyContactData(List<EmergencyContact> emergencyContactData) {
        this.emergencyContactData = emergencyContactData;
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
        return "AllEmergencyContactsResponse{" + "emergency_contact_data = '" + emergencyContactData + '\'' + ",success = '" + success + '\'' + ",message = '" + message + '\'' + "}";
    }
}