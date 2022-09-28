package com.elluminati.eber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.adapter.FavouriteDriverAdapter;
import com.elluminati.eber.fragments.FeedbackFragment;
import com.elluminati.eber.models.datamodels.FavouriteDriver;
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

public class FavouriteDriverActivity extends BaseAppCompatActivity {

    private final ArrayList<FavouriteDriver> favouriteDrivers = new ArrayList<>();
    private RecyclerView rcvFavouriteDriver;
    private FavouriteDriverAdapter favouriteDriverAdapter;
    private TextView tvNoFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_driver);
        initToolBar();
        setTitleOnToolbar(getResources().getString(R.string.text_favourite_driver));
        tvNoFavourite = findViewById(R.id.tvNoFavourite);
        Button btnAddDriver = findViewById(R.id.btnAddDriver);
        btnAddDriver.setOnClickListener(this);
        initRcvFavouriteDriver();
        getFavouriteDriver();
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
        if (v.getId() == R.id.btnAddDriver) {
            Intent intent = new Intent(this, AddFavouriteDriverActivity.class);
            startActivityForResult(intent, Const.REQUEST_ADD_DRIVER);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.REQUEST_ADD_DRIVER && resultCode == Activity.RESULT_OK) {
            getFavouriteDriver();
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
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getFavouriteDriver() {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_waiting_for_ratting), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());

            Call<FavouriteDriverResponse> call = ApiClient.getClient().create(ApiInterface.class).getFavouriteDriver(ApiClient.makeJSONRequestBody(jsonObject));

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
                                tvNoFavourite.setVisibility(View.VISIBLE);
                                rcvFavouriteDriver.setVisibility(View.GONE);
                            } else {
                                tvNoFavourite.setVisibility(View.GONE);
                                rcvFavouriteDriver.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), FavouriteDriverActivity.this);
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

    public void removeFavouriteDriver(final int position) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_waiting_for_ratting), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.PROVIDER_ID, favouriteDrivers.get(position).getId());

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).removeFavouriteDriver(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            favouriteDrivers.remove(position);
                            favouriteDriverAdapter.notifyDataSetChanged();
                            if (favouriteDrivers.isEmpty()) {
                                tvNoFavourite.setVisibility(View.VISIBLE);
                                rcvFavouriteDriver.setVisibility(View.GONE);
                            } else {
                                tvNoFavourite.setVisibility(View.GONE);
                                rcvFavouriteDriver.setVisibility(View.VISIBLE);
                            }
                            Utils.showMessageToast(response.body().getMessage(), FavouriteDriverActivity.this);
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), FavouriteDriverActivity.this);

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
            AppLog.handleException(FavouriteDriverActivity.class.getSimpleName(), e);
        }


    }

    private void initRcvFavouriteDriver() {
        rcvFavouriteDriver = findViewById(R.id.rcvFavouriteDriver);
        rcvFavouriteDriver.setLayoutManager(new LinearLayoutManager(this));
        favouriteDriverAdapter = new FavouriteDriverAdapter(favouriteDrivers) {

            @Override
            public void onClick(int position) {
                removeFavouriteDriver(position);

            }
        };
        rcvFavouriteDriver.setAdapter(favouriteDriverAdapter);
    }
}
