package com.elluminati.eber.models.responsemodels;

import com.google.gson.annotations.SerializedName;

public class IsSuccessResponse {


    @SerializedName("total_referral_credit")
    private double totalReferralCredit;
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("is_referral")
    private int isReferral;

    public double getTotalReferralCredit() {
        return totalReferralCredit;
    }

    public void setTotalReferralCredit(double totalReferralCredit) {
        this.totalReferralCredit = totalReferralCredit;
    }

    public int getIsReferral() {
        return isReferral;
    }

    public void setIsReferral(int isReferral) {
        this.isReferral = isReferral;
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

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
