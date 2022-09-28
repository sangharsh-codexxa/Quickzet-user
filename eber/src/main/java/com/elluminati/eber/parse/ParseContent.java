package com.elluminati.eber.parse;

import static com.elluminati.eber.utils.Const.IMAGE_BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;

import com.elluminati.eber.MainActivity;
import com.elluminati.eber.R;
import com.elluminati.eber.models.datamodels.AdminSettings;
import com.elluminati.eber.models.datamodels.CityDetail;
import com.elluminati.eber.models.datamodels.CityType;
import com.elluminati.eber.models.datamodels.Country;
import com.elluminati.eber.models.datamodels.Invoice;
import com.elluminati.eber.models.datamodels.Provider;
import com.elluminati.eber.models.datamodels.Trip;
import com.elluminati.eber.models.datamodels.UserData;
import com.elluminati.eber.models.responsemodels.CreateTripResponse;
import com.elluminati.eber.models.responsemodels.ProviderDetailResponse;
import com.elluminati.eber.models.responsemodels.SettingsDetailsResponse;
import com.elluminati.eber.models.responsemodels.TripResponse;
import com.elluminati.eber.models.responsemodels.TypesResponse;
import com.elluminati.eber.models.responsemodels.UserDataResponse;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.CurrencyHelper;
import com.elluminati.eber.utils.PreferenceHelper;
import com.elluminati.eber.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by elluminati on 03-06-2016.
 */
public class ParseContent {
    private static final String TAG = "ParseContent";
    private static final ParseContent parseContent = new ParseContent();
    public SimpleDateFormat webFormat, webFormatWithLocalTimeZone;
    public SimpleDateFormat dateTimeFormat;
    public SimpleDateFormat timeFormat;
    public SimpleDateFormat timeFormat_am;
    public SimpleDateFormat dateFormat;
    public SimpleDateFormat dateFormatMonth;
    public SimpleDateFormat day;
    public DecimalFormat twoDigitDecimalFormat, timeDecimalFormat, oneDigitDecimalFormat;
    public SimpleDateFormat dateFormatDayDate;
    public SimpleDateFormat dailyEarningDateFormat;
    private Context context;
    private PreferenceHelper preferenceHelper;

    private ParseContent() {
        initDateTimeFormat();
    }

    public static ParseContent getInstance() {
        return parseContent;
    }

    public void getContext(Context context) {
        preferenceHelper = PreferenceHelper.getInstance(context);
        this.context = context;
    }

    public void initDateTimeFormat() {
        webFormat = new SimpleDateFormat(Const.DATE_TIME_FORMAT_WEB, Locale.getDefault());
        webFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateTimeFormat = new SimpleDateFormat(Const.DATE_TIME_FORMAT, Locale.getDefault());
        dateFormat = new SimpleDateFormat(Const.DATE_FORMAT, Locale.getDefault());
        timeFormat = new SimpleDateFormat(Const.TIME_FORMAT, Locale.getDefault());
        timeFormat_am = new SimpleDateFormat(Const.TIME_FORMAT_AM, Locale.getDefault());
        twoDigitDecimalFormat = new DecimalFormat("0.00");
        oneDigitDecimalFormat = new DecimalFormat("0.0");
        timeDecimalFormat = new DecimalFormat("#");
        dateFormatMonth = new SimpleDateFormat(Const.DATE_FORMAT_MONTH, Locale.getDefault());
        day = new SimpleDateFormat(Const.DAY, Locale.getDefault());
        dailyEarningDateFormat = new SimpleDateFormat(Const.DATE_FORMAT_EARNING, Locale.getDefault());
        dateFormatDayDate = new SimpleDateFormat(Const.DAY_DATE_FORMAT, Locale.getDefault());
        webFormatWithLocalTimeZone = new SimpleDateFormat(Const.DATE_TIME_FORMAT_WEB, Locale.getDefault());
        webFormatWithLocalTimeZone.setTimeZone(TimeZone.getDefault());
    }

