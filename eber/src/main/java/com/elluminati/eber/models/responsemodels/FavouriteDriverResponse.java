package com.elluminati.eber.models.responsemodels;

import com.elluminati.eber.models.datamodels.FavouriteDriver;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavouriteDriverResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("success")
    private boolean success;
    @SerializedName("provider_list")
    private List<FavouriteDriver> providerList;

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<FavouriteDriver> getProviderList() {
        return providerList;
    }

    public void setProviderList(List<FavouriteDriver> providerList) {
        this.providerList = providerList;
    }

    @Override
    public String toString() {
        return "FavouriteDriverResponse{" + "success = '" + success + '\'' + ",provider_list = '" + providerList + '\'' + "}";
    }
}