package com.elluminati.eber.models.datamodels;

import com.google.gson.annotations.SerializedName;


public class EmergencyContact {

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("phone")
    private String phone;

    @SerializedName("name")
    private String name;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("_id")
    private String id;

    @SerializedName("is_always_share_ride_detail")
    private int isAlwaysShareRideDetail;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIsAlwaysShareRideDetail() {
        return isAlwaysShareRideDetail;
    }

    public void setIsAlwaysShareRideDetail(int isAlwaysShareRideDetail) {
        this.isAlwaysShareRideDetail = isAlwaysShareRideDetail;
    }

    @Override
    public String toString() {
        return "EmergencyContact{" + "updated_at = '" + updatedAt + '\'' + ",user_id = '" + userId + '\'' + ",phone = '" + phone + '\'' + ",name = '" + name + '\'' + ",created_at = '" + createdAt + '\'' + ",_id = '" + id + '\'' + ",is_always_share_ride_detail = '" + isAlwaysShareRideDetail + '\'' + "}";
    }
}