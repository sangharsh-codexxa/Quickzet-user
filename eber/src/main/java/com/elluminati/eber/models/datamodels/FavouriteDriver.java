package com.elluminati.eber.models.datamodels;

import com.google.gson.annotations.SerializedName;

public class FavouriteDriver {

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("_id")
    private String id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("picture")
    private String picture;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "FavouriteDriver{" + "last_name = '" + lastName + '\'' + ",_id = '" + id + '\'' + ",first_name = '" + firstName + '\'' + ",picture = '" + picture + '\'' + "}";
    }
}