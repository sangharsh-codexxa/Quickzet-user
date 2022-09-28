package com.elluminati.eber.models.datamodels;

import com.elluminati.eber.utils.Const;
import com.google.gson.annotations.SerializedName;

public class AdminSettings {

    @SerializedName("image_base_url")
    private String imageBaseUrl;

    @SerializedName("privacy_policy_url")
    private String privacyPolicyUrl;

    @SerializedName("admin_phone")
    private String adminPhone;

    @SerializedName("is_show_estimation_in_user_app")
    private boolean isShowEstimationInUserApp;

    @SerializedName("userPath")
    private boolean userPath;

    @SerializedName("android_user_app_google_key")
    private String androidUserAppGoogleKey;

    @SerializedName("android_places_autocomplete_key")
    private String androidPlacesAutoCompleteKey;

    @SerializedName("stripe_publishable_key")
    private String stripePublishableKey;

    @SerializedName("android_user_app_version_code")
    private String androidUserAppVersionCode;

    @SerializedName("contactUsEmail")
    private String contactUsEmail;

    @SerializedName("android_user_app_force_update")
    private boolean androidUserAppForceUpdate;

    @SerializedName("twilio_number")
    private String twilioNumber;

    @SerializedName("is_tip")
    private boolean isTip;

    @SerializedName("userEmailVerification")
    private boolean userEmailVerification;

    @SerializedName("userSms")
    private boolean userSms;

    @SerializedName("success")
    private boolean success;

    @SerializedName("scheduled_request_pre_start_minute")
    private int scheduledRequestPreStartMinute;

    @SerializedName("terms_and_condition_url")
    private String termsAndConditionUrl;

    @SerializedName("twilio_call_masking")
    private boolean twilioCallMasking;

    @SerializedName("is_user_social_login")
    private boolean isUserSocialLogin;

    @SerializedName("minimum_phone_number_length")
    private int minimumPhoneNumberLength = Const.PhoneNumber.MINIMUM_PHONE_NUMBER_LENGTH;

    @SerializedName("maximum_phone_number_length")
    private int maximumPhoneNumberLength = Const.PhoneNumber.MAXIMUM_PHONE_NUMBER_LENGTH;

    @SerializedName("is_split_payment")
    private boolean isSplitPayment;

    @SerializedName("max_split_user")
    private int maxSplitUser;
    @SerializedName("is_allow_multiple_stop")
    private boolean isAllowMultipleStops;

    @SerializedName("multiple_stop_count")
    private int multipleStopCount;

    public boolean isTwilioCallMasking() {
        return twilioCallMasking;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public boolean isUserPath() {
        return userPath;
    }

    public String getAndroidUserAppGoogleKey() {
        return androidUserAppGoogleKey;
    }

    public String getStripePublishableKey() {
        return stripePublishableKey;
    }

    public String getAndroidUserAppVersionCode() {
        return androidUserAppVersionCode;
    }

    public String getContactUsEmail() {
        return contactUsEmail;
    }

    public boolean isAndroidUserAppForceUpdate() {
        return androidUserAppForceUpdate;
    }

    public String getTwilioNumber() {
        return twilioNumber;
    }

    public boolean isUserEmailVerification() {
        return userEmailVerification;
    }

    public boolean isUserSms() {
        return userSms;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getScheduledRequestPreStartMinute() {
        return scheduledRequestPreStartMinute;
    }

    public String getTermsAndConditionUrl() {
        return termsAndConditionUrl;
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public boolean isShowEstimationInUserApp() {
        return isShowEstimationInUserApp;
    }

    public String getAndroidPlacesAutoCompleteKey() {
        return androidPlacesAutoCompleteKey;
    }

    public String getPrivacyPolicyUrl() {
        return privacyPolicyUrl;
    }

    public void setPrivacyPolicyUrl(String privacyPolicyUrl) {
        this.privacyPolicyUrl = privacyPolicyUrl;
    }

    public boolean isUserSocialLogin() {
        return isUserSocialLogin;
    }

    public int getMinimumPhoneNumberLength() {
        return minimumPhoneNumberLength;
    }

    public void setMinimumPhoneNumberLength(int minimumPhoneNumberLength) {
        this.minimumPhoneNumberLength = minimumPhoneNumberLength;
    }

    public int getMaximumPhoneNumberLength() {
        return maximumPhoneNumberLength;
    }

    public void setMaximumPhoneNumberLength(int maximumPhoneNumberLength) {
        this.maximumPhoneNumberLength = maximumPhoneNumberLength;
    }

    public boolean isSplitPayment() {
        return isSplitPayment;
    }

    public void setSplitPayment(boolean splitPayment) {
        isSplitPayment = splitPayment;
    }

    public int getMaxSplitUser() {
        return maxSplitUser;
    }

    public void setMaxSplitUser(int maxSplitUser) {
        this.maxSplitUser = maxSplitUser;
    }

    public void setAllowMultipleStops(boolean allowMultipleStops) {
        isAllowMultipleStops = allowMultipleStops;
    }

    public boolean isAllowMultipleStops() {
        return isAllowMultipleStops;
    }

    public void setMultipleStopCount(int multipleStopCount) {
        this.multipleStopCount = multipleStopCount;
    }

    public int getMultipleStopCount() {
        return multipleStopCount;
    }
}