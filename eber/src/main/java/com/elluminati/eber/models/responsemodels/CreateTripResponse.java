package com.elluminati.eber.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateTripResponse {


    @SerializedName("car_model")
    private String carModel;

    @SerializedName("trip_id")
    private String tripId;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("bio")
    private String bio;

    @SerializedName("message")
    private String message;

    @SerializedName("picture")
    private String picture;

    @SerializedName("service_type")
    private String serviceType;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("phone")
    private String phone;

    @SerializedName("success")
    private boolean success;

    @SerializedName("car_number")
    private String carNumber;

    @SerializedName("provider_id")
    private String providerId;

    @SerializedName("sourceLocation")
    private List<Double> sourceLocation;

    @SerializedName("first_name")
    private String firstName;


    @SerializedName("error_code")
    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public List<Double> getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(List<Double> sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return "CreateTripResponse{" + "car_model = '" + carModel + '\'' + ",trip_id = '" + tripId + '\'' + ",last_name = '" + lastName + '\'' + ",bio = '" + bio + '\'' + ",message = '" + message + '\'' + ",picture = '" + picture + '\'' + ",service_type = '" + serviceType + '\'' + ",user_id = '" + userId + '\'' + ",phone = '" + phone + '\'' + ",success = '" + success + '\'' + ",car_number = '" + carNumber + '\'' + ",provider_id = '" + providerId + '\'' + ",sourceLocation = '" + sourceLocation + '\'' + ",first_name = '" + firstName + '\'' + "}";
    }
}