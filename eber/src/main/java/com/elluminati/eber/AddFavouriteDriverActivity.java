package com.elluminati.eber;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.adapter.FavouriteDriverAdapter;
import com.elluminati.eber.fragments.FeedbackFragment;
import com.elluminati.eber.models.datamodels.FavouriteDriver;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.responsemodels.FavouriteDriverResponse;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFavouriteDriverActivity extends BaseAppCompatActivity {

    private final ArrayList<FavouriteDriver> favouriteDrivers = new ArrayList<>();
    private FavouriteDriverAdapter favouriteDriverAdapter;
    private EditText etSearchDriver;
    private TextView tvNoDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favourite_driver);
        initToolBar();
        setTitleOnToolbar(getResources().getString(R.string.text_add_fav_driver));
        initRcvFavouriteDriver();
        tvNoDriver = findViewById(R.id.tvNoDriver);
        etSearchDriver = findViewById(R.id.etSearchDriver);
        Button btnSearchDriver = findViewById(R.id.btnSearchDriver);
        btnSearchDriver.setOnClickListener(this);
        etSearchDriver.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getAllDriver(etSearchDriver.getText().toString().trim());
                    return true;
                }

                return false;
            }
        });

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

        if (v.getId() == R.id.btnSearchDriver) {
            getAllDriver(etSearchDriver.getText().toString().trim());
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
        goWithAdminApproved();
    }

    @Override
    public void onAdminDeclined() {
        goWithAdminDecline();
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
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }


    public void getAllDriver(String search) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_waiting_for_ratting), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.SEARCH_VALUE, search);

            Call<FavouriteDriverResponse> call = ApiClient.getClient().create(ApiInterface.class).getDrivers(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<FavouriteDriverResponse>() {
                @Override
                public void onResponse(Call<FavouriteDriverResponse> call, Response<FavouriteDriverResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            favouriteDrivers.clear();
                            favouriteDrivers.addAll(response.body().getProviderList());
                            favouriteDriverAdapter.notifyDataSetChanged();
                            if (favouriteDrivers.isEmpty()) {
                                tvNoDriver.setVisibility(View.VISIBLE);
                            } else {
                                tvNoDriver.setVisibility(View.GONE);
                            }

                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), AddFavouriteDriverActivity.this);

                        }
                    }

                }

                @Override
                public void onFailure(Call<FavouriteDriverResponse> call, Throwable t) {
                    AppLog.handleThrowable(FeedbackFragment.class.getSimpleName(), t);
                    Utils.hideCustomProgressDialog();
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(FavouriteDriverActivity.class.getSimpleName(), e);
        }


    }

    private void addFavouriteDriver(final int position) {
        Utils.showCustomProgressDialog(this, this.getResources().getString(R.string.msg_waiting_for_ratting), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, this.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, this.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.PROVIDER_ID, favouriteDrivers.get(position).getId());

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).addFavouriteDriver(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            favouriteDrivers.remove(position);
                            favouriteDriverAdapter.notifyDataSetChanged();
                            Utils.showMessageToast(response.body().getMessage(), AddFavouriteDriverActivity.this);
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), AddFavouriteDriverActivity.this);

                        }
                    }

                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(FeedbackFragment.class.getSimpleName(), t);
                    Utils.hideCustomProgressDialog();
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.FEEDBACK_FRAGMENT, e);
        }


    }

    private void initRcvFavouriteDriver() {
        RecyclerView rcvFavouriteDriver = findViewById(R.id.rcvFavouriteDriver);
        rcvFavouriteDriver.setLayoutManager(new LinearLayoutManager(this));
        favouriteDriverAdapter = new FavouriteDriverAdapter(favouriteDrivers) {

            @Override
            public void onClick(int position) {
                addFavouriteDriver(position);

            }
        };
        favouriteDriverAdapter.enableAddDriver(true);
        rcvFavouriteDriver.setAdapter(favouriteDriverAdapter);
    }
}