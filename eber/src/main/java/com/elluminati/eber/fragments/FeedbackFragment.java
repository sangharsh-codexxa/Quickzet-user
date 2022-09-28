package com.elluminati.eber.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.elluminati.eber.PaymentActivity;
import com.elluminati.eber.PaystackPaymentActivity;
import com.elluminati.eber.R;
import com.elluminati.eber.components.DialogVerifyPayment;
import com.elluminati.eber.components.MyFontButton;
import com.elluminati.eber.components.MyFontEdittextView;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.responsemodels.CardsResponse;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.GlideApp;
import com.elluminati.eber.utils.Utils;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by elluminati on 04-07-2016.
 */
public class FeedbackFragment extends BaseFragments {


    private MyFontTextView tvTripDistance, tvTripTime, tvDriverFullName, tvGiveTipYourDriver;
    private MyFontEdittextView etComment;
    private ImageView ivDriverImage;
    private RatingBar ratingBar;
    private MyFontButton btnFeedBackDone,btnFeedBackCancel;
    private ImageView ivFavourites;
    private LinearLayout llFavourite, llTip;
    private TextView tvFive, tvTen, tvFifteen, tvTwenty, tvTwentyFive;
    private EditText etTip;
    private NumberFormat currencyFormat;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View feedFrag = inflater.inflate(R.layout.fragment_feedback, container, false);

        tvTripDistance = feedFrag.findViewById(R.id.tvTripDistance);
        tvTripTime = feedFrag.findViewById(R.id.tvTripTime);
        tvGiveTipYourDriver = feedFrag.findViewById(R.id.tvGiveTipYourDriver);
        ivDriverImage = feedFrag.findViewById(R.id.ivDriverImage);
        etComment = feedFrag.findViewById(R.id.etComment);
        ratingBar = feedFrag.findViewById(R.id.ratingBar);
        tvDriverFullName = feedFrag.findViewById(R.id.tvDriverFullName);
        btnFeedBackDone = feedFrag.findViewById(R.id.btnFeedBackDone);
        btnFeedBackCancel = feedFrag.findViewById(R.id.btnFeedBackCancel);

        ivFavourites = feedFrag.findViewById(R.id.ivFavourites);
        llFavourite = feedFrag.findViewById(R.id.llFavourite);
        llTip = feedFrag.findViewById(R.id.llTip);
        tvFive = feedFrag.findViewById(R.id.tvFive);
        tvTen = feedFrag.findViewById(R.id.tvTen);
        tvFifteen = feedFrag.findViewById(R.id.tvFifteen);
        tvTwenty = feedFrag.findViewById(R.id.tvTwenty);
        tvTwentyFive = feedFrag.findViewById(R.id.tvTwentyFive);
        etTip = feedFrag.findViewById(R.id.etTip);
        return feedFrag;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currencyFormat = drawerActivity.currencyHelper.getCurrencyFormat(CurrentTrip.getInstance().getCurrencyCode());
        drawerActivity.setToolbarBackgroundWithElevation(true, R.drawable.toolbar_bg_rounded, R.dimen.toolbar_elevation);
        drawerActivity.closedTripDialog();
        drawerActivity.preferenceHelper.putIsShowInvoice(true);
        drawerActivity.setTitleOnToolbar(drawerActivity.getResources().getString(R.string.text_feed_back));
        tvTripTime.setText(drawerActivity.parseContent.twoDigitDecimalFormat.format(CurrentTrip.getInstance().getTime()) + " " + drawerActivity.getResources().getString(R.string.text_unit_mins));
        tvTripDistance.setText(drawerActivity.parseContent.twoDigitDecimalFormat.format(CurrentTrip.getInstance().getDistance()) + " " + Utils.showUnit(drawerActivity, CurrentTrip.getInstance().getUnit()));
        btnFeedBackDone.setOnClickListener(this);
        btnFeedBackCancel.setOnClickListener(this);

        GlideApp.with(drawerActivity).load(drawerActivity.currentTrip.getProviderProfileImage()).override(200, 200).placeholder(R.drawable.ellipse).fallback(R.drawable.ellipse).dontAnimate().into(ivDriverImage);
        tvDriverFullName.setText(drawerActivity.currentTrip.getProviderFirstName() + " " + drawerActivity.currentTrip.getProviderLastName());

