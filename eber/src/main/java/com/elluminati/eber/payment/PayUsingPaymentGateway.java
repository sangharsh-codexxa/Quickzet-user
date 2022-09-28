package com.elluminati.eber.payment;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.elluminati.eber.PaymentActivity;
import com.elluminati.eber.PaystackPaymentActivity;
import com.elluminati.eber.R;
import com.elluminati.eber.components.CustomDialogPaymentFailed;
import com.elluminati.eber.components.DialogVerifyPayment;
import com.elluminati.eber.fragments.FeedbackFragment;
import com.elluminati.eber.models.responsemodels.CardsResponse;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.Utils;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayUsingPaymentGateway {

    public static final String TAG = PayUsingPaymentGateway.class.getSimpleName();

    private final AppCompatActivity activity;
    private final Stripe stripe;
    private final String tripId;
    private final String userId;
    private final String token;
    private final boolean isCashAvailable;
    private final ParseContent parseContent;

    private final PaymentSuccessListener listener;

    private CustomDialogPaymentFailed dialogPendingPayment;

    public PayUsingPaymentGateway(AppCompatActivity activity, Stripe stripe, String tripId, String userId, String token,
                                  boolean isCashAvailable, PaymentSuccessListener listener) {
        this.activity = activity;
        this.stripe = stripe;
        this.tripId = tripId;
        this.userId = userId;
        this.token = token;
        this.isCashAvailable = isCashAvailable;
        this.parseContent = ParseContent.getInstance();
        this.listener = listener;
    }

    public void createStripePaymentIntent() {
        Utils.showCustomProgressDialog(activity, "", false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TYPE, Const.USER_UNIQUE_NUMBER);
            jsonObject.put(Const.Params.TRIP_ID, tripId);
            jsonObject.put(Const.Params.USER_ID, userId);
            jsonObject.put(Const.Params.TOKEN, token);
            Call<CardsResponse> call = ApiClient.getClient().create(ApiInterface.class).getStripPaymentIntent(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<CardsResponse>() {
                @Override
                public void onResponse(@NonNull Call<CardsResponse> call, @NonNull Response<CardsResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body() != null && response.body().isSuccess()) {
                            if (TextUtils.isEmpty(response.body().getError())) {
                                if (!TextUtils.isEmpty(response.body().getHtmlForm())) {
                                    Utils.hideCustomProgressDialog();
                                    Intent intent = new Intent(activity, PaystackPaymentActivity.class);
                                    intent.putExtra(Const.Params.PAYU_HTML, response.body().getHtmlForm());
                                    activity.startActivityForResult(intent, Const.REQUEST_PAYU);
                                } else if (response.body().getMessage() != null
                                        && response.body().getMessage().equals(Const.MESSAGE_CODE_PAID_FROM_WALLET)) {
                                    if (listener != null) {
                                        listener.onPaymentSuccess();
                                    }
                                    Utils.showMessageToast(response.body().getMessage(), activity);
                                    Utils.hideCustomProgressDialog();
                                    if (listener != null) {
                                        listener.onPaymentSuccess();
                                    }
                                } else if (TextUtils.isEmpty(response.body().getPaymentMethod())) {
                                    Utils.hideCustomProgressDialog();
                                    openPendingPaymentDialog(true);
                                } else {
                                    if (stripe != null) {
                                        ConfirmPaymentIntentParams paymentIntentParams = ConfirmPaymentIntentParams.createWithPaymentMethodId(response.body().getPaymentMethod(), response.body().getClientSecret());
                                        stripe.confirmPayment(activity, paymentIntentParams);
                                    } else {
                                        Utils.showToast(activity.getString(R.string.msg_stripe_not_added), activity);
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
                            Utils.showToast(response.body().getError(), activity);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CardsResponse> call, @NonNull Throwable t) {
                    Utils.hideCustomProgressDialog();
                    AppLog.handleThrowable(TAG, t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.VIEW_AND_ADD_PAYMENT_ACTIVITY, e);
        }
    }

    private void openVerificationDialog(String requiredParam, String reference) {
        DialogVerifyPayment verifyDialog = new DialogVerifyPayment(activity, requiredParam, reference) {

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
        map.put(Const.Params.USER_ID, userId);
        map.put(Const.Params.TOKEN, token);
        map.put(Const.Params.TYPE, Const.USER_UNIQUE_NUMBER);
        map.put(Const.Params.PAYMENT_GATEWAY_TYPE, Const.PaymentMethod.PAY_STACK);
        map.put(Const.Params.TRIP_ID, tripId);

        Utils.showCustomProgressDialog(activity, "", false, null);
        Call<CardsResponse> call = ApiClient.getClient().create(ApiInterface.class).sendPayStackRequiredDetails(map);
        call.enqueue(new Callback<CardsResponse>() {
            @Override
            public void onResponse(@NonNull Call<CardsResponse> call, @NonNull Response<CardsResponse> response) {
                if (ParseContent.getInstance().isSuccessful(response)) {
                    if (response.body() != null && response.body().isSuccess()) {
                        Utils.hideCustomProgressDialog();
                        if (listener != null) {
                            listener.onPaymentSuccess();
                        }
                    } else {
                        Utils.hideCustomProgressDialog();
                        if (TextUtils.isEmpty(response.body().getRequiredParam())) {
                            if (!TextUtils.isEmpty(response.body().getError())) {
                                Utils.showToast(response.body().getError(), activity);
                            } else if (!TextUtils.isEmpty(response.body().getErrorMessage())) {
                                Utils.showToast(response.body().getErrorMessage(), activity);
                            } else {
                                Utils.showErrorToast(response.body().getErrorCode(), activity);
                            }
                            openPendingPaymentDialog(true);
                        } else {
                            openVerificationDialog(response.body().getRequiredParam(), response.body().getReference());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CardsResponse> call, @NonNull Throwable t) {
                AppLog.handleThrowable(FeedbackFragment.class.getSimpleName(), t);
                Utils.hideCustomProgressDialog();
            }
        });
    }

    /**
     * If at end of trip payment was not done due to incorrect card then this dialog is open.
     */
    public void openPendingPaymentDialog(boolean isShowAddCard) {
        if (dialogPendingPayment != null && dialogPendingPayment.isShowing()) {
            return;
        }

        dialogPendingPayment = new CustomDialogPaymentFailed(activity, activity.getString(R.string.text_payment_failed), isShowAddCard ? activity.getString(R.string.msg_payment_failed) : activity.getString(R.string.msg_payment_failed_for_payu), activity.getString(R.string.text_pay_agin), isShowAddCard ? activity.getString(R.string.text_add_new_card) : null, isCashAvailable ? activity.getString(R.string.text_pay_by_cash) : activity.getString(R.string.text_pay_by_wallet)) {
            @Override
            public void positiveButton() {
                closePendingPaymentDialog();
                createStripePaymentIntent();
            }

            @Override
            public void negativeButton() {
                closePendingPaymentDialog();
                Intent paymentIntent = new Intent(activity, PaymentActivity.class);
                paymentIntent.putExtra(Const.IS_FROM_INVOICE, true);
                activity.startActivityForResult(paymentIntent, Const.PENDING_PAYMENT);
            }

            @Override
            public void payByOtherPaymentButton() {
                closePendingPaymentDialog();
                payByOtherPaymentMode();
            }
        };

        if (!activity.isFinishing()) {
            dialogPendingPayment.show();
        }
    }

    public void closePendingPaymentDialog() {
        if (dialogPendingPayment != null && dialogPendingPayment.isShowing() && !activity.isFinishing()) {
            dialogPendingPayment.dismiss();
            dialogPendingPayment = null;
        }
    }

    public void payStripPaymentIntentPayment() {
        Utils.showCustomProgressDialog(activity, "", false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TYPE, Const.USER_UNIQUE_NUMBER);
            jsonObject.put(Const.Params.TRIP_ID, tripId);
            jsonObject.put(Const.Params.USER_ID, userId);
            jsonObject.put(Const.Params.TOKEN, token);
            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).getPayStripPaymentIntentPayment(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(@NonNull Call<IsSuccessResponse> call, @NonNull Response<IsSuccessResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        Utils.hideCustomProgressDialog();
                        if (response.body().isSuccess()) {
                            if (listener != null) {
                                listener.onPaymentSuccess();
                            }
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), activity);
                        }
                    }
                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    Utils.hideCustomProgressDialog();
                    AppLog.handleThrowable(TAG, t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.VIEW_AND_ADD_PAYMENT_ACTIVITY, e);
        }
    }

    public void payByOtherPaymentMode() {
        Utils.showCustomProgressDialog(activity, "", false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TYPE, Const.USER_UNIQUE_NUMBER);
            jsonObject.put(Const.Params.TRIP_ID, tripId);
            jsonObject.put(Const.Params.USER_ID, userId);
            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).payByOtherPaymentMode(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (parseContent.isSuccessful(response)) {
                        if (listener != null) {
                            listener.onPaymentSuccess();
                        }
                        Utils.showMessageToast(response.body().getMessage(), activity);
                    } else {
                        Utils.showErrorToast(response.body().getErrorCode(), activity);
                    }
                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    Utils.hideCustomProgressDialog();
                    AppLog.handleThrowable(TAG, t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.VIEW_AND_ADD_PAYMENT_ACTIVITY, e);
        }
    }

    public interface PaymentSuccessListener {
        void onPaymentSuccess();
    }
}