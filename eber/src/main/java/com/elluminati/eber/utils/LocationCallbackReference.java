package com.elluminati.eber.utils;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.SoftReference;

public final class LocationCallbackReference extends LocationCallback {
    private final SoftReference mLocationCallbackRef;

    public LocationCallbackReference(@Nullable LocationCallback locationCallback) {
        this.mLocationCallbackRef = new SoftReference(locationCallback);
    }

    public void onLocationResult(@NotNull LocationResult locationResult) {
        super.onLocationResult(locationResult);
        if (this.mLocationCallbackRef.get() != null) {
            ((LocationCallback) this.mLocationCallbackRef.get()).onLocationResult(locationResult);
        }

    }

    public void onLocationAvailability(@NotNull LocationAvailability locationAvailability) {
        super.onLocationAvailability(locationAvailability);
        if (this.mLocationCallbackRef.get() != null) {
            ((LocationCallback) this.mLocationCallbackRef.get()).onLocationAvailability(locationAvailability);
        }

    }
}
