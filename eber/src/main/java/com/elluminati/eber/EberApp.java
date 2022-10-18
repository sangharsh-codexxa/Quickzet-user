package com.elluminati.eber;

import android.app.Application;


import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

public class EberApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new Instabug.Builder(this, "55620665d19b824d3749622f57aafe8d")
                .setInvocationEvents(InstabugInvocationEvent.SHAKE, InstabugInvocationEvent.NONE)
                .build();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);
    }
}