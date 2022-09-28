package com.elluminati.eber.models.datamodels;

import com.google.gson.annotations.SerializedName;

public class CorporateDetail {

    @SerializedName("_id")
    private String id;
    @SerializedName("corporate_detail")
    private CorporateDetail corporateDetail;
    @SerializedName("phone")
    private String phone = "n/a";
    @SerializedName("country_phone_code")
    private String countryPhoneCode;
    @SerializedName("name")
    private String name = "n/a";
    @SerializedName("status")
    private int status;
    @SerializedName("is_approved")
    private int isApproved;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }

    public CorporateDetail getCorporateDetail() {
        return corporateDetail;
    }

    public void setCorporateDetail(CorporateDetail corporateDetail) {
        this.corporateDetail = corporateDetail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryPhoneCode() {
        return countryPhoneCode;
    }

    public void setCountryPhoneCode(String countryPhoneCode) {
        this.countryPhoneCode = countryPhoneCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CorporateDetail{" + "corporate_detail = '" + corporateDetail + '\'' + ",phone = '" + phone + '\'' + ",country_phone_code = '" + countryPhoneCode + '\'' + ",name = '" + name + '\'' + ",status = '" + status + '\'' + "}";
    }
}