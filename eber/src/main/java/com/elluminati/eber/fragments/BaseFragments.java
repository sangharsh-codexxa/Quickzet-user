package com.elluminati.eber.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.elluminati.eber.MainDrawerActivity;
import com.elluminati.eber.utils.AppLog;

/**
 * Created by elluminati on 14-06-2016.
 */
public abstract class BaseFragments extends Fragment implements View.OnClickListener {

    public String TAG = this.getClass().getSimpleName();
    protected MainDrawerActivity drawerActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawerActivity = (MainDrawerActivity) getActivity();
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception e) {
            AppLog.handleException("Base Fragment", e);
        }
    }
}
