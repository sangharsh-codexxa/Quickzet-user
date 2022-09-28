package com.elluminati.eber.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.elluminati.eber.BuildConfig;

/**
 * Created by elluminati on 29-03-2016.
 */
public class PreferenceHelper {

    /**
     * Preference Const
     */
    public static final String PREF_NAME = "TaxiAnyTimeAnyWhereClient";
    private static final PreferenceHelper preferenceHelper = new PreferenceHelper();
    private static SharedPreferences app_preferences;
    private final String USER_ID = "user_id";
    private final String EMAIL = "email";
    private final String DEVICE_TOKEN = "device_token";
    private final String SESSION_TOKEN = "session_token";
    private final String CONTACT = "contact";
    private final String REFERRAL_CODE = "referral_code";
    private final String SOCIAL_ID = "social_id";
    private final String FIRST_NAME = "first_name";
    private final String LAST_NAME = "last_name";
    private final String ADDRESS = "address";
    private final String PROFILE_PIC = "profile_pic";
    private final String TRIP_ID = "trip_id";
    private final String COUNTRY_CODE = "country_code";
    private final String HOME_ADDRESS = "home_address";
    private final String WORK_ADDRESS = "work_address";
    private final String PAYMENT_CARD_AVAILABLE = "payment_card_available";
    private final String PAYMENT_CASH_AVAILABLE = "payment_cash_available";
    private final String IS_PROMO_APPLY_FOR_CASH = "is_promo_apply_for_cash";
    private final String IS_PROMO_APPLY_FOR_CARD = "is_promo_apply_for_card";
    private final String IS_SHOW_INVOICE = "is_show_invoice";
    private final String GOOGLE_SERVER_KEY = "google_server_key";
    private final String STRIPE_PUBLIC_KEY = "stripe_public_key";
    private final String USER_EMAIL_VERIFICATION = "user_email_verification";
    private final String IS_USER_SOCIAL_LOGIN = "is_user_social_login";
    private final String USER_SMS_VERIFICATION = "user_sms_verification";
    private final String CONTACT_US_EMAIL = "contact_us_email";
    private final String SCHEDULED_MINUTE = "scheduled_minute";
    private final String IS_TRIP_STATUS_SOUND_ON = "is_trip_status_sound_on";
    private final String IS_DRIVER_ARRIVED_SOUND_ON = "is_driver_arrived_sound_on";
    private final String IS_PUSH_SOUND_ON = "is_push_sound_on";
    private final String IS_PATH_DRAW = "is_path_draw";
    private final String IS_APPROVED = "is_approved";
    private final String HOT_LINE_APP_ID = "hot_line_app_id";
    private final String HOT_LINE_APP_KEY = "hot_line_app_key";
    private final String MANUFACTURER_DEPENDENCY = "manufacturer_dependency";
    private final String IS_ALL_DOCUMENT_UPLOAD = "is_all_document_upload";
    private final String TWILIO_NUMBER = "twilio_number";
    private final String IS_HAVE_REFERRAL = "is_have_referral";
    private final String IS_REFERRAL_APPLY = "is_referral_apply";
    private final String SELECTED_PAYMENT_TYPE = "selected_payment_type";
    private final String CITY = "city";
    private final String LANGUAGE = "language";
    private final String ADMIN_PHONE = "admin_phone";
    private final String T_AND_C = "t_and_c";
    private final String POLICY = "policy";
    private final String TWILIO_CALL_MASKING = "twilio_call_masking";
    private final String IS_SHOW_ESTIMATION = "is_show_estimation";
    private final String ANDROID_PLACES_AUTOCOMPLETE_KEY = "android_places_autocomplete_key";
    private final String FIREBASE_USER_TOKEN = "firebase_user_token";
    private final String MINIMUM_PHONE_NUMBER_LENGTH = "minimum_phone_number_length";
    private final String MAXIMUM_PHONE_NUMBER_LENGTH = "maximum_phone_number_length";
    private final String BASE_URL = "base_url";
    private final String IS_SPLIT_PAYMENT = "is_split_payment";
    private final String MAX_SPLIT_USER = "max_split_user";
    private final String IS_ALLOW_MULTIPLE_STOPS = "is_allow_multiple_stops";
    private final String MULTIPLE_STOPS_COUNT = "multiple_stops_count";

    private PreferenceHelper() {
    }

    public static PreferenceHelper getInstance(Context context) {
        app_preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferenceHelper;
    }

