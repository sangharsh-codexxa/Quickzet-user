package com.elluminati.eber;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.elluminati.eber.components.MyFontButton;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.models.singleton.CurrentTrip;
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

public class ReferralDisplayActivtiy extends BaseAppCompatActivity {
    private MyFontTextView tvUserReferralCode;
    private MyFontButton btnShareReferralCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_display);
        initToolBar();
        setTitleOnToolbar(getResources().getString(R.string.text_referral));
        tvUserReferralCode = findViewById(R.id.tvUserReferralCode);
        btnShareReferralCode = findViewById(R.id.btnShareReferralCode);
        btnShareReferralCode.setOnClickListener(this);
        tvUserReferralCode.setText(preferenceHelper.getReferralCode());
        getReferralCredits();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
    }

    @Override
    protected boolean isValidate() {
        return false;
    }

    @Override
    public void goWithBackArrow() {
        onBackPressed();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShareReferralCode:
                shareReferralCode();
                break;

            default:

                break;
        }
    }

    private void shareReferralCode() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + "" + " " + getResources().getString(R.string.msg_my_referral_code_is) + " " + preferenceHelper.getReferralCode());
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.text_referral_share_with_)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

    private void getReferralCredits() {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).getReferralCredit(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            TextView referralCredits = findViewById(R.id.tvUserReferralCradit);
                            referralCredits.setText(String.format("%s %s", response.body().getTotalReferralCredit(), CurrentTrip.getInstance().getCurrencyCode()));
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), ReferralDisplayActivtiy.this);
                        }
                    }

                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(ReferralDisplayActivtiy.class.getSimpleName(), t);

                }
            });

        } catch (JSONException e) {
            AppLog.handleException(ReferralDisplayActivtiy.class.getSimpleName(), e);
        }
    }
}