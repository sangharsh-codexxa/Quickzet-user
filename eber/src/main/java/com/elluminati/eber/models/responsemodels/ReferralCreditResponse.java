package com.elluminati.eber.models.responsemodels;

import com.google.gson.annotations.SerializedName;

public class ReferralCreditResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("referral_credit")
    private double referralCredit;
    @SerializedName("currency")
    private String currency;

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

    public double getReferralCredit() {
        return referralCredit;
    }

    public void setReferralCredit(double referralCredit) {
        this.referralCredit = referralCredit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
