package com.elluminati.eber.utils;

import android.content.Context;

import com.elluminati.eber.BuildConfig;
import com.elluminati.eber.parse.ApiClient;

public class ServerConfig {

    public static String BASE_URL = BuildConfig.BASE_URL;

    public static void setURL(Context context) {
        BASE_URL = PreferenceHelper.getInstance(context).getBaseUrl();
        new ApiClient().changeAllApiBaseUrl(BASE_URL);
    }
}