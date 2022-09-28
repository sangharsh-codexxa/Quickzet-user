package com.elluminati.eber.models.responsemodels;

import com.google.gson.annotations.SerializedName;

public class PromoResponse {

    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("promo_apply_for_card")
    private int promoApplyForCard;
    @SerializedName("success")
    private boolean success;
    @SerializedName("promo_id")
    private String promoId;
    @SerializedName("promo_apply_for_cash")
    private int promoApplyForCash;
    @SerializedName("message")
    private String message;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getPromoApplyForCard() {
        return promoApplyForCard;
    }

    public void setPromoApplyForCard(int promoApplyForCard) {
        this.promoApplyForCard = promoApplyForCard;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPromoId() {
        return promoId;
    }

    public void setPromoId(String promoId) {
        this.promoId = promoId;
    }

    public int getPromoApplyForCash() {
        return promoApplyForCash;
    }

    public void setPromoApplyForCash(int promoApplyForCash) {
        this.promoApplyForCash = promoApplyForCash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "PromoResponse{" + "promo_apply_for_card = '" + promoApplyForCard + '\'' + ",success = '" + success + '\'' + ",promo_id = '" + promoId + '\'' + ",promo_apply_for_cash = '" + promoApplyForCash + '\'' + ",message = '" + message + '\'' + "}";
    }
}