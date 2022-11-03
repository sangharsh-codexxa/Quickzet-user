package com.elluminati.eber;

import static com.elluminati.eber.utils.Const.REQUEST_CHECK_SETTINGS;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.adapter.DrawerAdapter;
import com.elluminati.eber.adapter.PlaceAutocompleteAdapter;
import com.elluminati.eber.components.CustomDialogBigLabel;
import com.elluminati.eber.components.CustomDialogEnable;
import com.elluminati.eber.components.CustomLanguageDialog;
import com.elluminati.eber.components.CustomTripDialog;
import com.elluminati.eber.components.DialogSplitPayment;
import com.elluminati.eber.components.TopSheet.TopSheetBehavior;
import com.elluminati.eber.fragments.FeedbackFragment;
import com.elluminati.eber.fragments.InvoiceFragment;
import com.elluminati.eber.fragments.MapFragment;
import com.elluminati.eber.fragments.TripFragment;
import com.elluminati.eber.interfaces.ClickListener;
import com.elluminati.eber.interfaces.RecyclerTouchListener;
import com.elluminati.eber.models.datamodels.TripDetailOnSocket;
import com.elluminati.eber.models.responsemodels.CancelTripResponse;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.payment.PayUsingPaymentGateway;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.LocationHelper;
import com.elluminati.eber.utils.SocketHelper;
import com.elluminati.eber.utils.SpacesItemDecoration;
import com.elluminati.eber.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.stripe.android.PaymentAuthConfig;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainDrawerActivity extends BaseAppCompatActivity implements LocationHelper.OnLocationReceived {

    private CustomLanguageDialog customLanguageDialog;

    public boolean isScheduleStart;
    public Location currentLocation;
    public CustomTripDialog customTripDialog;
    public LocationHelper locationHelper;
    public PlaceAutocompleteAdapter autocompleteAdapter;
    public Stripe stripe;
    private int drawerItemPosition;
    private CustomDialogBigLabel customDialogLogOut;
    private CustomDialogEnable customDialogEnable;
    private LocationReceivedListener locationReceivedListener;
    private FrameLayout containFrame;
    private CustomDialogBigLabel customCancelTripDialog;
    private LinearLayout llDrawerBg;
    private TopSheetBehavior topSheetBehavior;
    private NetworkListener networkListener;
    private TripSocketListener tripSocketListener;

    private DialogSplitPayment splitPaymentDialog;
    private PayUsingPaymentGateway payUsingPaymentGateway;

    Emitter.Listener onTripDetail = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null) {
                final JSONObject jsonObject = (JSONObject) args[0];
                final TripDetailOnSocket tripDetailOnSocket = ApiClient.getGsonInstance().fromJson(jsonObject.toString(), TripDetailOnSocket.class);
                runOnUiThread(() -> {
                    if (tripSocketListener != null) {
                        tripSocketListener.onTripSocket(tripDetailOnSocket);
                    }
                });
            }
        }
    };

    /**
     * Sets location listener.
     *
     * @param locationReceivedListener the location received listener
     */
    public void setLocationListener(LocationReceivedListener locationReceivedListener) {
        this.locationReceivedListener = locationReceivedListener;

    }

    /**
     * Sets trip socket listener.
     *
     * @param tripSocketListener the trip socket listener
     */
    public void setTripSocketListener(TripSocketListener tripSocketListener) {
        this.tripSocketListener = tripSocketListener;
    }

    /**
     * Sets network listener.
     *
     * @param networkListener the network listener
     */
    public void setNetworkListener(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        initToolBar();
        hideDrawerToggle(false);
        initDrawer();
        if (!TextUtils.isEmpty(preferenceHelper.getStripePublicKey())) {
            initStripePayment();
        }
        locationHelper = new LocationHelper(this);
        locationHelper.setLocationReceivedLister(this);
        containFrame = findViewById(R.id.contain_frame);
        autocompleteAdapter = new PlaceAutocompleteAdapter(this);
        checkPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (currentLocation == null) {
            currentLocation = location;
        }
        if (locationReceivedListener != null) {
            locationReceivedListener.onLocationReceived(location);
        }
    }

    private void loadFragmentAccordingStatus() {
        if (CurrentTrip.getInstance().getIsProviderAccepted() == Const.ProviderStatus.PROVIDER_STATUS_ACCEPTED && !TextUtils.isEmpty(preferenceHelper.getTripId())) {
            registerTripStatusSocket();
            if (CurrentTrip.getInstance().getIsTripEnd() == Const.TRUE) {
                goToInvoiceFragment();
            } else {
                goToTripFragment();
            }
        } else {
            goToMapFragment();
        }

        if (currentTrip.getSplitPaymentRequest() != null) {
            if (TextUtils.isEmpty(currentTrip.getSplitPaymentRequest().getPaymentMode())) {
                openSplitPaymentRequestDialog();
            } else if (TextUtils.equals(currentTrip.getSplitPaymentRequest().getPaymentMode(), String.valueOf(Const.CARD))
                    && currentTrip.getSplitPaymentRequest().getIsTripEnd() == Const.TRUE
                    && (currentTrip.getSplitPaymentRequest().getPaymentStatus() == Const.PaymentStatus.WAITING
                    || currentTrip.getSplitPaymentRequest().getPaymentStatus() == Const.PaymentStatus.FAILED)) {
                openSplitPaymentDialog();
            }
        }
    }


    /**
     * Sets place filter.
     *
     * @param countryCode the country code
     */
    public void setPlaceFilter(String countryCode) {
        if (autocompleteAdapter != null) {
            autocompleteAdapter.setPlaceFilter(countryCode);
            locationHelper.getLastLocation(location -> {
                currentLocation = location;
                if (currentLocation != null) {
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    RectangularBounds latLngBounds = RectangularBounds.newInstance(latLng, latLng);
                    autocompleteAdapter.setBounds(latLngBounds);
                } else {
                    Utils.showToast(getResources().getString(R.string.text_location_not_found), MainDrawerActivity.this);
                }
            });
        }
    }

    @Override
    protected boolean isValidate() {
        return false;
    }

    @Override
    public void goWithBackArrow() {
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(Const.Tag.MAP_FRAGMENT);
        if (mapFragment != null) {
            if (mapFragment.llWhereToGo.getVisibility() == View.GONE) {
                ((MapFragment) getSupportFragmentManager().findFragmentByTag(Const.Tag.MAP_FRAGMENT)).updateUiForCreateTrip(false);
                CurrentTrip.getInstance().setVehiclePriceType(Const.TYPE_NORMAL_PRICE);
            } else {
                drawerOpen();
            }
        } else {
            drawerOpen();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        drawerClosed();
    }

    @Override
    public void onClick(View v) {

    }

    private void initDrawer() {
        LinearLayout llDrawer = findViewById(R.id.llDrawer);
        llDrawerBg = findViewById(R.id.llDrawerBg);
        topSheetBehavior = TopSheetBehavior.from(llDrawer);
        topSheetBehavior.setTopSheetCallback(new TopSheetBehavior.TopSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                llDrawerBg.setClickable(topSheetBehavior.getState() == TopSheetBehavior.STATE_EXPANDED);

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                llDrawerBg.setAlpha(slideOffset);
                llDrawerBg.setVisibility(slideOffset == 0 ? View.GONE : View.VISIBLE);


            }
        });
        findViewById(R.id.ivClosedDrawerMenu).setOnClickListener(view -> drawerClosed());
        findViewById(R.id.btnLogOut).setOnClickListener(view -> {
            drawerClosed();
            openLogoutDialog();
        });
        llDrawerBg.setOnClickListener(view -> drawerClosed());
        DrawerAdapter drawerAdapter = new DrawerAdapter(this);
        RecyclerView recycleView = findViewById(R.id.listViewDrawer);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recycleView.setLayoutManager(gridLayoutManager);
        recycleView.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelOffset(R.dimen.dimen_bill_line)));
        recycleView.setAdapter(drawerAdapter);
        recycleView.addOnItemTouchListener(new RecyclerTouchListener(this, recycleView, new ClickListener() {

            @Override
            public void onLongClick(View view, int position) {

            }

            @Override
            public void onClick(View view, int position) {
                drawerItemPosition = position;
                switch (drawerItemPosition) {
                    case 0:
                        goToProfileActivity();
                        break;
                    case 1:
                        goToPaymentActivity();
                        break;
                    case 2:
                        goToBookingActivity();
                        break;
                    case 3:
                        goToHistoryActivity();
                        break;
                    case 4:
                        goToDocumentActivity(true);
                        break;
                    case 5:
                        goToReferralActivity();
                        break;
                    case 6:
                        goToSettingsActivity();
                        break;
                    case 7:
                        openLanguageDialog();
//                        goToFavouriteDriverActivity();
                        break;
                    case 8:
                        goToContactUsActivity();
                        break;
                    default:
                        break;
                }
            }
        }));

    }
    private void openLanguageDialog() {
        if (customLanguageDialog != null && customLanguageDialog.isShowing()) {
            return;
        }
        customLanguageDialog = new CustomLanguageDialog(this) {
            @Override
            public void onSelect(String languageName, String languageCode) {
//                tvLanguage.setText(languageName);
                 if (!TextUtils.equals(preferenceHelper.getLanguageCode(), languageCode)) {
                    preferenceHelper.putLanguageCode(languageCode);
                    finishAffinity();
                    restartApp();
                }
                dismiss();

            }
        };
        customLanguageDialog.show();
    }

    /**
     * Hide drawer toggle.
     *
     * @param isHide the is hide
     */
    public void hideDrawerToggle(boolean isHide) {
        if (isHide) {
            setToolbarNavigationIcon(R.drawable.left_arrow);
        } else {
            setToolbarNavigationIcon(R.drawable.ic_menu_black_24dp);
        }
    }

    private void resetScreenAfterCreateTrip() {
        addressUtils.resetAddress();
        hideDrawerToggle(false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, R.id.appToolbar);
        containFrame.setLayoutParams(params);
    }

    /**
     * Reset to full screen view.
     */
    public void resetToFullScreenView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, 0);
        containFrame.setLayoutParams(params);
    }

    /**
     * Go to map fragment.
     */
    public void goToMapFragment() {
        if (isFragmentNotAddedEver(Const.Tag.MAP_FRAGMENT)) {
            MapFragment mapFragment = new MapFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in_out, R.anim.slide_out_left, R.anim.fade_in_out, R.anim.slide_out_right);
            ft.replace(R.id.contain_frame, mapFragment, Const.Tag.MAP_FRAGMENT);
            ft.commitNowAllowingStateLoss();
        }
    }

    /**
     * Go to trip fragment.
     */
    public void goToTripFragment() {
        if (isFragmentNotAddedEver(Const.Tag.TRIP_FRAGMENT)) {
            resetScreenAfterCreateTrip();
            TripFragment tripFragment = new TripFragment();
            addFragment(tripFragment, false, true, Const.Tag.TRIP_FRAGMENT);
        }
    }

    /**
     * Go to feed back fragment.
     */
    public void goToFeedBackFragment() {
        if (isFragmentNotAddedEver(Const.Tag.FEEDBACK_FRAGMENT)) {
            FeedbackFragment feedbackFragment = new FeedbackFragment();
            addFragment(feedbackFragment, false, true, Const.Tag.FEEDBACK_FRAGMENT);
        }
    }

    /**
     * Go to booking activity.
     */
    public void goToBookingActivity() {
        Intent bookingIntent = new Intent(MainDrawerActivity.this, BookingActivity.class);
        startActivity(bookingIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Go to invoice fragment.
     */
    public void goToInvoiceFragment() {
        if (isFragmentNotAddedEver(Const.Tag.INVOICE_FRAGMENT)) {
            resetScreenAfterCreateTrip();
            InvoiceFragment invoiceFragment = new InvoiceFragment();
            addFragment(invoiceFragment, false, true, Const.Tag.INVOICE_FRAGMENT);
        }
    }

    private void goToReferralActivity() {
        Intent referralIntent = new Intent(MainDrawerActivity.this, ReferralDisplayActivtiy.class);
        startActivity(referralIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goToPaymentActivity() {
        Intent paymentIntent = new Intent(MainDrawerActivity.this, PaymentActivity.class);
        paymentIntent.putExtra(Const.IS_CLICK_INSIDE_DRAWER, true);
        startActivity(paymentIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    private void goToHistoryActivity() {
        Intent historyIntent = new Intent(MainDrawerActivity.this, TripHistoryActivity.class);
        startActivity(historyIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goToProfileActivity() {
        Intent intent = new Intent(MainDrawerActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goToContactUsActivity() {
        Intent intent = new Intent(MainDrawerActivity.this, ContactUsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goToSettingsActivity() {
        Intent settings = new Intent(this, SettingActivity.class);
        startActivity(settings);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goToFavouriteDriverActivity() {
        Intent settings = new Intent(this, FavouriteDriverActivity.class);
        startActivity(settings);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        if (topSheetBehavior.getState() == TopSheetBehavior.STATE_EXPANDED) {
            drawerClosed();
        } else {
            openExitDialog(this);
        }
    }

    /**
     * this method is used to cancel trip
     *
     * @param cancelReason       - give reason for cancel trip
     * @param cancelTripListener - callback to get response
     */
    public void cancelTrip(String cancelReason, final CancelTripListener cancelTripListener) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_waiting_for_cancel_trip), false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TRIP_ID, preferenceHelper.getTripId());
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.CANCEL_REASON, cancelReason);

            Call<CancelTripResponse> call = ApiClient.getClient().create(ApiInterface.class).cancelTrip(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<CancelTripResponse>() {
                @Override
                public void onResponse(Call<CancelTripResponse> call, Response<CancelTripResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        Utils.hideCustomProgressDialog();
                        if (cancelTripListener != null) {
                            CurrentTrip.getInstance().setVehiclePriceType(Const.TYPE_NORMAL_PRICE);
                            cancelTripListener.onCancelTrip(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CancelTripResponse> call, Throwable t) {
                    AppLog.handleThrowable(MainDrawerActivity.class.getSimpleName(), t);
                    Utils.hideCustomProgressDialog();
                }
            });

        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
    }


    /**
     * Open trip dialog.
     *
     * @param cancelTripListener the cancel trip listener
     */
    public void openTripDialog(final CancelTripListener cancelTripListener) {
        if (customTripDialog != null && customTripDialog.isShowing()) {
            return;
        }
        customTripDialog = new CustomTripDialog(this) {
            @Override
            public void doWithOk() {
                cancelTrip("", cancelTripListener);
            }
        };

        customTripDialog.getWindow().setDimAmount(0.5f);
        if (!isFinishing()) {
            customTripDialog.show();
        }
    }

    /**
     * Closed trip dialog.
     */
    public void closedTripDialog() {
        if (customTripDialog != null && customTripDialog.isShowing()) {
            customTripDialog.dismiss();
            customTripDialog = null;
        }
    }

    /**
     * Open logout dialog.
     */
    protected void openLogoutDialog() {
        customDialogLogOut = new CustomDialogBigLabel(this, getString(R.string.text_logout), getString(R.string.msg_are_you_sure), getString(R.string.text_yes), getString(R.string.text_no)) {
            @Override
            public void positiveButton() {
                customDialogLogOut.dismiss();
                logOut(false);
            }

            @Override
            public void negativeButton() {
                customDialogLogOut.dismiss();
            }
        };
        customDialogLogOut.show();
    }

    /**
     * Hide key pad.
     */
    public void hideKeyPad() {
        InputMethodManager inm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void closedPermissionDialog() {
        if (customDialogEnable != null && customDialogEnable.isShowing()) {
            customDialogEnable.dismiss();
            customDialogEnable = null;
        }
    }

    private void openPermissionDialog() {
        if (customDialogEnable != null && customDialogEnable.isShowing()) {
            return;
        }
        customDialogEnable = new CustomDialogEnable(this, getResources().getString(R.string.msg_reason_for_permission_location), getString(R.string.text_i_am_sure), getString(R.string.text_re_try)) {
            @Override
            public void doWithEnable() {
                ActivityCompat.requestPermissions(MainDrawerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Const.PERMISSION_FOR_LOCATION);
                closedPermissionDialog();
            }

            @Override
            public void doWithDisable() {
                closedPermissionDialog();
                finishAffinity();
            }
        };
        customDialogEnable.show();
    }

    private void openPermissionNotifyDialog(final int code) {
        if (customDialogEnable != null && customDialogEnable.isShowing()) {
            return;
        }
        customDialogEnable = new CustomDialogEnable(this, getResources().getString(R.string.msg_permission_notification), getResources().getString(R.string.text_exit_caps), getResources().getString(R.string.text_settings)) {
            @Override
            public void doWithEnable() {
                closedPermissionDialog();
                startActivityForResult(getIntentForPermission(), code);

            }

            @Override
            public void doWithDisable() {
                closedPermissionDialog();
                finishAffinity();
            }
        };
        customDialogEnable.show();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Const.PERMISSION_FOR_LOCATION);
        } else {
            locationHelper.onStart();
            loadFragmentAccordingStatus();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Const.PERMISSION_FOR_LOCATION:
                checkPermission();
                break;
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(Const.Tag.MAP_FRAGMENT);
                    if (mapFragment != null) {
                        mapFragment.moveCameraFirstMyLocation(true);
                    }
                }
                break;
            case Const.REQUEST_CHECK_LOCATION_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED:
                        Utils.showToast(getResources().getString(R.string.text_location_not_found), MainDrawerActivity.this);
                        break;
                    case Activity.RESULT_OK:
                        TripFragment tripFragment = (TripFragment) getSupportFragmentManager().findFragmentByTag(Const.Tag.TRIP_FRAGMENT);
                        if (tripFragment != null) {
                            tripFragment.openSOSDialog(10);
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            switch (requestCode) {
                case Const.PERMISSION_FOR_LOCATION:
                    goWithLocationPermission(grantResults);
                    break;
                default:
                    //do with default
                    break;
            }
        }
    }


    private void goWithLocationPermission(int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Do the stuff that requires permission...
            loadFragmentAccordingStatus();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                openPermissionDialog();
            } else {
                closedPermissionDialog();
                openPermissionNotifyDialog(Const.PERMISSION_FOR_LOCATION);
            }
        }
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (networkListener != null) {
            networkListener.onNetwork(isConnected);
        }
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

    /**
     * Open driver cancel trip dialog.
     */
    public void openDriverCancelTripDialog() {
        if (customCancelTripDialog != null && customCancelTripDialog.isShowing()) {
            return;
        }

        customCancelTripDialog = new CustomDialogBigLabel(this, getString(R.string.text_trip_cancelled), getString(R.string.message_trip_cancel), getString(R.string.text_ok), "") {
            @Override
            public void positiveButton() {
                closeDriverCancelTripDialog();
            }

            @Override
            public void negativeButton() {

            }
        };

        customCancelTripDialog.show();
    }

    private void closeDriverCancelTripDialog() {
        if (customCancelTripDialog != null && customCancelTripDialog.isShowing()) {
            customCancelTripDialog.dismiss();
            customCancelTripDialog = null;
        }
    }

    /**
     * Drawer closed.
     */
    public void drawerClosed() {
        if (topSheetBehavior.getState() == TopSheetBehavior.STATE_EXPANDED) {
            topSheetBehavior.setState(TopSheetBehavior.STATE_COLLAPSED);
        }

    }

    /**
     * Drawer open.
     */
    public void drawerOpen() {
        if (topSheetBehavior.getState() == TopSheetBehavior.STATE_COLLAPSED) {
            topSheetBehavior.setState(TopSheetBehavior.STATE_EXPANDED);
        }
    }

    /**
     * Use for add fragment in container.
     *
     * @param fragment       the fragment
     * @param addToBackStack the add to back stack
     * @param isAnimate      the is animate
     * @param tag            the tag
     */
    public void addFragment(Fragment fragment, boolean addToBackStack, boolean isAnimate, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (isAnimate) {
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        }
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.contain_frame, fragment, tag);
        ft.commitNowAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        locationHelper.stopLocationUpdate();
        super.onDestroy();
    }

    private boolean isFragmentNotAddedEver(String fragmentTag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        return fragment == null || !fragment.isAdded();
    }

    /**
     * Register trip status socket.
     */
    public void registerTripStatusSocket() {
        SocketHelper socketHelper = SocketHelper.getInstance();
        if (socketHelper != null && !TextUtils.isEmpty(preferenceHelper.getTripId())) {
            String tripId = String.format("'%s'", preferenceHelper.getTripId());
            socketHelper.getSocket().off();
            socketHelper.getSocket().on(tripId, onTripDetail);
        }
    }

    private void initStripePayment() {
        final PaymentAuthConfig.Stripe3ds2UiCustomization uiCustomization;
        uiCustomization = new PaymentAuthConfig.Stripe3ds2UiCustomization.Builder().build();
        PaymentAuthConfig.init(new PaymentAuthConfig.Builder().set3ds2Config(new PaymentAuthConfig.Stripe3ds2Config.Builder()
                // set a 5 minute timeout for challenge flow
                .setTimeout(5)
                // customize the UI of the challenge flow
                .setUiCustomization(uiCustomization).build()).build());

        PaymentConfiguration.init(this, preferenceHelper.getStripePublicKey());
        stripe = new Stripe(this, PaymentConfiguration.getInstance(this).getPublishableKey());
    }


    /**
     * Interface which help to get current location in fragment
     */
    public interface LocationReceivedListener {
        /**
         * On location received.
         *
         * @param location the location
         */
        void onLocationReceived(Location location);

    }

    /**
     * The interface Cancel trip listener.
     */
    public interface CancelTripListener {

        /**
         * On cancel trip.
         *
         * @param cancelTripResponse the cancel trip response
         */
        void onCancelTrip(CancelTripResponse cancelTripResponse);
    }

    /**
     * The interface Network listener.
     */
    public interface NetworkListener {
        /**
         * On network.
         *
         * @param isConnected the is connected
         */
        void onNetwork(boolean isConnected);
    }

    /**
     * The interface Trip socket listener.
     */
    public interface TripSocketListener {
        /**
         * On trip socket.
         *
         * @param tripDetailOnSocket the trip detail on socket
         */
        void onTripSocket(TripDetailOnSocket tripDetailOnSocket);
    }
}