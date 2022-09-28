package com.elluminati.eber;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.adapter.BookingAdapter;
import com.elluminati.eber.components.MyFontEdittextView;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.components.MyTitleFontTextView;
import com.elluminati.eber.interfaces.ClickListener;
import com.elluminati.eber.interfaces.RecyclerTouchListener;
import com.elluminati.eber.models.datamodels.Trip;
import com.elluminati.eber.models.responsemodels.CancelTripResponse;
import com.elluminati.eber.models.responsemodels.SchedulesTripResponse;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends BaseAppCompatActivity {

    private RecyclerView rcvBooking;
    private CardView cvOngoingBookingCardView;
    private MyFontTextView tvOngoingTrip, tvOngoingTripAddress, tvOngoingTripStatus;
    private MyTitleFontTextView tvNoItemBooking;
    private ArrayList<Trip> tripBookingList;
    private BookingAdapter bookingAdapter;
    private String cancelTripReason = "";
    private int cancelTripPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        initToolBar();
        setTitleOnToolbar(getResources().getString(R.string.text_my_booking));


        rcvBooking = findViewById(R.id.rcvBooking);
        cvOngoingBookingCardView = findViewById(R.id.cvOngoingTrip);
        cvOngoingBookingCardView.setOnClickListener(this);
        tvOngoingTrip = findViewById(R.id.tvOngoingTrip);
        tvOngoingTripAddress = findViewById(R.id.tvOngoingTripAddress);
        tvOngoingTripStatus = findViewById(R.id.tvOngoingTripStatus);
        tvOngoingTrip = findViewById(R.id.tvOngoingTrip);
        tvNoItemBooking = findViewById(R.id.tvNoItemBooking);


        tripBookingList = new ArrayList<>();
        rcvBooking.setLayoutManager(new LinearLayoutManager(this));
        getSchedulesTrip();
        upDateUiList(false);
        if (currentTrip.getIsProviderAccepted() == Const.ProviderStatus.PROVIDER_STATUS_ACCEPTED && currentTrip.getIsTripEnd() == Const.FALSE && currentTrip.getIsTripCanceled() == Const.FALSE) {
            updateUiTrip(true);
            int isProviderStatus = currentTrip.getIsProviderStatus();
            tvOngoingTripAddress.setText(currentTrip.getSrcAddress());
            setProviderStatus(isProviderStatus);
        } else {
            updateUiTrip(false);
        }

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

    private void updateUiTrip(boolean isUpdate) {
        if (isUpdate) {
            tvOngoingTrip.setVisibility(View.VISIBLE);
            cvOngoingBookingCardView.setVisibility(View.VISIBLE);
        } else {
            tvOngoingTrip.setVisibility(View.GONE);
            cvOngoingBookingCardView.setVisibility(View.GONE);
        }
    }

    private void upDateUiList(boolean isUpdate) {
        if (isUpdate) {
            rcvBooking.setVisibility(View.VISIBLE);
            tvNoItemBooking.setVisibility(View.GONE);
        } else {
            rcvBooking.setVisibility(View.GONE);
            tvNoItemBooking.setVisibility(View.VISIBLE);
        }
    }

    private void setProviderStatus(int providerStatus) {
        switch (providerStatus) {
            case Const.ProviderStatus.PROVIDER_STATUS_ACCEPTED:
                tvOngoingTripStatus.setText(getResources().getString(R.string.text_driver_accepted));
                break;
            case Const.ProviderStatus.PROVIDER_STATUS_STARTED:
                tvOngoingTripStatus.setText(getResources().getString(R.string.text_driver_started));
                break;
            case Const.ProviderStatus.PROVIDER_STATUS_ARRIVED:
                tvOngoingTripStatus.setText(getResources().getString(R.string.text_driver_arrvied));
                break;
            case Const.ProviderStatus.PROVIDER_STATUS_TRIP_STARTED:
                tvOngoingTripStatus.setText(getResources().getString(R.string.text_trip_started));
                break;
            case Const.ProviderStatus.PROVIDER_STATUS_TRIP_END:
                tvOngoingTripStatus.setText(getResources().getString(R.string.text_trip_completed));
                break;
        }
    }

    private void getSchedulesTrip() {
        Utils.showCustomProgressDialog(this, "", false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());

            Call<SchedulesTripResponse> call = ApiClient.getClient().create(ApiInterface.class).getSchedulesTrip(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<SchedulesTripResponse>() {
                @Override
                public void onResponse(Call<SchedulesTripResponse> call, Response<SchedulesTripResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        Utils.hideCustomProgressDialog();
                        if (response.body().isSuccess()) {
                            tripBookingList.addAll(response.body().getScheduledTrips());
                            bookingAdapter = new BookingAdapter(tripBookingList) {
                                @Override
                                public void onCancel(int position) {
                                    openCancelTripDialog(tripBookingList.get(position).getId());
                                }

                                @Override
                                public void onSelect(int position) {
                                    cancelTripPosition = position;
                                    Intent bookingDetailIntent = new Intent(BookingActivity.this, BookingDetailActivity.class);
                                    bookingDetailIntent.putExtra(Const.Params.TRIP_ID, tripBookingList.get(position));
                                    startActivity(bookingDetailIntent);
                                }
                            };
                            rcvBooking.setAdapter(bookingAdapter);
                            upDateUiList(true);
                        } else {
                            upDateUiList(false);
                        }
                    }

                }

                @Override
                public void onFailure(Call<SchedulesTripResponse> call, Throwable t) {
                    AppLog.handleThrowable(BookingActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.BOOKING_ACTIVITY, e);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvOngoingTrip:
                onBackPressed();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private void cancelBookingTrip(String bookingTripId, String cancelReason) {
        Utils.showCustomProgressDialog(this, "", false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TRIP_ID, bookingTripId);
            jsonObject.put(Const.Params.CANCEL_REASON, cancelReason);
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());

            Call<CancelTripResponse> call = ApiClient.getClient().create(ApiInterface.class).cancelTrip(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<CancelTripResponse>() {
                @Override
                public void onResponse(Call<CancelTripResponse> call, Response<CancelTripResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        Utils.hideCustomProgressDialog();
                        if (response.body().isSuccess()) {
                            tripBookingList.remove(cancelTripPosition);
                            bookingAdapter.notifyDataSetChanged();
                            cancelTripReason = "";
                            if (tripBookingList.isEmpty()) {
                                upDateUiList(false);
                            }
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), BookingActivity
                                    .this);
                        }
                    }

                }

                @Override
                public void onFailure(Call<CancelTripResponse> call, Throwable t) {
                    AppLog.handleThrowable(BookingActivity.class.getSimpleName(), t);

                }
            });

        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.BOOKING_ACTIVITY, e);
        }


    }

    private void openCancelTripDialog(final String tripId) {

        final Dialog cancelTripDialog = new Dialog(this);
        RadioGroup dialogRadioGroup;
        final MyFontEdittextView etOtherReason;
        final RadioButton rbReasonOne, rbReasonTwo, rbReasonThree, rbReasonOther;
        cancelTripDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelTripDialog.setContentView(R.layout.dialog_cancle_trip_reason);
        etOtherReason = cancelTripDialog.findViewById(R.id.etOtherReason);
        rbReasonOne = cancelTripDialog.findViewById(R.id.rbReasonOne);
        rbReasonTwo = cancelTripDialog.findViewById(R.id.rbReasonTwo);
        rbReasonThree = cancelTripDialog.findViewById(R.id.rbReasonThree);
        rbReasonOther = cancelTripDialog.findViewById(R.id.rbReasonOther);
        dialogRadioGroup = cancelTripDialog.findViewById(R.id.dialogRadioGroup);
        cancelTripDialog.findViewById(R.id.btnIamSure).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (rbReasonOther.isChecked()) {
                    cancelTripReason = etOtherReason.getText().toString();
                }
                if (!cancelTripReason.isEmpty()) {
                    cancelBookingTrip(tripId, cancelTripReason);
                    cancelTripDialog.dismiss();
                } else {
                    Utils.showToast(getResources().getString(R.string.msg_plz_give_valid_reason), BookingActivity.this);
                }

            }
        });
        cancelTripDialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                hideKeyBord();
                cancelTripDialog.dismiss();
            }
        });

        dialogRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbReasonOne:
                        cancelTripReason = rbReasonOne.getText().toString();
                        etOtherReason.setVisibility(View.GONE);
                        break;
                    case R.id.rbReasonTwo:
                        cancelTripReason = rbReasonTwo.getText().toString();
                        etOtherReason.setVisibility(View.GONE);
                        break;
                    case R.id.rbReasonThree:
                        cancelTripReason = rbReasonThree.getText().toString();
                        etOtherReason.setVisibility(View.GONE);
                        break;
                    case R.id.rbReasonOther:
                        etOtherReason.setVisibility(View.VISIBLE);
                        break;
                }

            }
        });
        WindowManager.LayoutParams params = cancelTripDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        cancelTripDialog.getWindow().setAttributes(params);
        cancelTripDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cancelTripDialog.setCancelable(false);
        cancelTripDialog.show();
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
