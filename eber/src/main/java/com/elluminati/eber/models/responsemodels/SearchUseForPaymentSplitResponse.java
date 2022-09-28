package com.elluminati.eber.models.responsemodels;

import com.elluminati.eber.models.datamodels.FavouriteDriver;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.google.gson.annotations.SerializedName;

public class SearchUseForPaymentSplitResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("success")
    private boolean success;
    @SerializedName("search_user_detail")
    private SplitPaymentRequest searchUserDetail;

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

    public SplitPaymentRequest getSearchUserDetail() {
        return searchUserDetail;
    }

    public void setSearchUserDetail(SplitPaymentRequest searchUserDetail) {
        this.searchUserDetail = searchUserDetail;
    }
}