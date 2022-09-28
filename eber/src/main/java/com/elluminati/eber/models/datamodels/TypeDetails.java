package com.elluminati.eber.models.datamodels;

import com.google.gson.annotations.SerializedName;

public class TypeDetails {

    @SerializedName("is_business")
    private int isBusiness;

    @SerializedName("service_type")
    private int serviceType;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("type_image_url")
    private String typeImageUrl;

    @SerializedName("description")
    private String description;

    @SerializedName("map_pin_image_url")
    private String mapPinImageUrl;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("_id")
    private String id;

    @SerializedName("priority")
    private int priority;

    @SerializedName("typename")
    private String typename;


    @SerializedName("is_default_selected")
    private boolean isDefaultSelected;

    public boolean isDefaultSelected() {
        return isDefaultSelected;
    }

    public void setDefaultSelected(boolean defaultSelected) {
        isDefaultSelected = defaultSelected;
    }

    public int getIsBusiness() {
        return isBusiness;
    }

    public void setIsBusiness(int isBusiness) {
        this.isBusiness = isBusiness;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTypeImageUrl() {
        return typeImageUrl;
    }

    public void setTypeImageUrl(String typeImageUrl) {
        this.typeImageUrl = typeImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMapPinImageUrl() {
        return mapPinImageUrl;
    }

    public void setMapPinImageUrl(String mapPinImageUrl) {
        this.mapPinImageUrl = mapPinImageUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    @Override
    public String toString() {
        return "TypeDetails{" + "is_business = '" + isBusiness + '\'' + ",service_type = '" + serviceType + '\'' + ",updated_at = '" + updatedAt + '\'' + ",type_image_url = '" + typeImageUrl + '\'' + ",description = '" + description + '\'' + ",map_pin_image_url = '" + mapPinImageUrl + '\'' + ",created_at = '" + createdAt + '\'' + ",_id = '" + id + '\'' + ",priority = '" + priority + '\'' + ",typename = '" + typename + '\'' + "}";
    }
}