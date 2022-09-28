package com.elluminati.eber.models.responsemodels;

import com.elluminati.eber.models.datamodels.Provider;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderDetailResponse {

    @SerializedName("provider")
    private Provider provider;

    @SerializedName("providers")
    private List<Provider> providers;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ProviderDetailResponse{" + "provider = '" + provider + '\'' + ",success = '" + success + '\'' + ",message = '" + message + '\'' + "}";
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }
}