package com.elluminati.eber.models.responsemodels;

import com.elluminati.eber.models.datamodels.CityDetail;
import com.elluminati.eber.models.datamodels.CityType;
import com.elluminati.eber.models.datamodels.PaymentGateway;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TypesResponse {

    @SerializedName("server_time")
    private String serverTime;

    @SerializedName("citytypes")
    private List<CityType> cityTypes;

    @SerializedName("payment_gateway")
    private List<PaymentGateway> paymentGateway;

    @SerializedName("success")
    private boolean success;

    @SerializedName("currency")
    private String currency;
    @SerializedName("currencycode")
    private String currencyCode;
    @SerializedName("message")
    private String message;
    @SerializedName("city_detail")
    private CityDetail cityDetail;
    @SerializedName("is_corporate_request")
    private boolean isCorporateRequest;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("pooltypes")
    private List<CityType> poolType;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public boolean isCorporateRequest() {
        return isCorporateRequest;
    }

    public void setCorporateRequest(boolean corporateRequest) {
        isCorporateRequest = corporateRequest;
    }

    public List<CityType> getCityTypes() {
        return cityTypes;
    }

    public void setCityTypes(List<CityType> cityTypes) {
        this.cityTypes = cityTypes;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public List<PaymentGateway> getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(List<PaymentGateway> paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CityDetail getCityDetail() {
        return cityDetail;
    }

    public void setCityDetail(CityDetail cityDetail) {
        this.cityDetail = cityDetail;
    }

    public List<CityType> getPoolType() {
        return poolType;
    }

    public void setPoolType(List<CityType> poolType) {
        this.poolType = poolType;
    }

    @Override
    public String toString() {
        return "TypesResponse{" +
                "serverTime='" + serverTime + '\'' +
                ", cityTypes=" + cityTypes +
                ", paymentGateway=" + paymentGateway +
                ", success=" + success +
                ", currency='" + currency + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", message='" + message + '\'' +
                ", cityDetail=" + cityDetail +
                ", isCorporateRequest=" + isCorporateRequest +
                ", errorCode=" + errorCode +
                ", poolType=" + poolType +
                '}';
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}