    public SimpleDateFormat getDateFormatInEnglish(String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH);
    }

    public ArrayList<Country> getRawCountryCodeList() {
        InputStream inputStream = context.getResources().openRawResource(R.raw.country_list);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Country>>() {
        }.getType();
        return gson.fromJson(byteArrayOutputStream.toString(), listType);
    }

    public boolean saveUserData(UserDataResponse dataResponse, boolean isSaveFullData, boolean showSuccessMessage) {
        if (dataResponse.isSuccess()) {
            UserData userData = dataResponse.getUserData();
            preferenceHelper.putUserId(userData.getUserId());
            preferenceHelper.putContact(userData.getPhone());
            preferenceHelper.putFirstName(userData.getFirstName());
            preferenceHelper.putLastName(userData.getLastName());
            preferenceHelper.putProfilePic(IMAGE_BASE_URL + userData.getPicture());
            preferenceHelper.putCountryPhoneCode(userData.getCountryPhoneCode());
            preferenceHelper.putEmail(userData.getEmail());
            if (isSaveFullData) {
                CurrentTrip.getInstance().setCurrencyCode(userData.getWalletCurrencyCode());
                preferenceHelper.putSessionToken(userData.getToken());
                if (userData.getSocialIds() != null && userData.getSocialIds().size() > 0) {
                    preferenceHelper.putSocialId(userData.getSocialIds().get(0));
                } else {
                    preferenceHelper.putSocialId("");
                }
                preferenceHelper.putReferralCode(userData.getReferralCode());
                preferenceHelper.putAllDocUpload(userData.getIsDocumentUploaded());
                preferenceHelper.putIsApproved(userData.getIsApproved());
                preferenceHelper.putIsApplyReferral(userData.getIsReferral());
                if (userData.getCountryDetail() != null) {
                    preferenceHelper.putIsHaveReferral(userData.getCountryDetail().isReferral());
                }
                CurrentTrip.getInstance().setCorporateDetail(userData.getCorporateDetail());
            }

            if (showSuccessMessage) {
                Utils.showMessageToast(dataResponse.getMessage(), context);
            }

            if (dataResponse.getSplitPaymentRequest() != null) {
                CurrentTrip.getInstance().setSplitPaymentRequest(dataResponse.getSplitPaymentRequest());
            }
            return true;
        } else {
            CurrentTrip.getInstance().setCountryCodes(parseContent.getRawCountryCodeList());
            if (dataResponse.getErrorCode() != 0) {
                Utils.showErrorToast(dataResponse.getErrorCode(), context);
            }
            return false;
        }
    }

    public boolean parseTripStatus(TripResponse response) {
        if (response.isSuccess()) {
            CurrentTrip currentTrip = CurrentTrip.getInstance();
            currentTrip.setPriceForWaitingTime(response.getPriceForWaitingTime());
            currentTrip.setTotalWaitTime(response.getTotalWaitTime());
            Trip trip = response.getTrip();
            currentTrip.setIsTripCanceled(trip.getIsTripCancelled());
            currentTrip.setTotal(trip.getTotal());
            currentTrip.setPromoPayment(trip.getPromoPayment());
            currentTrip.setReferralPayment(trip.getReferralPayment());
            currentTrip.setTotalAfterSurgeFees(trip.getTotalAfterSurgeFees());
            currentTrip.setFixedRate(trip.isFixedFare());
            currentTrip.setIsTripEnd(trip.getIsTripEnd());
            currentTrip.setTip(trip.isIsTip());
            preferenceHelper.putTripId(trip.getId());
            currentTrip.setPaymentMode(trip.getPaymentMode());
            currentTrip.setProviderId(trip.getConfirmedProvider());
            currentTrip.setSrcAddress(trip.getSourceAddress());
            currentTrip.setDestAddress(trip.getDestinationAddress());
            currentTrip.setIsProviderAccepted(trip.getIsProviderAccepted());
            currentTrip.setIsProviderStatus(trip.getIsProviderStatus());
            currentTrip.setIsProviderRated(trip.getIsProviderRated());
            currentTrip.setUnit(trip.getUnit());
            currentTrip.setCurrencyCode(trip.getCurrencycode());
            currentTrip.setTripNumber(String.valueOf(trip.getUniqueId()));
            currentTrip.setTripTypeAmount(trip.getTripTypeAmount());
            currentTrip.setTripType(trip.getTripType());
            currentTrip.setCancellationFee(response.getCancellationFee());
            currentTrip.setSrcLatitude(trip.getSourceLocation().get(0));
            currentTrip.setSrcLongitude(trip.getSourceLocation().get(1));
            if (trip.getDestinationLocation() != null && !trip.getDestinationLocation().isEmpty() && trip.getDestinationLocation().get(0) != null) {
                currentTrip.setDestLatitude(trip.getDestinationLocation().get(0));
                currentTrip.setDestLongitude(trip.getDestinationLocation().get(1));
            } else {
                currentTrip.setDestLatitude(0.0);
                currentTrip.setDestLongitude(0.0);
            }

            /*if (trip.getTripStopAddresses().isEmpty()) {
                currentTrip.setDestAddress(trip.getDestinationAddress());
                if (trip.getDestinationLocation() != null && !trip.getDestinationLocation().isEmpty() && trip.getDestinationLocation().get(0) != null) {
                    currentTrip.setDestLatitude(trip.getDestinationLocation().get(0));
                    currentTrip.setDestLongitude(trip.getDestinationLocation().get(1));
                } else {
                    currentTrip.setDestLatitude(0.0);
                    currentTrip.setDestLongitude(0.0);
                }
            } else {
                if (trip.getTripStopAddresses().size() == (trip.getActualTripStopAddress().size() - 1)) {
                    currentTrip.setDestAddress(trip.getDestinationAddress());
                    if (trip.getDestinationLocation() != null && !trip.getDestinationLocation().isEmpty() && trip.getDestinationLocation().get(0) != null) {
                        currentTrip.setDestLatitude(trip.getDestinationLocation().get(0));
                        currentTrip.setDestLongitude(trip.getDestinationLocation().get(1));
                    } else {
                        currentTrip.setDestLatitude(0.0);
                        currentTrip.setDestLongitude(0.0);
                    }
                } else {
                    if (trip.getActualTripStopAddress().size() == 0) {
                        currentTrip.setDestLatitude(trip.getTripStopAddresses().get(trip.getActualTripStopAddress().size()).getLocation().get(0));
                        currentTrip.setDestLongitude(trip.getTripStopAddresses().get(trip.getActualTripStopAddress().size()).getLocation().get(1));
                        currentTrip.setDestAddress(trip.getTripStopAddresses().get(trip.getActualTripStopAddress().size()).getAddress());
                    } else {
                        currentTrip.setDestLatitude(trip.getTripStopAddresses().get(trip.getActualTripStopAddress().size() - 1).getLocation().get(0));
                        currentTrip.setDestLongitude(trip.getTripStopAddresses().get(trip.getActualTripStopAddress().size() - 1).getLocation().get(1));
                        currentTrip.setDestAddress(trip.getTripStopAddresses().get(trip.getActualTripStopAddress().size() - 1).getAddress());
                    }
                }
            }*/

            CityDetail cityDetail = response.getCityDetail();
            preferenceHelper.putPaymentCardAvailable(cityDetail.getIsPaymentModeCard());
            preferenceHelper.putPaymentCashAvailable(cityDetail.getIsPaymentModeCash());
            preferenceHelper.putPromoApplyForCard(cityDetail.getIsPromoApplyForCard());
            preferenceHelper.putPromoApplyForCash(cityDetail.getIsPromoApplyForCash());
            currentTrip.setIsPromoUsed(response.getIsPromoUsed());
            currentTrip.setDriverCarImage(IMAGE_BASE_URL + response.getMapPinImageUrl());
            currentTrip.setTotalTime((int) trip.getTotalTime());
            currentTrip.setTotalDistance(trip.getTotalDistance());
            currentTrip.setFavouriteProvider(trip.isFavouriteProvider());
            currentTrip.setSplitPaymentUsers(trip.getSplitPaymentUsers());
            currentTrip.setRideShare(trip.isRideShare());
            return true;
        } else {
            return false;
        }
    }

    public boolean parseTypes(TypesResponse response) {
        if (response.isSuccess() && response.getCityTypes() != null && !response.getCityTypes().isEmpty()) {
            CityDetail cityDetail = response.getCityDetail();
            preferenceHelper.putPaymentCardAvailable(cityDetail.getIsPaymentModeCard());
            preferenceHelper.putPaymentCashAvailable(cityDetail.getIsPaymentModeCash());
            preferenceHelper.putPromoApplyForCard(cityDetail.getIsPromoApplyForCard());
            preferenceHelper.putPromoApplyForCash(cityDetail.getIsPromoApplyForCash());
            CurrentTrip currentTrip = CurrentTrip.getInstance();
            currentTrip.setServerTime(response.getServerTime());
            currentTrip.setCityTimeZone(cityDetail.getTimezone());
            currentTrip.setAskFroFixedRate(cityDetail.isIsAskUserForFixedFare());
            currentTrip.setCountryName(cityDetail.getCountryname());
            return true;
        }
        return false;
    }

    public boolean parseTrip(CreateTripResponse response) {
        if (response.isSuccess()) {
            preferenceHelper.putTripId(response.getTripId());
            return true;
        }
        return false;
    }

    public boolean parseProvider(ProviderDetailResponse response) {
        if (response.isSuccess()) {
            CurrentTrip currentTrip = CurrentTrip.getInstance();
            Provider provider = response.getProvider();
            currentTrip.setProviderFirstName(provider.getFirstName());
            currentTrip.setProviderLastName(provider.getLastName());
            currentTrip.setProviderId(provider.getId());
            currentTrip.setProviderCarModal(provider.getCarModel());
            currentTrip.setProviderCarNumber(provider.getCarNumber());
            currentTrip.setProviderProfileImage(IMAGE_BASE_URL + provider.getPicture());
            currentTrip.setProviderPhone(provider.getPhone());
            currentTrip.setRating(oneDigitDecimalFormat.format(provider.getRate()));
            currentTrip.setPhoneCountryCode(provider.getCountryPhoneCode());
            return true;
        } else {
            Utils.showErrorToast(response.getErrorCode(), context);
        }

        return false;
    }

    public HashMap<String, String> parsDistanceMatrix(Response<ResponseBody> response) {
        if (isSuccessful(response)) {
            HashMap<String, String> map = new HashMap<>();
            String destAddress, distance, time, originAddress, text;
            try {
                String distanceMatrix = response.body().string();
                JSONObject jsonObject = new JSONObject(distanceMatrix);
                if (jsonObject.getString(Const.google.STATUS).equals(Const.google.OK)) {
                    destAddress = jsonObject.getJSONArray(Const.google.DESTINATION_ADDRESSES).getString(0);
                    originAddress = jsonObject.getJSONArray(Const.google.ORIGIN_ADDRESSES).getString(0);
                    JSONObject rowsJson = jsonObject.getJSONArray(Const.google.ROWS).getJSONObject(0);
                    JSONObject elementsJson = rowsJson.getJSONArray(Const.google.ELEMENTS).getJSONObject(0);
                    distance = elementsJson.getJSONObject(Const.google.DISTANCE).getString(Const.google.VALUE);
                    time = elementsJson.getJSONObject(Const.google.DURATION).getString(Const.google.VALUE);
                    text = elementsJson.getJSONObject(Const.google.DURATION).getString(Const.google.TEXT);
                    map.put(Const.google.DESTINATION_ADDRESSES, destAddress);
                    map.put(Const.google.DISTANCE, distance);
                    map.put(Const.google.DURATION, time);
                    map.put(Const.google.ORIGIN_ADDRESSES, originAddress);
                    map.put(Const.google.TEXT, text);
                    return map;
                } else {
                    Utils.showToast(jsonObject.optString(Const.google.ERROR_MESSAGE) + "", context);
                }
            } catch (JSONException | IOException e) {
                AppLog.handleException(TAG, e);
            }
        }
        return null;
    }

    public HashMap<String, String> parseDirection(Response<ResponseBody> response) {
        if (isSuccessful(response)) {
            HashMap<String, String> map = new HashMap<>();
            Double distance = 0.0, time = 0.0;
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            try {
                String direction = response.body().string();

                JSONObject jsonObject = new JSONObject(direction);
                if (jsonObject.getString(Const.google.STATUS).equals(Const.google.OK)) {
                    jRoutes = jsonObject.getJSONArray(Const.google.ROUTES);

                    int jRouteSize = jRoutes.length();
                    for (int i = 0; i < jRouteSize; i++) {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray(Const.google.LEGS);
                        int jLegsSize = jLegs.length();
                        for (int j = 0; j < jLegsSize; j++) {
                            distance += Double.parseDouble(((JSONObject) jLegs.get(j)).getJSONObject(Const.google.DISTANCE).getString(Const.google.VALUE));
                            time += Double.parseDouble(((JSONObject) jLegs.get(j)).getJSONObject(Const.google.DURATION).getString(Const.google.VALUE));
                        }
                    }

                    map.put(Const.google.DISTANCE, distance.toString());
                    map.put(Const.google.DURATION, time.toString());
                    return map;
                }

            } catch (JSONException | IOException e) {
                AppLog.handleException(TAG, e);
            }
        }
        return null;
    }

    public void parseUserSettingDetail(SettingsDetailsResponse response) {
        AdminSettings adminSettings = response.getAdminSettings();
        if (!TextUtils.isEmpty(response.getAdminSettings().getImageBaseUrl())) {
            Const.IMAGE_BASE_URL = response.getAdminSettings().getImageBaseUrl();
        }
        preferenceHelper.putIsShowEstimation(adminSettings.isShowEstimationInUserApp());
        preferenceHelper.putGoogleServerKey(adminSettings.getAndroidUserAppGoogleKey());
        if (!TextUtils.isEmpty(adminSettings.getAndroidPlacesAutoCompleteKey())) {
            preferenceHelper.putGoogleAutoCompleteKey(adminSettings.getAndroidPlacesAutoCompleteKey());
        } else if (!TextUtils.isEmpty(adminSettings.getAndroidUserAppGoogleKey())) {
            preferenceHelper.putGoogleAutoCompleteKey(adminSettings.getAndroidUserAppGoogleKey());
        } else {
            preferenceHelper.putGoogleAutoCompleteKey(context.getResources().getString(R.string.GOOGLE_ANDROID_API_KEY));
        }

        preferenceHelper.putStripePublicKey(adminSettings.getStripePublishableKey());
        preferenceHelper.putTwilioNumber(adminSettings.getTwilioNumber());
        preferenceHelper.putTermsANdConditions(adminSettings.getTermsAndConditionUrl());
        preferenceHelper.putPolicy(adminSettings.getPrivacyPolicyUrl());
        preferenceHelper.putIsUserEmailVerification(adminSettings.isUserEmailVerification());
        preferenceHelper.putIsUserSocialLogin(adminSettings.isUserSocialLogin());
        preferenceHelper.putIsUserSMSVerification(adminSettings.isUserSms());
        preferenceHelper.putContactUsEmail(adminSettings.getContactUsEmail());
        preferenceHelper.putAdminPhone(adminSettings.getAdminPhone());
        preferenceHelper.putScheduledMinute(adminSettings.getScheduledRequestPreStartMinute());
        preferenceHelper.putIsPathDraw(adminSettings.isUserPath());
        preferenceHelper.putTripId("");
        preferenceHelper.putTwilioCallMaskEnable(adminSettings.isTwilioCallMasking());
        preferenceHelper.putMinimumPhoneNumberLength(adminSettings.getMinimumPhoneNumberLength());
        preferenceHelper.putMaximumPhoneNumberLength(adminSettings.getMaximumPhoneNumberLength());
        preferenceHelper.putIsSplitPayment(adminSettings.isSplitPayment());
        preferenceHelper.putMaxSplitUser(adminSettings.getMaxSplitUser());
        preferenceHelper.putAllowMultipleStops(adminSettings.isAllowMultipleStops());
        preferenceHelper.putMultipleStopsCount(adminSettings.getMultipleStopCount());
        if (response.getUserData() != null) {
            UserData userData = response.getUserData();
            preferenceHelper.putIsApplyReferral(userData.getIsReferral());
            preferenceHelper.putTripId(userData.getTripId());
            CurrentTrip.getInstance().setIsTripEnd(userData.getIsTripEnd());
            CurrentTrip.getInstance().setProviderId(userData.getProviderId());
            CurrentTrip.getInstance().setIsProviderAccepted(userData.getIsProviderAccepted());
            CurrentTrip.getInstance().setIsProviderStatus(userData.getIsProviderStatus());
            CurrentTrip.getInstance().setCorporateDetail(userData.getCorporateDetail());
            if (!TextUtils.isEmpty(userData.getWalletCurrencyCode())) {
                CurrentTrip.getInstance().setCurrencyCode(userData.getWalletCurrencyCode());
                CurrencyHelper.getInstance(context).getCurrencyFormat(userData.getWalletCurrencyCode());
            }
            if (userData.getCountryDetail() != null) {
                preferenceHelper.putIsHaveReferral(userData.getCountryDetail().isReferral());
            }
        } else {
            preferenceHelper.putSessionToken(null);
        }
        if (response.getSplitPaymentRequest() != null) {
            CurrentTrip.getInstance().setSplitPaymentRequest(response.getSplitPaymentRequest());
        }
    }

    private void goToMainActivity() {
        Intent sigInIntent = new Intent(context, MainActivity.class);
        sigInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        sigInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sigInIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        sigInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(sigInIntent);
    }

    public HashMap<String, String> parsGeocode(String response) {
        HashMap<String, String> map = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString(Const.google.STATUS).equals(Const.google.OK)) {

                JSONObject resultObject = jsonObject.getJSONArray(Const.google.RESULTS).getJSONObject(0);
                JSONArray addressComponent = resultObject.getJSONArray(Const.google.ADDRESS_COMPONENTS);
                map.put(Const.google.FORMATTED_ADDRESS, resultObject.getString(Const.google.FORMATTED_ADDRESS));
                JSONObject geometryObject = resultObject.getJSONObject(Const.google.GEOMETRY);
                map.put(Const.google.LAT, geometryObject.getJSONObject(Const.google.LOCATION).getString(Const.google.LAT));
                map.put(Const.google.LNG, geometryObject.getJSONObject(Const.google.LOCATION).getString(Const.google.LNG));

                int addressSize = addressComponent.length();
                for (int i = 0; i < addressSize; i++) {
                    JSONObject address = addressComponent.getJSONObject(i);
                    JSONArray typesArray = address.getJSONArray(Const.google.TYPES);
                    if (typesArray.length() > 0) {
                        if (Const.google.LOCALITY.equals(typesArray.get(0).toString())) {
                            map.put(Const.google.LOCALITY, address.getString(Const.google.LONG_NAME));
                        } else if (Const.google.ADMINISTRATIVE_AREA_LEVEL_2.equals(typesArray.get(0).toString())) {
                            map.put(Const.google.ADMINISTRATIVE_AREA_LEVEL_2, address.getString(Const.google.LONG_NAME));
                        } else if (Const.google.ADMINISTRATIVE_AREA_LEVEL_1.equals(typesArray.get(0).toString())) {
                            map.put(Const.google.ADMINISTRATIVE_AREA_LEVEL_1, address.getString(Const.google.LONG_NAME));
                        } else if (Const.google.COUNTRY.equals(typesArray.get(0).toString())) {
                            map.put(Const.google.COUNTRY, address.getString(Const.google.LONG_NAME));
                            map.put(Const.google.COUNTRY_CODE, address.getString(Const.google.SHORT_NAME));
                        }
                    }

                }
                return map;
            } else {
                Utils.showToast(jsonObject.optString(Const.google.ERROR_MESSAGE) + "", context);
            }

        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
        return null;
    }


    public boolean isSuccessful(Response<?> response) {
        if (response.isSuccessful() && response.body() != null) {

            return true;

        } else {
            Utils.showHttpErrorToast(response.code(), context);
            Utils.hideCustomProgressDialog();
        }
        return false;
    }

    public ArrayList<Invoice> parseInvoice(Context context, Trip trip, CityType tripService, NumberFormat currencyFormat) {
        ArrayList<Invoice> invoices = new ArrayList<>();
        if (trip != null && tripService != null) {
            String unit = Utils.showUnit(context, trip.getUnit());
            Resources res = context.getResources();
            if (trip.getTripType() == Const.TripType.NORMAL) {
                if (trip.isFixedFare() && trip.getFixedPrice() > 0) {
                    invoices.add(loadInvoiceData(res.getString(R.string.text_fixed_rate), currencyFormat.format(trip.getFixedPrice())));
                }
            } else if (trip.getTripTypeAmount() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_fixed_rate), currencyFormat.format(trip.getTripTypeAmount())));
            }

            if (!TextUtils.isEmpty(trip.getCarRentalId())) {
                String baseTimeAndDistance = ParseContent.getInstance().twoDigitDecimalFormat.format(tripService.getBasePriceTime()) + context.getResources().getString(R.string.text_unit_mins) + " & " + ParseContent.getInstance().twoDigitDecimalFormat.format(tripService.getBasePriceDistance()) + Utils.showUnit(context, trip.getUnit());
                invoices.add(loadInvoiceData(res.getString(R.string.text_base_price), currencyFormat.format(tripService.getBasePrice()), baseTimeAndDistance));
            } else if (tripService.getBasePrice() > 0 && !trip.isFixedFare()) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_base_price), currencyFormat.format(tripService.getBasePrice()), currencyFormat.format(tripService.getBasePrice()) + appendString(tripService.getBasePriceDistance(), unit)));
            }


            if (trip.getDistanceCost() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_distance_cost), currencyFormat.format(trip.getDistanceCost()), currencyFormat.format(tripService.getPricePerUnitDistance()) + appendString(0.0, unit)));
            }

            if (trip.getTimeCost() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_time_cost), currencyFormat.format(trip.getTimeCost()), currencyFormat.format(tripService.getPriceForTotalTime()) + getStrings(R.string.text_unit_per_time)));
            }

            if (trip.getWaitingTimeCost() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_wait_time_cost), currencyFormat.format(trip.getWaitingTimeCost()), currencyFormat.format(tripService.getPriceForWaitingTime()) + getStrings(R.string.text_unit_per_time)));
            }

            if (trip.getTaxFee() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_tax), currencyFormat.format(trip.getTaxFee()), ParseContent.getInstance().twoDigitDecimalFormat.format(tripService.getTax()) + Const.PERCENTAGE));
            }


            if (trip.getSurgeFee() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_surge_price), currencyFormat.format(trip.getSurgeFee()), "x" + ParseContent.getInstance().twoDigitDecimalFormat.format(trip.getSurgeMultiplier())));
            }

            if (trip.getTipAmount() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_tip), currencyFormat.format(trip.getTipAmount())));
            }

            if (trip.getTollAmount() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_toll), currencyFormat.format(trip.getTollAmount())));
            }

            if (trip.getUserMiscellaneousFee() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_user_miscellaneous_fee), currencyFormat.format(trip.getUserMiscellaneousFee())));
            }

            if (trip.getUserTaxFee() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_user_city_tax), currencyFormat.format(trip.getUserTaxFee()), ParseContent.getInstance().twoDigitDecimalFormat.format(tripService.getUserTax()) + Const.PERCENTAGE));
            }
            if (trip.getReferralPayment() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_referral_bonus), currencyFormat.format(trip.getReferralPayment())));
            }
            if (trip.getPromoPayment() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_promo_bonus), currencyFormat.format(trip.getPromoPayment())));
            }

            if (trip.getWalletPayment() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_wallet), currencyFormat.format(trip.getWalletPayment())));
            }

            /*if (trip.getPaymentMode() == Const.CARD) {
                if (trip.getRemainingPayment() > 0) {
                    invoices.add(loadInvoiceData(res.getString(R.string.text_remain), currencyFormat.format(trip.getRemainingPayment())));
                } else {
                    invoices.add(loadInvoiceData(res.getString(R.string.text_payment_with_card), currencyFormat.format(trip.getCardPayment())));
                }
            } else {
                invoices.add(loadInvoiceData(res.getString(R.string.text_payment_with_cash), currencyFormat.format(trip.getCashPayment())));
            }*/

            if (trip.getRemainingPayment() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_remain), currencyFormat.format(trip.getRemainingPayment())));
            }
            if (trip.getCardPayment() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_payment_with_card), currencyFormat.format(trip.getCardPayment())));
            }
            if (trip.getCashPayment() > 0) {
                invoices.add(loadInvoiceData(res.getString(R.string.text_payment_with_cash), currencyFormat.format(trip.getCashPayment())));
            }
        }
        return invoices;
    }

    private Invoice loadInvoiceData(String title, String mainPrice, String subString) {

        Invoice invoice = new Invoice();
        invoice.setPrice(mainPrice);
        invoice.setSubTitle(subString);
        invoice.setTitle(title);
        return invoice;
    }

    private Invoice loadInvoiceData(String title, String mainPrice) {

        Invoice invoice = new Invoice();
        invoice.setPrice(mainPrice);
        invoice.setTitle(title);
        return invoice;
    }

    private String appendString(Double value, String unit) {
        if (value <= 1) {
            return Const.SLASH + unit;
        } else {
            return Const.SLASH + value + unit;

        }
    }


    private String getStrings(int resId) {
        return context.getResources().getString(resId);
    }
}
