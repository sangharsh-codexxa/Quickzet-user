package com.elluminati.eber;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.adapter.UserAdapter;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.models.responsemodels.SearchUseForPaymentSplitResponse;
import com.elluminati.eber.models.responsemodels.TripResponse;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.SocketHelper;
import com.elluminati.eber.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplitPaymentActivity extends BaseAppCompatActivity {

    private final ArrayList<SplitPaymentRequest> userList = new ArrayList<>();
    private RecyclerView rcvUser;
    private UserAdapter userAdapter;
    private TextView tvNoUser;
    private EditText etSearchUser;
    private View llSearch;
    private MyFontTextView tvNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_payment);
        initToolBar();
        setTitleOnToolbar(getString(R.string.text_split_payment));

        tvNoUser = findViewById(R.id.tvNoUser);
        etSearchUser = findViewById(R.id.etSearchUser);
        rcvUser = findViewById(R.id.rcvUser);
        llSearch = findViewById(R.id.llSearch);
        tvNote = findViewById(R.id.tvNote);
        Button btnSearchUser = findViewById(R.id.btnSearchUser);
        btnSearchUser.setOnClickListener(this);

        etSearchUser.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchUserForSplitPayment(etSearchUser.getText().toString().trim());
                return true;
            }
            return false;
        });

        initRcvUser(null);
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
        if (v.getId() == R.id.btnSearchUser) {
            searchUserForSplitPayment(etSearchUser.getText().toString().trim());
        }
    }

    private void searchUserForSplitPayment(String search) {
        if (TextUtils.isEmpty(search)) {
            return;
        }

        Utils.showCustomProgressDialog(this, "", false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.SEARCH_USER, search);

            Call<SearchUseForPaymentSplitResponse> call = ApiClient.getClient().create(ApiInterface.class).searchUserForSplitPayment(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<SearchUseForPaymentSplitResponse>() {
                @Override
                public void onResponse(@NonNull Call<SearchUseForPaymentSplitResponse> call, @NonNull Response<SearchUseForPaymentSplitResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                etSearchUser.setText("");
                                initRcvUser(response.body().getSearchUserDetail());
                            } else {
                                Utils.showErrorToast(response.body().getErrorCode(), SplitPaymentActivity.this);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SearchUseForPaymentSplitResponse> call, @NonNull Throwable t) {
                    AppLog.handleThrowable(TAG, t);
                    Utils.hideCustomProgressDialog();
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
    }

    private void sendSplitPaymentRequest(String userId) {
        Utils.showCustomProgressDialog(this, "", false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.SPLIT_REQUEST_USER_ID, userId);
            jsonObject.put(Const.Params.TRIP_ID, preferenceHelper.getTripId());

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).sendSplitPaymentRequest(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(@NonNull Call<IsSuccessResponse> call, @NonNull Response<IsSuccessResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                getTripStatus(true);
                            } else {
                                Utils.showErrorToast(response.body().getErrorCode(), SplitPaymentActivity.this);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<IsSuccessResponse> call, @NonNull Throwable t) {
                    AppLog.handleThrowable(TAG, t);
                    Utils.hideCustomProgressDialog();
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
    }

    private void removeSplitPaymentRequest(String userId) {
        Utils.showCustomProgressDialog(this, "", false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.SPLIT_REQUEST_USER_ID, userId);
            jsonObject.put(Const.Params.TRIP_ID, preferenceHelper.getTripId());

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).removeSplitPaymentRequest(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(@NonNull Call<IsSuccessResponse> call, @NonNull Response<IsSuccessResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                getTripStatus(true);
                            } else {
                                Utils.showErrorToast(response.body().getErrorCode(), SplitPaymentActivity.this);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<IsSuccessResponse> call, @NonNull Throwable t) {
                    AppLog.handleThrowable(TAG, t);
                    Utils.hideCustomProgressDialog();
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
    }

    @SuppressLint({"NotifyDataSetChanged", "StringFormatInvalid"})
    private void initRcvUser(SplitPaymentRequest searchUserDetail) {
        userList.clear();
        if (searchUserDetail != null) {
            userList.add(0, searchUserDetail);
        }

        if (CurrentTrip.getInstance().getSplitPaymentUsers() != null
                && !CurrentTrip.getInstance().getSplitPaymentUsers().isEmpty()) {
            userList.addAll(CurrentTrip.getInstance().getSplitPaymentUsers());

            if (preferenceHelper.getMaxSplitUser() == CurrentTrip.getInstance().getSplitPaymentUsers().size()) {
                llSearch.setVisibility(View.GONE);
                tvNote.setText(String.format(getString(R.string.text_note_you_can_add_maximum_users), preferenceHelper.getMaxSplitUser()));
            } else {
                llSearch.setVisibility(View.VISIBLE);
                tvNote.setText(getString(R.string.text_note_search_user_by_email_or_phone_number_exclude_country_code));
            }
        }

        if (userAdapter == null) {
            rcvUser.setLayoutManager(new LinearLayoutManager(this));
            userAdapter = new UserAdapter(this, userList) {
                @Override
                public void onButtonClick(int position) {
                    if (userList.get(position).getStatus() >= Const.SplitPaymentStatus.WAITING) {
                        removeSplitPaymentRequest(userList.get(position).getUserId());
                    } else {
                        sendSplitPaymentRequest(userList.get(position).getId());
                    }
                }

                @Override
                public void onStatusClick(int position) {
                    if (userList.get(position).getStatus() == Const.SplitPaymentStatus.REJECTED) {
                        sendSplitPaymentRequest(userList.get(position).getUserId());
                    }
                }
            };
            rcvUser.setAdapter(userAdapter);
        } else {
            userAdapter.notifyDataSetChanged();
        }

        if (userList.isEmpty()) {
            tvNoUser.setVisibility(View.VISIBLE);
        } else {
            tvNoUser.setVisibility(View.GONE);
        }
    }

    /**
     * Register trip status socket.
     */
    private void registerTripStatusSocket() {
        SocketHelper socketHelper = SocketHelper.getInstance();
        if (socketHelper != null && !TextUtils.isEmpty(preferenceHelper.getTripId())) {
            String tripId = String.format("'%s'", preferenceHelper.getTripId());
            socketHelper.getSocket().off(tripId);
            socketHelper.getSocket().on(tripId, args -> runOnUiThread(() -> getTripStatus(false)));
        }
    }

    private void getTripStatus(boolean isShowLoading) {
        if (isShowLoading) {
            Utils.showCustomProgressDialog(this, "", false, null);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            Call<TripResponse> call = ApiClient.getClient().create(ApiInterface.class).getTripStatus(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<TripResponse>() {
                @Override
                public void onResponse(@NonNull Call<TripResponse> call, @NonNull Response<TripResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        TripResponse tripResponse = response.body();
                        if (tripResponse != null && parseContent.parseTripStatus(tripResponse)) {
                            initRcvUser(null);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TripResponse> call, @NonNull Throwable t) {
                    Utils.hideCustomProgressDialog();
                    AppLog.handleThrowable(TAG, t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
        registerTripStatusSocket();
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
}