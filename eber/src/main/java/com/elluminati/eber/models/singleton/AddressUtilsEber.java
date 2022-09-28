package com.elluminati.eber.models.singleton;

import com.elluminati.eber.models.datamodels.TripStopAddresses;
import com.elluminati.eber.utils.Const;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Elluminati on 28-Jun-17.
 */

public class AddressUtilsEber {

    private static final AddressUtilsEber addressUtils = new AddressUtilsEber();
    private String pickupAddress = "";
    private String destinationAddress = "";
    private String trimedPickupAddress;
    private String trimedDestinationAddress;
    private LatLng pickupLatLng;
    private LatLng destinationLatLng;
    private String currentCountry;
    private String homeAddress;
    private String workAddress;
    private double homeLatitude;
    private double homeLongitude;
    private double workLatitude;
    private double workLongitude;
    private String trimmedHomeAddress;
    private String trimmedWorkAddress;
    private String countryCode;
    private ArrayList<TripStopAddresses> tripStopAddressesArrayList = new ArrayList<>();

    private AddressUtilsEber() {

    }

    public static AddressUtilsEber getInstance() {
        return addressUtils;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String sourceAddress) {
        this.pickupAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getTrimedPickupAddress() {
        return trimedPickupAddress;
    }

    public void setTrimedPickupAddress(String trimedPickupAddress) {
        this.trimedPickupAddress = trimedPickupAddress;
    }

    public String getTrimedDestinationAddress() {
        return trimedDestinationAddress;
    }

    public void setTrimedDestinationAddress(String trimedDestinationAddress) {
        this.trimedDestinationAddress = trimedDestinationAddress;
    }

    public LatLng getPickupLatLng() {
        return pickupLatLng;
    }

    public void setPickupLatLng(LatLng pickupLatLng) {
        this.pickupLatLng = pickupLatLng;
    }

    public LatLng getDestinationLatLng() {
        return destinationLatLng;
    }

    public void setDestinationLatLng(LatLng destinationLatLng) {
        this.destinationLatLng = destinationLatLng;
    }


    public String getCurrentCountry() {
        return currentCountry;
    }

    public void setCurrentCountry(String currentCountry) {
        this.currentCountry = currentCountry;
    }


    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public double getHomeLatitude() {
        return homeLatitude;
    }

    public void setHomeLatitude(double homeLatitude) {
        this.homeLatitude = homeLatitude;
    }

    public double getHomeLongitude() {
        return homeLongitude;
    }

    public void setHomeLongitude(double homeLongitude) {
        this.homeLongitude = homeLongitude;
    }

    public double getWorkLatitude() {
        return workLatitude;
    }

    public void setWorkLatitude(double workLatitude) {
        this.workLatitude = workLatitude;
    }

    public double getWorkLongitude() {
        return workLongitude;
    }

    public void setWorkLongitude(double workLongitude) {
        this.workLongitude = workLongitude;
    }

    public String getTrimmedHomeAddress() {
        return trimmedHomeAddress;
    }

    public void setTrimmedHomeAddress(String trimmedHomeAddress) {
        this.trimmedHomeAddress = trimmedHomeAddress;
    }

    public String getTrimmedWorkAddress() {
        return trimmedWorkAddress;
    }

    public void setTrimmedWorkAddress(String trimmedWorkAddress) {
        this.trimmedWorkAddress = trimmedWorkAddress;
    }

    public void setTripStopAddressesArrayList(ArrayList<TripStopAddresses> tripStopAddressesArrayList) {
        this.tripStopAddressesArrayList = tripStopAddressesArrayList;
    }

    public ArrayList<TripStopAddresses> getTripStopAddressesArrayList() {
        return tripStopAddressesArrayList;
    }

    public void clearDestination() {
        setDestinationLatLng(null);
        setDestinationAddress("");
        setTrimedDestinationAddress("");
        CurrentTrip.getInstance().setTripType(Const.TripType.NORMAL);
    }

    public void clearPickup() {
        setPickupLatLng(null);
        setPickupAddress("");
        setTrimedPickupAddress("");
    }


    public void resetAddress() {
        clearPickup();
        clearDestination();
        getTripStopAddressesArrayList().clear();
    }

    public void clearHomeAddress() {
        setHomeAddress("");
        setHomeLatitude(0);
        setHomeLongitude(0);
        setTrimmedHomeAddress("");
    }

    public void clearWorkAddress() {
        setWorkAddress("");
        setWorkLatitude(0);
        setWorkLongitude(0);
        setTrimmedWorkAddress("");
    }

}
