package com.elluminati.eber.models.responsemodels;

import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.datamodels.UserData;
import com.google.gson.annotations.SerializedName;

public class UserDataResponse {
    @SerializedName("error_code")
    private int errorCode;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("user_detail")
    private UserData userData;

    @SerializedName("split_payment_request")
    private SplitPaymentRequest splitPaymentRequest;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public SplitPaymentRequest getSplitPaymentRequest() {
        return splitPaymentRequest;
    }

    public void setSplitPaymentRequest(SplitPaymentRequest splitPaymentRequest) {
        this.splitPaymentRequest = splitPaymentRequest;
    }
}
