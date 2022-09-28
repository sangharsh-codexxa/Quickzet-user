package com.elluminati.eber.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.MainDrawerActivity;
import com.elluminati.eber.PaymentActivity;
import com.elluminati.eber.PaystackPaymentActivity;
import com.elluminati.eber.R;
import com.elluminati.eber.adapter.CircularProgressViewAdapter;
import com.elluminati.eber.adapter.InvoiceAdapter;
import com.elluminati.eber.components.CustomCircularProgressView;
import com.elluminati.eber.components.CustomDialogPaymentFailed;
import com.elluminati.eber.components.DialogVerifyPayment;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.components.MyFontTextViewMedium;
import com.elluminati.eber.models.datamodels.CityType;
import com.elluminati.eber.models.datamodels.Trip;
import com.elluminati.eber.models.datamodels.TripDetailOnSocket;
import com.elluminati.eber.models.responsemodels.CardsResponse;
import com.elluminati.eber.models.responsemodels.InvoiceResponse;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.SocketHelper;
import com.elluminati.eber.utils.Utils;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by elluminati on 26-07-2016.
 */
public class InvoiceFragment extends BaseFragments implements MainDrawerActivity.TripSocketListener {

    private TextView tvPaymentWith, tvInvoiceNumber, tvInvoiceDistance, tvInvoiceTripTime,
            tvInvoiceTotal, tvTotalText, tvInvoicePayInfo;
    private String unit;
    private ImageView ivPaymentImage;
    private MyFontTextViewMedium tvInvoiceTripType;
    private MyFontTextView tvInvoiceMinFareApplied;
    private CustomDialogPaymentFailed dialogPendingPayment;
    private Dialog dialogProgress;
    private CustomCircularProgressView ivProgressBar;
    private RecyclerView rcvInvoice;
    private NumberFormat currencyFormat;
    private Dialog paymentProcess;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawerActivity.setTripSocketListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getUserInvoice();
        View invoiceFrag = inflater.inflate(R.layout.fragment_invoice, container, false);
        ivPaymentImage = invoiceFrag.findViewById(R.id.ivPaymentImage);
        tvPaymentWith = invoiceFrag.findViewById(R.id.tvPaymentWith);
        tvInvoiceNumber = invoiceFrag.findViewById(R.id.tvInvoiceNumber);

