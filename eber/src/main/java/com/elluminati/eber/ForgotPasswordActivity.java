package com.elluminati.eber;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.core.content.res.ResourcesCompat;

import com.elluminati.eber.components.MyFontEdittextView;
import com.elluminati.eber.models.datamodels.Country;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.models.responsemodels.VerificationResponse;
import com.elluminati.eber.models.validations.Validator;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.FieldValidation;
import com.elluminati.eber.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseAppCompatActivity {
    private MyFontEdittextView etContactNumber, etUserEmail;
    private boolean isEmailVerify, isSMSVerify;
    private String contactNumber;
    private RadioButton rbEmail, rbContactNo;
    private LinearLayout llPassword, llEmail;
    private Country country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initToolBar();
        // change toolbar color here
        toolbar.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.color_white, null));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        setTitleOnToolbar("");
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.color_black), PorterDuff.Mode.SRC_ATOP);

        loadExtrasData();
        isEmailVerify = preferenceHelper.getIsUserEmailVerification();
        isSMSVerify = preferenceHelper.getIsUserSMSVerification();
        etContactNumber = findViewById(R.id.etContactNumber);
        rbContactNo = findViewById(R.id.rbContactNo);
        llPassword = findViewById(R.id.llPassword);
        llEmail = findViewById(R.id.llEmail);
        rbEmail = findViewById(R.id.rbEmail);
        etUserEmail = findViewById(R.id.etUserEmail);
        if (isEmailVerify) {
            llEmail.setVisibility(View.VISIBLE);
//            llPassword.setVisibility(View.VISIBLE);
            rbEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbContactNo.setChecked(!isChecked);
                }
            });
            rbContactNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rbEmail.setChecked(!isChecked);
                }
            });
        } else {
            rbContactNo.setChecked(true);
            rbEmail.setChecked(false);
            llEmail.setVisibility(View.GONE);
            llPassword.setVisibility(View.VISIBLE);
        }
        etContactNumber.setText(country.getCountryPhoneCode() + " " + contactNumber);
        findViewById(R.id.btn_done).setOnClickListener(this);
    }

    private void loadExtrasData() {
        if (getIntent().getExtras() != null) {
            country = getIntent().getExtras().getParcelable(Const.Params.COUNTRY);
            contactNumber = getIntent().getExtras().getString(Const.Params.PHONE);
        }
    }


    @Override
    protected boolean isValidate() {
        String msg = null;
        Validator emailValidation = FieldValidation.isEmailValid(ForgotPasswordActivity.this, etUserEmail.getText().toString().trim());
        if (rbEmail.isChecked()) {
            if (!emailValidation.isValid()) {
                msg = getString(R.string.msg_enter_valid_email);
                etUserEmail.requestFocus();
                etUserEmail.setError(msg);
            }
        } else if (rbContactNo.isChecked()) {

        } else {
            msg = getString(R.string.msg_select_email_or_phone);
        }
        return msg == null;

    }

    @Override
    public void goWithBackArrow() {
        onBackPressed();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done:
                if (isValidate()) {
                    if (rbEmail.isChecked()) {
                        forgotPassword(etUserEmail.getText().toString().trim());
                    } else {
                        getOtp(country, contactNumber);
                    }
                }
                break;
        }

    }

    private void getOtp(Country countryCode, final String contactNumber) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_loading), false, null);

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.PHONE, contactNumber);
            jsonObject.put(Const.Params.COUNTRY_PHONE_CODE, countryCode.getCountryPhoneCode());
            Call<VerificationResponse> call = ApiClient.getClient().create(ApiInterface.class).getOtp(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<VerificationResponse>() {
                @Override
                public void onResponse(Call<VerificationResponse> call, Response<VerificationResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            Utils.hideCustomProgressDialog();
                            // go for otp screen
                            Intent otpIntent = new Intent(ForgotPasswordActivity.this, OtpVerifyActivity.class);
                            otpIntent.putExtra(Const.Params.COUNTRY, country);
                            otpIntent.putExtra(Const.Params.PHONE, contactNumber);
                            otpIntent.putExtra(Const.Params.IS_OPEN_FOR_RESULT, false);
                            String otpForSMS = response.body().getOtpForSMS();
                            otpIntent.putExtra(Const.Params.OTP_FOR_SMS, otpForSMS);
                            otpIntent.putExtra(Const.Params.IS_VERIFIED, false);
                            otpIntent.putExtra(Const.Params.IS_FROM_FORGET_PASSWORD, true);
                            goToOtpVerifyActivity(otpIntent);

                        } else {
                            Utils.hideCustomProgressDialog();
                            Utils.showErrorToast(response.body().getErrorCode(), ForgotPasswordActivity.this);
                        }
                    }
                }

                @Override
                public void onFailure(Call<VerificationResponse> call, Throwable t) {
                    AppLog.handleThrowable(OtpVerifyActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.SIGN_IN_ACTIVITY, e);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
    }

    protected void goToOtpVerifyActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    private void forgotPassword(String email) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_loading), false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.EMAIL, email);
            jsonObject.put(Const.Params.TYPE, Const.USER);
            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).forgotPassword(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            Utils.hideCustomProgressDialog();
                            Utils.showMessageToast(response.body().getMessage(), ForgotPasswordActivity.this);
                            goToMainActivity();
                        } else {
                            Utils.hideCustomProgressDialog();
                            Utils.showErrorToast(response.body().getErrorCode(), ForgotPasswordActivity.this);
                        }
                    }

                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(ForgotPasswordActivity.class.getSimpleName(), t);
                }
            });

        } catch (JSONException e) {
            AppLog.handleException(ForgotPasswordActivity.class.getSimpleName(), e);
        }
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
}