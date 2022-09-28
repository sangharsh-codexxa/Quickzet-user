package com.elluminati.eber.models.datamodels;

import com.google.gson.annotations.SerializedName;

public class SplitPaymentRequest {

    @SerializedName("_id")
    private String id;
    @SerializedName("trip_id")
    private String tripId;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("picture")
    private String picture;
    @SerializedName("phone")
    private String phone;
    @SerializedName("country_phone_code")
    private String countryPhoneCode;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("status")
    private int status = -1;
    @SerializedName("payment_mode")
    private String paymentMode;
    @SerializedName("payment_status")
    private int paymentStatus;
    @SerializedName("payment_intent_id")
    private String paymentIntentId;
    @SerializedName("currency")
    private String currency;
    @SerializedName("total")
    private double total;
    @SerializedName("is_trip_end")
    private int isTripEnd;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryPhoneCode() {
        return countryPhoneCode;
    }

    public void setCountryPhoneCode(String countryPhoneCode) {
        this.countryPhoneCode = countryPhoneCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getIsTripEnd() {
        return isTripEnd;
    }

    public void setIsTripEnd(int isTripEnd) {
        this.isTripEnd = isTripEnd;
    }
}