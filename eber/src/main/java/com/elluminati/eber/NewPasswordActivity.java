package com.elluminati.eber;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.elluminati.eber.components.MyFontEdittextView;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPasswordActivity extends BaseAppCompatActivity {

    private MyFontEdittextView etNewPassword, etConfirmPassword;
    private String countryCode, contactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        initToolBar();
        // change toolbar color here
        toolbar.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.color_white, null));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        setTitleOnToolbar("");
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.color_black), PorterDuff.Mode.SRC_ATOP);

        loadExtrasData();
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        findViewById(R.id.btnUpdatePassword).setOnClickListener(this);
    }

    @Override
    public void goWithBackArrow() {
        hideKeyBord();
        onBackPressed();
    }

    @Override
    protected boolean isValidate() {
        String msg = null;
        String NewPassword = etNewPassword.getText().toString().trim();
        String ConfirmPassword = etConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(NewPassword)) {
            msg = getString(R.string.msg_enter_password);
            etNewPassword.requestFocus();
            etNewPassword.setError(msg);
        } else if (NewPassword.length() < 6) {
            msg = getString(R.string.msg_enter_valid_password);
            etNewPassword.requestFocus();
            etNewPassword.setError(msg);
        } else if (TextUtils.isEmpty(ConfirmPassword)) {
            msg = getString(R.string.msg_enter_password);
            etConfirmPassword.requestFocus();
            etConfirmPassword.setError(msg);
        } else if (ConfirmPassword.length() <6 ){
            msg = getString(R.string.msg_enter_valid_password);
            etConfirmPassword.requestFocus();
            etConfirmPassword.setError(msg);
        }else if (!TextUtils.equals(NewPassword, ConfirmPassword)) {
            msg = getString(R.string.msg_incorrect_confirm_password);
            etConfirmPassword.setError(msg);
        }
        return msg == null;
    }

    private void loadExtrasData() {
        if (getIntent().getExtras() != null) {
            contactNumber = getIntent().getExtras().getString(Const.Params.PHONE);
            countryCode = getIntent().getExtras().getString(Const.Params.COUNTRY_PHONE_CODE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdatePassword:
                if (isValidate()) {
                    updatePassword(countryCode, contactNumber, etConfirmPassword.getText().toString().trim());
                }
                break;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
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


    private void updatePassword(String countryCode, String contactNumber, String password) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_loading), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.PHONE, contactNumber);
            jsonObject.put(Const.Params.COUNTRY_PHONE_CODE, countryCode);
            jsonObject.put(Const.Params.PASSWORD, password);
            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).updatePassword(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            goToMainActivity();
                            Utils.showMessageToast(response.body().getMessage(), NewPasswordActivity.this);
                            Utils.hideCustomProgressDialog();
                        } else {
                            Utils.hideCustomProgressDialog();
                            Utils.showErrorToast(response.body().getErrorCode(), NewPasswordActivity.this);
                        }
                    }


                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(NewPasswordActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.SIGN_IN_ACTIVITY, e);
        }
    }
}