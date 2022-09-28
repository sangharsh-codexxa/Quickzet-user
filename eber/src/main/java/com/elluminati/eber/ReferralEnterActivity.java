package com.elluminati.eber;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.elluminati.eber.components.MyFontButton;
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

public class ReferralEnterActivity extends BaseAppCompatActivity {
    private LinearLayout llInvalidReferral;
    private MyFontEdittextView etReferralCode;
    private MyFontButton btnReferralSubmit, btnReferralSkip;
    private String tampSassoonToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_enter);
        llInvalidReferral = findViewById(R.id.llInvalidReferral);
        etReferralCode = findViewById(R.id.etReferralCode);
        btnReferralSkip = findViewById(R.id.btnReferralSkip);
        btnReferralSubmit = findViewById(R.id.btnReferralSubmit);
        btnReferralSkip.setOnClickListener(this);
        btnReferralSubmit.setOnClickListener(this);
        updateUi(false);
        tampSassoonToken = preferenceHelper.getSessionToken();
        preferenceHelper.putSessionToken(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
    }

    @Override
    protected boolean isValidate() {
        if (!TextUtils.isEmpty(etReferralCode.getText().toString())) {
            return true;
        } else {
            etReferralCode.requestFocus();
            etReferralCode.setError(getString(R.string.error_referral));
        }
        return false;
    }

    @Override
    public void goWithBackArrow() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReferralSubmit:
                if (isValidate()) {
                    applyReferral(Const.ProviderStatus.IS_REFERRAL_APPLY);
                }
                break;
            case R.id.btnReferralSkip:
                applyReferral(Const.ProviderStatus.IS_REFERRAL_SKIP);
                break;
            default:
                break;
        }
    }

    private void applyReferral(int status) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_waiting_for_apply_referral), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, tampSassoonToken);
            if (status == Const.ProviderStatus.IS_REFERRAL_SKIP) {
                jsonObject.put(Const.Params.REFERRAL_CODE, preferenceHelper.getReferralCode());
            } else {
                jsonObject.put(Const.Params.REFERRAL_CODE, etReferralCode.getText().toString());
            }
            jsonObject.put(Const.Params.IS_SKIP, status);

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).applyReferralCode(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {

                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            preferenceHelper.putSessionToken(tampSassoonToken);
                            preferenceHelper.putIsApplyReferral(response.body().getIsReferral());
                            Utils.hideCustomProgressDialog();
                            Utils.showMessageToast(response.body().getMessage(), ReferralEnterActivity.this);
                            moveWithUserSpecificPreference();
                        } else {
                            Utils.hideCustomProgressDialog();
                            updateUi(true);
                        }
                    }
                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(ReferralEnterActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.REFERRAL_CODE_ACTIVITY, e);
        }
    }

    private void updateUi(boolean isUpdate) {
        if (isUpdate) {
            llInvalidReferral.setVisibility(View.VISIBLE);
        } else {
            llInvalidReferral.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        openExitDialog(this);
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
    public void onAdminApproved() {
        goWithAdminApproved();
    }

    @Override
    public void onAdminDeclined() {
        goWithAdminDecline();
    }
}