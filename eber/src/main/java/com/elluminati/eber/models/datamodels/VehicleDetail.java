package com.elluminati.eber.models.datamodels;

import com.google.gson.annotations.SerializedName;

public class VehicleDetail {

    @SerializedName("passing_year")
    private String passingYear;

    @SerializedName("service_type")
    private String serviceType;

    @SerializedName("color")
    private String color;

    @SerializedName("plate_no")
    private String plateNo;

    @SerializedName("is_documents_expired")
    private boolean isDocumentsExpired;

    @SerializedName("is_selected")
    private boolean isSelected;

    @SerializedName("admin_type_id")
    private String adminTypeId;

    @SerializedName("name")
    private String name;

    @SerializedName("model")
    private String model;

    @SerializedName("_id")
    private String id;

    public String getPassingYear() {
        return passingYear;
    }

    public void setPassingYear(String passingYear) {
        this.passingYear = passingYear;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public boolean isIsDocumentsExpired() {
        return isDocumentsExpired;
    }

    public void setIsDocumentsExpired(boolean isDocumentsExpired) {
        this.isDocumentsExpired = isDocumentsExpired;
    }

    public boolean isIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getAdminTypeId() {
        return adminTypeId;
    }

    public void setAdminTypeId(String adminTypeId) {
        this.adminTypeId = adminTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "VehicleDetail{" + "passing_year = '" + passingYear + '\'' + ",service_type = '" + serviceType + '\'' + ",color = '" + color + '\'' + ",plate_no = '" + plateNo + '\'' + ",is_documents_expired = '" + isDocumentsExpired + '\'' + ",is_selected = '" + isSelected + '\'' + ",admin_type_id = '" + adminTypeId + '\'' + ",name = '" + name + '\'' + ",model = '" + model + '\'' + ",_id = '" + id + '\'' + "}";
    }
}