        tvInvoiceTripType = invoiceFrag.findViewById(R.id.tvInvoiceTripType);
        tvInvoiceMinFareApplied = invoiceFrag.findViewById(R.id.tvInvoiceMinFareApplied);
        tvInvoiceDistance = invoiceFrag.findViewById(R.id.tvInvoiceDistance);
        tvInvoiceTripTime = invoiceFrag.findViewById(R.id.tvInvoiceTripTime);
        tvInvoiceTotal = invoiceFrag.findViewById(R.id.tvInvoiceTotal);
        tvTotalText = invoiceFrag.findViewById(R.id.tvTotalText);
        tvInvoicePayInfo = invoiceFrag.findViewById(R.id.tvInvoicePayInfo);
        rcvInvoice = invoiceFrag.findViewById(R.id.rcvInvoice);
        return invoiceFrag;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currencyFormat = drawerActivity.currencyHelper.getCurrencyFormat(CurrentTrip.getInstance().getCurrencyCode());
        tvTotalText.setVisibility(View.GONE);
        tvInvoiceTotal.setVisibility(View.GONE);
        tvInvoicePayInfo.setVisibility(View.GONE);
        drawerActivity.setToolbarBackgroundWithElevation(false, R.color.color_white, 0);
        drawerActivity.closedTripDialog();
        drawerActivity.setTitleOnToolbar(drawerActivity.getResources().getString(R.string.text_invoice));
        drawerActivity.setToolbarRightSideIcon(AppCompatResources.getDrawable(drawerActivity, R.drawable.ic_done_black_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSubmitInvoice();
            }
        });
        drawerActivity.btnToolbar.setBackground(ResourcesCompat.getDrawable(drawerActivity.getResources(), R.drawable.selector_round_rect_shape_blue, null));
        rcvInvoice.setLayoutManager(new LinearLayoutManager(drawerActivity));
        rcvInvoice.setNestedScrollingEnabled(false);

    }


    @Override
    public void onStart() {
        super.onStart();
        SocketHelper.getInstance().socketConnect();
    }

    private void setInvoiceData(Trip trip, CityType tripService) {
        unit = Utils.showUnit(drawerActivity, trip.getUnit());
        drawerActivity.currentTrip.setProviderFirstName(trip.getProviderFirstName());
        drawerActivity.currentTrip.setProviderLastName(trip.getProviderLastName());
        drawerActivity.currentTrip.setFavouriteProvider(trip.isFavouriteProvider());
        if (trip.getIsMinFareUsed() == Const.TRUE && trip.getTripType() == Const.TripType.NORMAL) {
            tvInvoiceMinFareApplied.setVisibility(View.VISIBLE);
            tvInvoiceMinFareApplied.setText(drawerActivity.getResources().getString(R.string.start_min_fare) + " " + currencyFormat.format(tripService.getMinFare()) + " " + drawerActivity.getResources().getString(R.string.text_applied));
        }

        if (trip.getPaymentMode() == Const.CASH) {
            ivPaymentImage.setImageDrawable(AppCompatResources.getDrawable(drawerActivity, R.drawable.cash));
            tvPaymentWith.setText(drawerActivity.getResources().getString(R.string.text_payment_with_cash));
        } else {
            ivPaymentImage.setImageDrawable(AppCompatResources.getDrawable(drawerActivity, R.drawable.card));
            tvPaymentWith.setText(drawerActivity.getResources().getString(R.string.text_payment_with_card));
        }

        tvInvoiceNumber.setText(trip.getInvoiceNumber());

        tvInvoiceDistance.setText(ParseContent.getInstance().twoDigitDecimalFormat.format(trip.getTotalDistance()) + " " + unit);
        tvInvoiceTripTime.setText(ParseContent.getInstance().twoDigitDecimalFormat.format(trip.getTotalTime()) + " " + drawerActivity.getResources().getString(R.string.text_unit_mins));
        tvInvoiceTotal.setText(currencyFormat.format(trip.getTotal()));
        if (trip.getPaymentMode() == Const.CASH) {
            int userCount = trip.getSplitPaymentUsers() != null ? trip.getSplitPaymentUsers().size() + 1 : 1;
            double userPay = trip.getTotal() / userCount;
            tvInvoicePayInfo.setText(String.format(getString(R.string.text_you_have_to_pay_this_cash), currencyFormat.format(userPay)));
            tvInvoicePayInfo.setVisibility(View.VISIBLE);
        }
        tvInvoiceTotal.setVisibility(View.VISIBLE);
        tvTotalText.setVisibility(View.VISIBLE);
        CurrentTrip.getInstance().setTime(trip.getTotalTime());
        CurrentTrip.getInstance().setDistance(trip.getTotalDistance());
        CurrentTrip.getInstance().setUnit(trip.getUnit());
        CurrentTrip.getInstance().setTip(trip.isIsTip());
        switch (trip.getTripType()) {
            case Const.TripType.AIRPORT:
                tvInvoiceTripType.setVisibility(View.VISIBLE);
                tvInvoiceTripType.setText(drawerActivity.getResources().getString(R.string.text_airport_trip));
                break;
            case Const.TripType.ZONE:
                tvInvoiceTripType.setVisibility(View.VISIBLE);
                tvInvoiceTripType.setText(drawerActivity.getResources().getString(R.string.text_zone_trip));
                break;
            case Const.TripType.CITY:
                tvInvoiceTripType.setVisibility(View.VISIBLE);
                tvInvoiceTripType.setText(drawerActivity.getResources().getString(R.string.text_city_trip));
                break;
            default:
                //Default case here..
                if (trip.isFixedFare()) {
                    tvInvoiceTripType.setVisibility(View.VISIBLE);
                    tvInvoiceTripType.setText(drawerActivity.getResources().getString(R.string.text_fixed_price));
                } else {
                    tvInvoiceTripType.setVisibility(View.GONE);
                }
                break;
        }

        if (rcvInvoice != null) {
            rcvInvoice.setAdapter(new InvoiceAdapter(drawerActivity.parseContent.parseInvoice(drawerActivity, trip, tripService, currencyFormat)));
        }


    }

    @Override
    public void onClick(View v) {
        // do with click
    }

    /**
     * This method is used to get Invoice or Bill after Running Trip End
     */
    private void getUserInvoice() {
        Utils.hideCustomProgressDialog();
        showCustomProgressDialog(drawerActivity);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            Call<InvoiceResponse> call = ApiClient.getClient().create(ApiInterface.class).getInvoice(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<InvoiceResponse>() {
                @Override
                public void onResponse(Call<InvoiceResponse> call, Response<InvoiceResponse> response) {

                    if (ParseContent.getInstance().isSuccessful(response)) {
                        hideCustomProgressDialog();
                        if (response.body().isSuccess()) {
                            Trip trip = response.body().getTrip();
                            drawerActivity.currentTrip.setPaymentMode(trip.getPaymentMode());
                            setInvoiceData(trip, response.body().getTripService());
                            if (trip.getPaymentStatus() == Const.PaymentStatus.WAITING && trip.getPaymentMode() == Const.CARD) {
                                showPaymentProcessing(drawerActivity);
                            } else if (trip.getPaymentStatus() == Const.PaymentStatus.FAILED && trip.getPaymentMode() == Const.CARD) {
                                hidePaymentProcessing();
                                createStripePaymentIntent();
                            } else {
                                hidePaymentProcessing();
                            }

                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                        }

                    }
                }

                @Override
                public void onFailure(Call<InvoiceResponse> call, Throwable t) {
                    AppLog.handleThrowable(InvoiceFragment.class.getSimpleName(), t);
                }
            });

        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.INVOICE_FRAGMENT, e);
        }
    }

    /**
     * If at end of trip payment was not done due to incorrect card then this dialog is open.
     */
    private void openPendingPaymentDialog(boolean isShowAddCard) {
        if (dialogPendingPayment != null && dialogPendingPayment.isShowing()) {
            return;
        }

        dialogPendingPayment = new CustomDialogPaymentFailed(drawerActivity, getResources().getString(R.string.text_payment_failed), isShowAddCard ? getResources().getString(R.string.msg_payment_failed) : getResources().getString(R.string.msg_payment_failed_for_payu), drawerActivity.getResources().getString(R.string.text_pay_agin), isShowAddCard ? drawerActivity.getResources().getString(R.string.text_add_new_card) : null, drawerActivity.preferenceHelper.getPaymentCashAvailable() == Const.NOT_AVAILABLE ? drawerActivity.getString(R.string.text_pay_by_wallet) : drawerActivity.getResources().getString(R.string.text_pay_by_cash)) {
            @Override
            public void positiveButton() {
                closePendingPaymentDialog();
                createStripePaymentIntent();
            }

            @Override
            public void negativeButton() {
                closePendingPaymentDialog();
                Intent paymentIntent = new Intent(drawerActivity, PaymentActivity.class);
                paymentIntent.putExtra(Const.IS_FROM_INVOICE, true);
                startActivityForResult(paymentIntent, Const.PENDING_PAYMENT);
            }

            @Override
            public void payByOtherPaymentButton() {
                closePendingPaymentDialog();
                payByOtherPaymentMode();
            }
        };

        if (!drawerActivity.isFinishing()) {
            dialogPendingPayment.show();
        }
    }

    private void closePendingPaymentDialog() {
        if (dialogPendingPayment != null && dialogPendingPayment.isShowing() && !drawerActivity.isFinishing()) {
            dialogPendingPayment.dismiss();
            dialogPendingPayment = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.PENDING_PAYMENT) {
            openPendingPaymentDialog(true);
        } else if (requestCode == Const.REQUEST_PAYU) {
            if (resultCode != Activity.RESULT_OK) {
                openPendingPaymentDialog(false); // for payu don't need to show add card, only show retry button
                Utils.showToast(getString(R.string.error_payment_cancel), drawerActivity);
            }
        }
        if (drawerActivity.stripe != null) {
            drawerActivity.stripe.onPaymentResult(requestCode, data, new ApiResultCallback<PaymentIntentResult>() {
                @Override
                public void onSuccess(@NonNull PaymentIntentResult result) {
                    final PaymentIntent paymentIntent = result.getIntent();
                    final PaymentIntent.Status status = paymentIntent.getStatus();
                    if (status == PaymentIntent.Status.Succeeded) {
                        payStripPaymentIntentPayment();
                    } else {
                        Utils.hideCustomProgressDialog();
                        openPendingPaymentDialog(true);
                    }

                }

                @Override
                public void onError(@NonNull Exception e) {
                    Utils.hideCustomProgressDialog();
                    Utils.showToast(e.getMessage(), drawerActivity);
                    openPendingPaymentDialog(true);
                }
            });
        }
    }


    private void userSubmitInvoice() {
        Utils.showCustomProgressDialog(drawerActivity, "Submit invoice", false, null);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).submitInvoice(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {

                    if (ParseContent.getInstance().isSuccessful(response)) {
                        Utils.hideCustomProgressDialog();
                        if (response.body().isSuccess()) {
                            if (isAdded()) {
                                drawerActivity.goToFeedBackFragment();
                            }
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                        }
                    }

                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(InvoiceFragment.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.INVOICE_FRAGMENT, e);
        }
    }


    public void showCustomProgressDialog(Context context) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) context;
        if (!appCompatActivity.isFinishing()) {
            if (dialogProgress != null && dialogProgress.isShowing()) {
                return;
            }
            dialogProgress = new Dialog(context);
            dialogProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogProgress.setContentView(R.layout.circuler_progerss_bar_two);
            dialogProgress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ivProgressBar = dialogProgress.findViewById(R.id.ivProgressBarTwo);
            ivProgressBar.addListener(new CircularProgressViewAdapter() {
                @Override
                public void onProgressUpdate(float currentProgress) {
                    Log.d("CPV", "onProgressUpdate: " + currentProgress);
                }

                @Override
                public void onProgressUpdateEnd(float currentProgress) {
                    Log.d("CPV", "onProgressUpdateEnd: " + currentProgress);
                }

                @Override
                public void onAnimationReset() {
                    Log.d("CPV", "onAnimationReset");
                }

                @Override
                public void onModeChanged(boolean isIndeterminate) {
                    Log.d("CPV", "onModeChanged: " + (isIndeterminate ? "indeterminate" : "determinate"));
                }
            });
            ivProgressBar.startAnimation();
            dialogProgress.setCancelable(false);
            WindowManager.LayoutParams params = dialogProgress.getWindow().getAttributes();
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialogProgress.getWindow().setAttributes(params);
            dialogProgress.getWindow().setDimAmount(0);
            dialogProgress.show();
        }
    }

    public void hideCustomProgressDialog() {
        try {
            if (dialogProgress != null && ivProgressBar != null) {

                dialogProgress.dismiss();

            }
        } catch (Exception e) {
            AppLog.handleException(TAG, e);
        }
    }

    private void showPaymentProcessing(Context context) {
        if (paymentProcess != null && paymentProcess.isShowing()) {
            return;
        }
        paymentProcess = new Dialog(context);
        paymentProcess.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentProcess.setContentView(R.layout.payment_process);
        paymentProcess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        paymentProcess.setCancelable(false);
        WindowManager.LayoutParams params = paymentProcess.getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        paymentProcess.getWindow().setAttributes(params);
        paymentProcess.show();
    }

    private void hidePaymentProcessing() {
        if (paymentProcess != null && paymentProcess.isShowing()) {
            paymentProcess.dismiss();
        }
    }

    private void createStripePaymentIntent() {
        Utils.showCustomProgressDialog(drawerActivity, "", false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TYPE, Const.USER_UNIQUE_NUMBER);
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            Call<CardsResponse> call = ApiClient.getClient().create(ApiInterface.class).getStripPaymentIntent(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<CardsResponse>() {
                @Override
                public void onResponse(Call<CardsResponse> call, Response<CardsResponse> response) {
                    if (drawerActivity.parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            if (TextUtils.isEmpty(response.body().getError())) {
                                if (!TextUtils.isEmpty(response.body().getHtmlForm())) {
                                    Utils.hideCustomProgressDialog();
                                    Intent intent = new Intent(drawerActivity, PaystackPaymentActivity.class);
                                    intent.putExtra(Const.Params.PAYU_HTML, response.body().getHtmlForm());
                                    startActivityForResult(intent, Const.REQUEST_PAYU);
                                } else if (response.body().getMessage() != null
                                        && response.body().getMessage().equals(Const.MESSAGE_CODE_PAID_FROM_WALLET)) {
                                    closePendingPaymentDialog();
                                    Utils.showMessageToast(response.body().getMessage(), drawerActivity);
                                    Utils.hideCustomProgressDialog();
                                } else if (TextUtils.isEmpty(response.body().getPaymentMethod())) {
                                    Utils.hideCustomProgressDialog();
                                    openPendingPaymentDialog(true);
                                } else {
                                    if (drawerActivity.stripe != null) {
                                        ConfirmPaymentIntentParams paymentIntentParams = ConfirmPaymentIntentParams.createWithPaymentMethodId(response.body().getPaymentMethod(), response.body().getClientSecret());
                                        drawerActivity.stripe.confirmPayment(InvoiceFragment.this, paymentIntentParams);
                                    } else {
                                        Utils.showToast(getString(R.string.msg_stripe_not_added), requireContext());
                                    }
                                }
                            }
                        } else {
                            Utils.hideCustomProgressDialog();
                            if (TextUtils.isEmpty(response.body().getRequiredParam())) {
                                Utils.hideCustomProgressDialog();
                                openPendingPaymentDialog(true);
                            } else {
                                openVerificationDialog(response.body().getRequiredParam(), response.body().getReference());
                            }
                        }
                        if (!TextUtils.isEmpty(response.body().getError())) {
                            Utils.showToast(response.body().getError(), drawerActivity);
                        }
                    }

                }

                @Override
                public void onFailure(Call<CardsResponse> call, Throwable t) {
                    Utils.hideCustomProgressDialog();
                    AppLog.handleThrowable(PaymentActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.VIEW_AND_ADD_PAYMENT_ACTIVITY, e);
        }
    }


    private void payStripPaymentIntentPayment() {
        Utils.showCustomProgressDialog(drawerActivity, "", false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TYPE, Const.USER_UNIQUE_NUMBER);
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).getPayStripPaymentIntentPayment(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    if (drawerActivity.parseContent.isSuccessful(response)) {
                        Utils.hideCustomProgressDialog();
                        if (!response.body().isSuccess()) {
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                        }
                    }

                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    Utils.hideCustomProgressDialog();
                    AppLog.handleThrowable(PaymentActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.VIEW_AND_ADD_PAYMENT_ACTIVITY, e);
        }
    }

    private void payByOtherPaymentMode() {
        Utils.showCustomProgressDialog(drawerActivity, "", false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TYPE, Const.USER_UNIQUE_NUMBER);
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).payByOtherPaymentMode(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (drawerActivity.parseContent.isSuccessful(response)) {
                        Utils.showMessageToast(response.body().getMessage(), drawerActivity);
                    } else {
                        Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                    }
                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    Utils.hideCustomProgressDialog();
                    AppLog.handleThrowable(PaymentActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.VIEW_AND_ADD_PAYMENT_ACTIVITY, e);
        }
    }

    @Override
    public void onTripSocket(TripDetailOnSocket tripDetailOnSocket) {
        if (tripDetailOnSocket.isTripUpdated() && isVisible()) {
            getUserInvoice();
        }
    }

    private void openVerificationDialog(String requiredParam, String reference) {
        DialogVerifyPayment verifyDialog = new DialogVerifyPayment(requireContext(), requiredParam, reference) {

            @Override
            public void doWithEnable(HashMap<String, Object> map) {
                dismiss();
                sendPayStackRequiredDetails(map);
            }

            @Override
            public void doWithDisable() {
                dismiss();
            }
        };
        verifyDialog.show();
    }

    private void sendPayStackRequiredDetails(HashMap<String, Object> map) {
        map.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
        map.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
        map.put(Const.Params.TYPE, Const.USER_UNIQUE_NUMBER);
        map.put(Const.Params.PAYMENT_GATEWAY_TYPE, Const.PaymentMethod.PAY_STACK);
        map.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());

        Utils.showCustomProgressDialog(drawerActivity, "", false, null);
        Call<CardsResponse> call = ApiClient.getClient().create(ApiInterface.class).sendPayStackRequiredDetails(map);
        call.enqueue(new Callback<CardsResponse>() {
            @Override
            public void onResponse(Call<CardsResponse> call, Response<CardsResponse> response) {
                if (ParseContent.getInstance().isSuccessful(response)) {
                    if (response.body().isSuccess()) {
                        Utils.hideCustomProgressDialog();
                    } else {
                        Utils.hideCustomProgressDialog();
                        if (TextUtils.isEmpty(response.body().getRequiredParam())) {
                            if (!TextUtils.isEmpty(response.body().getError())) {
                                Utils.showToast(response.body().getError(), drawerActivity);
                            } else if (!TextUtils.isEmpty(response.body().getErrorMessage())) {
                                Utils.showToast(response.body().getErrorMessage(), drawerActivity);
                            } else {
                                Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                            }
                            openPendingPaymentDialog(true);
                        } else {
                            openVerificationDialog(response.body().getRequiredParam(), response.body().getReference());
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<CardsResponse> call, Throwable t) {
                AppLog.handleThrowable(FeedbackFragment.class.getSimpleName(), t);
                Utils.hideCustomProgressDialog();
            }
        });
    }
}
