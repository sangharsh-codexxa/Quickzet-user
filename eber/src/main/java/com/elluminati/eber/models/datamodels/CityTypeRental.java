package com.elluminati.eber.models.datamodels;

import com.google.gson.annotations.SerializedName;

public class CityTypeRental {

    private boolean isSelected;
    @SerializedName("price_per_unit_distance")
    private double pricePerUnitDistance;
    @SerializedName("base_price_time")
    private double basePriceTime;
    @SerializedName("base_price")
    private double basePrice;
    @SerializedName("base_price_distance")
    private double basePriceDistance;
    @SerializedName("_id")
    private String id;
    @SerializedName("typename")
    private String typeName;
    @SerializedName("price_for_total_time")
    private double priceForTotalTime;

    public double getPriceForTotalTime() {
        return priceForTotalTime;
    }

    public void setPriceForTotalTime(double priceForTotalTime) {
        this.priceForTotalTime = priceForTotalTime;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }


    public double getPricePerUnitDistance() {
        return pricePerUnitDistance;
    }

    public void setPricePerUnitDistance(double pricePerUnitDistance) {
        this.pricePerUnitDistance = pricePerUnitDistance;
    }


    public double getBasePriceTime() {
        return basePriceTime;
    }

    public void setBasePriceTime(double basePriceTime) {
        this.basePriceTime = basePriceTime;
    }


    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }


    public double getBasePriceDistance() {
        return basePriceDistance;
    }

    public void setBasePriceDistance(double basePriceDistance) {
        this.basePriceDistance = basePriceDistance;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}