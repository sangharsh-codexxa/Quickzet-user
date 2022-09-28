package com.elluminati.eber;

import static com.elluminati.eber.utils.Const.MESSAGE_CODE_USER_EXIST;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;

import androidx.core.content.res.ResourcesCompat;

import com.elluminati.eber.components.CustomCountryDialog;
import com.elluminati.eber.components.MyFontEdittextView;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.Country;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.responsemodels.VerificationResponse;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseAppCompatActivity {

    private MyFontTextView tvCountryCode, ivGetMovingWith;
    private MyFontEdittextView etContactNumber;
    private CustomCountryDialog customCountryDialog;
    private Country country;
    private FloatingActionButton btnGetOtp;
    private int phoneNumberLength, phoneNumberMinLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_login);
        etContactNumber = findViewById(R.id.etContactNumber);
        etContactNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        loadExtraData();
        initToolBar();
        // change toolbar color here
        toolbar.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.color_white, null));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        setTitleOnToolbar("");
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.color_black), PorterDuff.Mode.SRC_ATOP);

        tvCountryCode = findViewById(R.id.tvCountryCode);
        ivGetMovingWith = findViewById(R.id.ivGetMovingWith);
        ivGetMovingWith.setText(getString(R.string.text_get_moving_with) + " " + getString(R.string.app_name));
        tvCountryCode.setOnClickListener(this);
        tvCountryCode.setText(country.getCountryPhoneCode());
        btnGetOtp = findViewById(R.id.btnGetOtp);
        btnGetOtp.setOnClickListener(this);
        etContactNumber.setLongClickable(false);
        etContactNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
    }


    private void loadExtraData() {
        if (getIntent().getExtras() != null) {
            country = getIntent().getExtras().getParcelable(Const.Params.COUNTRY);
//            phoneNumberLength = country.getPhoneNumberLength();
//            phoneNumberMinLength = country.getPhoneNumberMinLength();
//            setContactNoLength(phoneNumberLength);
        }

    }

    private void setContactNoLength(int length) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        etContactNumber.setFilters(FilterArray);
    }

    @Override
    protected boolean isValidate() {
        String msg = null;
        if (TextUtils.isEmpty(etContactNumber.getText().toString().trim())) {
            msg = getString(R.string.msg_enter_number);
            etContactNumber.setError(msg);
            etContactNumber.requestFocus();
        } else if (!Utils.isValidPhoneNumber(etContactNumber.getText().toString(), preferenceHelper.getMinimumPhoneNumberLength(), preferenceHelper.getMaximumPhoneNumberLength())) {
            msg = getString(R.string.msg_enter_valid_number);
            etContactNumber.requestFocus();
            etContactNumber.setError(msg);
        }

        return TextUtils.isEmpty(msg);
    }

    @Override
    public void goWithBackArrow() {
        hideKeyBord();
        onBackPressed();
    }

    private void openCountryCodeDialog() {
        customCountryDialog = null;
        customCountryDialog = new CustomCountryDialog(this, CurrentTrip.getInstance().getCountryCodes()) {
            @Override
            public void onSelect(int position, ArrayList<Country> filterList) {
                country = filterList.get(position);
                tvCountryCode.setText(country.getCountryPhoneCode());
//                phoneNumberLength = country.getPhoneNumberLength();
//                phoneNumberMinLength = country.getPhoneNumberMinLength();
//                setContactNoLength(phoneNumberLength);
                customCountryDialog.dismiss();
            }
        };
        customCountryDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCountryCode:
                openCountryCodeDialog();
                break;
            case R.id.btnGetOtp:
                hideKeyBord();
                if (isValidate()) {
                    verifyUserToServer(country, etContactNumber.getText().toString().trim());
                }
                break;
            default:

                break;
        }
    }

    private void verifyUserToServer(final Country country, String contactNumber) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_loading), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.PHONE, contactNumber);
            jsonObject.put(Const.Params.COUNTRY_PHONE_CODE, country.getCountryPhoneCode());
            Call<VerificationResponse> call = ApiClient.getClient().create(ApiInterface.class).checkUserRegistered(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<VerificationResponse>() {
                @Override
                public void onResponse(Call<VerificationResponse> call, Response<VerificationResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            Utils.hideCustomProgressDialog();
                            if (!TextUtils.isEmpty(response.body().getMessage())) {
                                if (response.body().getMessage().equalsIgnoreCase(MESSAGE_CODE_USER_EXIST)) {
                                    // go to password enter screen
                                    Intent passwordIntent = new Intent(LoginActivity.this, EnterPasswordActivity.class);
                                    passwordIntent.putExtra(Const.Params.COUNTRY, country);
                                    passwordIntent.putExtra(Const.Params.PHONE, etContactNumber.getText().toString().trim());
                                    goToEnterPasswordActivity(passwordIntent);
                                }
                            } else {
                                if (response.body().isUserSms()) {
                                    Intent otpIntent = new Intent(LoginActivity.this, OtpVerifyActivity.class);
                                    otpIntent.putExtra(Const.Params.COUNTRY, country);
                                    otpIntent.putExtra(Const.Params.PHONE, etContactNumber.getText().toString().trim());
                                    otpIntent.putExtra(Const.Params.IS_OPEN_FOR_RESULT, false);
                                    otpIntent.putExtra(Const.Params.OTP_FOR_SMS, response.body().getOtpForSMS());
                                    otpIntent.putExtra(Const.Params.IS_VERIFIED, false);
                                    goToOtpVerifyActivity(otpIntent);
                                } else {
                                    // go to register screen.
                                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                    intent.putExtra(Const.Params.PHONE, etContactNumber.getText().toString().trim());
                                    intent.putExtra(Const.Params.IS_VERIFIED, true);
                                    intent.putExtra(Const.Params.LOGIN_BY, Const.MANUAL);
                                    intent.putExtra(Const.Params.COUNTRY, country);
                                    goToRegisterActivity(intent);
                                }
                            }
                        } else {
                            Utils.hideCustomProgressDialog();
                            Utils.showErrorToast(response.body().getErrorCode(), LoginActivity.this);
                        }
                    }
                }

                @Override
                public void onFailure(Call<VerificationResponse> call, Throwable t) {
                    AppLog.handleThrowable(LoginActivity.class.getSimpleName(), t);
                }
            });

        } catch (JSONException e) {
            AppLog.handleException(LoginActivity.class.getSimpleName(), e);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
    }


    private void goToEnterPasswordActivity(Intent intent) {
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


    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition transition;
            transition = TransitionInflater.from(this).inflateTransition(R.transition.slide_and_changebounds);
            getWindow().setSharedElementEnterTransition(transition);
        }
    }

    protected void goToOtpVerifyActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}