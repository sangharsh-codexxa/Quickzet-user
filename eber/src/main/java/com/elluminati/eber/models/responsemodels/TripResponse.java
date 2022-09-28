package com.elluminati.eber.models.responsemodels;

import com.elluminati.eber.models.datamodels.CityDetail;
import com.elluminati.eber.models.datamodels.PaymentGateway;
import com.elluminati.eber.models.datamodels.Trip;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TripResponse {

    @SerializedName("time_left_for_tip")
    private int timeLeftForTip;
    @SerializedName("price_for_waiting_time")
    private double priceForWaitingTime;
    @SerializedName("cancellation_fee")
    private double cancellationFee;
    @SerializedName("trip")
    private Trip trip;
    @SerializedName("payment_gateway")
    private List<PaymentGateway> paymentGateway;
    @SerializedName("success")
    private boolean success;
    @SerializedName("waiting_time_start_after_minute")
    private int waitingTimeStartAfterMinute;
    @SerializedName("total_wait_time")
    private int totalWaitTime;
    @SerializedName("map_pin_image_url")
    private String mapPinImageUrl;
    @SerializedName("message")
    private String message;
    @SerializedName("city_detail")
    private CityDetail cityDetail;
    @SerializedName("isPromoUsed")
    private int isPromoUsed;
    @SerializedName("error_code")
    private int errorCode;

    public int getTimeLeftForTip() {
        return timeLeftForTip;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public double getPriceForWaitingTime() {
        return priceForWaitingTime;
    }

    public void setPriceForWaitingTime(double priceForWaitingTime) {
        this.priceForWaitingTime = priceForWaitingTime;
    }

    public double getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(double cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
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

    public int getWaitingTimeStartAfterMinute() {
        return waitingTimeStartAfterMinute;
    }

    public void setWaitingTimeStartAfterMinute(int waitingTimeStartAfterMinute) {
        this.waitingTimeStartAfterMinute = waitingTimeStartAfterMinute;
    }

    public int getTotalWaitTime() {
        return totalWaitTime;
    }

    public void setTotalWaitTime(int totalWaitTime) {
        this.totalWaitTime = totalWaitTime;
    }

    public String getMapPinImageUrl() {
        return mapPinImageUrl;
    }

    public void setMapPinImageUrl(String mapPinImageUrl) {
        this.mapPinImageUrl = mapPinImageUrl;
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

    public int getIsPromoUsed() {
        return isPromoUsed;
    }

    public void setIsPromoUsed(int isPromoUsed) {
        this.isPromoUsed = isPromoUsed;
    }

    @Override
    public String toString() {
        return "TripResponse{" + "price_for_waiting_time = '" + priceForWaitingTime + '\'' + ",cancellation_fee = '" + cancellationFee + '\'' + ",trip = '" + trip + '\'' + ",payment_gateway = '" + paymentGateway + '\'' + ",success = '" + success + '\'' + ",waiting_time_start_after_minute = '" + waitingTimeStartAfterMinute + '\'' + ",total_wait_time = '" + totalWaitTime + '\'' + ",map_pin_image_url = '" + mapPinImageUrl + '\'' + ",message = '" + message + '\'' + ",city_detail = '" + cityDetail + '\'' + ",isPromoUsed = '" + isPromoUsed + '\'' + "}";
    }

}