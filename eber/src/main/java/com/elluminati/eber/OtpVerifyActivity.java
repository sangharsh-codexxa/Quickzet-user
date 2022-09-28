package com.elluminati.eber;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.components.PinView;
import com.elluminati.eber.interfaces.OTPListener;
import com.elluminati.eber.models.datamodels.Country;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.responsemodels.VerificationResponse;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerifyActivity extends BaseAppCompatActivity implements OTPListener {

    private PinView otp_view;
    private MyFontTextView tvOtpHint, tvResendCodeTime, tvEditMobileNumber;
    private String countryCode, countryName, contactNumber, otpForSMS, otpForEmail;
    private boolean isOpenForResult = false, isFromForgetPassword = false;
    private OtpReader otpReader;
    private Country country;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        initToolBar();
        // change toolbar color here
        toolbar.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.color_white, null));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        setTitleOnToolbar("");
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.color_black), PorterDuff.Mode.SRC_ATOP);

        loadExtrasData();
        otp_view = findViewById(R.id.otp_view);
        findViewById(R.id.btnVerifyOtp).setOnClickListener(this);

        tvOtpHint = findViewById(R.id.tvOtpHint);
        tvEditMobileNumber = findViewById(R.id.tvEditMobileNumber);
        tvResendCodeTime = findViewById(R.id.tvResendCodeTime);
        tvEditMobileNumber.setOnClickListener(this);
        tvResendCodeTime.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 24) {
            tvOtpHint.setText(Html.fromHtml(String.format(Html.toHtml(new SpannedString(getResources().getText(R.string.msg_hint_otp_send_number)), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), countryCode, contactNumber, Html.FROM_HTML_MODE_LEGACY)));
        } else {
            tvOtpHint.setText(Html.fromHtml(String.format(Html.toHtml(new SpannedString(getResources().getText(R.string.msg_hint_otp_send_number))), countryCode, contactNumber)));
        }
        tvResendCodeTime.setEnabled(false);

        otp_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == otpForSMS.length()) {
                    checkForOtpVerification();
                }
            }
        });
        new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvResendCodeTime.setText(getString(R.string.msg_resend_otp_time, TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                tvResendCodeTime.setText(getString(R.string.msg_resend_code));
                tvResendCodeTime.setTextColor(getResources().getColor(R.color.color_app_button));
                tvResendCodeTime.setEnabled(true);

                if (Build.VERSION.SDK_INT >= 24) {
                    tvOtpHint.setText(Html.fromHtml(String.format(Html.toHtml(new SpannedString(getResources().getText(R.string.msg_hint_otp_send_number_2)), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), countryCode, contactNumber, Html.FROM_HTML_MODE_LEGACY)));
                } else {
                    tvOtpHint.setText(Html.fromHtml(String.format(Html.toHtml(new SpannedString(getResources().getText(R.string.msg_hint_otp_send_number_2))), countryCode, contactNumber)));
                }
            }
        }.start();
        hideKeyBord();
