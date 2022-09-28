package com.elluminati.eber.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ravi Bhalodi on 09,March,2020 in Elluminati
 */
public class CancelTripResponse {


    @SerializedName("error")
    private String error;

    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("error_code")
    private int errorCode;

    @SerializedName("payment_status")
    private int paymentStatus;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("client_secret")
    private String clientSecret;

    @SerializedName("required_param")
    private String requiredParam;

    @SerializedName("reference")
    private String reference;

    @SerializedName("error_message")
    private String errorMessage;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getError() {
        return error;
    }

    public String getRequiredParam() {
        return requiredParam;
    }

    public String getReference() {
        return reference;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