    public void putDeviceToken(String deviceToken) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString(DEVICE_TOKEN, deviceToken);
        editor.commit();
    }

    public void putLanguageCode(String code) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(LANGUAGE, code);
        edit.commit();
    }

    public String getLanguageCode() {
        return app_preferences.getString(LANGUAGE, "");
    }

    public String getDeviceToken() {
        return app_preferences.getString(DEVICE_TOKEN, null);
    }

    public void putEmail(String email) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(EMAIL, email);
        edit.commit();
    }

    public String getEmail() {
        return app_preferences.getString(EMAIL, null);
    }

    public void putUserId(String userId) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(USER_ID, userId);
        edit.commit();
    }

    public String getUserId() {
        return app_preferences.getString(USER_ID, null);
    }

    public void putSocialId(String id) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(SOCIAL_ID, id);
        edit.commit();
    }

    public String getSocialId() {
        return app_preferences.getString(SOCIAL_ID, null);
    }

    public void putSessionToken(String sessionToken) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(SESSION_TOKEN, sessionToken);
        edit.commit();
    }

    public String getSessionToken() {
        return app_preferences.getString(SESSION_TOKEN, null);
    }

    public void putContact(String contact) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(CONTACT, contact);
        edit.commit();
    }

    public String getContact() {
        return app_preferences.getString(CONTACT, null);
    }

    public void putReferralCode(String referralCode) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(REFERRAL_CODE, referralCode);
        edit.commit();
    }

    public String getReferralCode() {
        return app_preferences.getString(REFERRAL_CODE, null);
    }


    public void putAddress(String address) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(ADDRESS, address);
        edit.commit();
    }

    public String getAddress() {
        return app_preferences.getString(ADDRESS, "");
    }


    public void putFirstName(String firstName) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(FIRST_NAME, firstName);
        edit.commit();
    }

    public String getFirstName() {
        return app_preferences.getString(FIRST_NAME, null);
    }

    public void putLastName(String lastName) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(LAST_NAME, lastName);
        edit.commit();
    }

    public String getLastName() {
        return app_preferences.getString(LAST_NAME, null);
    }

    public void putProfilePic(String profilePic) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(PROFILE_PIC, profilePic);
        edit.commit();
    }

    public String getProfilePic() {
        return app_preferences.getString(PROFILE_PIC, null);
    }

    public void putTripId(String TripID) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(TRIP_ID, TripID);
        edit.commit();
    }

    public String getTripId() {
        return app_preferences.getString(TRIP_ID, null);
    }

    public void putHomeAddress(String homeAddress) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(HOME_ADDRESS, homeAddress);
        edit.commit();
    }

    public String getHomeAddress() {
        return app_preferences.getString(HOME_ADDRESS, "");
    }

    public void putWorkAddress(String workAddress) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(WORK_ADDRESS, workAddress);
        edit.commit();
    }

    public String getWorkAddress() {
        return app_preferences.getString(WORK_ADDRESS, "");
    }

    public void putIsShowInvoice(boolean isShowInvoice) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(IS_SHOW_INVOICE, isShowInvoice);
        edit.commit();
    }

    public boolean getIsShowInvoice() {
        return app_preferences.getBoolean(IS_SHOW_INVOICE, false);
    }

    public void putCountryPhoneCode(String code) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(COUNTRY_CODE, code);
        edit.commit();
    }

    public String getCountryPhoneCode() {
        return app_preferences.getString(COUNTRY_CODE, null);
    }

    public void putPaymentCardAvailable(int card) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putInt(PAYMENT_CARD_AVAILABLE, card);
        edit.commit();
    }

    public int getPaymentCardAvailable() {
        return app_preferences.getInt(PAYMENT_CARD_AVAILABLE, 0);
    }

    public void putPaymentCashAvailable(int Cash) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putInt(PAYMENT_CASH_AVAILABLE, Cash);
        edit.commit();
    }

    public int getPaymentCashAvailable() {
        return app_preferences.getInt(PAYMENT_CASH_AVAILABLE, 0);
    }

    public void putGoogleServerKey(String serverKey) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(GOOGLE_SERVER_KEY, serverKey);
        edit.commit();
    }

    public String getGoogleServerKey() {
        return app_preferences.getString(GOOGLE_SERVER_KEY, null);
    }

    public void putGoogleAutoCompleteKey(String serverKey) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(ANDROID_PLACES_AUTOCOMPLETE_KEY, serverKey);
        edit.commit();
    }

    public String getGoogleAutoCompleteKey() {
        return app_preferences.getString(ANDROID_PLACES_AUTOCOMPLETE_KEY, null);
    }

    public void putStripePublicKey(String stripeKey) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(STRIPE_PUBLIC_KEY, stripeKey);
        edit.commit();
    }

    public String getStripePublicKey() {
        return app_preferences.getString(STRIPE_PUBLIC_KEY, null);
    }

    public void putIsUserEmailVerification(boolean isEmailVerify) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(USER_EMAIL_VERIFICATION, isEmailVerify);
        edit.commit();
    }

    public boolean getIsUserEmailVerification() {
        return app_preferences.getBoolean(USER_EMAIL_VERIFICATION, false);
    }

    public void putIsUserSocialLogin(boolean isEmailVerify) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(IS_USER_SOCIAL_LOGIN, isEmailVerify);
        edit.commit();
    }

    public boolean getIsUserSocialLogin() {
        return app_preferences.getBoolean(IS_USER_SOCIAL_LOGIN, false);
    }

    public void putIsUserSMSVerification(boolean isSMSVerify) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(USER_SMS_VERIFICATION, isSMSVerify);
        edit.commit();
    }

    public boolean getIsUserSMSVerification() {
        return app_preferences.getBoolean(USER_SMS_VERIFICATION, false);
    }

    public void putContactUsEmail(String email) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(CONTACT_US_EMAIL, email);
        edit.commit();
    }

    public String getContactUsEmail() {
        return app_preferences.getString(CONTACT_US_EMAIL, null);
    }

    public void putAdminPhone(String phone) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(ADMIN_PHONE, phone);
        edit.commit();
    }

    public String getAdminPhone() {
        return app_preferences.getString(ADMIN_PHONE, null);
    }

    public void putScheduledMinute(int minute) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putInt(SCHEDULED_MINUTE, minute);
        edit.commit();
    }

    public int getScheduledMinute() {
        return app_preferences.getInt(SCHEDULED_MINUTE, 0);
    }

    public void putPromoApplyForCard(int isApply) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putInt(IS_PROMO_APPLY_FOR_CARD, isApply);
        edit.commit();
    }

    public int getPromoApplyForCard() {
        return app_preferences.getInt(IS_PROMO_APPLY_FOR_CARD, 0);
    }

    public void putPromoApplyForCash(int isApply) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putInt(IS_PROMO_APPLY_FOR_CASH, isApply);
        edit.commit();
    }

    public int getPromoApplyForCash() {
        return app_preferences.getInt(IS_PROMO_APPLY_FOR_CASH, 0);
    }

    public void putIsTripStatusSoundOn(boolean isSoundOn) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(IS_TRIP_STATUS_SOUND_ON, isSoundOn);
        edit.commit();
    }

    public boolean getIsTripStatusSoundOn() {
        return app_preferences.getBoolean(IS_TRIP_STATUS_SOUND_ON, true);
    }

    public void putIsDriverArrivedSoundOn(boolean isSoundOn) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(IS_DRIVER_ARRIVED_SOUND_ON, isSoundOn);
        edit.commit();
    }

    public boolean getIsDriverArrivedSoundOn() {
        return app_preferences.getBoolean(IS_DRIVER_ARRIVED_SOUND_ON, true);
    }

    public void putIsPushNotificationSoundOn(boolean isSoundOn) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(IS_PUSH_SOUND_ON, isSoundOn);
        edit.commit();
    }

    public boolean getIsPushNotificationSoundOn() {
        return app_preferences.getBoolean(IS_PUSH_SOUND_ON, true);
    }

    public void putIsPathDraw(boolean isPthDraw) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(IS_PATH_DRAW, isPthDraw);
        edit.commit();
    }

    public boolean getIsPathDraw() {
        return app_preferences.getBoolean(IS_PATH_DRAW, true);
    }

    public void putIsApproved(int is_approved) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putInt(IS_APPROVED, is_approved);
        edit.commit();
    }

    public int getIsApproved() {
        return app_preferences.getInt(IS_APPROVED, 0);
    }

    public void putHotLineAppId(String appId) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(HOT_LINE_APP_ID, appId);
        edit.commit();
    }

    public String getHotLineAppId() {
        return app_preferences.getString(HOT_LINE_APP_ID, null);
    }

    public void putHotLineAppKey(String appKey) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(HOT_LINE_APP_KEY, appKey);
        edit.commit();
    }

    public String getHotLineAppKey() {
        return app_preferences.getString(HOT_LINE_APP_KEY, null);
    }

    public void putIsManufacturerDependency(boolean isChecked) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(MANUFACTURER_DEPENDENCY, isChecked);
        edit.commit();
    }

    public boolean getIsManufacturerDependency() {
        return app_preferences.getBoolean(MANUFACTURER_DEPENDENCY, true);
    }

    public void putAllDocUpload(int is_Upload) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putInt(IS_ALL_DOCUMENT_UPLOAD, is_Upload);
        edit.commit();
    }

    public int getAllDocUpload() {
        return app_preferences.getInt(IS_ALL_DOCUMENT_UPLOAD, 0);
    }

    public void putTwilioNumber(String number) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(TWILIO_NUMBER, number);
        edit.commit();
    }

    public String getTwilioNumber() {
        return app_preferences.getString(TWILIO_NUMBER, "");
    }

    public void putIsHaveReferral(boolean isHaveReferral) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(IS_HAVE_REFERRAL, isHaveReferral);
        edit.commit();
    }

    public boolean getIsHaveReferral() {
        return app_preferences.getBoolean(IS_HAVE_REFERRAL, true);
    }

    public void putIsApplyReferral(int isApplyReferral) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putInt(IS_REFERRAL_APPLY, isApplyReferral);
        edit.commit();
    }

    public int getIsApplyReferral() {
        return app_preferences.getInt(IS_REFERRAL_APPLY, 0);
    }

    public void putSelectedPaymentType(int paymentType) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putInt(SELECTED_PAYMENT_TYPE, paymentType);
        edit.commit();
    }

    public int getSelectedPaymentType() {
        return app_preferences.getInt(SELECTED_PAYMENT_TYPE, 3);
    }

    public void putUserCity(String city) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(CITY, city);
        edit.commit();
    }

    public String getUserCity() {
        return app_preferences.getString(CITY, "");
    }

    public void putTermsANdConditions(String tandc) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(T_AND_C, tandc);
        edit.commit();
    }

    public String getTermsANdConditions() {
        return app_preferences.getString(T_AND_C, null);
    }

    public void putPolicy(String policy) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(POLICY, policy);
        edit.commit();
    }

    public String getPolicy() {
        return app_preferences.getString(POLICY, null);
    }

    public void logout() {
        putSessionToken(null);
        putUserId(null);
        putFirebaseUserToken("");
    }

    public void putTwilioCallMaskEnable(boolean isEnable) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(TWILIO_CALL_MASKING, isEnable);
        edit.commit();
    }

    public boolean getTwilioCallMaskEnable() {
        return app_preferences.getBoolean(TWILIO_CALL_MASKING, false);
    }

    public void putIsShowEstimation(boolean isShow) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(IS_SHOW_ESTIMATION, isShow);
        edit.commit();
    }

    public boolean getIsShowEstimation() {
        return app_preferences.getBoolean(IS_SHOW_ESTIMATION, false);
    }

    public void putFirebaseUserToken(String firebaseUserToken) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(FIREBASE_USER_TOKEN, firebaseUserToken);
        edit.commit();
    }

    public String getFirebaseUserToken() {
        return app_preferences.getString(FIREBASE_USER_TOKEN, null);
    }

    public void putBaseUrl(String baseUrl) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putString(BASE_URL, baseUrl);
        edit.commit();
    }

    public String getBaseUrl() {
        return app_preferences.getString(BASE_URL, BuildConfig.BASE_URL);
    }

    public void putMinimumPhoneNumberLength(int length) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putInt(MINIMUM_PHONE_NUMBER_LENGTH, length);
        editor.commit();
    }

    public int getMinimumPhoneNumberLength() {
        return app_preferences.getInt(MINIMUM_PHONE_NUMBER_LENGTH, Const.PhoneNumber.MINIMUM_PHONE_NUMBER_LENGTH);
    }

    public void putMaximumPhoneNumberLength(int length) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putInt(MAXIMUM_PHONE_NUMBER_LENGTH, length);
        editor.commit();
    }

    public int getMaximumPhoneNumberLength() {
        return app_preferences.getInt(MAXIMUM_PHONE_NUMBER_LENGTH, Const.PhoneNumber.MAXIMUM_PHONE_NUMBER_LENGTH);
    }

    public void putIsSplitPayment(boolean isSplitPaymentEnable) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putBoolean(IS_SPLIT_PAYMENT, isSplitPaymentEnable);
        editor.commit();
    }

    public boolean getIsSplitPayment() {
        return app_preferences.getBoolean(IS_SPLIT_PAYMENT, false);
    }

    public void putAllowMultipleStops(boolean isAllowMultipleStops) {
        SharedPreferences.Editor edit = app_preferences.edit();
        edit.putBoolean(IS_ALLOW_MULTIPLE_STOPS, isAllowMultipleStops);
        edit.commit();
    }

    public boolean getAllowMultipleStops() {
        return app_preferences.getBoolean(IS_ALLOW_MULTIPLE_STOPS, false);
    }

    public void putMultipleStopsCount(int multipleStopsCount) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putInt(MULTIPLE_STOPS_COUNT, multipleStopsCount);
        editor.commit();
    }

    public int getMultipleStopsCount() {
        return app_preferences.getInt(MULTIPLE_STOPS_COUNT, 0);
    }

    public void putMaxSplitUser(int maxSplitUser) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putInt(MAX_SPLIT_USER, maxSplitUser);
        editor.commit();
    }

    public int getMaxSplitUser() {
        return app_preferences.getInt(MAX_SPLIT_USER, 0);
    }
}