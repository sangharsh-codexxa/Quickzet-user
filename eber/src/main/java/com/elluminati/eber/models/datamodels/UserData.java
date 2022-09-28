package com.elluminati.eber.models.datamodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserData {

    @SerializedName("is_trip_completed")
    private int isTripCompleted;
    @SerializedName("is_trip_end")
    private int isTripEnd;
    @SerializedName("is_provider_status")
    private int isProviderStatus;
    @SerializedName("is_provider_accepted")
    private int isProviderAccepted;
    @SerializedName("provider_id")
    private String providerId = "";
    @SerializedName("trip_id")
    private String tripId = "";
    @SerializedName("country")
    private String country;
    @SerializedName("app_version")
    private String appVersion;
    @SerializedName("city")
    private String city;
    @SerializedName("is_document_uploaded")
    private int isDocumentUploaded;
    @SerializedName("country_detail")
    private Country countryDetail;
    @SerializedName("is_referral")
    private int isReferral;
    @SerializedName("country_phone_code")
    private String countryPhoneCode;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("email")
    private String email;
    @SerializedName("address")
    private String address;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("message")
    private String message;
    @SerializedName("picture")
    private String picture;
    @SerializedName("token")
    private String token;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("phone")
    private String phone;
    @SerializedName("success")
    private boolean success;
    @SerializedName("social_ids")
    private List<String> socialIds;
    @SerializedName("device_token")
    private String deviceToken;
    @SerializedName("referral_code")
    private String referralCode;
    @SerializedName("is_approved")
    private int isApproved;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("wallet_currency_code")
    private String walletCurrencyCode;
    @SerializedName("corporate_detail")
    private CorporateDetail corporateDetail;

    public String getWalletCurrencyCode() {
        return walletCurrencyCode;
    }

    public void setWalletCurrencyCode(String walletCurrencyCode) {
        this.walletCurrencyCode = walletCurrencyCode;
    }

    public CorporateDetail getCorporateDetail() {
        return corporateDetail;
    }

    public void setCorporateDetail(CorporateDetail corporateDetail) {
        this.corporateDetail = corporateDetail;
    }

    public int getIsTripCompleted() {
        return isTripCompleted;
    }

    public void setIsTripCompleted(int isTripCompleted) {
        this.isTripCompleted = isTripCompleted;
    }

    public int getIsTripEnd() {
        return isTripEnd;
    }

    public void setIsTripEnd(int isTripEnd) {
        this.isTripEnd = isTripEnd;
    }

    public int getIsProviderStatus() {
        return isProviderStatus;
    }

    public void setIsProviderStatus(int isProviderStatus) {
        this.isProviderStatus = isProviderStatus;
    }

    public int getIsProviderAccepted() {
        return isProviderAccepted;
    }

    public void setIsProviderAccepted(int isProviderAccepted) {
        this.isProviderAccepted = isProviderAccepted;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public int getIsDocumentUploaded() {
        return isDocumentUploaded;
    }

    public void setIsDocumentUploaded(int isDocumentUploaded) {
        this.isDocumentUploaded = isDocumentUploaded;
    }


    public Country getCountryDetail() {
        return countryDetail;
    }

    public void setCountryDetail(Country countryDetail) {
        this.countryDetail = countryDetail;
    }


    public int getIsReferral() {
        return isReferral;
    }

    public void setIsReferral(int isReferral) {
        this.isReferral = isReferral;
    }

    public String getCountryPhoneCode() {
        return countryPhoneCode;
    }

    public void setCountryPhoneCode(String countryPhoneCode) {
        this.countryPhoneCode = countryPhoneCode;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getSocialIds() {
        return socialIds;
    }

    public void setSocialIds(List<String> socialIds) {
        this.socialIds = socialIds;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }


}