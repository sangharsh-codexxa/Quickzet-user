package com.elluminati.eber.parse;


import com.elluminati.eber.models.datamodels.AdminSettings;
import com.elluminati.eber.models.datamodels.Document;
import com.elluminati.eber.models.datamodels.UserData;
import com.elluminati.eber.models.responsemodels.AddWalletResponse;
import com.elluminati.eber.models.responsemodels.AllEmergencyContactsResponse;
import com.elluminati.eber.models.responsemodels.CancelTripResponse;
import com.elluminati.eber.models.responsemodels.CardsResponse;
import com.elluminati.eber.models.responsemodels.CountriesResponse;
import com.elluminati.eber.models.responsemodels.CreateTripResponse;
import com.elluminati.eber.models.responsemodels.DocumentResponse;
import com.elluminati.eber.models.responsemodels.ETAResponse;
import com.elluminati.eber.models.responsemodels.EmergencyContactResponse;
import com.elluminati.eber.models.responsemodels.FavouriteDriverResponse;
import com.elluminati.eber.models.responsemodels.GenerateFirebaseTokenResponse;
import com.elluminati.eber.models.responsemodels.InvoiceResponse;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.models.responsemodels.LanguageResponse;
import com.elluminati.eber.models.responsemodels.PromoResponse;
import com.elluminati.eber.models.responsemodels.ProviderDetailResponse;
import com.elluminati.eber.models.responsemodels.ProviderLocationResponse;
import com.elluminati.eber.models.responsemodels.SaveAddressResponse;
import com.elluminati.eber.models.responsemodels.SchedulesTripResponse;
import com.elluminati.eber.models.responsemodels.SearchUseForPaymentSplitResponse;
import com.elluminati.eber.models.responsemodels.SettingsDetailsResponse;
import com.elluminati.eber.models.responsemodels.TripHistoryDetailResponse;
import com.elluminati.eber.models.responsemodels.TripHistoryResponse;
import com.elluminati.eber.models.responsemodels.TripPathResponse;
import com.elluminati.eber.models.responsemodels.TripResponse;
import com.elluminati.eber.models.responsemodels.TypesResponse;
import com.elluminati.eber.models.responsemodels.UpdateDestinationResponse;
import com.elluminati.eber.models.responsemodels.UserDataResponse;
import com.elluminati.eber.models.responsemodels.VerificationResponse;
import com.elluminati.eber.models.responsemodels.WalletHistoryResponse;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("userslogin")
    Call<UserDataResponse> login(@Body RequestBody requestBody);

    @Multipart
    @POST("userupdate")
    Call<UserDataResponse> updateProfile(@Part MultipartBody.Part file, @PartMap() Map<String, RequestBody> partMap);

    @Multipart
    @POST("userregister")
    Call<UserDataResponse> register(@Part MultipartBody.Part file, @PartMap() Map<String, RequestBody> partMap);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("createtrip")
    Call<CreateTripResponse> createTrip(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("canceltrip")
    Call<CancelTripResponse> cancelTrip(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("cards")
    Call<CardsResponse> getCards(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("addcard")
    Call<IsSuccessResponse> addCard(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("delete_card")
    Call<IsSuccessResponse> deleteCard(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("typelist_selectedcountrycity")
    Call<TypesResponse> getVehicleTypes(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("logout")
    Call<IsSuccessResponse> logout(@Body RequestBody requestBody);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("apply_referral_code")
    Call<IsSuccessResponse> applyReferralCode(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("usergettripstatus")
    Call<TripResponse> getTripStatus(@Body RequestBody requestBody);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("userhistory")
    Call<TripHistoryResponse> getTripHistory(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("usertripdetail")
    Call<TripHistoryDetailResponse> getTripHistoryDetail(@Body RequestBody requestBody);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("usergiverating")
    Call<IsSuccessResponse> giveRating(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("get_provider_info")
    Call<ProviderDetailResponse> getProviderDetail(@Body RequestBody requestBody);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("getproviderlatlong")
    Call<ProviderLocationResponse> getProviderLocation(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("getnearbyprovider")
    Call<ProviderDetailResponse> getNearbyProviders(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("getfareestimate")
    Call<ETAResponse> getETAForeTrip(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("apply_promo_code")
    Call<PromoResponse> applyPromoCode(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("getuserinvoice")
    Call<InvoiceResponse> getInvoice(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("card_selection")
    Call<IsSuccessResponse> setSelectedCard(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("getfuturetrip")
    Call<SchedulesTripResponse> getSchedulesTrip(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("usersetdestination")
    Call<UpdateDestinationResponse> updateDestination(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("userchangepaymenttype")
    Call<IsSuccessResponse> changePaymentType(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("updateuserdevicetoken")
    Call<IsSuccessResponse> updateDeviceToken(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("forgotpassword")
    Call<IsSuccessResponse> forgotPassword(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("getappkeys")
    Call<AdminSettings> getAppKeys(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("get_user_setting_detail")
    Call<SettingsDetailsResponse> getUserSettingDetail(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("verification")
    Call<VerificationResponse> verification(@Body RequestBody requestBody);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("get_emergency_contact_list")
    Call<AllEmergencyContactsResponse> getEmergencyContacts(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("add_emergency_contact")
    Call<EmergencyContactResponse> addEmergencyContact(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("update_emergency_contact")
    Call<EmergencyContactResponse> updateEmergencyContact(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("delete_emergency_contact")
    Call<IsSuccessResponse> deleteEmergencyContact(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("send_sms_to_emergency_contact")
    Call<IsSuccessResponse> sendEmergencySMS(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("change_user_wallet_status")
    Call<IsSuccessResponse> setUsedWallet(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("pay_payment")
    Call<IsSuccessResponse> payPayment(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @GET("country_list")
    Call<CountriesResponse> getCountries();

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("getuserdocument")
    Call<DocumentResponse> getDocuments(@Body RequestBody requestBody);

    @Multipart
    @POST("uploaduserdocument")
    Call<Document> uploadDocument(@Part MultipartBody.Part file, @PartMap() Map<String, RequestBody> partMap);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("pay_pending_payment")
    Call<IsSuccessResponse> payPendingPayment(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("user_submit_invoice")
    Call<IsSuccessResponse> submitInvoice(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("set_home_address")
    Call<IsSuccessResponse> saveAddress(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("get_home_address")
    Call<SaveAddressResponse> getSaveAddress(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("getlanguages")
    Call<LanguageResponse> getLanguageForTrip(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("check_user_registered")
    Call<VerificationResponse> checkUserRegistered(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("get_otp")
    Call<VerificationResponse> getOtp(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("update_password")
    Call<IsSuccessResponse> updatePassword(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("get_wallet_history")
    Call<WalletHistoryResponse> getWalletHistory(@Body RequestBody requestBody);

    @GET("api/distancematrix/json")
    Call<ResponseBody> getGoogleDistanceMatrix(@QueryMap Map<String, String> stringMap);

    @GET("api/geocode/json")
    Call<ResponseBody> getGoogleGeocode(@QueryMap Map<String, String> stringMap);

    @GET("api/directions/json")
    Call<ResponseBody> getGoogleDirection(@QueryMap Map<String, String> stringMap);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("getgooglemappath")
    Call<TripPathResponse> getTripPath(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("add_wallet_amount")
    Call<AddWalletResponse> addWalletAmount(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("getuserdetail")
    Call<UserData> getUserDetail(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("twilio_voice_call")
    Call<IsSuccessResponse> twilioCall(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("user_accept_reject_corporate_request")
    Call<IsSuccessResponse> respondsCorporateRequest(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("add_favourite_driver")
    Call<IsSuccessResponse> addFavouriteDriver(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("get_favourite_driver")
    Call<FavouriteDriverResponse> getFavouriteDriver(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("remove_favourite_driver")
    Call<IsSuccessResponse> removeFavouriteDriver(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("get_all_driver_list")
    Call<FavouriteDriverResponse> getDrivers(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("get_user_referal_credit")
    Call<IsSuccessResponse> getReferralCredit(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("remove_promo_code")
    Call<IsSuccessResponse> removePromoCode(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("setgooglemappath")
    Call<IsSuccessResponse> setTripPath(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("get_stripe_add_card_intent")
    Call<CardsResponse> getStripSetupIntent(@Body HashMap<String, Object> map);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("get_stripe_payment_intent")
    Call<CardsResponse> getStripPaymentIntent(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("pay_stripe_intent_payment")
    Call<IsSuccessResponse> getPayStripPaymentIntentPayment(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("pay_tip_payment")
    Call<IsSuccessResponse> payTipPayment(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("send_paystack_required_detail")
    Call<CardsResponse> sendPayStackRequiredDetails(@Body HashMap<String, Object> map);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("generate_firebase_access_token")
    Call<GenerateFirebaseTokenResponse> generateFirebaseAccessToken(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("pay_by_other_payment_mode")
    Call<IsSuccessResponse> payByOtherPaymentMode(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("search_user_for_split_payment")
    Call<SearchUseForPaymentSplitResponse> searchUserForSplitPayment(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("send_split_payment_request")
    Call<IsSuccessResponse> sendSplitPaymentRequest(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("remove_split_payment_request")
    Call<IsSuccessResponse> removeSplitPaymentRequest(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("accept_or_reject_split_payment_request")
    Call<IsSuccessResponse> acceptOrRejectSplitPaymentRequest(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("update_split_payment_payment_mode")
    Call<IsSuccessResponse> updateSplitPaymentPaymentMode(@Body RequestBody requestBody);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("delete_user")
    Call<IsSuccessResponse> deleteUser(@Body RequestBody requestBody);
}