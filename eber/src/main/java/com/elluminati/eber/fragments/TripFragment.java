package com.elluminati.eber.fragments;


import static android.view.View.GONE;
import static com.elluminati.eber.utils.Const.REQUEST_CHECK_SETTINGS;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Lifecycle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elluminati.eber.ChatActivity;
import com.elluminati.eber.MainDrawerActivity;
import com.elluminati.eber.PaymentActivity;
import com.elluminati.eber.PaystackPaymentActivity;
import com.elluminati.eber.R;
import com.elluminati.eber.SplitPaymentActivity;
import com.elluminati.eber.adapter.TripPaymentAdapter;
import com.elluminati.eber.components.CustomAddPaymentDialog;
import com.elluminati.eber.components.CustomAddressChooseDialog;
import com.elluminati.eber.components.CustomDialogBigLabel;
import com.elluminati.eber.components.CustomDialogNotification;
import com.elluminati.eber.components.CustomDialogVerifyAccount;
import com.elluminati.eber.components.CustomEventMapView;
import com.elluminati.eber.components.DialogVerifyPayment;
import com.elluminati.eber.components.MyFontAutocompleteView;
import com.elluminati.eber.components.MyFontButton;
import com.elluminati.eber.components.MyFontEdittextView;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.components.MyFontTextViewMedium;
import com.elluminati.eber.models.datamodels.CityDetail;
import com.elluminati.eber.models.datamodels.LegsItem;
import com.elluminati.eber.models.datamodels.Message;
import com.elluminati.eber.models.datamodels.PaymentGateway;
import com.elluminati.eber.models.datamodels.RoutesItem;
import com.elluminati.eber.models.datamodels.StepsItem;
import com.elluminati.eber.models.datamodels.TripDetailOnSocket;
import com.elluminati.eber.models.datamodels.TripStopAddresses;
import com.elluminati.eber.models.responsemodels.AllEmergencyContactsResponse;
import com.elluminati.eber.models.responsemodels.CancelTripResponse;
import com.elluminati.eber.models.responsemodels.CardsResponse;
import com.elluminati.eber.models.responsemodels.GoogleDirectionResponse;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.models.responsemodels.PromoResponse;
import com.elluminati.eber.models.responsemodels.ProviderDetailResponse;
import com.elluminati.eber.models.responsemodels.TripPathResponse;
import com.elluminati.eber.models.responsemodels.TripResponse;
import com.elluminati.eber.models.responsemodels.UpdateDestinationResponse;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.GlideApp;
import com.elluminati.eber.utils.LatLngInterpolator;
import com.elluminati.eber.utils.SocketHelper;
import com.elluminati.eber.utils.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.StripeIntent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripFragment extends BaseFragments implements OnMapReadyCallback, MainDrawerActivity.LocationReceivedListener, MainDrawerActivity.CancelTripListener, ValueEventListener, MainDrawerActivity.NetworkListener, MainDrawerActivity.TripSocketListener {

    private static Timer tripTimer;
    private MyFontTextViewMedium tvDriverName, tvLabelCarId, tvLabelPlateNo, tvWaitTimeLabel;
    private TextView tvTotalDistance, tvTotalTime, tvWaitTimeValue,tvOtpTextview;
    private ImageView ivDriverPhoto;
    private FloatingActionButton ivSelectPayment;
    private FloatingActionButton ivTripTargetLocation;
    private Button btnTripCancel;
    private TextView ivBtnPromo;
    private ImageView ivEtaShare, btnCallDriver, btnChat;
    private CustomEventMapView tripMapView;
    private GoogleMap googleMap;
    private TextView tvSelectPayment, tvTripNumber, tvTipTimer, tvSplitPayment;
    private LinearLayout btnTripCash, btnTripCard, llDriverDetail, llWaitTime, llCarAndTimeDetail, llTripNo;
    private CustomDialogNotification customDialogNotification;
    private boolean isPaymentShow;
    private LocalBroadcastManager localBroadcastManager;
    private TripStatusReceiver tripStatusReceiver;
    private String unit, cancelTripReason = "";
    private int providerStatus = Const.ProviderStatus.PROVIDER_STATUS_UN_DEFINE;
    private ArrayList<LatLng> markerList;
    private LatLng destLatLng, srcLatLng;
    private ImageButton ivBtnCard, ivBtnCash;
    private CardView otpCardView;
    private LatLng providerLatLng;
    private CustomDialogVerifyAccount promoDialog;
    private Dialog tipDialog;
    private SoundPool soundPool;
    private int tripNotificationSoundId, driverArrivedSoundId;
    private boolean loaded, plays, isCameraBearingChange = true, isWaitTimeCountDownTimerStart, isTripTimeCounter;
    private Marker destinationMarker, providerMarker, pickUpMarker;
    private FrameLayout llSelectPayment;
    private CustomDialogBigLabel sosDialog;
    private Polyline googlePathPolyline;
    private PolylineOptions currentPathPolylineOptions;
    private float cameraBearing = 0;
    private CountDownTimer countDownTimer;
    private Spinner spinnerPayment;
    private ArrayList<PaymentGateway> selectedPaymentList;
    private TripPaymentAdapter tripPaymentAdapter;
    private ImageView ivDriverCar;
    private Dialog cancelTripDialog;
    private boolean isClickable = true;
    private CustomAddressChooseDialog dialogFavAddress;
    private TextView tvPickupAddress, tvDestinationAddress, tvRatting;
    private CustomAddPaymentDialog addPaymentDialog;
    private ImageView ivHaveMessage;
    private DatabaseReference databaseReference;
    private NumberFormat currencyFormat;
    private TripResponse tripResponse;
    private IntentFilter intentFilter;
    private CustomDialogBigLabel dialogPendingPayment;
    private CityDetail cityDetail;
    private LinearLayout llTripStops;

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawerActivity.setToolbarRightSideIcon(null, null);
        initializeSoundPool();
        drawerActivity.setTripSocketListener(this);
        drawerActivity.locationHelper.setLocationSettingRequest(drawerActivity, REQUEST_CHECK_SETTINGS, o -> {
        }, () -> {
        });
    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View tripFragView = inflater.inflate(R.layout.fragment_trip, container, false);
        tvDriverName = tripFragView.findViewById(R.id.tvDriverName);
        tvTotalDistance = tripFragView.findViewById(R.id.tvCarNumber);
        tvTotalTime = tripFragView.findViewById(R.id.tvCarModal);
        btnCallDriver = tripFragView.findViewById(R.id.btnCallDriver);
        ivDriverPhoto = tripFragView.findViewById(R.id.ivDriverPhoto);
        tripMapView = tripFragView.findViewById(R.id.tripMapView);
        btnTripCard = tripFragView.findViewById(R.id.llMapCard);
        btnTripCash = tripFragView.findViewById(R.id.llMapCash);
        tvLabelPlateNo = tripFragView.findViewById(R.id.tvLabelPlateNo);
        tvLabelCarId = tripFragView.findViewById(R.id.tvLabelCarId);
        llDriverDetail = tripFragView.findViewById(R.id.llDriverDetail);
        ivBtnCard = tripFragView.findViewById(R.id.ivBtnCard);
        ivBtnCash = tripFragView.findViewById(R.id.ivBtnCash);
        ivSelectPayment = tripFragView.findViewById(R.id.ivSelectPayment);
        tvSelectPayment = tripFragView.findViewById(R.id.tvSelectPayment);
        llSelectPayment = tripFragView.findViewById(R.id.llSelectPayment);
        ivBtnPromo = tripFragView.findViewById(R.id.ivBtnPromo);
        ivTripTargetLocation = tripFragView.findViewById(R.id.ivTripTargetLocation);
        tvTripNumber = tripFragView.findViewById(R.id.tvTripNumber);
        llWaitTime = tripFragView.findViewById(R.id.llWaitTime);
        llCarAndTimeDetail = tripFragView.findViewById(R.id.llCarAndTimeDetail);
        llTripNo = tripFragView.findViewById(R.id.llTripNo);
        tvWaitTimeLabel = tripFragView.findViewById(R.id.tvWaitTimeLabel);
        tvWaitTimeValue = tripFragView.findViewById(R.id.tvWaitTimeValue);
        ivEtaShare = tripFragView.findViewById(R.id.ivEtaShare);
        spinnerPayment = tripFragView.findViewById(R.id.tripPaymentSpinner);
        ivDriverCar = tripFragView.findViewById(R.id.ivDriverCar);
        btnTripCancel = tripFragView.findViewById(R.id.btnTripCancel);
        tvPickupAddress = tripFragView.findViewById(R.id.tvPickupAddress);
        tvDestinationAddress = tripFragView.findViewById(R.id.tvDestinationAddress);
        btnChat = tripFragView.findViewById(R.id.btnChat);
        ivHaveMessage = tripFragView.findViewById(R.id.ivHaveMessage);
        tvRatting = tripFragView.findViewById(R.id.tvRatting);
        llTripStops = tripFragView.findViewById(R.id.llTripStops);
        tvSplitPayment = tripFragView.findViewById(R.id.tvSplitPayment);
        tvOtpTextview  = tripFragView.findViewById(R.id.tv_otp);
        otpCardView = tripFragView.findViewById(R.id.card_otpCard);
        return tripFragView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        drawerActivity.setToolbarBackgroundWithElevation(false, R.color.color_white, 0);
        tripMapView.onCreate(savedInstanceState);
        tripMapView.getMapAsync(this);
        Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getResources().getString(R.string.msg_loading), false, null);
        llSelectPayment.setVisibility(View.VISIBLE);
        ivSelectPayment.setOnClickListener(this);
        setUpMapScreenUI();

        btnTripCancel.setText(drawerActivity.getResources().getString(R.string.text_cancel_trip));
        btnTripCancel.setOnClickListener(this);
        btnCallDriver.setOnClickListener(this);
        ivEtaShare.setOnClickListener(this);
        ivBtnCash.setOnClickListener(this);
        ivBtnCard.setOnClickListener(this);
        ivBtnPromo.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        ivTripTargetLocation.setOnClickListener(this);
        tvDestinationAddress.setOnClickListener(this);
        tvSplitPayment.setOnClickListener(this);
        markerList = new ArrayList<>();
        tvSplitPayment.setVisibility(drawerActivity.preferenceHelper.getIsSplitPayment() ? View.VISIBLE : GONE);
        initPaymentGatewaySpinner();
        upDateUiWhenFixedRateTrip(true);
        initFirebaseChat();
        initCurrentPathDraw();
        initTripStatusReceiver();
    }

    @Override
    public void onStart() {
        super.onStart();
        SocketHelper.getInstance().socketConnect();
        getTripStatus();
        drawerActivity.preferenceHelper.putIsShowInvoice(false);
        drawerActivity.closedTripDialog();
        if (databaseReference != null) {
            databaseReference.addValueEventListener(this);
        }
        drawerActivity.setNetworkListener(this);
        localBroadcastManager.registerReceiver(tripStatusReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        tripMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        tripMapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        tripMapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopWaitTimeCountDownTimer();
        localBroadcastManager.unregisterReceiver(tripStatusReceiver);
        if (databaseReference != null) {
            databaseReference.removeEventListener(this);
        }
        drawerActivity.setNetworkListener(null);
        stopTripTimeCounter();
    }

    @Override
    public void onDestroyView() {
        if (googleMap != null) {
            googleMap.clear();
        }
        if (tripMapView != null) {
            tripMapView.onDestroy();
            tripMapView = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        closedStatusNotifyDialog();
        closedTipDialog();
        super.onDestroy();
    }

    private void checkCurrentTripStatus(int tripStatus) {
        switch (tripStatus) {
            case Const.ProviderStatus.PROVIDER_STATUS_ACCEPTED:
                drawerActivity.closedTripDialog();
                tvOtpTextview.setVisibility(View.VISIBLE);
                String str = tripResponse.getTrip().getId();
                String numberOnly = str.replaceAll("[^0-9]", "");
                otpCardView.setVisibility(View.VISIBLE);
                tvOtpTextview.setText(numberOnly.substring(numberOnly.length() - 4));

                break;
            case Const.ProviderStatus.PROVIDER_STATUS_RESPONSE_PENDING:
                drawerActivity.openTripDialog(this);
                break;
            default:
                //Do somethings with default
                break;
        }
    }

    /**
     * handle provider status for trip which is call by trip status service in every 5 sec.
     * Use for open trip status dialog.
     *
     * @param status status
     */
    private void checkProviderStatus(int status) {
        switch (status) {
            case Const.ProviderStatus.PROVIDER_STATUS_ACCEPTED : providerStatus = Const.ProviderStatus.PROVIDER_STATUS_STARTED;
                String str = tripResponse.getTrip().getId();
                String numberOnly = str.replaceAll("[^0-9]", "");
                tvOtpTextview.setVisibility(View.VISIBLE);
                otpCardView.setVisibility(View.VISIBLE);
                tvOtpTextview.setText(numberOnly.substring(numberOnly.length() - 4));
                break;
            case Const.ProviderStatus.PROVIDER_STATUS_STARTED:

                if (providerStatus == status) {
                    goWithStatusStarted();
                }
                break;
            case Const.ProviderStatus.PROVIDER_STATUS_ARRIVED:
                if (providerStatus == status) {
                    goWithStatusArrived();
                }
                if (drawerActivity.currentTrip.getPriceForWaitingTime() > 0 && !isWaitTimeCountDownTimerStart && !tripResponse.getTrip().isFixedFare()) {
                    startWaitTimeCountDownTimer(drawerActivity.currentTrip.getTotalWaitTime());
                }
                break;
            case Const.ProviderStatus.PROVIDER_STATUS_TRIP_STARTED:
                stopWaitTimeCountDownTimer();
                if (providerStatus == status) {
                    goWithStatusTripStarted();
                }
                btnTripCancel.setVisibility(GONE);
                llWaitTime.setVisibility(GONE);
                tvLabelCarId.setText(getString(R.string.text_total_time));


                setTotalTime(CurrentTrip.getInstance().getTotalTime());
                setTotalDistance(CurrentTrip.getInstance().getTotalDistance());
//              startTripTimeCounter(CurrentTrip.getInstance().getTotalTime());
                closeTripCancelDialog();
                break;
            case Const.ProviderStatus.PROVIDER_STATUS_TRIP_END:
                goWithStatusTripEnd();
                break;
            default:
                //Do somethings with default
                break;
        }

    }

    private void goWithStatusStarted() {
        tvLabelPlateNo.setText(getResources().getString(R.string.text_total_distance));
        providerStatus = Const.ProviderStatus.PROVIDER_STATUS_ARRIVED;
        openStatusNotifyDialog(drawerActivity.getResources().getString(R.string.text_driver_started));
        playNotificationSound();
        closeTripCancelDialog();
    }

    private void goWithStatusArrived() {
        providerStatus = Const.ProviderStatus.PROVIDER_STATUS_TRIP_STARTED;
        openStatusNotifyDialog(drawerActivity.getResources().getString(R.string.text_driver_arrvied));
        playDriverArrivedSound();
        if (!TextUtils.isEmpty(tripResponse.getTrip().getDestinationAddress())) {
            getGoogleMapPath();
        }
        closeTripCancelDialog();
    }

    private void goWithStatusTripStarted() {
        stopWaitTimeCountDownTimer();
        stopDriverArrivedSound();
        providerStatus = Const.ProviderStatus.PROVIDER_STATUS_TRIP_END;
        btnTripCancel.setText(drawerActivity.getResources().getString(R.string.text_sos));
        openStatusNotifyDialog(drawerActivity.getResources().getString(R.string.text_trip_started));


        playNotificationSound();
        tvLabelCarId.setText(drawerActivity.getResources().getString(R.string.text_total_time));
        tvLabelPlateNo.setText(drawerActivity.getResources().getString(R.string.text_total_distance));
        setTotalDistance(0);
        getGoogleMapPath();
        closeTripCancelDialog();
    }

    private void goWithStatusTripEnd() {
        if (drawerActivity.currentTrip.getIsProviderRated() == Const.ProviderStatus.IS_PROVIDER_RATED) {
            closedStatusNotifyDialog();
            goToMapFragment();
        } else {
            closedStatusNotifyDialog();
            if (isAdded() && Lifecycle.State.RESUMED == getLifecycle().getCurrentState()) {
                drawerActivity.goToInvoiceFragment();
            }
        }
    }

    /**
     * this method is used to set marker on map according to trip ProviderStatus
     *
     * @param providerLatLng
     * @param destLatLng
     * @param srcLatLng
     */
    private void setMarkerOnLocation(final LatLng providerLatLng, LatLng srcLatLng, LatLng destLatLng, float bearing) {
        BitmapDescriptor bitmapDescriptor;
        if (googleMap != null) {
            markerList.clear();
            boolean isBound = false;
            if (providerLatLng != null) {
                if (providerMarker == null && ivDriverCar != null && ivDriverCar.getDrawable() != null) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.drawableToBitmap(ivDriverCar.getDrawable()));
                    providerMarker = googleMap.addMarker(new MarkerOptions().position(providerLatLng).icon(bitmapDescriptor));
                    providerMarker.setAnchor(0.5f, 0.5f);
                }
                animateMarkerToGB(providerMarker, providerLatLng, new LatLngInterpolator.Linear(), bearing);
                float rotation = getBearing(providerMarker.getPosition(), providerLatLng);
                if (!Float.isNaN(rotation)) {
                    updateCameraPathDraw(providerLatLng, rotation);
                }
                markerList.add(providerLatLng);
            }
            if (pickUpMarker == null) {
                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.vectorToBitmap(drawerActivity, R.drawable.user_pin));
                pickUpMarker = googleMap.addMarker(new MarkerOptions().position(srcLatLng).icon(bitmapDescriptor).anchor(0.5f, 0.5f));
                isBound = true;
            }
            if (destLatLng != null) {
                if (destinationMarker == null) {
                    setDestinationMarker(destLatLng);
                } else {
                    destinationMarker.setPosition(destLatLng);
                }
            }
            if (drawerActivity.currentTrip.getIsProviderStatus() == Const.ProviderStatus.PROVIDER_STATUS_ARRIVED || (drawerActivity.currentTrip.getIsProviderStatus() == Const.ProviderStatus.PROVIDER_STATUS_TRIP_STARTED)) {
                if (destLatLng != null) {
                    markerList.add(destLatLng);
                }
            } else {
                markerList.add(srcLatLng);
            }
            if (isBound) {
                try {
                    setLocationBounds(false, markerList);
                } catch (Exception e) {
                    AppLog.handleException(Const.Tag.TRIP_FRAGMENT, e);

                }

            } else {
                updateTripRootCamera();
            }
        }
    }

    /**
     * Use for bound map.
     *
     * @param isCameraAnim    isCameraAnim
     * @param markerArrayList markerArrayList
     */
    private void setLocationBounds(boolean isCameraAnim, ArrayList<LatLng> markerArrayList) {
        if (markerArrayList != null && !markerArrayList.isEmpty()) {
            LatLngBounds.Builder bounds = new LatLngBounds.Builder();
            int driverListSize = markerArrayList.size();
            for (int i = 0; i < driverListSize; i++) {
                bounds.include(markerArrayList.get(i));
            }
            //Change the padding as per needed

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds.build(), drawerActivity.getResources().getDimensionPixelOffset(R.dimen.map_padding));
            if (isCameraAnim) {
                googleMap.animateCamera(cu);
            } else {
                googleMap.moveCamera(cu);
            }
        }
    }

    /**
     * use for animate car position from one location to other location .
     * Which use to show moving car smoothly.
     *
     * @param marker             marker
     * @param finalPosition      finalPosition
     * @param latLngInterpolator latLngInterpolator
     * @param bearing            bearing
     */
    private void animateMarkerToGB(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator, final float bearing) {
        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(finalPosition.latitude, finalPosition.longitude);

            final float startRotation = marker.getRotation();
            final LatLngInterpolator interpolator = new LatLngInterpolator.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setAnchor(0.5f, 0.5f);
                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            valueAnimator.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBtnCash:
                if (drawerActivity.currentTrip.getPaymentMode() == Const.CARD) {
                    updateCardUi(false);
                    paymentSlicedOutAnim();
                    paymentModeChange(Const.CASH);
                } else {
                    paymentSlicedOutAnim();
                }
                break;
            case R.id.ivBtnCard:
                if (drawerActivity.currentTrip.getPaymentMode() == Const.CASH) {
                    updateCardUi(true);
                    paymentSlicedOutAnim();
                    paymentModeChange(Const.CARD);
                } else {
                    paymentSlicedOutAnim();
                }
                break;
            case R.id.btnCallDriver:
                if (!TextUtils.isEmpty(drawerActivity.currentTrip.getPhoneCountryCode()) && !TextUtils.isEmpty(drawerActivity.currentTrip.getProviderPhone())) {
                    if (drawerActivity.preferenceHelper.getTwilioCallMaskEnable()) {
                        callDriverViaTwilio();
                    } else {
                        Utils.openCallChooser(drawerActivity, drawerActivity.currentTrip.getPhoneCountryCode() + drawerActivity.currentTrip.getProviderPhone());
                    }
                } else {
                    Utils.showToast(drawerActivity.getResources().getString(R.string.text_phone_not_available), drawerActivity);
                }
                break;
            case R.id.ivSelectPayment:
                openPaymentModeDialog();
                break;
            case R.id.tvSplitPayment:
                goToSplitPaymentActivity();
                break;
            case R.id.ivBtnPromo:
                if (ivBtnPromo.getTag() != null && (boolean) ivBtnPromo.getTag()) {
                    removePromoCode();
                } else {
                    openPromoApplyDialog();
                }
                break;
            case R.id.ivTripTargetLocation:
                isCameraBearingChange = true;
                if (markerList != null) {
                    setLocationBounds(true, markerList);
                }
                break;
            case R.id.btnTripCancel:
                if (providerStatus == Const.ProviderStatus.PROVIDER_STATUS_TRIP_END) {
                    getIsEmergencyContact();
                } else {
                    openCancelTripReasonDialog();
                }
                break;
            case R.id.ivEtaShare:
                if (isClickable) {
                    clickShareETA();
                }
                break;
            case R.id.tvDestinationAddress:
                openDestinationSelectDialog();
                break;
            case R.id.btnChat:
                goToChatActivity();
                break;
            default:
                //Do somethings with default
                break;
        }
    }

    /**
     * Handle click event of payment mode.
     */
    private void clickSelectPayment() {
        if (!isPaymentShow) {
            if (tvSelectPayment.getText().toString().equals(drawerActivity.getResources().getString(R.string.text_card))) {
                btnTripCash.setVisibility(View.VISIBLE);
                final Animation cashSlide = AnimationUtils.loadAnimation(drawerActivity, R.anim.flot_slide_out_top);
                btnTripCash.startAnimation(cashSlide);
            } else {
                btnTripCard.setVisibility(View.VISIBLE);
                final Animation cardSlide = AnimationUtils.loadAnimation(drawerActivity, R.anim.flot_slide_out_top);
                btnTripCard.startAnimation(cardSlide);
            }
            isPaymentShow = true;
        } else {
            paymentSlicedOutAnim();
        }
    }

    /**
     * Handle click event of share ETA button.
     */
    private void clickShareETA() {
        isClickable = false;
        if (drawerActivity.currentTrip.getIsProviderStatus() == Const.ProviderStatus.PROVIDER_STATUS_TRIP_STARTED) {
            if (destLatLng != null) {
                getDistanceMatrix(srcLatLng, destLatLng);
            } else {
                isClickable = true;
                Utils.showToast(drawerActivity.getResources().getString(R.string.msg_with_out_destination_eta_not_share), drawerActivity);
            }
        } else {
            isClickable = true;
            Utils.showToast(drawerActivity.getResources().getString(R.string.msg_share_et_after_trip_start), drawerActivity);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setUpMap();
    }

    /**
     * This method is used to setUpMap option which help to load map as per option
     */
    private void setUpMap() {
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setCompassEnabled(false);
        this.googleMap.setPadding(0, drawerActivity.getResources().getDimensionPixelOffset(R.dimen.map_trip_padding_top), 0, drawerActivity.getResources().getDimensionPixelOffset(R.dimen.map_trip_padding_bottom));
    }

    /**
     * Use for change selected payment UI as per selected payment type.
     *
     * @param isCardSelect isCardSelect
     */
    private void updateCardUi(boolean isCardSelect) {
        if (isCardSelect) {
            ivSelectPayment.setImageDrawable(AppCompatResources.getDrawable(drawerActivity, R.drawable.card_white));
            tvSelectPayment.setText(drawerActivity.getResources().getString(R.string.text_card));
            drawerActivity.currentTrip.setPaymentMode(Const.CARD);
        } else {
            ivSelectPayment.setImageDrawable(AppCompatResources.getDrawable(drawerActivity, R.drawable.cash_white));
            tvSelectPayment.setText(drawerActivity.getResources().getString(R.string.text_cash));
            drawerActivity.currentTrip.setPaymentMode(Const.CASH);
        }
    }

    @Override
    public void onLocationReceived(Location location) {
        //Do something with location
    }

    /**
     * Use to show driver status.
     *
     * @param message message
     */
    private void openStatusNotifyDialog(String message) {
        if (customDialogNotification != null && customDialogNotification.isShowing()) {
            customDialogNotification.notifyDataSetChange(message);
        } else {
            customDialogNotification = new CustomDialogNotification(drawerActivity, message) {
                @Override
                public void doWithClose() {
                    closedStatusNotifyDialog();
                }
            };
            if (!drawerActivity.isFinishing()) {
                customDialogNotification.show();
            }
        }
    }

    private void closedStatusNotifyDialog() {
        if (customDialogNotification != null && customDialogNotification.isShowing()) {
            customDialogNotification.dismiss();
            customDialogNotification = null;
            stopSound();
        }
    }

    /**
     * Call when update destination_pin address.
     *
     * @param destination destination
     */
    private void updateDestination(LatLng destination, String destAddress) {
        Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getString(R.string.msg_waiting_for_update_destination), false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.DEST_LATITUDE, destination.latitude);
            jsonObject.put(Const.Params.DEST_LONGITUDE, destination.longitude);
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.DESTINATION_ADDRESS, destAddress);

            Call<UpdateDestinationResponse> call = ApiClient.getClient().create(ApiInterface.class).updateDestination(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<UpdateDestinationResponse>() {
                @Override
                public void onResponse(Call<UpdateDestinationResponse> call, Response<UpdateDestinationResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            Utils.showMessageToast(response.body().getMessage(), drawerActivity);
                            if (drawerActivity.currentTrip.getIsProviderStatus() == Const.ProviderStatus.PROVIDER_STATUS_TRIP_STARTED || drawerActivity.currentTrip.getIsProviderStatus() == Const.ProviderStatus.PROVIDER_STATUS_ARRIVED) {
                                getPathDrawOnMap(srcLatLng, new LatLng(response.body().getDestinationLocation().get(0), response.body().getDestinationLocation().get(1)), drawerActivity.preferenceHelper.getIsPathDraw());
                            }
                            isCameraBearingChange = true;
                            Utils.hideCustomProgressDialog();
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                            Utils.hideCustomProgressDialog();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UpdateDestinationResponse> call, Throwable t) {
                    AppLog.handleThrowable(TripFragment.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }

    }

    /**
     * Call when paymet mode is change.
     *
     * @param paymentMode paymentMode
     */
    private void paymentModeChange(final int paymentMode) {
        Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getResources().getString(R.string.msg_loading), false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.PAYMENT_TYPE, paymentMode);

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).changePaymentType(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (!response.body().isSuccess()) {
                            updateCardUi(CurrentTrip.getInstance().getPaymentMode() == Const.CARD);
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                            Utils.hideCustomProgressDialog();
                        } else {
                            Utils.showMessageToast(response.body().getMessage(), drawerActivity);
                            updateCardUi(paymentMode == Const.CARD);
                            if (drawerActivity.preferenceHelper.getPromoApplyForCard() == Const.FALSE && paymentMode == Const.CARD) {
                                removePromoCode();
                            } else if (drawerActivity.preferenceHelper.getPromoApplyForCash() == Const.FALSE && paymentMode == Const.CASH) {
                                removePromoCode();
                            }
                            Utils.hideCustomProgressDialog();
                        }
                    }
                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(TripFragment.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
    }

    /**
     * Call service when promocode enter.
     *
     * @param promoCode promoCode
     */
    private void promoCodeApply(String promoCode) {
        Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getResources().getString(R.string.msg_waiting_for_apply_promo), false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.PROMO_CODE, promoCode);
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());

            Call<PromoResponse> call = ApiClient.getClient().create(ApiInterface.class).applyPromoCode(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<PromoResponse>() {
                @Override
                public void onResponse(Call<PromoResponse> call, Response<PromoResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            CurrentTrip.getInstance().setIsPromoUsed(Const.TRUE);
                            if (promoDialog != null && promoDialog.isShowing()) {
                                promoDialog.dismiss();
                            }
                            Utils.showMessageToast(response.body().getMessage(), drawerActivity);
                            updateUiAfterPromo(true);
                            tripResponse.getTrip().setPromoId(response.body().getPromoId());
                            Utils.hideCustomProgressDialog();
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                            Utils.hideCustomProgressDialog();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PromoResponse> call, Throwable t) {
                    AppLog.handleThrowable(TripFragment.class.getSimpleName(), t);
                }
            });

        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.TRIP_FRAGMENT, e);
        }
    }

    /**
     * Open cancel trip reason dialog.
     */
    private void openCancelTripReasonDialog() {
        if (!drawerActivity.isFinishing()) {
            if (cancelTripDialog != null && cancelTripDialog.isShowing()) {
                return;
            }

            cancelTripDialog = new Dialog(drawerActivity);
            RadioGroup dialogRadioGroup;
            final MyFontEdittextView etOtherReason;
            final MyFontTextViewMedium tvCancelCharge;
            final LinearLayout llCancelReason;
            final RadioButton rbReasonOne, rbReasonTwo, rbReasonThree, rbReasonOther;
            cancelTripDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cancelTripDialog.setContentView(R.layout.dialog_cancle_trip_reason);
            llCancelReason = cancelTripDialog.findViewById(R.id.llCancelCharge);
            etOtherReason = cancelTripDialog.findViewById(R.id.etOtherReason);
            rbReasonOne = cancelTripDialog.findViewById(R.id.rbReasonOne);
            rbReasonTwo = cancelTripDialog.findViewById(R.id.rbReasonTwo);
            rbReasonThree = cancelTripDialog.findViewById(R.id.rbReasonThree);
            rbReasonOther = cancelTripDialog.findViewById(R.id.rbReasonOther);
            dialogRadioGroup = cancelTripDialog.findViewById(R.id.dialogRadioGroup);
            tvCancelCharge = cancelTripDialog.findViewById(R.id.tvCancelCharge);
            if (drawerActivity.currentTrip.getIsProviderStatus() == Const.ProviderStatus.PROVIDER_STATUS_ARRIVED) {
                tvCancelCharge.setText(currencyFormat.format(drawerActivity.currentTrip.getCancellationFee()));
                llCancelReason.setVisibility(View.VISIBLE);
            }
            cancelTripDialog.findViewById(R.id.btnIamSure).setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    if (rbReasonOther.isChecked()) {
                        cancelTripReason = etOtherReason.getText().toString();
                    }
                    if (!cancelTripReason.isEmpty()) {
                        drawerActivity.setTripSocketListener(null);
                        drawerActivity.cancelTrip(cancelTripReason, TripFragment.this);
                        cancelTripDialog.dismiss();
                    } else {
                        Utils.showToast(drawerActivity.getResources().getString(R.string.msg_plz_give_valid_reason), drawerActivity);
                    }

                }
            });
            cancelTripDialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    cancelTripReason = "";
                    closeTripCancelDialog();
                }
            });
            dialogRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.rbReasonOne:
                            cancelTripReason = rbReasonOne.getText().toString();
                            etOtherReason.setVisibility(GONE);
                            break;
                        case R.id.rbReasonTwo:
                            cancelTripReason = rbReasonTwo.getText().toString();
                            etOtherReason.setVisibility(GONE);
                            break;
                        case R.id.rbReasonThree:
                            cancelTripReason = rbReasonThree.getText().toString();
                            etOtherReason.setVisibility(GONE);
                            break;
                        case R.id.rbReasonOther:
                            etOtherReason.setVisibility(View.VISIBLE);
                            break;
                        default:
                            //Do somethings with default
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
    }

    /**
     * Use for close trip cancellation dialog....
     */
    private void closeTripCancelDialog() {
        if (cancelTripDialog != null && cancelTripDialog.isShowing()) {
            hideSoftKeyboard(drawerActivity);
            cancelTripDialog.dismiss();
            cancelTripDialog = null;
        }
    }

    private void checkPaymentModeAndPromoAvailable(ArrayList<PaymentGateway> selectedPaymentList) {
        if (drawerActivity.preferenceHelper.getPaymentCardAvailable() == Const.NOT_AVAILABLE) {
            btnTripCard.setVisibility(GONE);
            ivSelectPayment.setClickable(false);
            selectedPaymentList.clear();
            PaymentGateway paymentGateway = new PaymentGateway();
            paymentGateway.setId(Const.CASH);
            paymentGateway.setName(drawerActivity.getResources().getString(R.string.text_cash));
            selectedPaymentList.add(paymentGateway);
        } else if (drawerActivity.preferenceHelper.getPaymentCashAvailable() == Const.NOT_AVAILABLE) {
            btnTripCash.setVisibility(GONE);
            ivSelectPayment.setClickable(false);
        } else {
            PaymentGateway paymentGateway = new PaymentGateway();
            paymentGateway.setId(Const.CASH);
            paymentGateway.setName(drawerActivity.getResources().getString(R.string.text_cash));
            selectedPaymentList.add(paymentGateway);
        }
        updateCardUi(drawerActivity.currentTrip.getPaymentMode() == Const.CARD);
    }


    private void updateUiAfterPromo(boolean isPromoApplied) {
        if (isPromoApplied) {
            ivBtnPromo.setText(String.format("%s %s", drawerActivity.getResources().getString(R.string.text_promo_applied), drawerActivity.getResources().getString(R.string.text_tap_to_remove)));
            ivBtnPromo.setTag(true);
        } else {
            ivBtnPromo.setText(drawerActivity.getResources().getString(R.string.text_have_promocode));
            ivBtnPromo.setTag(false);
        }
    }

    private void paymentSlicedOutAnim() {
        if (btnTripCash.getVisibility() == View.VISIBLE) {
            final Animation cashSlide = AnimationUtils.loadAnimation(drawerActivity, R.anim.flot_slide_in_top);
            btnTripCash.startAnimation(cashSlide);

        } else {
            final Animation cardSlide = AnimationUtils.loadAnimation(drawerActivity, R.anim.flot_slide_in_top);
            btnTripCard.startAnimation(cardSlide);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnTripCard.setVisibility(GONE);
                btnTripCash.setVisibility(GONE);
            }
        }, 300);
        isPaymentShow = false;
    }

    private void openPromoApplyDialog() {
        if (!drawerActivity.isFinishing()) {

            if (promoDialog != null && promoDialog.isShowing()) {
                return;
            }
            promoDialog = new CustomDialogVerifyAccount(drawerActivity, drawerActivity.getResources().getString(R.string.text_promo_apply), drawerActivity.getResources().getString(R.string.text_apply), drawerActivity.getResources().getString(R.string.text_cancel), drawerActivity.getResources().getString(R.string.text_enter_promo_hint), true) {
                @Override
                public void doWithEnable(EditText editText) {

                    String promoCode = editText.getText().toString();
                    if (!promoCode.isEmpty()) {
                        promoCodeApply(promoCode);
                    } else {
                        Utils.showToast(drawerActivity.getResources().getString(R.string.text_enter_promo_code), drawerActivity);
                    }
                }

                @Override
                public void doWithDisable() {
                    this.dismiss();
                }

                @Override
                public void clickOnText() {
                }
            };

            promoDialog.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            promoDialog.show();
        }
    }

    private void removePromoCode() {
        if (!TextUtils.isEmpty(tripResponse.getTrip().getPromoId())) {
            Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getResources().getString(R.string.msg_waiting_for_apply_promo), false, null);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Const.Params.PROMO_ID, tripResponse.getTrip().getPromoId());
                jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
                jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
                jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());

                Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).removePromoCode(ApiClient.makeJSONRequestBody(jsonObject));
                call.enqueue(new Callback<IsSuccessResponse>() {
                    @Override
                    public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                        Utils.hideCustomProgressDialog();
                        if (ParseContent.getInstance().isSuccessful(response)) {
                            if (response.body().isSuccess()) {
                                Utils.showMessageToast(response.body().getMessage(), drawerActivity);
                                updateUiAfterPromo(false);
                            } else {
                                Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                        AppLog.handleThrowable(TripFragment.class.getSimpleName(), t);
                    }
                });

            } catch (JSONException e) {
                AppLog.handleException(Const.Tag.TRIP_FRAGMENT, e);
            }
        }
    }

    /**
     * this method is used to init sound pool for play sound file
     */
    private void initializeSoundPool() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build();
            soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build();
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    loaded = status == 0;
                }
            });
            tripNotificationSoundId = soundPool.load(drawerActivity, R.raw.bullitin, 1);
            driverArrivedSoundId = soundPool.load(drawerActivity, R.raw.driver_arrived_at_pickup, 1);

        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    loaded = status == 0;
                }
            });
            tripNotificationSoundId = soundPool.load(drawerActivity, R.raw.bullitin, 1);
            driverArrivedSoundId = soundPool.load(drawerActivity, R.raw.driver_arrived_at_pickup, 1);
        }
    }

    /**
     * Use to play sound when driver start and at start trip time.
     */
    public void playNotificationSound() {
        // Is the sound loaded does it already play?
        if (loaded && !plays && drawerActivity.preferenceHelper.getIsTripStatusSoundOn()) {
            soundPool.play(tripNotificationSoundId, 1, 1, 1, 0, 1f);
            plays = true;
        }
    }

    /**
     * Play sound when driver arrive at pickup location.
     */
    public void playDriverArrivedSound() {
        // Is the sound loaded does it already play?
        if (loaded && !plays && drawerActivity.preferenceHelper.getIsDriverArrivedSoundOn()) {
            soundPool.play(driverArrivedSoundId, 1, 1, 1, 0, 1f);
            plays = true;
        }
    }

    /**
     * Stop driver arrived sound.
     */
    public void stopDriverArrivedSound() {
        if (plays) {
            soundPool.stop(driverArrivedSoundId);
//            driverArrivedSoundId = soundPool.load(drawerActivity, R.raw.driver_arrived_at_pickup,
//                    1);
            plays = false;
        }
    }

    /**
     * Stop sound.
     */
    public void stopSound() {
        if (plays) {
            soundPool.stop(tripNotificationSoundId);
//           tripNotificationSoundId = soundPool.load(drawerActivity, R.raw.bullitin, 1);
            plays = false;
        }
    }

    private void updateCamera(final float bearing, LatLng positionLatLng) {
        if (positionLatLng != null && isCameraBearingChange && googleMap != null) {
            cameraBearing = bearing;
            CameraPosition oldPos = googleMap.getCameraPosition();

            CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).target(positionLatLng).zoom(17f).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos), 3000, new GoogleMap.CancelableCallback() {

                @Override
                public void onFinish() {
                    isCameraBearingChange = false;
                }

                @Override
                public void onCancel() {
                    isCameraBearingChange = false;
                }
            });
            isCameraBearingChange = false;
        }
    }

    private void updateCameraPathDraw(LatLng positionLatLng, float bearing) {
        if (positionLatLng != null && googleMap != null && !isCameraBearingChange) {

            CameraPosition oldPos = googleMap.getCameraPosition();

            CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).target(positionLatLng).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos), 3000, new GoogleMap.CancelableCallback() {

                @Override
                public void onFinish() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            drawCurrentPath(providerLatLng);
                        }
                    }, 3000);

                }

                @Override
                public void onCancel() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            drawCurrentPath(providerLatLng);
                        }
                    }, 3000);
                }
            });
        }
    }

    private void sendEmergencySms() {
        if (ContextCompat.checkSelfPermission(drawerActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(drawerActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(drawerActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Const.PERMISSION_FOR_LOCATION);
        } else {
            Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getResources().getString(R.string.msg_loading), false, null);
            drawerActivity.locationHelper.onStart();
            drawerActivity.locationHelper.getLastLocation(location -> {
                if (location != null) {
                    StringBuilder locationUrl = new StringBuilder();
                    locationUrl.append("https://www.google.co.in/maps/place/");
                    locationUrl.append(location.getLatitude());
                    locationUrl.append(",");
                    locationUrl.append(location.getLongitude());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
                        jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
                        jsonObject.put(Const.Params.MAP, locationUrl.toString());
                        Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).sendEmergencySMS(ApiClient.makeJSONRequestBody(jsonObject));
                        call.enqueue(new Callback<IsSuccessResponse>() {
                            @Override
                            public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                                if (ParseContent.getInstance().isSuccessful(response)) {
                                    if (response.body().isSuccess()) {
                                        Utils.hideCustomProgressDialog();
                                        Utils.showMessageToast(response.body().getMessage(), drawerActivity);
                                    } else {
                                        Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                                        Utils.hideCustomProgressDialog();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                                Utils.hideCustomProgressDialog();
                                AppLog.handleThrowable(TripFragment.class.getSimpleName(), t);
                            }
                        });
                    } catch (JSONException e) {
                        AppLog.handleException(Const.Tag.MAIN_DRAWER_ACTIVITY, e);
                    }
                } else {
                    Utils.hideCustomProgressDialog();
                    Utils.showToast(getString(R.string.text_location_not_found), drawerActivity);
                }
            });
        }
    }

    public void openSOSDialog(int seconds) {
        if (!drawerActivity.isFinishing()) {
            MyFontTextView tvDialogMessage;
            final long milliSecond = 1000;
            long millisUntilFinished = seconds * milliSecond;
            final CountDownTimer countDownTimer = new CountDownTimer(millisUntilFinished, milliSecond) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / milliSecond;
                    sosDialog.notifyDataSetChanged(String.valueOf(seconds));
                }

                @Override
                public void onFinish() {
                    sosDialog.dismiss();
                    sendEmergencySms();
                }
            };
            sosDialog = new CustomDialogBigLabel(drawerActivity, drawerActivity.getResources().getString(R.string.text_sos), "", drawerActivity.getResources().getString(R.string.text_send), drawerActivity.getResources().getString(R.string.text_cancel)) {
                @Override
                public void positiveButton() {
                    countDownTimer.cancel();
                    dismiss();
                    sendEmergencySms();
                }

                @Override
                public void negativeButton() {
                    countDownTimer.cancel();
                    dismiss();
                }
            };
            tvDialogMessage = sosDialog.findViewById(R.id.tvDialogMessage);
            tvDialogMessage.setTextSize(drawerActivity.getResources().getDimension(R.dimen.size_general));
            sosDialog.show();
            countDownTimer.start();
        }
    }

    private void initTripStatusReceiver() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Const.ACTION_TRIP_START);
        intentFilter.addAction(Const.ACTION_TRIP_END);
        intentFilter.addAction(Const.ACTION_PROVIDER_STARTED);
        intentFilter.addAction(Const.ACTION_PROVIDER_ARRIVED);
        intentFilter.addAction(Const.ACTION_WAITING_FOR_TIP);
        intentFilter.addAction(Const.ACTION_TRIP_CANCEL_BY_PROVIDER);
        intentFilter.addAction(Const.ACTION_TRIP_CANCEL_BY_ADMIN);
        tripStatusReceiver = new TripStatusReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(drawerActivity);

    }

    /**
     * Call service to get google path between two locations.
     */
    private void getGoogleMapPath() {
        if (drawerActivity.preferenceHelper.getIsPathDraw()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
                jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
                jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
                Call<TripPathResponse> call = ApiClient.getClient().create(ApiInterface.class).getTripPath(ApiClient.makeJSONRequestBody(jsonObject));
                call.enqueue(new Callback<TripPathResponse>() {
                    @Override
                    public void onResponse(Call<TripPathResponse> call, Response<TripPathResponse> response) {
                        if (ParseContent.getInstance().isSuccessful(response)) {
                            if (response.body().isSuccess()) {
                                String responsePicUpToDestination = response.body().getTriplocation().getGooglePickUpLocationToDestinationLocation();
                                if (destLatLng != null) {
                                    if (TextUtils.isEmpty(responsePicUpToDestination)) {
                                        getPathDrawOnMap(srcLatLng, destLatLng, drawerActivity.preferenceHelper.getIsPathDraw());
                                    } else {
                                        GoogleDirectionResponse googleDirectionResponse = ApiClient.getGsonInstance().fromJson(responsePicUpToDestination, GoogleDirectionResponse.class);
                                        drawPath(googleDirectionResponse);
                                    }
                                }
                                List<List<Double>> tripsLocations = response.body().getTriplocation().getStartTripToEndTripLocations();
                                int size = tripsLocations.size();
                                for (int i = 0; i < size; i++) {
                                    List<Double> locations = tripsLocations.get(i);
                                    LatLng latLng = new LatLng(locations.get(0), locations.get(1));
                                    currentPathPolylineOptions.add(latLng);
                                }

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TripPathResponse> call, Throwable t) {
                        AppLog.handleThrowable(TripFragment.class.getSimpleName(), t);
                    }
                });
            } catch (JSONException e) {
                AppLog.handleException(Const.Tag.MAIN_DRAWER_ACTIVITY, e);
            }
        }


    }

    /**
     * Use to draw path on map as per travel.
     *
     * @param driverLntLng
     */
    private void drawCurrentPath(LatLng driverLntLng) {
        if (drawerActivity.currentTrip.getIsProviderStatus() == Const.ProviderStatus.PROVIDER_STATUS_TRIP_STARTED) {
            currentPathPolylineOptions.add(driverLntLng);
            // draw path when app not in background
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                googleMap.addPolyline(currentPathPolylineOptions);
            }
        }

    }

    /**
     * Initialize polyline fro draw travel path.
     */
    private void initCurrentPathDraw() {
        currentPathPolylineOptions = new PolylineOptions();
        currentPathPolylineOptions.color(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_red_path, null));
        currentPathPolylineOptions.width(15);
    }


    /**
     * Use to get bearing from two location .
     *
     * @param begin
     * @param end
     * @return
     */
    private float getBearing(LatLng begin, LatLng end) {
        float bearing = 0;
        if (begin != null && end != null) {
            Location startLocation = new Location("start");
            startLocation.setLatitude(begin.latitude);
            startLocation.setLongitude(begin.longitude);

            Location endLocation = new Location("end");
            endLocation.setLatitude(end.latitude);
            endLocation.setLongitude(end.longitude);
            bearing = startLocation.bearingTo(endLocation);
        }


        return bearing;
    }

    private void setDestinationMarker(LatLng destinationLatLng) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.vectorToBitmap(drawerActivity, R.drawable.destination_pin));

        destinationMarker = googleMap.addMarker(new MarkerOptions().position(destinationLatLng).icon(bitmapDescriptor).anchor(0.5f, 0.5f));

    }

    /**
     * Use to start waiting timer when driver arrived at pickup location.
     *
     * @param seconds
     */
    private void startWaitTimeCountDownTimer(final int seconds) {
        if (!isWaitTimeCountDownTimerStart) {
            updateUiForWaitTime(true);
            isWaitTimeCountDownTimerStart = true;
            final long milliSecond = 1000;
            final long totalSeconds = 86400 * milliSecond;
            countDownTimer = null;
            countDownTimer = new CountDownTimer(totalSeconds, milliSecond) {

                long remain = seconds;

                public void onTick(long millisUntilFinished) {

                    remain = remain + 1;
                    if (remain > 0 && TextUtils.equals(tvWaitTimeLabel.getText().toString(), drawerActivity.getResources().getString(R.string.text_wait_time_start_after))) {
                        tvWaitTimeLabel.setText(drawerActivity.getResources().getString(R.string.text_wait_time));
                    }
                    if (remain < 0) {
                        tvWaitTimeValue.setText(String.format("%ss", remain * (-1)));
                    } else {
                        tvWaitTimeValue.setText(String.format("%ss", remain));
                    }
                }

                public void onFinish() {
                    isWaitTimeCountDownTimerStart = false;
                }

            }.start();
        }
    }

    /**
     * Use to stop waiting timer.
     */
    private void stopWaitTimeCountDownTimer() {
        if (isWaitTimeCountDownTimerStart) {

            isWaitTimeCountDownTimerStart = false;
            countDownTimer.cancel();
            updateUiForWaitTime(false);
        }
    }


    /**
     * trip timer used to count a trip time when trip is started
     *
     * @param minute
     */
    private void startTripTimeCounter(final int minute) {
        if (isAdded()) {
            if (!isTripTimeCounter) {
                isTripTimeCounter = true;
                tripTimer = null;
                tripTimer = new Timer();
                tripTimer.scheduleAtFixedRate(new TimerTask() {
                    int count = minute;

                    @Override
                    public void run() {
                        drawerActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (tvTotalTime.getTag() != null && ((int) tvTotalTime.getTag()) < count) {
                                    setTotalTime(count);
                                }
                                count++;
                            }
                        });

                    }
                }, 1000, 60000);
            }
        }
    }

    /**
     * Use to stop trip timer.
     */
    private void stopTripTimeCounter() {
        if (isTripTimeCounter) {
            isTripTimeCounter = false;
            tripTimer.cancel();
        }
    }

    /**
     * This method is use to show/hide waiting timer.Trip
     *
     * @param isUpdate
     */
    private void updateUiForWaitTime(boolean isUpdate) {
        if (isUpdate) {
//          llCarAndTimeDetail.setVisibility(View.GONE);
            llTripNo.setVisibility(GONE);
            llWaitTime.setVisibility(View.VISIBLE);
        } else {
//            llWaitTime.setVisibility(View.VISIBLE);
            llTripNo.setVisibility(View.GONE);
            llCarAndTimeDetail.setVisibility(View.VISIBLE);
        }

    }

    /**
     * use for get duration and distance between user_pin and destination_pin.
     *
     * @param srcLatLng
     * @param destLatLng
     */
    private void getDistanceMatrix(LatLng srcLatLng, LatLng destLatLng) {
        Utils.showCustomProgressDialog(drawerActivity, "", false, null);

        String origins = srcLatLng.latitude + "," + srcLatLng.longitude;
        String destination = destLatLng.latitude + "," + destLatLng.longitude;
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Const.google.ORIGINS, origins);
        hashMap.put(Const.google.DESTINATIONS, destination);
        hashMap.put(Const.google.KEY, drawerActivity.preferenceHelper.getGoogleServerKey());

        ApiInterface apiInterface = new ApiClient().changeApiBaseUrl(Const.GOOGLE_API_URL).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getGoogleDistanceMatrix(hashMap);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (ParseContent.getInstance().isSuccessful(response)) {
                    Utils.hideCustomProgressDialog();
                    HashMap<String, String> map = drawerActivity.parseContent.parsDistanceMatrix(response);
                    if (map != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(drawerActivity.getResources().getString(R.string.text_i_will_reach) + " ");
                        stringBuilder.append(map.get(Const.google.DESTINATION_ADDRESSES) + " ");
                        stringBuilder.append(drawerActivity.getResources().getString(R.string.text_with) + " ");
                        stringBuilder.append(drawerActivity.currentTrip.getProviderFirstName() + " " + drawerActivity.currentTrip.getProviderLastName() + " ");
//                        stringBuilder.append(map.get(Const.google.TEXT+" "));
                        stringBuilder.append("\n" + "https://www.google.com/maps/search/?api=1&query="+destLatLng.latitude+","+destLatLng.longitude);
                        shareEta(stringBuilder.toString());
                    }


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                AppLog.handleThrowable(MapFragment.class.getSimpleName(), t);
                Utils.hideCustomProgressDialog();
            }
        });
    }

    /**
     * Call when shre ETA button pressed.
     *
     * @param message
     */
    private void shareEta(String message) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(sharingIntent, drawerActivity.getResources().getString(R.string.text_wait_share_eta)));
        isClickable = true;
    }

    private void initPaymentGatewaySpinner() {
        selectedPaymentList = new ArrayList<>();
        tripPaymentAdapter = new TripPaymentAdapter(drawerActivity, selectedPaymentList);
        spinnerPayment.setAdapter(tripPaymentAdapter);
    }


    /**
     * Use to open tip dialog at end of trip.
     * Dialog open if tip enable from admin panel.
     */
    private void openTipDialog() {
        if (!drawerActivity.isFinishing()) {

            if (tipDialog != null && tipDialog.isShowing()) {
                return;
            }
            final MyFontEdittextView etTipAmount;
            MyFontTextView tvDialogTipTotal, tvDialogTipPromoBonusValue, tvDialogTipReferralBonusValue, tvDialogTipPayments;


            final long milliSecond = 1000;
            long millisUntilFinished = tripResponse.getTimeLeftForTip() * milliSecond;
            final CountDownTimer countDownTimerTip = new CountDownTimer(millisUntilFinished, milliSecond) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / milliSecond;
                    String second = seconds + "s";
                    if (tipDialog != null) {
                        tvTipTimer.setText(second);
                    }
                }

                @Override
                public void onFinish() {
                    if (tipDialog != null && tipDialog.isShowing()) {
                        payPayment(0);
                        tipDialog.dismiss();
                    }

                }
            };

            tipDialog = new Dialog(drawerActivity);
            tipDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            tipDialog.setContentView(R.layout.dialog_give_a_tip);
            tvDialogTipTotal = tipDialog.findViewById(R.id.tvDialogTipTotal);
            tvDialogTipPromoBonusValue = tipDialog.findViewById(R.id.tvDialogTipPromoBonusValue);
            tvDialogTipReferralBonusValue = tipDialog.findViewById(R.id.tvDialogTipReferralBonusValue);
            tvDialogTipPayments = tipDialog.findViewById(R.id.tvDialogTipPayments);
            tvTipTimer = tipDialog.findViewById(R.id.tvTipTimer);
            etTipAmount = tipDialog.findViewById(R.id.etTipAmount);

            tvDialogTipPayments.setText(currencyFormat.format(drawerActivity.currentTrip.getTotal()));
            tvDialogTipPromoBonusValue.setText(currencyFormat.format(drawerActivity.currentTrip.getPromoPayment()));
            tvDialogTipReferralBonusValue.setText(currencyFormat.format(drawerActivity.currentTrip.getReferralPayment()));

            switch (drawerActivity.currentTrip.getTripType()) {
                case Const.TripType.AIRPORT:
                case Const.TripType.CITY:
                case Const.TripType.ZONE:
                    tvDialogTipTotal.setText(currencyFormat.format(drawerActivity.currentTrip.getTripTypeAmount()));
                    break;

                default:
                    tvDialogTipTotal.setText(currencyFormat.format(drawerActivity.currentTrip.getTotalAfterSurgeFees()));
                    break;
            }

            tipDialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    countDownTimerTip.cancel();
                    payPayment(0);
                    tipDialog.dismiss();
                }
            });
            tipDialog.findViewById(R.id.btnApply).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    countDownTimerTip.cancel();
                    String tipAmount = etTipAmount.getText().toString();

                    if (!TextUtils.isEmpty(tipAmount)) {
                        try {
                            payPayment(Double.valueOf(tipAmount));
                            tipDialog.dismiss();
                        } catch (NumberFormatException e) {
                            Utils.showToast(drawerActivity.getResources().getString(R.string.text_plz_enter_amount), drawerActivity);
                        }
                    } else {
                        Utils.showToast(drawerActivity.getResources().getString(R.string.text_plz_enter_amount), drawerActivity);
                    }


                }
            });
            WindowManager.LayoutParams params = tipDialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            tipDialog.getWindow().setAttributes(params);
            tipDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            tipDialog.setCancelable(false);
            tipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    countDownTimerTip.cancel();
                }
            });
            tipDialog.show();
            countDownTimerTip.start();
        }

    }

    private void closedTipDialog() {
        if (tipDialog != null && tipDialog.isShowing()) {
            tipDialog.dismiss();
        }
    }

    /**
     * This service call from tip dialog click event.
     * And if tip is off from the admin panel then this service call from the driver side.
     *
     * @param tipAmount
     */
    private void payPayment(double tipAmount) {
        Utils.showCustomProgressDialog(drawerActivity, "", false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            jsonObject.put(Const.Params.TIP_AMOUNT, tipAmount);

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).payPayment(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        Utils.hideCustomProgressDialog();
                        if (response.body().isSuccess()) {
                            goWithStatusTripEnd();
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                        }
                    }

                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(TripFragment.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.MAIN_DRAWER_ACTIVITY, e);
        }
    }

    private void getIsEmergencyContact() {
        JSONObject jsonObject = new JSONObject();
        Utils.showCustomProgressDialog(drawerActivity, getResources().getString(R.string.msg_loading), false, null);
        try {
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());

            Call<AllEmergencyContactsResponse> call = ApiClient.getClient().create(ApiInterface.class).getEmergencyContacts(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<AllEmergencyContactsResponse>() {
                @Override
                public void onResponse(Call<AllEmergencyContactsResponse> call, Response<AllEmergencyContactsResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        Utils.hideCustomProgressDialog();
                        if (response.body().isSuccess()) {
                            drawerActivity.locationHelper.setLocationSettingRequest(drawerActivity, Const.REQUEST_CHECK_LOCATION_SETTINGS, o -> openSOSDialog(10), () -> openSOSDialog(10));
                        } else {
                            Utils.showToast(drawerActivity.getResources().getString(R.string.text_no_emergency_contact), drawerActivity);
                        }
                    }
                }

                @Override
                public void onFailure(Call<AllEmergencyContactsResponse> call, Throwable t) {
                    AppLog.handleThrowable(TripFragment.class.getSimpleName(), t);
                    Utils.hideCustomProgressDialog();
                }
            });

        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.TRIP_FRAGMENT, e);
        }
    }


    private void upDateUiWhenFixedRateTrip(boolean isFixedRate) {
        tvDestinationAddress.setClickable(!isFixedRate);
    }


    @Override
    public void onCancelTrip(CancelTripResponse response) {
        drawerActivity.closedTripDialog();
        closedStatusNotifyDialog();
        if (response.isSuccess() && response.getPaymentStatus() == Const.PaymentStatus.COMPLETED) {
            Utils.hideCustomProgressDialog();
            drawerActivity.currentTrip.setIsTripCanceled(Const.TRUE);
            Utils.showMessageToast(response.getMessage(), drawerActivity);
            drawerActivity.closedTripDialog();
            closedStatusNotifyDialog();
            goToMapFragment();
        } else {
            Utils.hideCustomProgressDialog();
            createStripePaymentIntent();
        }
        if (!TextUtils.isEmpty(response.getError())) {
            Utils.showToast(response.getError(), drawerActivity);
        }

    }

    private void getProviderDetail() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.PROVIDER_ID, CurrentTrip.getInstance().getProviderId());
            Call<ProviderDetailResponse> call = ApiClient.getClient().create(ApiInterface.class).getProviderDetail(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<ProviderDetailResponse>() {
                @Override
                public void onResponse(Call<ProviderDetailResponse> call, Response<ProviderDetailResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (drawerActivity.parseContent.parseProvider(response.body())) {
                            tvDriverName.setText(String.format("%s %s", drawerActivity.currentTrip.getProviderFirstName(), drawerActivity.currentTrip.getProviderLastName()));
                            GlideApp.with(drawerActivity).load(drawerActivity.currentTrip.getProviderProfileImage()).override(200, 200).placeholder(R.drawable.ellipse).fallback(R.drawable.ellipse).dontAnimate().into(ivDriverPhoto);
                            if (CurrentTrip.getInstance().getIsProviderStatus() != Const.ProviderStatus.PROVIDER_STATUS_TRIP_STARTED) {
                                tvTotalTime.setText(drawerActivity.currentTrip.getProviderCarModal());
                                tvTotalDistance.setText(drawerActivity.currentTrip.getProviderCarNumber());
                            }

                            tvRatting.setText(drawerActivity.currentTrip.getRating());
                            List<Double> locations = response.body().getProvider().getProviderLocation();
                            if (locations != null && !locations.isEmpty()) {
                                providerLatLng = new LatLng(locations.get(0), locations.get(1));
                            }
                            setMarkerOnLocation(providerLatLng, srcLatLng, destLatLng, (float) (0));
                        }

                    }
                }

                @Override
                public void onFailure(Call<ProviderDetailResponse> call, Throwable t) {
                    AppLog.handleThrowable(MainDrawerActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.TRIP_FRAGMENT, e);
        }
    }


    private synchronized void getTripStatus() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            Call<TripResponse> call = ApiClient.getClient().create(ApiInterface.class).getTripStatus(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<TripResponse>() {
                @Override
                public void onResponse(Call<TripResponse> call, Response<TripResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        stopSound();
                        tripResponse = response.body();
                        cityDetail = response.body().getCityDetail();
                        if (drawerActivity.parseContent.parseTripStatus(tripResponse) && googleMap != null) {
                            currencyFormat = drawerActivity.currencyHelper.getCurrencyFormat(drawerActivity.currentTrip.getCurrencyCode());
                            upDateUiWhenFixedRateTrip(drawerActivity.currentTrip.isFixedRate());
                            GlideApp.with(drawerActivity.getApplicationContext()).load(drawerActivity.currentTrip.getDriverCarImage()).override(drawerActivity.getResources().getDimensionPixelSize(R.dimen.vehicle_pin_width), drawerActivity.getResources().getDimensionPixelSize(R.dimen.vehicle_pin_height)).placeholder(R.drawable.driver_car).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivDriverCar);

                            // set address location
                            srcLatLng = new LatLng(drawerActivity.currentTrip.getSrcLatitude(), drawerActivity.currentTrip.getSrcLongitude());
                            if (drawerActivity.currentTrip.getDestLatitude() != 0.0 && drawerActivity.currentTrip.getDestLongitude() != 0.0) {
                                destLatLng = new LatLng(drawerActivity.currentTrip.getDestLatitude(), drawerActivity.currentTrip.getDestLongitude());
                            }
                            if (TextUtils.isEmpty(tvDriverName.getText().toString())) {
                                tvTripNumber.setText(drawerActivity.currentTrip.getTripNumber());
                                String tripNumber = drawerActivity.getResources().getString(R.string.text_trip_number) + "" + drawerActivity.currentTrip.getTripNumber();
                                drawerActivity.tvTitleToolbar.setText(tripNumber);
                                getProviderDetail();
                            }
                            unit = Utils.showUnit(drawerActivity, drawerActivity.currentTrip.getUnit());

                            if (!TextUtils.isEmpty(drawerActivity.currentTrip.getSrcAddress())) {
                                tvPickupAddress.setText(Utils.trimString(drawerActivity.currentTrip.getSrcAddress()));
                            }
                            if (!TextUtils.isEmpty(drawerActivity.currentTrip.getDestAddress())) {
                                tvDestinationAddress.setText(Utils.trimString(drawerActivity.currentTrip.getDestAddress()));
                            }

                            updateUiAfterPromo(drawerActivity.currentTrip.getIsPromoUsed() == Const.TRUE);
                            checkCurrentTripStatus(drawerActivity.currentTrip.getIsProviderAccepted());

                            // save data according to provider status one time
                            if (providerStatus == Const.ProviderStatus.PROVIDER_STATUS_UN_DEFINE && drawerActivity.currentTrip.getIsProviderStatus() >= Const.ProviderStatus.PROVIDER_STATUS_ACCEPTED) {
                                providerStatus = drawerActivity.currentTrip.getIsProviderStatus();
                                selectedPaymentList.addAll(tripResponse.getPaymentGateway());
                                checkPaymentModeAndPromoAvailable(selectedPaymentList);
                                tripPaymentAdapter.notifyDataSetChanged();
                                // if trip is cancelled  call  crate stripe payment one time in
                                // getTripStatus method when fragment load first time to prevent
                                // multiple call of  createStripePaymentIntent method when user get
                                // app from background
                                if (tripResponse.getTrip().getIsTripCancelledByUser() == Const.TRUE && tripResponse.getTrip().getIsCancellationFee() == Const.TRUE && drawerActivity.currentTrip.getIsTripCanceled() == Const.TRUE && dialogPendingPayment == null) {
                                    drawerActivity.setTripSocketListener(null);
                                    createStripePaymentIntent();
                                }
                            }
                            checkProviderStatus(drawerActivity.currentTrip.getIsProviderStatus());

                            if (tripResponse.getTrip().isRideShare()) {
                                tvSplitPayment.setVisibility(GONE);
                                tvDestinationAddress.setOnClickListener(null);
                            }

                            if (!tripResponse.getTrip().getTripStopAddresses().isEmpty()) {
                                tvDestinationAddress.setOnClickListener(null);
                                llTripStops.removeAllViews();

                                for (int i = 0; i < tripResponse.getTrip().getTripStopAddresses().size(); i++) {
                                    TripStopAddresses tripStopAddresses = tripResponse.getTrip().getTripStopAddresses().get(i);
                                    View stopView = LayoutInflater.from(drawerActivity).inflate(R.layout.layout_trip_stop, llTripStops, false);
                                    MyFontAutocompleteView acStopLocation = stopView.findViewById(R.id.acStopLocation);
                                    acStopLocation.setBackground(null);
                                    acStopLocation.setText(Utils.trimString(tripStopAddresses.getAddress()));
                                    acStopLocation.setTextColor(getResources().getColor(R.color.color_app_text));
                                    ImageView ivClearStopTextMap = stopView.findViewById(R.id.ivClearStopTextMap);
                                    ivClearStopTextMap.setVisibility(GONE);
                                    acStopLocation.setInputType(InputType.TYPE_NULL);
                                    acStopLocation.setEnabled(false);
                                    acStopLocation.setPadding(
                                            0,
                                            getResources().getDimensionPixelOffset(R.dimen.activity_margin_register),
                                            getResources().getDimensionPixelOffset(R.dimen.dimen_bill_line),
                                            getResources().getDimensionPixelOffset(R.dimen.activity_margin_register)
                                    );
                                    llTripStops.addView(stopView);
                                }
                            } else {
                                llTripStops.removeAllViews();
                            }

                            Utils.hideCustomProgressDialog();
                        } else {
                            Utils.hideCustomProgressDialog();
                            closeTripCancelDialog();
                            closedStatusNotifyDialog();
                            drawerActivity.currentTrip.setIsTripCanceled(Const.TRUE);
                            goToMapFragment();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TripResponse> call, Throwable t) {
                    AppLog.handleThrowable(MainDrawerActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.MAIN_DRAWER_ACTIVITY, e);
        }
    }


    private void openDestinationSelectDialog() {
        if (!drawerActivity.isFinishing()) {
            if (dialogFavAddress != null && dialogFavAddress.isShowing()) {
                return;
            }
            dialogFavAddress = new CustomAddressChooseDialog(drawerActivity, destLatLng, drawerActivity.currentTrip.getDestAddress(), Const.DESTINATION_ADDRESS) {

                @Override
                public void setSavedData(String address, LatLng latLng, int addressRequestCode) {
                    if (addressRequestCode == Const.DESTINATION_ADDRESS) {
                        updateDestination(latLng, address);
                        if (destinationMarker != null) {
                            destinationMarker.setPosition(latLng);
                        } else {
                            setDestinationMarker(latLng);
                        }
                    }
                }
            };

            dialogFavAddress.show();
        }
    }

    private void setUpMapScreenUI() {
        drawerActivity.setLocationListener(TripFragment.this);

    }

    private void openPaymentModeDialog() {
        if (!drawerActivity.isFinishing()) {


            if (addPaymentDialog != null && addPaymentDialog.isShowing()) {
                return;
            }

            addPaymentDialog = new CustomAddPaymentDialog(drawerActivity) {
                @Override
                public void onSelect(int paymentType) {
                    if (CurrentTrip.getInstance().getIsPromoUsed() == Const.TRUE) {
                        if ((paymentType == Const.CARD && cityDetail.getIsPromoApplyForCard() == Const.FALSE) || (paymentType == Const.CASH && cityDetail.getIsPromoApplyForCash() == Const.FALSE)) {
                            openPromoVoidDialog(paymentType);
                            addPaymentDialog.dismiss();
                        } else {
                            if (CurrentTrip.getInstance().getPaymentMode() != paymentType) {
                                paymentModeChange(paymentType);
                            }
                            dismiss();
                        }
                    } else {
                        if (CurrentTrip.getInstance().getPaymentMode() != paymentType) {
                            paymentModeChange(paymentType);
                        }
                        dismiss();
                    }


                }
            };
            addPaymentDialog.checkPaymentMode(drawerActivity.preferenceHelper.getPaymentCardAvailable(), drawerActivity.preferenceHelper.getPaymentCashAvailable());
            addPaymentDialog.show();
        }
    }

    private void callDriverViaTwilio() {
        Utils.showCustomProgressDialog(drawerActivity, "", false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            jsonObject.put(Const.Params.TYPE, Const.USER);
            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).twilioCall(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    Utils.hideCustomProgressDialog();
                    openWaitForCallAssignDialog();
                    btnCallDriver.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnCallDriver.setEnabled(true);
                        }
                    }, 5000);
                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    Utils.hideCustomProgressDialog();
                    AppLog.handleThrowable(TripFragment.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.TRIP_FRAGMENT, e);
        }
    }

    private void openWaitForCallAssignDialog() {
        if (!drawerActivity.isFinishing()) {


            CustomDialogNotification customDialogNotification = new CustomDialogNotification(drawerActivity, drawerActivity.getResources().getString(R.string.text_call_message)) {
                @Override
                public void doWithClose() {
                    dismiss();
                }
            };
            customDialogNotification.show();
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        int visible = GONE;
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Message chatMessage = snapshot.getValue(Message.class);
            if (chatMessage != null) {
                if (!chatMessage.isIs_read() && chatMessage.getType() == Const.PROVIDER_UNIQUE_NUMBER) {
                    visible = View.VISIBLE;
                    break;
                }
            }
        }
        ivHaveMessage.setVisibility(visible);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    public void onNetwork(boolean isConnected) {
        if (isConnected) {
            getTripStatus();
        } else {
            Utils.hideCustomProgressDialog();
        }

    }

    @Override
    public void onTripSocket(TripDetailOnSocket tripDetailOnSocket) {
        if (Lifecycle.State.RESUMED == getLifecycle().getCurrentState()) {
            if (tripDetailOnSocket.isTripUpdated()) {
                getTripStatus();
            } else {
                if (drawerActivity.currentTrip.getIsProviderStatus() == Const.ProviderStatus.PROVIDER_STATUS_TRIP_STARTED && googleMap != null) {
                    setTotalDistance(tripDetailOnSocket.getTotalDistance());
                    CurrentTrip.getInstance().setTotalTime((int) tripDetailOnSocket.getTotalTime());
                    setTotalTime((int) tripDetailOnSocket.getTotalTime());
                }
                List<Double> locations = tripDetailOnSocket.getProviderLocations();
                if (locations != null && !locations.isEmpty()) {
                    providerLatLng = new LatLng(locations.get(0), locations.get(1));
                }
                // draw current trip path and set marker when app is not in background other wise
                // just add currentLatLng in currentPathPolylineOptions
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    setMarkerOnLocation(providerLatLng, srcLatLng, destLatLng, tripDetailOnSocket.getBearing() - cameraBearing);
                } else {
                    drawCurrentPath(providerLatLng);
                }
            }
        }
    }

    private void goToChatActivity() {
        Intent intent = new Intent(drawerActivity, ChatActivity.class);
        startActivity(intent);
        drawerActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goToSplitPaymentActivity() {
        Intent intent = new Intent(drawerActivity, SplitPaymentActivity.class);
        startActivity(intent);
        drawerActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void initFirebaseChat() {
        if (!TextUtils.isEmpty(drawerActivity.preferenceHelper.getTripId())) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child(drawerActivity.preferenceHelper.getTripId());
        }
    }

    private void updateTripRootCamera() {
        if (drawerActivity.currentTrip.getIsProviderStatus() >= Const.ProviderStatus.PROVIDER_STATUS_ARRIVED) {
            updateCamera(getBearing(providerLatLng, destLatLng), providerLatLng);
        } else {
            updateCamera(getBearing(providerLatLng, srcLatLng), providerLatLng);
        }
    }

    private void setTotalDistance(double distance) {
        if(CurrentTrip.getInstance().getIsProviderStatus() == Const.ProviderStatus.PROVIDER_STATUS_TRIP_STARTED){
            tvLabelPlateNo.setText("Total Distance");
        }
        tvTotalDistance.setText(String.format("%s %s", drawerActivity.parseContent.twoDigitDecimalFormat.format(distance), unit));

    }

    private void setTotalTime(int time) {
        tvTotalTime.setTag(time);
        tvTotalTime.setText(String.format("%s %s", drawerActivity.parseContent.timeDecimalFormat.format(time), drawerActivity.getResources().getString(R.string.text_unit_mins)));

    }

    private void openPromoVoidDialog(int paymentType) {
        CustomDialogBigLabel promoVoidDialog = new CustomDialogBigLabel(drawerActivity, drawerActivity.getResources().getString(R.string.text_attention), drawerActivity.getResources().getString(R.string.msg_promo_void), drawerActivity.getResources().getString(R.string.text_continue_or_next), drawerActivity.getResources().getString(R.string.text_cancel)) {
            @Override
            public void positiveButton() {
                dismiss();
                removePromoCode();
                drawerActivity.currentTrip.setPaymentMode(paymentType);
                updateCardUi(CurrentTrip.getInstance().getPaymentMode() == Const.CARD);
//                openPaymentModeDialog();
            }

            @Override
            public void negativeButton() {
                dismiss();
            }
        };
        promoVoidDialog.show();
    }

    private void updateGooglePickUpLocationToDestinationLocation(String routes) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TRIP_ID, drawerActivity.preferenceHelper.getTripId());
            jsonObject.put(Const.Params.GOOGLE_PATH_START_LOCATION_TO_PICKUP_LOCATION, "");
            jsonObject.put(Const.Params.GOOGLE_PICKUP_LOCATION_TO_DESTINATION_LOCATION, routes);
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).setTripPath(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {

                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(TripFragment.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.TRIP_FRAGMENT, e);
        }
    }

    private void goToMapFragment() {
        if (isAdded()) {
            drawerActivity.goToMapFragment();
        }

    }

    /**
     * If at end of trip payment was not done due to incorrect card then this dialog is open.
     */
    private void openPendingPaymentDialog(int errorCode) {
        if (dialogPendingPayment != null && dialogPendingPayment.isShowing()) {
            return;
        }

        dialogPendingPayment = new CustomDialogBigLabel(drawerActivity, getResources().getString(R.string.text_payment_failed), tripResponse.getTrip().getPaymentGatewayType() == Const.PaymentMethod.PAYU ? getResources().getString(R.string.msg_payment_failed_for_payu) : getResources().getString(R.string.msg_payment_failed), drawerActivity.getResources().getString(R.string.text_pay_agin), tripResponse.getTrip().getPaymentGatewayType() != Const.PaymentMethod.PAYU ? drawerActivity.getResources().getString(R.string.text_add_new_card) : null) { // for payu don't need to show add card, only show retry button
            @Override
            public void positiveButton() {
                dialogPendingPayment.dismiss();
                createStripePaymentIntent();
            }

            @Override
            public void negativeButton() {
                dialogPendingPayment.dismiss();
                Intent paymentIntent = new Intent(drawerActivity, PaymentActivity.class);
                paymentIntent.putExtra(Const.IS_FROM_INVOICE, true);
                startActivityForResult(paymentIntent, Const.PENDING_PAYMENT);
            }
        };
        dialogPendingPayment.show();
        if (errorCode == Const.ERROR_CODE_ADD_CARD_FIRST) {
            dialogPendingPayment.findViewById(R.id.btnYes).setVisibility(GONE);
            MyFontButton btnNo = dialogPendingPayment.findViewById(R.id.btnNo);
            btnNo.setVisibility(View.VISIBLE);
            btnNo.setBackground(ResourcesCompat.getDrawable(drawerActivity.getResources(), R.drawable.selector_rect_shape_blue, null));
            btnNo.setTextColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(drawerActivity.getResources().getDimensionPixelSize(R.dimen.dialog_button_size), LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, drawerActivity.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin));
            btnNo.setLayoutParams(params);
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
                        if (response.body() != null && response.body().isSuccess()) {
                            if (response.body().getPaymentStatus() == Const.PaymentStatus.COMPLETED) {
                                drawerActivity.closedTripDialog();
                                closedStatusNotifyDialog();
                                goToMapFragment();
                            } else {
                                if (TextUtils.isEmpty(response.body().getError())) {
                                    if (!TextUtils.isEmpty(response.body().getHtmlForm())) {
                                        Utils.hideCustomProgressDialog();
                                        Intent intent = new Intent(drawerActivity, PaystackPaymentActivity.class);
                                        intent.putExtra(Const.Params.PAYU_HTML, response.body().getHtmlForm());
                                        startActivityForResult(intent, Const.REQUEST_PAYU);
                                    } else if (TextUtils.isEmpty(response.body().getPaymentMethod())) {
                                        Utils.hideCustomProgressDialog();
                                        openPendingPaymentDialog(0);
                                    } else {
                                        if (drawerActivity.stripe != null) {
                                            ConfirmPaymentIntentParams paymentIntentParams = ConfirmPaymentIntentParams.createWithPaymentMethodId(response.body().getPaymentMethod(), response.body().getClientSecret());
                                            drawerActivity.stripe.confirmPayment(TripFragment.this, paymentIntentParams);
                                        } else {
                                            Utils.showToast(getString(R.string.msg_stripe_not_added), requireContext());
                                        }
                                    }
                                } else {
                                    Utils.hideCustomProgressDialog();
                                    openPendingPaymentDialog(response.body().getErrorCode());
                                }
                                if (response.body().getMessage() != null && response.body().getMessage().equals(Const.MESSAGE_CODE_PAID_FROM_WALLET)) {
//                                if (response.body().getMessage().equals(Const.MESSAGE_CODE_PAID_FROM_WALLET)) {
                                    if (dialogPendingPayment != null && dialogPendingPayment.isShowing()) {
                                        dialogPendingPayment.dismiss();
                                    }
                                    Utils.hideCustomProgressDialog();
                                    Utils.showMessageToast(response.body().getMessage(), drawerActivity);
                                    drawerActivity.closedTripDialog();
                                    closedStatusNotifyDialog();
                                    goToMapFragment();
                                }
                            }
                        } else {
                            Utils.hideCustomProgressDialog();
                            if (TextUtils.isEmpty(response.body().getRequiredParam())) {
                                Utils.hideCustomProgressDialog();
                                openPendingPaymentDialog(response.body().getErrorCode());
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
                        if (response.body().isSuccess()) {
                            drawerActivity.closedTripDialog();
                            closedStatusNotifyDialog();
                            goToMapFragment();
                        } else {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.PENDING_PAYMENT) {
            openPendingPaymentDialog(0);
        } else if (requestCode == Const.REQUEST_PAYU) {
            if (resultCode == Activity.RESULT_OK) {
                Utils.hideCustomProgressDialog();
                drawerActivity.closedTripDialog();
                closedStatusNotifyDialog();
                goToMapFragment();
            } else {
                openPendingPaymentDialog(0);
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
                        openPendingPaymentDialog(0);
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

    private void drawPath(GoogleDirectionResponse pathResponse) {
        if (pathResponse.getStatus().equals(Const.google.OK)) {
            PolylineOptions polylineOptions = new PolylineOptions();
            try {
                for (RoutesItem routesItem : pathResponse.getRoutes()) {
                    for (LegsItem legsItem : routesItem.getLegs()) {
                        for (StepsItem stepsItem : legsItem.getSteps()) {
                            polylineOptions.addAll(PolyUtil.decode(stepsItem.getPolyline().getPoints()));
                        }
                    }
                }
            } catch (Exception e) {
                AppLog.handleException(TAG, e);
            }
            polylineOptions.width(15);
            polylineOptions.color(ResourcesCompat.getColor(getResources(), R.color.color_app_blue_path, null));
            if (googlePathPolyline != null) {
                googlePathPolyline.remove();
            }
            googlePathPolyline = this.googleMap.addPolyline(polylineOptions);
        }
    }

    public void getPathDrawOnMap(LatLng pickUpLatLng, LatLng destinationLatLng, boolean isWantToDrawPath) {
        if (pickUpLatLng != null && destinationLatLng != null & isWantToDrawPath) {
            String origins = pickUpLatLng.latitude + "," + pickUpLatLng.longitude;
            String destination = destinationLatLng.latitude + "," + destinationLatLng.longitude;
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Const.google.ORIGIN, origins);
            hashMap.put(Const.google.DESTINATION, destination);
            hashMap.put(Const.google.KEY, drawerActivity.preferenceHelper.getGoogleServerKey());
            ApiInterface apiInterface = new ApiClient().changeApiBaseUrl(Const.GOOGLE_API_URL).create(ApiInterface.class);
            Call<ResponseBody> call = apiInterface.getGoogleDirection(hashMap);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        try {
                            String googleResponse = response.body().string();
                            GoogleDirectionResponse googleDirectionResponse = ApiClient.getGsonInstance().fromJson(googleResponse, GoogleDirectionResponse.class);
                            drawPath(googleDirectionResponse);
                            if ((CurrentTrip.getInstance().getIsProviderStatus() == Const.ProviderStatus.PROVIDER_STATUS_ARRIVED || CurrentTrip.getInstance().getIsProviderStatus() == Const.ProviderStatus.PROVIDER_STATUS_TRIP_STARTED)) {
                                updateGooglePickUpLocationToDestinationLocation(googleResponse);
                            }
                        } catch (Exception e) {
                            AppLog.handleException(TAG, e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppLog.handleThrowable(TAG, t);
                }
            });
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
                        drawerActivity.closedTripDialog();
                        closedStatusNotifyDialog();
                        goToMapFragment();
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
                            openPendingPaymentDialog(0);
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

    /**
     * Use for handle trip status push.
     */
    public class TripStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!drawerActivity.isFinishing() && isAdded() && Lifecycle.State.RESUMED == getLifecycle().getCurrentState()) {
                switch (intent.getAction()) {
                    case Const.ACTION_PROVIDER_STARTED:
                    case Const.ACTION_PROVIDER_ARRIVED:
                    case Const.ACTION_TRIP_START:
                    case Const.ACTION_TRIP_END:
                    case Const.ACTION_WAITING_FOR_TIP:
                        getTripStatus();
                        break;
                    case Const.ACTION_TRIP_CANCEL_BY_PROVIDER:
                        drawerActivity.openDriverCancelTripDialog();
                        goToMapFragment();
                        break;
                    case Const.ACTION_TRIP_CANCEL_BY_ADMIN:
                        goToMapFragment();
                        break;
                    default:
                        //Do somethings with default
                        break;
                }
            }

        }
    }
}
