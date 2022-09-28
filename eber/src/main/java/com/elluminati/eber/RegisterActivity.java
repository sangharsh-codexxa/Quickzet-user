package com.elluminati.eber;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.elluminati.eber.components.CustomCountryDialog;
import com.elluminati.eber.components.CustomDialogBigLabel;
import com.elluminati.eber.components.MyFontEdittextView;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.Country;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.responsemodels.UserDataResponse;
import com.elluminati.eber.models.responsemodels.VerificationResponse;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.models.validations.Validator;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.FieldValidation;
import com.elluminati.eber.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseAppCompatActivity {
    private final static int REQUEST_CODE_OTP = 5677;
    private MyFontEdittextView etFirstName, etLastName, etRegisterPassword, etRegisterEmailId, etContactNumber;
    private MyFontTextView tvTerms, tvCountryCode;
    private FloatingActionButton btnRegister;
    private ArrayList<Country> codeArrayList;
    private CustomCountryDialog customCountryDialog;
    private String contactNumber, countryCode, countryName;
    private boolean isVerified = false;
    private boolean isEmailVerify, isSMSVerify, isBackPressedOnce;
    private String loginType = Const.MANUAL;
    private String socialId, socialEmail = "", socialPhotoUrl, socialFirstName, socialLastName;
    private Country country;
    private int phoneNumberLength, phoneNumberMinLength;
    private TextInputLayout tilPassword;
    private CheckBox cbTerms;
    private String msg = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initToolBar();
        // change toolbar color here
        toolbar.setBackgroundColor(getResources().getColor(R.color.color_white));
        setTitleOnToolbar("");
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.color_black), PorterDuff.Mode.SRC_ATOP);

        isEmailVerify = preferenceHelper.getIsUserEmailVerification();
        isSMSVerify = preferenceHelper.getIsUserSMSVerification();
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etRegisterEmailId = findViewById(R.id.etRegisterEmailId);
        tvCountryCode = findViewById(R.id.tvCountryCode);
        etContactNumber = findViewById(R.id.etContactNumber);
        etContactNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        tvTerms = findViewById(R.id.tvTerms);
        btnRegister = findViewById(R.id.btnRegister);
        tilPassword = findViewById(R.id.tilPassword);
        btnRegister.setOnClickListener(this);
        tvCountryCode.setOnClickListener(this);
        codeArrayList = CurrentTrip.getInstance().getCountryCodes();

        if (codeArrayList != null && !codeArrayList.isEmpty()) {
            setCountry(codeArrayList.get(0));
        }
        loadExtraData();
        String link = getResources().getString(R.string.text_trems_and_condition_main, preferenceHelper.getTermsANdConditions(), preferenceHelper.getPolicy());
        tvTerms.setText(Utils.fromHtml(link));
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        setUpUIForManualRegister(TextUtils.equals(loginType, Const.MANUAL));
        etRegisterPassword.addTextChangedListener(new TextWatcher() {
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

        etContactNumber.setLongClickable(false);
        etContactNumber.setInputType(InputType.TYPE_CLASS_NUMBER);

        cbTerms = findViewById(R.id.cbTerms);
        disableRegisterButton();
        cbTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableRegisterButton();
                } else {
                    disableRegisterButton();
                }
            }
        });

    }

    private void enableRegisterButton() {
        btnRegister.setEnabled(true);
        btnRegister.setAlpha(1f);
    }

    private void disableRegisterButton() {
        btnRegister.setEnabled(false);
        btnRegister.setAlpha(0.5f);
    }

    private void setUpUIForManualRegister(boolean isManual) {
        if (isManual) {
            tvCountryCode.setEnabled(false);
            etContactNumber.setEnabled(false);
            tvCountryCode.setText(countryCode);
            etContactNumber.setText(contactNumber);
            etRegisterPassword.setVisibility(View.VISIBLE);
            tilPassword.setVisibility(View.VISIBLE);
        } else {
            tvCountryCode.setEnabled(true);
            etContactNumber.setEnabled(true);
            etRegisterPassword.setVisibility(View.GONE);
            tilPassword.setVisibility(View.GONE);
            etFirstName.setText(socialFirstName);
            etLastName.setText(socialLastName);
            etRegisterEmailId.setText(socialEmail);
            etRegisterEmailId.setText(socialEmail);
        }
    }


    @Override
    public void onBackPressed() {
        if (isBackPressedOnce) {
            super.onBackPressed();
            backToMainActivity();
        } else {
            openDetailNotSaveDialog();
        }
    }

    private void openDetailNotSaveDialog() {
        CustomDialogBigLabel detailNotSaveDialog = new CustomDialogBigLabel(this, getResources().getString(R.string.msg_are_you_sure), getResources().getString(R.string.msg_not_save), getResources().getString(R.string.text_yes), getResources().getString(R.string.text_no)) {
            @Override
            public void positiveButton() {
                this.dismiss();
                isBackPressedOnce = true;
                RegisterActivity.this.onBackPressed();

            }

            @Override
            public void negativeButton() {
                this.dismiss();
            }
        };
        detailNotSaveDialog.show();
    }

    private void backToMainActivity() {
        Intent sigInIntent = new Intent(this, MainActivity.class);
        sigInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        sigInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(sigInIntent);
        finishAffinity();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }


    private void setCountry(Country country) {
        this.country = country;
        tvCountryCode.setText(country.getCountryPhoneCode());
        countryName = country.getCountryName();
//        phoneNumberLength = country.getPhoneNumberLength();
//        phoneNumberMinLength = country.getPhoneNumberMinLength();
//        setContactNoLength(phoneNumberLength);
    }


    @Override
    protected boolean isValidate() {
        msg = null;
        Validator emailValidation = FieldValidation.isEmailValid(RegisterActivity.this, etRegisterEmailId.getText().toString().trim());

        if (TextUtils.isEmpty(etFirstName.getText().toString().trim())) {
            msg = getString(R.string.msg_enter_name);
            etFirstName.requestFocus();
            etFirstName.setError(msg);
        } else if (!TextUtils.isEmpty(etRegisterEmailId.getText().toString().trim()) || preferenceHelper.getIsUserEmailVerification()) {
            if (!emailValidation.isValid()) {
                msg = emailValidation.getErrorMsg();
                etRegisterEmailId.requestFocus();
                etRegisterEmailId.setError(msg);
            } else {
                validateOtherInformation();
            }
        } else {
            validateOtherInformation();
        }
        return TextUtils.isEmpty(msg);
    }


    private void validateOtherInformation() {
        Validator passwordValidation = FieldValidation.isPasswordValid(RegisterActivity.this, etRegisterPassword.getText().toString().trim());
        if (etRegisterPassword.getVisibility() == View.VISIBLE && !passwordValidation.isValid()) {
            msg = passwordValidation.getErrorMsg();
            etRegisterPassword.requestFocus();
            etRegisterPassword.setError(msg);
            tilPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
        } else if (TextUtils.isEmpty(etContactNumber.getText().toString().trim())) {
            msg = getString(R.string.msg_enter_number);
            etContactNumber.setError(msg);
            etContactNumber.requestFocus();
        } else if (!Utils.isValidPhoneNumber(etContactNumber.getText().toString(), preferenceHelper.getMinimumPhoneNumberLength(), preferenceHelper.getMaximumPhoneNumberLength())) {
            msg = getString(R.string.msg_enter_valid_number);
            etContactNumber.requestFocus();
            etContactNumber.setError(msg);
        }
    }


    @Override
    public void goWithBackArrow() {
        hideKeyBord();
        onBackPressed();
    }

    private void loadExtraData() {
        if (getIntent().getExtras() != null) {
            loginType = getIntent().getExtras().getString(Const.Params.LOGIN_BY);
            if (TextUtils.equals(loginType, Const.MANUAL)) {
                country = getIntent().getExtras().getParcelable(Const.Params.COUNTRY);
                isVerified = getIntent().getExtras().getBoolean(Const.Params.IS_VERIFIED);
                contactNumber = getIntent().getExtras().getString(Const.Params.PHONE);
                countryName = country.getCountryName();
                countryCode = country.getCountryPhoneCode();
//                phoneNumberMinLength = country.getPhoneNumberMinLength();
//                phoneNumberLength = country.getPhoneNumberLength();
//                setContactNoLength(phoneNumberLength);

            } else {
                socialEmail = getIntent().getExtras().getString(Const.Params.EMAIL);
                socialFirstName = getIntent().getExtras().getString(Const.Params.FIRST_NAME);
                socialLastName = getIntent().getExtras().getString(Const.Params.LAST_NAME);
                socialId = getIntent().getExtras().getString(Const.Params.SOCIAL_UNIQUE_ID);
                socialPhotoUrl = getIntent().getExtras().getString(Const.Params.PICTURE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCountryCode:
                openCountryCodeDialog();
                break;
            case R.id.btnRegister:
                checkValidationForRegister();
                break;

        }
    }

    private void checkValidationForRegister() {
        if (isValidate()) {
            if (isSMSVerify) {
                if (isVerified) {
                    // otp already verified go for register.
                    register(loginType);
                } else {
                    // Social login but need verify contact No ,go to otp screen its not verified.
                    verifyUserToServer(tvCountryCode.getText().toString(), etContactNumber.getText().toString());
                }
            } else {
                register(loginType);
            }
        }
    }

    private void verifyUserToServer(final String countryCode, String contactNumber) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_loading), false, null);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.PHONE, contactNumber);
            jsonObject.put(Const.Params.COUNTRY_PHONE_CODE, countryCode);
            Call<VerificationResponse> call = ApiClient.getClient().create(ApiInterface.class).checkUserRegistered(ApiClient.makeJSONRequestBody(jsonObject));

            call.enqueue(new Callback<VerificationResponse>() {
                @Override
                public void onResponse(Call<VerificationResponse> call, Response<VerificationResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            Utils.hideCustomProgressDialog();
                            if (TextUtils.equals(Const.MESSAGE_CODE_USER_EXIST, response.body().getMessage())) {
                                Utils.showToast(getResources().getString(R.string.error_user_already_register), RegisterActivity.this);
                            } else {
                                Intent otpIntent = new Intent(RegisterActivity.this, OtpVerifyActivity.class);
                                otpIntent.putExtra(Const.Params.COUNTRY, country);
                                otpIntent.putExtra(Const.Params.PHONE, etContactNumber.getText().toString().trim());
                                String otpForSMS = response.body().getOtpForSMS();
                                String otpForEmail = response.body().getOtpForEmail();
                                otpIntent.putExtra(Const.Params.OTP_FOR_EMAIL, otpForEmail);
                                otpIntent.putExtra(Const.Params.OTP_FOR_SMS, otpForSMS);
                                otpIntent.putExtra(Const.Params.IS_OPEN_FOR_RESULT, true);
                                otpIntent.putExtra(Const.Params.EMAIL, socialEmail);
                                otpIntent.putExtra(Const.Params.IS_FROM_FORGET_PASSWORD, false);
                                startActivityForResult(otpIntent, REQUEST_CODE_OTP);
                            }
                        } else {
                            Utils.hideCustomProgressDialog();
                            Utils.showErrorToast(response.body().getErrorCode(), RegisterActivity.this);
                        }
                    }

                }

                @Override
                public void onFailure(Call<VerificationResponse> call, Throwable t) {
                    AppLog.handleThrowable(RegisterActivity.class.getSimpleName(), t);
                }
            });

        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.SIGN_IN_ACTIVITY, e);
        }
    }


    private void register(String loginType) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_waiting_for_registering), false, null);
        HashMap<String, RequestBody> map = new HashMap<>();
        try {
            map.put(Const.Params.EMAIL, ApiClient.makeTextRequestBody(etRegisterEmailId.getText().toString()));
            map.put(Const.Params.FIRST_NAME, ApiClient.makeTextRequestBody(etFirstName.getText().toString()));
            map.put(Const.Params.LAST_NAME, ApiClient.makeTextRequestBody(etLastName.getText().toString()));
            map.put(Const.Params.SOCIAL_UNIQUE_ID, ApiClient.makeTextRequestBody(""));
            map.put(Const.Params.DEVICE_TYPE, ApiClient.makeTextRequestBody(Const.DEVICE_TYPE_ANDROID));
            map.put(Const.Params.DEVICE_TOKEN, ApiClient.makeTextRequestBody(preferenceHelper.getDeviceToken()));
            if (loginType.equalsIgnoreCase(Const.MANUAL)) {
                map.put(Const.Params.PASSWORD, ApiClient.makeTextRequestBody(etRegisterPassword.getText().toString()));
            } else {
                map.put(Const.Params.PASSWORD, ApiClient.makeTextRequestBody(""));
                map.put(Const.Params.SOCIAL_UNIQUE_ID, ApiClient.makeTextRequestBody(socialId));
            }
            map.put(Const.Params.PHONE, ApiClient.makeTextRequestBody(etContactNumber.getText().toString()));
            map.put(Const.Params.COUNTRY_PHONE_CODE, ApiClient.makeTextRequestBody(tvCountryCode.getText().toString()));
            map.put(Const.Params.DEVICE_TIMEZONE, ApiClient.makeTextRequestBody(Utils.getTimeZoneName()));
            map.put(Const.Params.COUNTRY, ApiClient.makeTextRequestBody(countryName));
            map.put(Const.Params.LOGIN_BY, ApiClient.makeTextRequestBody(loginType));
            map.put(Const.Params.APP_VERSION, ApiClient.makeTextRequestBody(getAppVersion()));
            Call<UserDataResponse> userDataResponseCall = ApiClient.getClient().create(ApiInterface.class).register(null, map);
            userDataResponseCall.enqueue(new Callback<UserDataResponse>() {
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
                    AppLog.handleThrowable(RegisterActivity.class.getSimpleName(), t);
                    Utils.hideCustomProgressDialog();
                }
            });
        } catch (Exception e) {
            AppLog.handleException(RegisterActivity.class.getSimpleName(), e);
            Utils.hideCustomProgressDialog();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_OTP:
                if (resultCode == RESULT_OK) {
                    register(loginType);
                }
                break;
        }
    }


    private void openCountryCodeDialog() {

        customCountryDialog = null;
        customCountryDialog = new CustomCountryDialog(this, codeArrayList) {
            @Override
            public void onSelect(int position, ArrayList<Country> filterList) {
                setCountry(filterList.get(position));
                customCountryDialog.dismiss();

            }
        };
        customCountryDialog.show();

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
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void setContactNoLength(int length) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        etContactNumber.setFilters(FilterArray);
    }
}