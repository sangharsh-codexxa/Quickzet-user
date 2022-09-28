package com.elluminati.eber.models.datamodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserAddress {

    @SerializedName("home_address")
    private String homeAddress;

    @SerializedName("work_address")
    private String workAddress;

    @SerializedName("home_location")
    private List<Double> homeLocation;

    @SerializedName("_id")
    private String id;

    @SerializedName("work_location")
    private List<Double> workLocation;

    @SerializedName("token")
    private String token;

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public List<Double> getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(List<Double> homeLocation) {
        this.homeLocation = homeLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Double> getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(List<Double> workLocation) {
        this.workLocation = workLocation;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "UserAddress{" + "home_address = '" + homeAddress + '\'' + ",work_address = '" + workAddress + '\'' + ",home_location = '" + homeLocation + '\'' + ",_id = '" + id + '\'' + ",work_location = '" + workLocation + '\'' + ",token = '" + token + '\'' + "}";
    }
}