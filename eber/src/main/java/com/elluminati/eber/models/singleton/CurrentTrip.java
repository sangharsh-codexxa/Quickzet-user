package com.elluminati.eber.models.singleton;

import com.elluminati.eber.models.datamodels.CorporateDetail;
import com.elluminati.eber.models.datamodels.Country;
import com.elluminati.eber.models.datamodels.Language;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elluminati on 03-09-2016.
 */
public class CurrentTrip {
    private static final CurrentTrip currentTrip = new CurrentTrip();
    private final ArrayList<Language> speakingLanguages = new ArrayList<>();
    private String providerFirstName;
    private String providerLastName;
    private String providerCarNumber;
    private String providerCarModal;
    private String rating;
    private String providerProfileImage;
    private String providerId;
    private String srcAddress;
    private String destAddress;
    private int isProviderStatus;
    private int isProviderAccepted;
    private int isProviderRated;
    private double srcLatitude;
    private double srcLongitude;
    private double destLatitude;
    private double destLongitude;
    private String providerPhone;
    private int unit;
    private String currencyCode;
    private double estimatedFareDistance;
    private double estimatedFareTime;
    private double estimatedFareTotal;
    private String phoneCountryCode;
    private int paymentMode;
    private double cancellationFee;
    private int isPromoUsed;
    private String serverTime;
    private String cityTimeZone;
    private String tripNumber;
    private int totalWaitTime;
    private double priceForWaitingTime;
    private boolean isTip;
    private int isTripEnd;
    private double total;
    private double referralPayment;
    private double promoPayment;
    private double totalAfterSurgeFees;
    private int tripType;
    private double tripTypeAmount;
    private String driverCarImage;
    private boolean isAskFroFixedRate;
    private boolean isFixedRate;
    private double distance;
    private double time;
    private int vehiclePriceType;
    private int isTripCanceled;
    private ArrayList<Country> countryCodes;
    private int totalTime;
    private double totalDistance;
    private boolean isFavouriteProvider;
    private AutocompleteSessionToken autocompleteSessionToken;
    private CorporateDetail corporateDetail;
    private String countryName;
    private List<SplitPaymentRequest> splitPaymentUsers = new ArrayList<>();
    private SplitPaymentRequest splitPaymentRequest;
    private boolean isRideShare;

    private CurrentTrip() {

    }

    public static CurrentTrip getInstance() {
        return currentTrip;
    }

    public AutocompleteSessionToken getAutocompleteSessionToken() {
        return autocompleteSessionToken;
    }

    public void setAutocompleteSessionToken(AutocompleteSessionToken autocompleteSessionToken) {
        this.autocompleteSessionToken = autocompleteSessionToken;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public ArrayList<Country> getCountryCodes() {
        return countryCodes;
    }

    public void setCountryCodes(ArrayList<Country> countryCodes) {
        this.countryCodes = countryCodes;
    }

    public CorporateDetail getCorporateDetail() {
        return corporateDetail;
    }

    public void setCorporateDetail(CorporateDetail corporateDetail) {
        this.corporateDetail = corporateDetail;
    }

    public int getIsTripCanceled() {
        return isTripCanceled;
    }

    public void setIsTripCanceled(int isTripCanceled) {
        this.isTripCanceled = isTripCanceled;
    }

    public int getVehiclePriceType() {
        return vehiclePriceType;
    }

    public void setVehiclePriceType(int vehiclePriceType) {
        this.vehiclePriceType = vehiclePriceType;
    }

    public boolean isFixedRate() {
        return isFixedRate;
    }

    public void setFixedRate(boolean fixedRate) {
        isFixedRate = fixedRate;
    }

    public boolean isAskFroFixedRate() {
        return isAskFroFixedRate;
    }

    public void setAskFroFixedRate(boolean askFroFixedRate) {
        isAskFroFixedRate = askFroFixedRate;
    }

    public double getPriceForWaitingTime() {
        return priceForWaitingTime;
    }

    public void setPriceForWaitingTime(double priceForWaitingTime) {
        this.priceForWaitingTime = priceForWaitingTime;
    }


    public int getTotalWaitTime() {
        return totalWaitTime;
    }

    public void setTotalWaitTime(int totalWaitTime) {
        this.totalWaitTime = totalWaitTime;
    }


    public String getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }

    public String getCityTimeZone() {
        return cityTimeZone;
    }

    public void setCityTimeZone(String cityTimeZone) {
        this.cityTimeZone = cityTimeZone;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }


    public int getIsPromoUsed() {
        return isPromoUsed;
    }

    public void setIsPromoUsed(int isPromoUsed) {
        this.isPromoUsed = isPromoUsed;
    }