//        otpReader = new OtpReader(this, preferenceHelper.getTwilioNumber());
//        registerReceiver(otpReader, new IntentFilter(Const.ACTION_OTP_SMS));
    }

    private void loadExtrasData() {
        if (getIntent().getExtras() != null) {
            country = getIntent().getExtras().getParcelable(Const.Params.COUNTRY);
            countryCode = Objects.requireNonNull(country).getCountryPhoneCode();
            countryName = country.getCountryName();
            contactNumber = getIntent().getExtras().getString(Const.Params.PHONE);
            otpForSMS = getIntent().getExtras().getString(Const.Params.OTP_FOR_SMS);
            isOpenForResult = getIntent().getExtras().getBoolean(Const.Params.IS_OPEN_FOR_RESULT);
            isFromForgetPassword = getIntent().getExtras().getBoolean(Const.Params.IS_FROM_FORGET_PASSWORD);
            email = getIntent().getExtras().getString(Const.Params.EMAIL, "");
        }
    }

    @Override
    protected void onDestroy() {
        // unregisterReceiver(otpReader);
        super.onDestroy();
    }

    @Override
    protected boolean isValidate() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
    }


    @Override
    public void goWithBackArrow() {
        hideKeyBord();
        onBackPressed();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnVerifyOtp:
                // check for otp  verification.
                checkForOtpVerification();
                break;
            case R.id.tvResendCodeTime:
                // resend code api call here
                if (isFromForgetPassword) {
                    getOtp(countryCode, contactNumber);
                } else {
                    verifyUserToServer(countryCode, contactNumber, email);
                }
                break;
            case R.id.tvEditMobileNumber:
                goWithBackArrow();
                break;

        }
    }

    private void getOtp(String countryCode, String contactNumber) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_loading), false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.PHONE, contactNumber);
            jsonObject.put(Const.Params.COUNTRY_PHONE_CODE, countryCode);
            Call<VerificationResponse> call = ApiClient.getClient().create(ApiInterface.class).getOtp(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<VerificationResponse>() {
                @Override
                public void onResponse(Call<VerificationResponse> call, Response<VerificationResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body() != null) {
                            verifiedUser(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(Call<VerificationResponse> call, Throwable t) {
                    AppLog.handleThrowable(OtpVerifyActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(OtpVerifyActivity.class.getSimpleName(), e);
        }
    }

    private void verifyUserToServer(final String countryCode, final String contactNumber, final String email) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_loading), false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.PHONE, contactNumber);
            jsonObject.put(Const.Params.COUNTRY_PHONE_CODE, countryCode);
            jsonObject.put(Const.Params.EMAIL, email);
            Call<VerificationResponse> call = ApiClient.getClient().create(ApiInterface.class).checkUserRegistered(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<VerificationResponse>() {
                @Override
                public void onResponse(Call<VerificationResponse> call, Response<VerificationResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body() != null) {
                            verifiedUser(response.body());
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


    private void verifiedUser(VerificationResponse response) {
        if (response.isSuccess()) {
            Utils.hideCustomProgressDialog();
            otpForSMS = response.getOtpForSMS();
            otpForEmail = response.getOtpForEmail();
            otp_view.setText("");
            tvResendCodeTime.setTextColor(getResources().getColor(R.color.color_app_text));
            tvResendCodeTime.setEnabled(false);
            new CountDownTimer(15000, 1000) {

                public void onTick(long millisUntilFinished) {
                    tvResendCodeTime.setText(getString(R.string.msg_resend_otp_time, TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }

                public void onFinish() {
                    tvResendCodeTime.setText(getString(R.string.msg_resend_code));
                    tvResendCodeTime.setTextColor(getResources().getColor(R.color.color_app_button));
                    tvResendCodeTime.setEnabled(true);
                    if (Build.VERSION.SDK_INT >= 24) {
                        tvOtpHint.setText(Html.fromHtml(String.format(Html.toHtml(new SpannedString(getResources().getText(R.string.msg_hint_otp_send_number_2)), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), countryCode, contactNumber, Html.FROM_HTML_MODE_LEGACY)));
                    } else {
                        tvOtpHint.setText(Html.fromHtml(String.format(Html.toHtml(new SpannedString(getResources().getText(R.string.msg_hint_otp_send_number_2))), countryCode, contactNumber)));
                    }
                }
            }.start();
        } else {
            Utils.hideCustomProgressDialog();
            Utils.showErrorToast(response.getErrorCode(), OtpVerifyActivity.this);
        }
    }

    private void checkForOtpVerification() {
        if (TextUtils.equals(otp_view.getText().toString().trim(), otpForSMS)) {
            if (isOpenForResult) {
                setResult(RESULT_OK);
                this.finish();
            } else if (isFromForgetPassword) {
                // go to update password screen
                Intent intent = new Intent(OtpVerifyActivity.this, NewPasswordActivity.class);
                intent.putExtra(Const.Params.PHONE, contactNumber);
                intent.putExtra(Const.Params.COUNTRY_PHONE_CODE, countryCode);
                goToUpdatePasswordActivity(intent);
            } else {
                Intent intent = new Intent(OtpVerifyActivity.this, RegisterActivity.class);
                intent.putExtra(Const.Params.PHONE, contactNumber);
                intent.putExtra(Const.Params.LOGIN_BY, Const.MANUAL);
                intent.putExtra(Const.Params.COUNTRY, country);
                intent.putExtra(Const.Params.EMAIL, email);
                intent.putExtra(Const.Params.IS_VERIFIED, true);
                goToRegisterActivity(intent);
            }
        } else {
            otp_view.setError(getString(R.string.msg_otp_wrong));
            otp_view.setEnabled(true);
        }
    }

    private void goToUpdatePasswordActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goToRegisterActivity(Intent intent) {
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
    public void otpReceived(String otp) {
        otp_view.setText(otp);
    }
}
