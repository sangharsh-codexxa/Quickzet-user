package com.elluminati.eber.models.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TripStopAddresses implements Parcelable {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("location")
    @Expose
    private List<Double> location = null;

    protected TripStopAddresses(Parcel in) {
        address = in.readString();
        location = in.readArrayList(Double.class.getClassLoader());
    }

    public TripStopAddresses() {

    }

    public static final Creator<TripStopAddresses> CREATOR = new Creator<TripStopAddresses>() {
        @Override
        public TripStopAddresses createFromParcel(Parcel in) {
            return new TripStopAddresses(in);
        }

        @Override
        public TripStopAddresses[] newArray(int size) {
            return new TripStopAddresses[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeList(location);
    }
}
