package com.elluminati.eber;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.elluminati.eber.components.MyFontEdittextView;
import com.elluminati.eber.models.datamodels.Country;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.responsemodels.UserDataResponse;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.models.validations.Validator;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.FieldValidation;
import com.elluminati.eber.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterPasswordActivity extends BaseAppCompatActivity {

    private String contactNumber;
    private MyFontEdittextView etPassword;
    private Country country;
    private TextInputLayout tilPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        initToolBar();
        // change toolbar color here
        toolbar.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.color_white, null));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        setTitleOnToolbar("");
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.color_black), PorterDuff.Mode.SRC_ATOP);

        loadExtrasData();
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.tvForgotPassword).setOnClickListener(this);
        etPassword = findViewById(R.id.etPassword);
        tilPassword = findViewById(R.id.tilPassword);

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tilPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.MULTIPLY);
            }
        });

    }

    private void loadExtrasData() {
        if (getIntent().getExtras() != null) {
            country = getIntent().getExtras().getParcelable(Const.Params.COUNTRY);
            contactNumber = getIntent().getExtras().getString(Const.Params.PHONE);
        }
    }


    @Override
    protected boolean isValidate() {
        return false;
    }

    @Override
    public void goWithBackArrow() {
        hideKeyBord();
        onBackPressed();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                // go for login verification.
                Validator passwordValidation = FieldValidation.isPasswordValid(EnterPasswordActivity.this, etPassword.getText().toString().trim());
                if (!passwordValidation.isValid()) {
                    etPassword.setError(passwordValidation.getErrorMsg());
                    etPassword.requestFocus();
                    tilPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
                } else {
                    signIn(Const.MANUAL);
                }
                break;
            case R.id.tvForgotPassword:
                goToForgotPasswordActivity();
                break;
        }
    }

    private void goToForgotPasswordActivity() {
        Intent intent = new Intent(EnterPasswordActivity.this, ForgotPasswordActivity.class);
        intent.putExtra(Const.Params.PHONE, contactNumber);
        intent.putExtra(Const.Params.COUNTRY, country);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onAdminApproved() {

    }

    @Override
    public void onAdminDeclined() {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            closedEnableDialogInternet();
        } else {
            openInternetDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
    }


    private void signIn(String loginType) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_waiting_for_sing_in), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.EMAIL, contactNumber);
            jsonObject.put(Const.Params.PASSWORD, etPassword.getText().toString());
            jsonObject.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
            jsonObject.put(Const.Params.DEVICE_TOKEN, preferenceHelper.getDeviceToken());
            jsonObject.put(Const.Params.LOGIN_BY, loginType);
            jsonObject.put(Const.Params.COUNTRY_PHONE_CODE, country.getCountryPhoneCode());
            jsonObject.put(Const.Params.APP_VERSION, getAppVersion());
            jsonObject.put(Const.Params.COUNTRY_PHONE_CODE, country.getCountryPhoneCode());

            Call<UserDataResponse> call = ApiClient.getClient().create(ApiInterface.class).login(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<UserDataResponse>() {
                @Override
                public void onResponse(Call<UserDataResponse> call, Response<UserDataResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        CurrentTrip.getInstance().clear();
                        if (parseContent.saveUserData(response.body(), true, true)) {
                            Utils.hideCustomProgressDialog();
                            moveWithUserSpecificPreference();
                            generateFirebaseAccessToken();
                        } else {
                            Utils.hideCustomProgressDialog();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserDataResponse> call, Throwable t) {
                    AppLog.handleThrowable(EnterPasswordActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.SIGN_IN_ACTIVITY, e);
        }
    }
}