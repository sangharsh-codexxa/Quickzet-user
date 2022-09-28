package com.elluminati.eber.models.datamodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class Document {
    @SerializedName("is_document_uploaded")
    private int isDocumentUploaded;
    @SerializedName("error_code")
    private int errorCode;
    @SerializedName("success")
    private boolean success;
    @SerializedName("is_unique_code")
    private boolean isUniqueCode;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("document_picture")
    private String documentPicture;
    @SerializedName("document_id")
    private String documentId;
    @SerializedName("expired_date")
    private String expiredDate = "";
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("name")
    private String name;
    @SerializedName("_id")
    private String id;
    @SerializedName("is_document_expired")
    private boolean isDocumentExpired;
    @SerializedName("is_expired_date")
    private boolean isExpiredDate;
    @SerializedName("unique_code")
    private String uniqueCode = "";
    @SerializedName("is_uploaded")
    private int isUploaded;
    @SerializedName("option")
    private int option;

    public int getIsDocumentUploaded() {
        return isDocumentUploaded;
    }

    public void setIsDocumentUploaded(int isDocumentUploaded) {
        this.isDocumentUploaded = isDocumentUploaded;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isIsUniqueCode() {
        return isUniqueCode;
    }

    public void setIsUniqueCode(boolean isUniqueCode) {
        this.isUniqueCode = isUniqueCode;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDocumentPicture() {
        return documentPicture;
    }

    public void setDocumentPicture(String documentPicture) {
        this.documentPicture = documentPicture;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getExpiredDate() {
        return TextUtils.isEmpty(expiredDate) ? "" : expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIsDocumentExpired() {
        return isDocumentExpired;
    }

    public void setIsDocumentExpired(boolean isDocumentExpired) {
        this.isDocumentExpired = isDocumentExpired;
    }

    public boolean isIsExpiredDate() {
        return isExpiredDate;
    }

    public void setIsExpiredDate(boolean isExpiredDate) {
        this.isExpiredDate = isExpiredDate;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public int getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(int isUploaded) {
        this.isUploaded = isUploaded;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return "Document{" + "is_unique_code = '" + isUniqueCode + '\'' + ",created_at = '" + createdAt + '\'' + ",document_picture = '" + documentPicture + '\'' + ",document_id = '" + documentId + '\'' + ",expired_date = '" + expiredDate + '\'' + ",updated_at = '" + updatedAt + '\'' + ",user_id = '" + userId + '\'' + ",name = '" + name + '\'' + ",_id = '" + id + '\'' + ",is_document_expired = '" + isDocumentExpired + '\'' + ",is_expired_date = '" + isExpiredDate + '\'' + ",unique_code = '" + uniqueCode + '\'' + ",is_uploaded = '" + isUploaded + '\'' + ",option = '" + option + '\'' + "}";
    }
}