        updateUiFavourite();
        llFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentTrip.getInstance().isFavouriteProvider()) {
                    removeFavouriteDriver(CurrentTrip.getInstance().getProviderId());
                } else {
                    addFavouriteDriver(CurrentTrip.getInstance().getProviderId());
                }

            }
        });
        loadTipData();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.REQUEST_PAYU) {
            if (resultCode == Activity.RESULT_OK) {
                giveFeedBack();
            } else {
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
                        payTipPayment();
                    } else if (status == PaymentIntent.Status.Canceled) {
                        Utils.hideCustomProgressDialog();
                        Utils.showToast(getString(R.string.error_payment_cancel), drawerActivity);
                    } else if (status == PaymentIntent.Status.Processing) {
                        Utils.hideCustomProgressDialog();
                        Utils.showToast(getString(R.string.error_payment_processing), drawerActivity);
                    } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                        Utils.hideCustomProgressDialog();
                        Utils.showToast(getString(R.string.error_payment_auth), drawerActivity);
                    }

                }

                @Override
                public void onError(@NonNull Exception e) {
                    Utils.hideCustomProgressDialog();
                    Utils.showToast(e.getMessage(), drawerActivity);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFeedBackDone:
                if (ratingBar.getRating() != 0) {
                    if (validTipAmount(etTip.getText().toString())) {
                        createStripePaymentIntent(Double.parseDouble(etTip.getText().toString()));
                    } else {
                        giveFeedBack();
                    }
                } else {
                    Utils.showToast(drawerActivity.getResources().getString(R.string.msg_give_ratting), drawerActivity);
                }
                break;

            case R.id.btnFeedBackCancel:
                drawerActivity.currentTrip.setIsProviderAccepted(Const.ProviderStatus.PROVIDER_STATUS_RESPONSE_PENDING);
                if (isAdded()) {
                    drawerActivity.goToMapFragment();
                    drawerActivity.currentTrip.clear();
                }
                break;

            default:
                //do with default
                break;
        }
        onClickTipAmount(v);
    }

    /**
     * This method is ued to give feedback or ratting of particular driver
     */
    private void giveFeedBack() {
        Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getResources().getString(R.string.msg_waiting_for_ratting), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            jsonObject.put(Const.Params.REVIEW, etComment.getText().toString());
            jsonObject.put(Const.Params.RATING, ratingBar.getRating());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).giveRating(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {

                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            drawerActivity.currentTrip.setIsProviderAccepted(Const.ProviderStatus.PROVIDER_STATUS_RESPONSE_PENDING);
                            if (isAdded()) {
                                drawerActivity.goToMapFragment();
                                drawerActivity.currentTrip.clear();
                            }
                            Utils.hideCustomProgressDialog();
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                            Utils.hideCustomProgressDialog();
                        }
                    }

                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(FeedbackFragment.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.FEEDBACK_FRAGMENT, e);
        }

    }

    private void payTipPayment() {
        Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getResources().getString(R.string.msg_waiting_for_ratting), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).payTipPayment(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {

                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            Utils.showMessageToast(response.body().getMessage(),drawerActivity);
                            giveFeedBack();
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                            Utils.hideCustomProgressDialog();
                        }
                    }

                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(FeedbackFragment.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.FEEDBACK_FRAGMENT, e);
        }

    }

    private void addFavouriteDriver(String providerId) {
        Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getResources().getString(R.string.msg_waiting_for_ratting), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.PROVIDER_ID, providerId);

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).addFavouriteDriver(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            CurrentTrip.getInstance().setFavouriteProvider(true);
                            updateUiFavourite();
                            Utils.showMessageToast(response.body().getMessage(), drawerActivity);
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);

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

    private void removeFavouriteDriver(String providerId) {
        Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getResources().getString(R.string.msg_waiting_for_ratting), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.PROVIDER_ID, providerId);

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).removeFavouriteDriver(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            CurrentTrip.getInstance().setFavouriteProvider(false);
                            updateUiFavourite();
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);

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

    private void updateUiFavourite() {
        if (CurrentTrip.getInstance().isFavouriteProvider()) {
            ivFavourites.setImageDrawable(AppCompatResources.getDrawable(drawerActivity, R.drawable.ic_favorite_fill));
        } else {
            ivFavourites.setImageDrawable(AppCompatResources.getDrawable(drawerActivity, R.drawable.ic_favorite_strock));
        }

    }

    private void loadTipData() {
        if (CurrentTrip.getInstance().isTip() && CurrentTrip.getInstance().getPaymentMode() != Const.CASH) {
            tvFive.setText(String.format(Locale.ENGLISH, "%s%d", currencyFormat.getCurrency().getSymbol(), 5));
            tvFive.setTag(5);
            tvTen.setText(String.format(Locale.getDefault(), "%s%d", currencyFormat.getCurrency().getSymbol(), 10));
            tvTen.setTag(10);
            tvFifteen.setText(String.format(Locale.getDefault(), "%s%d", currencyFormat.getCurrency().getSymbol(), 15));
            tvFifteen.setTag(15);
            tvTwenty.setText(String.format(Locale.getDefault(), "%s%d", currencyFormat.getCurrency().getSymbol(), 20));
            tvTwenty.setTag(20);
            tvTwentyFive.setText(String.format(Locale.getDefault(), "%s%d", currencyFormat.getCurrency().getSymbol(), 25));
            tvTwentyFive.setTag(25);
            tvFive.setOnClickListener(this);
            tvTen.setOnClickListener(this);
            tvFifteen.setOnClickListener(this);
            tvTwenty.setOnClickListener(this);
            tvTwentyFive.setOnClickListener(this);
            llTip.setVisibility(View.VISIBLE);
            etTip.setVisibility(View.VISIBLE);
        } else {
            tvGiveTipYourDriver.setVisibility(View.GONE);
            llTip.setVisibility(View.GONE);
            etTip.setVisibility(View.GONE);
        }
    }

    private void onClickTipAmount(View view) {
        if (view.getTag() != null && (int) view.getTag() != 0 && CurrentTrip.getInstance().isTip()) {
            etTip.setText(String.valueOf(view.getTag()));
        }
    }

    private boolean validTipAmount(String amount) {
        try {
            if (Double.parseDouble(etTip.getText().toString()) > 0) {
                return true;
            }
        } catch (NumberFormatException ignored) {

        }
        return false;
    }

    private void createStripePaymentIntent(double amount) {
        Utils.showCustomProgressDialog(drawerActivity, "", false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TYPE, Const.USER_UNIQUE_NUMBER);
            jsonObject.put(Const.Params.AMOUNT, amount);
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.IS_PAYMENT_FOR_TIP, true);
            Call<CardsResponse> call = ApiClient.getClient().create(ApiInterface.class).getStripPaymentIntent(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<CardsResponse>() {
                @Override
                public void onResponse(Call<CardsResponse> call, Response<CardsResponse> response) {
                    if (drawerActivity.parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            if (!TextUtils.isEmpty(response.body().getHtmlForm())) {
                                Intent intent = new Intent(drawerActivity, PaystackPaymentActivity.class);
                                intent.putExtra(Const.Params.PAYU_HTML, response.body().getHtmlForm());
                                startActivityForResult(intent, Const.REQUEST_PAYU);
                            } else if (TextUtils.isEmpty(response.body().getPaymentMethod())) {
                                giveFeedBack();
                            } else if (drawerActivity.stripe != null) {
                                ConfirmPaymentIntentParams paymentIntentParams = ConfirmPaymentIntentParams.createWithPaymentMethodId(response.body().getPaymentMethod(), response.body().getClientSecret());
                                drawerActivity.stripe.confirmPayment(FeedbackFragment.this, paymentIntentParams);
                            } else {
                                giveFeedBack();
                            }
                        } else {
                            Utils.hideCustomProgressDialog();
                            if (TextUtils.isEmpty(response.body().getRequiredParam())) {
                                Utils.hideCustomProgressDialog();
                                if (TextUtils.isEmpty(response.body().getError())) {
                                    Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                                } else {
                                    Utils.showToast(response.body().getError(), drawerActivity);
                                }
                            } else {
                                openVerificationDialog(response.body().getRequiredParam(), response.body().getReference());
                            }
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
                        giveFeedBack();
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