    public double getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(double cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public int getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(int paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }


    public double getEstimatedFareDistance() {
        return estimatedFareDistance;
    }

    public void setEstimatedFareDistance(double estimatedFareDistance) {
        this.estimatedFareDistance = estimatedFareDistance;
    }

    public double getEstimatedFareTime() {
        return estimatedFareTime;
    }

    public void setEstimatedFareTime(double estimatedFareTime) {
        this.estimatedFareTime = estimatedFareTime;
    }

    public double getEstimatedFareTotal() {
        return estimatedFareTotal;
    }

    public void setEstimatedFareTotal(double estimatedFareTotal) {
        this.estimatedFareTotal = estimatedFareTotal;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(String providerPhone) {
        this.providerPhone = providerPhone;
    }

    public double getDestLatitude() {
        return destLatitude;
    }

    public void setDestLatitude(double destLatitude) {
        this.destLatitude = destLatitude;
    }

    public double getDestLongitude() {
        return destLongitude;
    }

    public void setDestLongitude(double destLongitude) {
        this.destLongitude = destLongitude;
    }

    public double getSrcLatitude() {
        return srcLatitude;
    }

    public void setSrcLatitude(double srcLatitude) {
        this.srcLatitude = srcLatitude;
    }

    public double getSrcLongitude() {
        return srcLongitude;
    }

    public void setSrcLongitude(double srcLongitude) {
        this.srcLongitude = srcLongitude;
    }

    public String getSrcAddress() {
        return srcAddress;
    }

    public void setSrcAddress(String srcAddress) {
        this.srcAddress = srcAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public int getIsProviderRated() {
        return isProviderRated;
    }

    public void setIsProviderRated(int isProviderRated) {
        this.isProviderRated = isProviderRated;
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


    public String getProviderFirstName() {
        return providerFirstName;
    }

    public void setProviderFirstName(String providerFirstName) {
        this.providerFirstName = providerFirstName;
    }

    public String getProviderLastName() {
        return providerLastName;
    }

    public void setProviderLastName(String providerLastName) {
        this.providerLastName = providerLastName;
    }

    public String getProviderCarNumber() {
        return providerCarNumber;
    }

    public void setProviderCarNumber(String providerCarNumber) {
        this.providerCarNumber = providerCarNumber;
    }

    public String getProviderCarModal() {
        return providerCarModal;
    }

    public void setProviderCarModal(String providerCarModal) {
        this.providerCarModal = providerCarModal;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getProviderProfileImage() {
        return providerProfileImage;
    }

    public void setProviderProfileImage(String providerProfileImage) {
        this.providerProfileImage = providerProfileImage;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public boolean isTip() {
        return isTip;
    }

    public void setTip(boolean tip) {
        isTip = tip;
    }

    public int getIsTripEnd() {
        return isTripEnd;
    }

    public void setIsTripEnd(int isTripEnd) {
        this.isTripEnd = isTripEnd;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getReferralPayment() {
        return referralPayment;
    }

    public void setReferralPayment(double referralPayment) {
        this.referralPayment = referralPayment;
    }

    public double getPromoPayment() {
        return promoPayment;
    }

    public void setPromoPayment(double promoPayment) {
        this.promoPayment = promoPayment;
    }

    public double getTotalAfterSurgeFees() {
        return totalAfterSurgeFees;
    }

    public void setTotalAfterSurgeFees(double totalAfterSurgeFees) {
        this.totalAfterSurgeFees = totalAfterSurgeFees;
    }

    public int getTripType() {
        return tripType;
    }

    public void setTripType(int tripType) {
        this.tripType = tripType;
    }

    public double getTripTypeAmount() {
        return tripTypeAmount;
    }

    public void setTripTypeAmount(double tripTypeAmount) {
        this.tripTypeAmount = tripTypeAmount;
    }

    public String getDriverCarImage() {
        return driverCarImage;
    }

    public void setDriverCarImage(String driverCarImage) {
        this.driverCarImage = driverCarImage;
    }

    public ArrayList<Language> getSpeakingLanguages() {
        return speakingLanguages;
    }

    public void setSpeakingLanguages(ArrayList<Language> speakingLanguages) {
        this.speakingLanguages.clear();
        this.speakingLanguages.addAll(speakingLanguages);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public boolean isFavouriteProvider() {
        return isFavouriteProvider;
    }

    public void setFavouriteProvider(boolean favouriteProvider) {
        isFavouriteProvider = favouriteProvider;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public List<SplitPaymentRequest> getSplitPaymentUsers() {
        return splitPaymentUsers;
    }

    public void setSplitPaymentUsers(List<SplitPaymentRequest> splitPaymentUsers) {
        this.splitPaymentUsers = splitPaymentUsers;
    }

    public SplitPaymentRequest getSplitPaymentRequest() {
        return splitPaymentRequest;
    }

    public void setSplitPaymentRequest(SplitPaymentRequest splitPaymentRequest) {
        this.splitPaymentRequest = splitPaymentRequest;
    }

    public boolean isRideShare() {
        return isRideShare;
    }

    public void setRideShare(boolean rideShare) {
        isRideShare = rideShare;
    }

    public void clear() {
        providerFirstName = "";
        providerLastName = "";
        providerCarNumber = "";
        providerCarModal = "";
        rating = "";
        providerProfileImage = "";
        providerId = "";
        srcAddress = "";
        destAddress = "";
        isProviderStatus = 0;
        isProviderAccepted = 0;
        isProviderRated = 0;
        srcLatitude = 0;
        srcLongitude = 0;
        destLatitude = 0;
        destLongitude = 0;
        providerPhone = "";
        unit = 0;
        currencyCode = "";
        estimatedFareDistance = 0;
        estimatedFareTime = 0;
        estimatedFareTotal = 0;
        phoneCountryCode = "";
        paymentMode = 0;
        cancellationFee = 0;
        isPromoUsed = 0;
        serverTime = "";
        cityTimeZone = "";
        tripNumber = null;
        totalWaitTime = 0;
        priceForWaitingTime = 0;
        isTip = false;
        isTripEnd = 0;
        total = 0;
        referralPayment = 0;
        promoPayment = 0;
        totalAfterSurgeFees = 0;
        tripType = 0;
        tripTypeAmount = 0;
        driverCarImage = "";
        isAskFroFixedRate = false;
        speakingLanguages.clear();
        isFixedRate = false;
        vehiclePriceType = 0;
        isTripCanceled = 0;
        countryCodes = null;
        totalTime = 0;
        isFavouriteProvider = false;
        totalDistance = 0;
        countryName = "";
        splitPaymentUsers.clear();
        splitPaymentRequest = null;
        isRideShare = false;
    }
}