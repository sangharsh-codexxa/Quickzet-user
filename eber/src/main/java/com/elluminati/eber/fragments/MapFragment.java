package com.elluminati.eber.fragments;


import static com.elluminati.eber.utils.Const.IMAGE_BASE_URL;
import static com.elluminati.eber.utils.Const.REQUEST_CHECK_SETTINGS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.elluminati.eber.DestinationSelectionActivity;
import com.elluminati.eber.MainDrawerActivity;
import com.elluminati.eber.PaymentActivity;
import com.elluminati.eber.PromotionActivity;
import com.elluminati.eber.R;
import com.elluminati.eber.adapter.RentalPackagesAdapter;
import com.elluminati.eber.adapter.SpeakingLanguageAdaptor;
import com.elluminati.eber.adapter.VehicleSelectAdapter;
import com.elluminati.eber.components.CustomAddPaymentDialog;
import com.elluminati.eber.components.CustomDialogBigLabel;
import com.elluminati.eber.components.CustomDialogFareEstimate;
import com.elluminati.eber.components.CustomDialogVerifyAccount;
import com.elluminati.eber.components.CustomEventMapView;
import com.elluminati.eber.components.MyFontAutocompleteView;
import com.elluminati.eber.components.MyFontButton;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.interfaces.ClickListener;
import com.elluminati.eber.models.datamodels.CityDetail;
import com.elluminati.eber.models.datamodels.CityType;
import com.elluminati.eber.models.datamodels.CityTypeRental;
import com.elluminati.eber.models.datamodels.CorporateDetail;
import com.elluminati.eber.models.datamodels.DayTime;
import com.elluminati.eber.models.datamodels.Language;
import com.elluminati.eber.models.datamodels.Provider;
import com.elluminati.eber.models.datamodels.ProviderLocation;
import com.elluminati.eber.models.datamodels.SurgeResult;
import com.elluminati.eber.models.datamodels.SurgeTime;
import com.elluminati.eber.models.datamodels.TripDetailOnSocket;
import com.elluminati.eber.models.datamodels.TripStopAddresses;
import com.elluminati.eber.models.responsemodels.CancelTripResponse;
import com.elluminati.eber.models.responsemodels.CreateTripResponse;
import com.elluminati.eber.models.responsemodels.ETAResponse;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.models.responsemodels.LanguageResponse;
import com.elluminati.eber.models.responsemodels.PromoResponse;
import com.elluminati.eber.models.responsemodels.ProviderDetailResponse;
import com.elluminati.eber.models.responsemodels.SaveAddressResponse;
import com.elluminati.eber.models.responsemodels.TripResponse;
import com.elluminati.eber.models.responsemodels.TypesResponse;
import com.elluminati.eber.models.singleton.AddressUtilsEber;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by elluminati on 14-06-2016.
 */
public class MapFragment extends BaseFragments implements OnMapReadyCallback, MainDrawerActivity.LocationReceivedListener, MainDrawerActivity.CancelTripListener, MainDrawerActivity.NetworkListener, MainDrawerActivity.TripSocketListener {
    private NumberFormat currencyFormat;
    private ETAResponse etaResponse;
    private final ArrayList<String> selectedGender = new ArrayList<>();
    private final ArrayList<String> selectedAccessibility = new ArrayList<>();
    private final ArrayList<String> selectedLanguage = new ArrayList<>();
    private final HashMap<String, Marker> nearByProviderMarkerHashMap = new HashMap<>();
    public LinearLayout llWhereToGo;
    private Dialog vehicleTypeRentalDialog;
    private TextView tvWhereTogo;
    private GoogleMap googleMap;
    private CustomEventMapView mapView;
    private CameraPosition cameraPosition;
    private FloatingActionButton ivTargetLocation;
    private CityType cityType = new CityType();
    private CityDetail cityDetail;
    private ArrayList<CityType> vehicleTypeList, poolVehicleTypeList;
    private Dialog fareNearPlaceDialog;
    private CustomDialogFareEstimate customDialog;
    private RecyclerView rcvMapVehicleTyp;
    private VehicleSelectAdapter vehicleSelectAdapter;
    private View.OnKeyListener onBackKeyListener;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar;
    private LocalBroadcastManager localBroadcastManager;
    private TripStatusReceiver tripStatusReceiver;
    private ArrayList<Marker> markerList;
    private ArrayList<Marker> driverMarkerList;
    private long selectDateLong, startDateAndTimeForFutureTrip;
    private Marker destinationMarker;
    private CustomDialogBigLabel dialogFixedRate;
    private ScheduledExecutorService nearByProviderSchedule;
    private boolean isSchedularStart, isGetDriverETA;
    private CustomDialogBigLabel pendingPayment;
    private ImageView ivMapCar;
    private BitmapDescriptor carBitmap;
    private CardView cardWhereTo, cvProductFilter;
    private Marker pickupMarker;
    private LinearLayout llFareEstimate, llAddPayment;
    private LinearLayout llRequestView, llMapAddress, llRideView, llNoService;
    private MyFontTextView tvMapPickupAddress, tvMapDestinationAddress;
    private MyFontButton btnRideNow, btnApplyFilter, btnCancel;
    private ImageView ivRideLater;
    private MyFontTextView tvFareEst;
    private double fareEstDistance, fareEstTime;
    private String textRideLaterBtn;
    private CustomAddPaymentDialog addPaymentDialog;
    private ImageView ivPaymentIcon;
    private MyFontTextView tvPaymentType, tvMapEta;
    private FloatingActionButton llHomeAddress, llWorkAddress, llAddAddress;
    private LinearLayout llSelectLanguage;
    private boolean isClickRideNow = false;
    private RecyclerView rcvSpeakingLanguage;
    private CheckBox cbMale, cbFemale;
    private CheckBox cbHandicap, cbBabySeat, cbHotspot, cbPayByCorporate;
    private LinearLayout llRequestFilter;
    private SpeakingLanguageAdaptor speakingLanguageAdaptor;
    private BottomSheetBehavior sheetBehavior;
    private TextView tvEberNow, tvEberRental, tvRentalPackage, tvEberRideShare;
    private Button btnSelectRent;
    private TextView ivBtnPromo;
    private LinearLayout llRentPackages, llRideNow, llETA;
    private Dialog corporateRequestDialog;
    private TextView tvMessageNoService, tvCheckOurDelivery;
    private IncomingHandler incomingHandler;
    private ArrayList<Provider> nearByProvider = new ArrayList<>();
    private CustomDialogVerifyAccount promoDialog;
    private View divETA;
    private IntentFilter intentFilter;

    // promo before trip
    private String promoId;

    // Multiple stops
    private LinearLayout llAddStop, llTripStops;
    private MyFontTextView tvAddStop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawerActivity.setTripSocketListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mapFragView = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = mapFragView.findViewById(R.id.mapView);
        ivTargetLocation = mapFragView.findViewById(R.id.ivTargetLocation);
        rcvMapVehicleTyp = mapFragView.findViewById(R.id.rcvMapVehicleType);
        ivMapCar = mapFragView.findViewById(R.id.ivMapCar);
        llRequestView = mapFragView.findViewById(R.id.llRequestView);
        cardWhereTo = mapFragView.findViewById(R.id.cardWhereTo);
        llMapAddress = mapFragView.findViewById(R.id.llMapAddress);
        tvMapPickupAddress = mapFragView.findViewById(R.id.tvMapPickupAddress);
        tvMapDestinationAddress = mapFragView.findViewById(R.id.tvMapDestinationAddress);
        llWhereToGo = mapFragView.findViewById(R.id.llWhereToGo);
        btnRideNow = mapFragView.findViewById(R.id.btnRideNow);
        ivRideLater = mapFragView.findViewById(R.id.ivRideLater);
        llFareEstimate = mapFragView.findViewById(R.id.llFareEstimate);
        llAddPayment = mapFragView.findViewById(R.id.llAddPayment);
        tvFareEst = mapFragView.findViewById(R.id.tvFareEst);
        llRideView = mapFragView.findViewById(R.id.llRideView);
        llNoService = mapFragView.findViewById(R.id.llNoService);
        ivPaymentIcon = mapFragView.findViewById(R.id.ivPaymentIcon);
        tvPaymentType = mapFragView.findViewById(R.id.tvPaymentType);
        tvMapEta = mapFragView.findViewById(R.id.tvMapEta);
        llHomeAddress = mapFragView.findViewById(R.id.llHomeAddress);
        llWorkAddress = mapFragView.findViewById(R.id.llWorkAddress);
        llAddAddress = mapFragView.findViewById(R.id.llAddAddress);
        rcvSpeakingLanguage = mapFragView.findViewById(R.id.rcvSpeakingLanguage);
        cbMale = mapFragView.findViewById(R.id.cbMale);
        cbFemale = mapFragView.findViewById(R.id.cbFemale);
        cbBabySeat = mapFragView.findViewById(R.id.cbBabySeat);
        cbHandicap = mapFragView.findViewById(R.id.cbHandicap);
        cbHotspot = mapFragView.findViewById(R.id.cbHotspot);
        llRequestFilter = mapFragView.findViewById(R.id.llRequestFilter);
        btnCancel = mapFragView.findViewById(R.id.btnCancel);
        btnApplyFilter = mapFragView.findViewById(R.id.btnApplyFilter);
        llSelectLanguage = mapFragView.findViewById(R.id.llSelectLanguage);
        cvProductFilter = mapFragView.findViewById(R.id.cvProductFilter);
        tvWhereTogo = mapFragView.findViewById(R.id.tvWhereTogo);
        tvEberNow = mapFragView.findViewById(R.id.tvEberNow);
        tvEberRental = mapFragView.findViewById(R.id.tvEberRental);
        tvEberRideShare = mapFragView.findViewById(R.id.tvEberRideShare);
        tvRentalPackage = mapFragView.findViewById(R.id.tvRentalPackage);
        btnSelectRent = mapFragView.findViewById(R.id.btnSelectRent);
        llRentPackages = mapFragView.findViewById(R.id.llRentPackages);
        llRideNow = mapFragView.findViewById(R.id.llRideNow);
        cbPayByCorporate = mapFragView.findViewById(R.id.cbPayByCorporate);
        tvMessageNoService = mapFragView.findViewById(R.id.tvMessageNoService);
        tvCheckOurDelivery = mapFragView.findViewById(R.id.tvCheckOurDelivery);
        ivBtnPromo = mapFragView.findViewById(R.id.ivBtnPromo);
        llETA = mapFragView.findViewById(R.id.llETA);
        divETA = mapFragView.findViewById(R.id.diveEta);
        llAddStop = mapFragView.findViewById(R.id.llAddStop);
        tvAddStop = mapFragView.findViewById(R.id.tvAddStop);
        llTripStops = mapFragView.findViewById(R.id.llTripStops);
        return mapFragView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUiDriverETA(drawerActivity.preferenceHelper.getIsShowEstimation());
        drawerActivity.setToolbarBackgroundWithElevation(false, android.R.color.transparent, 0);
        drawerActivity.setTitleOnToolbar("");
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        initVehicleTypeBottomSheet();
        getSpeakingLanguages();
        setUpMapScreenUI();
        initVehicleRecycleView();
        initTripStatusReceiver();
        initNearProviderHandler();
        ivTargetLocation.setOnClickListener(this);
        calendar = Calendar.getInstance();
        markerList = new ArrayList<>();
        driverMarkerList = new ArrayList<>();
        nearByProvider = new ArrayList<>();
        cardWhereTo.setOnClickListener(this);
        btnRideNow.setOnClickListener(this);
        ivRideLater.setOnClickListener(this);
        llAddPayment.setOnClickListener(this);
        llHomeAddress.setOnClickListener(this);
        llWorkAddress.setOnClickListener(this);
        llAddAddress.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        llFareEstimate.setOnClickListener(this);
        btnApplyFilter.setOnClickListener(this);
        tvEberRental.setOnClickListener(this);
        tvEberRideShare.setOnClickListener(this);
        tvEberNow.setOnClickListener(this);
        btnSelectRent.setOnClickListener(this);
        ivBtnPromo.setOnClickListener(this);
        tvAddStop.setOnClickListener(this);
        tvMapPickupAddress.setOnClickListener(this);
        tvMapDestinationAddress.setOnClickListener(this);
        if (drawerActivity.getPackageName().equals("com.elluminatiinc.taxi.user")) {
            tvCheckOurDelivery.setVisibility(View.GONE);
            tvCheckOurDelivery.setOnClickListener(this);
        } else {
            tvCheckOurDelivery.setVisibility(View.GONE);
        }


        tvAddStop.setVisibility(drawerActivity.preferenceHelper.getAllowMultipleStops() ? View.VISIBLE : View.GONE);

        drawerActivity.locationHelper.setLocationSettingRequest(drawerActivity, REQUEST_CHECK_SETTINGS, o -> {
            moveCameraFirstMyLocation(true);
        }, () -> moveCameraFirstMyLocation(true));

        onBackKeyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED && isShowAddressView()) {
                        updateUiForCreateTrip(false);
                        CurrentTrip.getInstance().setVehiclePriceType(Const.TYPE_NORMAL_PRICE);
                        return true;
                    }
                }
                return false;
            }
        };
        getHomeAndWorkAddress();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @SuppressLint("RestrictedApi")
    private void setUpMapScreenUI() {
        llAddAddress.setVisibility(View.GONE);
        llHomeAddress.setVisibility(View.GONE);
        llWorkAddress.setVisibility(View.GONE);
        drawerActivity.resetToFullScreenView();
        drawerActivity.setLocationListener(MapFragment.this);
        drawerActivity.setTitleOnToolbar("");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cardWhereTo.getLayoutParams();
            params.setMargins(0, (int) drawerActivity.getResources().getDimension(R.dimen.dimen_where_to_go_card_margin), 0, 0);
            cardWhereTo.setLayoutParams(params);
        }
        llRequestFilter.setVisibility(View.GONE);
        cvProductFilter.setVisibility(View.VISIBLE);
        rcvSpeakingLanguage.setLayoutManager(new LinearLayoutManager(drawerActivity));
        speakingLanguageAdaptor = new SpeakingLanguageAdaptor(drawerActivity, drawerActivity.currentTrip.getSpeakingLanguages(), selectedLanguage);
        rcvSpeakingLanguage.setAdapter(speakingLanguageAdaptor);
        rcvSpeakingLanguage.setNestedScrollingEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (getView() != null) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(onBackKeyListener);
        }
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopNearProviderScheduler();
        drawerActivity.setNetworkListener(null);
        localBroadcastManager.unregisterReceiver(tripStatusReceiver);
    }

    @Override
    public void onDestroyView() {
        if (googleMap != null) {
            googleMap.clear();
        }
        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        unRegisterAllProviderSocket();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setUpMap();
        this.googleMap.clear();
        moveCameraFirstMyLocation(false);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationReceived(Location location) {
        if (googleMap != null && googleMap.getCameraPosition().target.latitude == 0 && googleMap.getCameraPosition().target.longitude == 0) {
            moveCameraFirstMyLocation(true);
        }
    }

    /**
     * This method is used to setUpMap option which help to load map as per option
     */
    private void setUpMap() {
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            final boolean doNotMoveCameraToCenterMarker = true;

            public boolean onMarkerClick(Marker marker) {
                return doNotMoveCameraToCenterMarker;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        SocketHelper.getInstance().socketConnect();
        drawerActivity.setNetworkListener(this);
        if (googleMap != null) {
            checkUISavedAddressButton();
        }
        getTripStatus();
        if (isShowAddressView()) {
            startNearProviderScheduler();
        }
        localBroadcastManager.registerReceiver(tripStatusReceiver, intentFilter);
    }

    /***
     * this method is used to move camera on map at current position and a isCameraMove is
     * used to decide when is move or not
     */
    public void moveCameraFirstMyLocation(final boolean isAnimate) {
        drawerActivity.locationHelper.getLastLocation(location -> {
            drawerActivity.currentLocation = location;
            if (drawerActivity.currentLocation != null) {
                LatLng latLngOfMyLocation = new LatLng(drawerActivity.currentLocation.getLatitude(), drawerActivity.currentLocation.getLongitude());
                if (!isShowAddressView()) {
                    new GetPickUpAddressFromLocation().executeOnExecutor(Executors.newSingleThreadExecutor(), latLngOfMyLocation);
                    drawerActivity.addressUtils.setPickupLatLng(latLngOfMyLocation);
                    dropPickUpPin();
                }
                cameraPosition = new CameraPosition.Builder().target(latLngOfMyLocation).zoom(17).build();
                if (isAnimate) {
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
    }


    /**
     * this method is update UI which is required as per our functionality
     *
     * @param isUpdate update UI according service type
     */
    private void updateUiServiceType(boolean isUpdate) {
        if (isUpdate) {
            llRideView.setVisibility(View.VISIBLE);
            llNoService.setVisibility(View.GONE);
        } else {
            llRideView.setVisibility(View.GONE);
            llNoService.setVisibility(View.VISIBLE);
        }
    }


    /**
     * It call whenever user destroy app after creating request.
     * It call only when request status is pending for accept.
     */
    private void setTripData() {
        drawerActivity.addressUtils.setPickupLatLng(new LatLng(drawerActivity.currentTrip.getSrcLatitude(), drawerActivity.currentTrip.getSrcLongitude()));
        drawerActivity.addressUtils.setPickupAddress(drawerActivity.currentTrip.getSrcAddress());
        drawerActivity.addressUtils.setTrimedPickupAddress(Utils.trimString(drawerActivity.currentTrip.getSrcAddress()));
        if (!TextUtils.isEmpty(drawerActivity.currentTrip.getDestAddress()) && drawerActivity.currentTrip.getDestLatitude() != 0.0 && drawerActivity.currentTrip.getDestLongitude() != 0.0) {
            drawerActivity.addressUtils.setDestinationLatLng(new LatLng(drawerActivity.currentTrip.getDestLatitude(), drawerActivity.currentTrip.getDestLongitude()));
            drawerActivity.addressUtils.setDestinationAddress(drawerActivity.currentTrip.getDestAddress());
            drawerActivity.addressUtils.setTrimedDestinationAddress(Utils.trimString(drawerActivity.currentTrip.getDestAddress()));
        } else {
            drawerActivity.addressUtils.setDestinationAddress("");
            drawerActivity.addressUtils.setDestinationLatLng(null);
        }
        googleMap.clear();
        setMarkerOnLocation(drawerActivity.addressUtils.getPickupLatLng(), drawerActivity.addressUtils.getDestinationLatLng(), false);
        updateUiForCreateTrip(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCheckOurDelivery:
                goToPromotionActivity();
                break;
            case R.id.btnRideNow:

//                if (!isClickRideNow) {
                    showFareEstimation();
//                    isClickRideNow = true;
//                    goWithRideEvent();
//                }
                break;
            case R.id.ivRideLater:
                openDatePickerDialog();
                break;
            case R.id.ivTargetLocation:
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED && isShowAddressView()) {
                    updateUiForCreateTrip(false);
                } else {
                    drawerActivity.locationHelper.setLocationSettingRequest(drawerActivity, REQUEST_CHECK_SETTINGS, o -> moveCameraFirstMyLocation(true), () -> moveCameraFirstMyLocation(true));
                }
                break;
            case R.id.llFareEstimate:

                showFareEstimation();
                break;
            case R.id.llAddPayment:
                if (drawerActivity.preferenceHelper.getPaymentCardAvailable() != Const.NOT_AVAILABLE || drawerActivity.preferenceHelper.getPaymentCashAvailable() != Const.NOT_AVAILABLE) {
                    openPaymentModeDialog();
                }
                break;
            case R.id.cardWhereTo:
            case R.id.llAddAddress:
                drawerActivity.locationHelper.setLocationSettingRequest(drawerActivity, REQUEST_CHECK_SETTINGS, (OnSuccessListener) o -> {
                    moveCameraFirstMyLocation(true);
                    goToDestinationSelectionActivity(false);
                }, () -> moveCameraFirstMyLocation(true));
                break;
            case R.id.llHomeAddress:
                goWithFavouriteAddressSelection(true);
                break;
            case R.id.llWorkAddress:
                goWithFavouriteAddressSelection(false);
                break;
            case R.id.ivToolbarIcon:
                openRequestFilter();
                break;
            case R.id.btnCancel:
                openRequestFilter();
                break;
            case R.id.btnApplyFilter:
                saveRequestFilter();
                getNearByProvider();
                break;
            case R.id.tvEberNow:
                CurrentTrip.getInstance().setVehiclePriceType(Const.TYPE_NORMAL_PRICE);
                selectVehiclePriceType(CurrentTrip.getInstance().getVehiclePriceType());
                selectVehicleType(0, CurrentTrip.getInstance().getVehiclePriceType(), true);
                break;
            case R.id.tvEberRental:
                CurrentTrip.getInstance().setVehiclePriceType(Const.TYPE_RENTAL_PRICE);
                selectVehiclePriceType(CurrentTrip.getInstance().getVehiclePriceType());
                selectVehicleType(0, CurrentTrip.getInstance().getVehiclePriceType(), true);
                break;
            case R.id.tvEberRideShare:
                CurrentTrip.getInstance().setVehiclePriceType(Const.TYPE_SHARE_TRIP_PRICE);
                selectVehiclePriceType(CurrentTrip.getInstance().getVehiclePriceType());
                selectVehicleType(0, CurrentTrip.getInstance().getVehiclePriceType(), true);
                break;
            case R.id.btnSelectRent:
                openVehicleTypeRentalPackagesDialog((ArrayList<CityTypeRental>) cityType.getRentalTypes());
                break;
            case R.id.ivBtnPromo:
                if (ivBtnPromo.getTag() != null && (boolean) ivBtnPromo.getTag()) {
                    updateUiAfterPromo(false);
                } else {
                    openPromoApplyDialog();
                }
                break;
            case R.id.tvAddStop:
                goToDestinationSelectionActivity(true);
                break;
            case R.id.tvMapPickupAddress:
            case R.id.tvMapDestinationAddress:
                goToDestinationSelectionActivity(false);
                break;
            default:
                break;
        }
    }

    /**
     * Use for set deatination address as per selection from favourite address.
     *
     * @param isHomeAddress pass boolean according selection
     */
    private void goWithFavouriteAddressSelection(boolean isHomeAddress) {
        if (TextUtils.isEmpty(drawerActivity.addressUtils.getPickupAddress())) {
            Utils.showToast(drawerActivity.getResources().getString(R.string.message_cant_get_current_address), drawerActivity);
            return;
        } else if (isHomeAddress) {
            drawerActivity.addressUtils.setTrimedDestinationAddress(drawerActivity.addressUtils.getTrimmedHomeAddress());
            drawerActivity.addressUtils.setDestinationAddress(drawerActivity.addressUtils.getHomeAddress());
            drawerActivity.addressUtils.setDestinationLatLng(new LatLng(drawerActivity.addressUtils.getHomeLatitude(), drawerActivity.addressUtils.getHomeLongitude()));
        } else {
            drawerActivity.addressUtils.setTrimedDestinationAddress(drawerActivity.addressUtils.getTrimmedWorkAddress());
            drawerActivity.addressUtils.setDestinationAddress(drawerActivity.addressUtils.getWorkAddress());
            drawerActivity.addressUtils.setDestinationLatLng(new LatLng(drawerActivity.addressUtils.getWorkLatitude(), drawerActivity.addressUtils.getWorkLongitude()));
        }
        goWithDestinationResult();
    }

    private void goWithRideEvent() {


        if (tvPaymentType.getText().toString().equals(drawerActivity.getResources().getString(R.string.text_pay_by))) {
            Utils.showToast(drawerActivity.getResources().getString(R.string.msg_please_select_payment_mode), drawerActivity);
            isClickRideNow = false;
        } else if (drawerActivity.getResources().getString(R.string.text_ride_now).equals(btnRideNow.getText().toString())) {
            SurgeResult surgeResult = checkSurgeTimeApply(cityType.getSurgeHours(), drawerActivity.currentTrip.getServerTime(), drawerActivity.currentTrip.getCityTimeZone(), false, 0);
            if (surgeResult.getIsSurge() == Const.TRUE) {
                openSurgePricingDialog(surgeResult.getSurgeMultiplier(), false);
            } else {
                openFixedRateDialog(false);
            }
        }
        else {
            openFixedRateDialog(true);
        }

    }



    private void showFareEstimation() {

        if (!vehicleTypeList.isEmpty() || !poolVehicleTypeList.isEmpty()) {
            SurgeResult surgeResult = checkSurgeTimeApply(cityType.getSurgeHours(), drawerActivity.currentTrip.getServerTime(), drawerActivity.currentTrip.getCityTimeZone(), false, 0);
            if (drawerActivity.addressUtils.getDestinationAddress().isEmpty()) {
                openFareDialog(false, surgeResult.getIsSurge() == Const.TRUE);
            } else {
                if (fareNearPlaceDialog != null && fareNearPlaceDialog.isShowing()) {
                    fareNearPlaceDialog.dismiss();
                    fareNearPlaceDialog = null;
                }
                openFareDialog(true, surgeResult.getIsSurge() == Const.TRUE);
            }
        } else {
            Utils.showToast(drawerActivity.getResources().getString(R.string.msg_no_service_type), drawerActivity);
        }
    }


    /***
     * this method used to create a trip and isDestination params is decide to that is user
     * set a
     * destination_pin or not
     *
     * @param isDestination is true when user select destination_pin
     */

    private synchronized void createTrip(boolean isDestination, boolean isFixedRatedTrip, String carRentalId) {
        Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getResources().getString(R.string.msg_waiting_for_request_trip), false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            if (isDestination && drawerActivity.addressUtils.getDestinationLatLng() != null) {

                if (!drawerActivity.addressUtils.getTripStopAddressesArrayList().isEmpty()) {
                    jsonObject.put(Const.Params.DESTINATION_ADDRESSES, ApiClient.JSONArray(drawerActivity.addressUtils.getTripStopAddressesArrayList()));
                }
                jsonObject.put(Const.Params.DEST_LATITUDE, drawerActivity.addressUtils.getDestinationLatLng().latitude);
                jsonObject.put(Const.Params.DEST_LONGITUDE, drawerActivity.addressUtils.getDestinationLatLng().longitude);
                jsonObject.put(Const.Params.DESTINATION_ADDRESS, drawerActivity.addressUtils.getDestinationAddress());
            } else {
                jsonObject.put(Const.Params.DESTINATION_ADDRESS, "");
                jsonObject.put(Const.Params.DEST_LATITUDE, "");
                jsonObject.put(Const.Params.DEST_LONGITUDE, "");
            }
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.PICK_UP_LATITUDE, drawerActivity.addressUtils.getPickupLatLng().latitude);
            jsonObject.put(Const.Params.PICK_UP_LONGITUDE, drawerActivity.addressUtils.getPickupLatLng().longitude);
            jsonObject.put(Const.Params.SOURCE_ADDRESS, drawerActivity.addressUtils.getPickupAddress());
            jsonObject.put(Const.Params.TIMEZONE, Utils.getTimeZoneName());
            jsonObject.put(Const.Params.PAYMENT_MODE, drawerActivity.currentTrip.getPaymentMode());
            jsonObject.put(Const.Params.SERVICE_TYPE, cityType.getId());
            SurgeResult surgeResult = checkSurgeTimeApply(cityType.getSurgeHours(), drawerActivity.currentTrip.getServerTime(), drawerActivity.currentTrip.getCityTimeZone(), false, 0);
            jsonObject.put(Const.Params.IS_SURGE_HOURS, surgeResult.getIsSurge());
            jsonObject.put(Const.Params.SURGE_MULTIPLIER, surgeResult.getSurgeMultiplier());
            jsonObject.put(Const.Params.IS_FIXED_FARE, isFixedRatedTrip);
            jsonObject.put(Const.Params.FIXED_PRICE, isFixedRatedTrip ? drawerActivity.currentTrip.getEstimatedFareTotal() : 0);
            jsonObject.put(Const.Params.ACCESSIBILITY, new JSONArray(selectedAccessibility));
            jsonObject.put(Const.Params.RECEIVED_TRIP_FROM_GENDER, new JSONArray(selectedGender));
            jsonObject.put(Const.Params.PROVIDER_LANGUAGE, new JSONArray(selectedLanguage));
            jsonObject.put(Const.Params.CAR_RENTAL_ID, carRentalId);
            if (cbPayByCorporate.isChecked() && CurrentTrip.getInstance().getCorporateDetail() != null) {
                jsonObject.put(Const.Params.TRIP_TYPE, Const.TripType.TRIP_TYPE_CORPORATE);
                jsonObject.put(Const.Params.CORPORATE_ID, CurrentTrip.getInstance().getCorporateDetail().getId());
            }
            if ((drawerActivity.currentTrip.getPaymentMode() == Const.CARD && cityDetail.getIsPromoApplyForCard() == Const.TRUE) || (drawerActivity.currentTrip.getPaymentMode() == Const.CASH && cityDetail.getIsPromoApplyForCash() == Const.TRUE)) {
                jsonObject.put(Const.Params.PROMO_ID, promoId);
            }
            Call<CreateTripResponse> call = ApiClient.getClient().create(ApiInterface.class).createTrip(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<CreateTripResponse>() {
                @Override
                public void onResponse(Call<CreateTripResponse> call, Response<CreateTripResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (drawerActivity.parseContent.parseTrip(response.body())) {
                            drawerActivity.registerTripStatusSocket();
                            stopNearProviderScheduler();
                            drawerActivity.openTripDialog(MapFragment.this);
                            if (!drawerActivity.addressUtils.getDestinationAddress().isEmpty()) {
                                drawerActivity.currentTrip.setDestAddress(drawerActivity.addressUtils.getDestinationAddress());
                            }
                        } else {
                            if (String.valueOf(response.body().getErrorCode()).equals(Const.ERROR_CODE_YOUR_TRIP_PAYMENT_IS_PENDING)) {
                                openPendingPaymentDialog();
                            } else if (String.valueOf(response.body().getErrorCode()).equals(Const.ERROR_CODE_USER_DECLINE)) {
                                drawerActivity.goWithAdminDecline();
                                Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                            } else if (response.body().getErrorCode() == Const.ERROR_CODE_TRIP_ALREADY_RUNNING) {
                                getTripStatus();
                            } else {
                                Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                            }
                        }
                        Utils.hideCustomProgressDialog();
                        isClickRideNow = false;
                    }

                }

                @Override
                public void onFailure(Call<CreateTripResponse> call, Throwable t) {
                    AppLog.handleThrowable(MapFragment.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
    }

    /**
     * This method is used to get vehicle list from web service
     */
    private void getVehicleType(String countryName, double cityLatitude, double cityLongitude, String countryCode) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.COUNTRY, countryName);
            jsonObject.put(Const.Params.LATITUDE, cityLatitude);
            jsonObject.put(Const.Params.LONGITUDE, cityLongitude);
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.google.COUNTRY_CODE, countryCode);
            if (drawerActivity.addressUtils.getDestinationLatLng() != null) {
                jsonObject.put(Const.Params.DESTINATION_LATITUDE, String.valueOf(drawerActivity.addressUtils.getDestinationLatLng().latitude));
                jsonObject.put(Const.Params.DESTINATION_lONGITUDE, String.valueOf(drawerActivity.addressUtils.getDestinationLatLng().longitude));
            }

            Call<TypesResponse> call = ApiClient.getClient().create(ApiInterface.class).getVehicleTypes(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<TypesResponse>() {
                @Override
                public void onResponse(@NonNull Call<TypesResponse> call, @NonNull Response<TypesResponse> response) {
                    btnRideNow.setEnabled(false);
                    btnRideNow.setAlpha(.5f);
                    if (ParseContent.getInstance().isSuccessful(response) && response.body() != null) {
                        vehicleTypeList.clear();
                        poolVehicleTypeList.clear();
                        cityDetail = response.body().getCityDetail();
                        updateUiAfterPromo(false);
                        if (drawerActivity.parseContent.parseTypes(response.body())) {
                            updateCorporateUi(response.body().isCorporateRequest());
                            vehicleTypeList.addAll(response.body().getCityTypes());
                            poolVehicleTypeList.addAll(response.body().getPoolType());
                            drawerActivity.currentTrip.setCurrencyCode(response.body().getCurrencyCode());
                            drawerActivity.currentTrip.setUnit(response.body().getCityDetail().getUnit());
                            updateUiServiceType(true);
                            selectVehiclePriceType(Const.TYPE_NORMAL_PRICE);
                            int rentalCount = selectVehicleType(0, Const.TYPE_NORMAL_PRICE, true);
                            tvEberRental.setVisibility(rentalCount == 0 || !drawerActivity.addressUtils.getTripStopAddressesArrayList().isEmpty() ? View.GONE : View.VISIBLE);
                            checkPaymentModeAvailable();
                            tvEberRideShare.setVisibility((response.body().getPoolType().isEmpty() || !drawerActivity.addressUtils.getTripStopAddressesArrayList().isEmpty()) ? View.GONE : View.VISIBLE);
                        } else {
                            try {
                                String msg;
                                String errorCode = Const.ERROR_CODE_PREFIX + response.body().getErrorCode();
                                msg = drawerActivity.getResources().getString(drawerActivity.getResources().getIdentifier(errorCode, Const.STRING, drawerActivity.getPackageName()));
                                tvMessageNoService.setText(msg);
                            } catch (Resources.NotFoundException e) {
                                tvMessageNoService.setText(drawerActivity.getResources().getString(R.string.error_code_1002));
                            }
                            updateUiServiceType(false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<TypesResponse> call, Throwable t) {
                    updateUiAfterPromo(false);
                    btnRideNow.setEnabled(false);
                    btnRideNow.setAlpha(.5f);
                    updateUiServiceType(false);
                    Utils.showToast(getString(R.string.something_went_wrong), requireContext());
                    AppLog.handleThrowable(MapFragment.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
    }

    /**
     * this method is used to set marker at location and params is decide to which location we
     * set a marker
     *
     * @param pickUpLatLng      pickUpLatLng
     * @param destinationLatLng destinationLatLng
     */
    private void setMarkerOnLocation(LatLng pickUpLatLng, LatLng destinationLatLng, boolean isFromScheduler) {
        BitmapDescriptor bitmapDescriptor;
        markerList.clear();
        if (pickUpLatLng != null) {
            if (pickupMarker != null) {
                pickupMarker.remove();
            }
            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.drawableToBitmap(ResourcesCompat.getDrawable(drawerActivity.getResources(), R.drawable.user_pin, null)));

            pickupMarker = googleMap.addMarker(new MarkerOptions().position(pickUpLatLng).title(drawerActivity.getResources().getString(R.string.text_pick_up)).icon(bitmapDescriptor).anchor(0.5f, 0.5f));
            markerList.add(pickupMarker);
        }
        if (destinationLatLng != null) {
            if (destinationMarker != null) {
                destinationMarker.remove();
            }
            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.vectorToBitmap(drawerActivity, R.drawable.destination_pin));
            destinationMarker = googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title(drawerActivity.getResources().getString(R.string.text_destination)).icon(bitmapDescriptor).anchor(0.5f, 0.5f));

            markerList.add(destinationMarker);
            markerList.addAll(driverMarkerList);

            try {
                if (!isFromScheduler) {
                    setLocationBounds(true, markerList);
                }
            } catch (Exception e) {
                AppLog.handleException(TAG, e);
            }
        } else {
            if (destinationMarker != null) {
                destinationMarker.remove();
            }
            if (!isFromScheduler) {
                googleMap.setPadding(0, 0, 0, 0);
                cameraPosition = new CameraPosition.Builder().target(drawerActivity.addressUtils.getPickupLatLng()).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

        if (!drawerActivity.addressUtils.getTripStopAddressesArrayList().isEmpty()) {
            for (TripStopAddresses tripStopAddresses : drawerActivity.addressUtils.getTripStopAddressesArrayList()) {
                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.vectorToBitmap(drawerActivity, R.drawable.ic_dot_pin));
                LatLng stops = new LatLng(tripStopAddresses.getLocation().get(0), tripStopAddresses.getLocation().get(1));
                Marker marker = googleMap.addMarker(new MarkerOptions().position(stops).title(drawerActivity.getResources().getString(R.string.text_destination_stop)).icon(bitmapDescriptor).anchor(0.5f, 0.5f));
                markerList.add(marker);
            }
        }
    }

    /**
     * This method is use to bound map as per total markers in map.
     *
     * @param isCameraAnim isCameraAnim
     * @param markerList   markerList
     */
    private void setLocationBounds(boolean isCameraAnim, ArrayList<Marker> markerList) {
        googleMap.setPadding(0, 0, 0, 0);
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        int driverListSize = markerList.size();
        for (int i = 0; i < driverListSize; i++) {
            bounds.include(markerList.get(i).getPosition());
        }
        //Change the padding as per needed
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds.build(), (int) drawerActivity.getResources().getDimension(R.dimen.map_bound));

        googleMap.setPadding(0, (int) drawerActivity.getResources().getDimension(R.dimen.map_padding_top), 0, (int) drawerActivity.getResources().getDimension(R.dimen.map_padding_bottom));

        if (isCameraAnim) {
            googleMap.animateCamera(cu);
        } else {
            googleMap.moveCamera(cu);
        }
    }


    /**
     * Use for open fare estimate dialog.
     *
     * @param isDestinationSelect isDestinationSelect
     * @param isSurgePricing      isSurgePricing
     */
    protected void openFareDialog(final boolean isDestinationSelect, final boolean isSurgePricing) {

        SurgeResult surgeResult = checkSurgeTimeApply(cityType.getSurgeHours(), drawerActivity.currentTrip.getServerTime(), drawerActivity.currentTrip.getCityTimeZone(), false, 0);
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.notifyDataSetChange(cityType.getTypeDetails().getTypename(), cityType.getBasePrice(), cityType.getBasePriceDistance(), cityType.getPricePerUnitDistance(), cityType.getPriceForTotalTime(), cityType.getMaxSpace(), CurrentTrip.getInstance().getEstimatedFareTotal(), CurrentTrip.getInstance().getEstimatedFareTime(), drawerActivity.addressUtils.getPickupAddress(), drawerActivity.addressUtils.getDestinationAddress(), CurrentTrip.getInstance().getCurrencyCode(), CurrentTrip.getInstance().getEstimatedFareDistance(), cityType.getTax(), Utils.showUnit(drawerActivity, CurrentTrip.getInstance().getUnit()), cityType.getCancellationFee(), cityType.getTypeDetails().getTypeImageUrl(), isDestinationSelect, surgeResult.getSurgeMultiplier(), isSurgePricing, CurrentTrip.getInstance().getTripType());

        } else {
            customDialog = new CustomDialogFareEstimate(drawerActivity) {
                @Override
                public void onClickFareEstimate(boolean isDestinationSelect) {
                    if (isDestinationSelect) {
                        dismiss();
                        setMarkerOnLocation(drawerActivity.addressUtils.getPickupLatLng(), drawerActivity.addressUtils.getDestinationLatLng(), false);
                    }
                }

                @Override
                public void onBack() {
                    dismiss();
                }

                @Override
                public void onProceed(){
                    goWithRideEvent();
                    dismiss();
                }

                @Override
                public void selectDestination(){
                }
            };
            customDialog.notifyDataSetChange(cityType.getTypeDetails().getTypename(), cityType.getBasePrice(), cityType.getBasePriceDistance(), cityType.getPricePerUnitDistance(), cityType.getPriceForTotalTime(), cityType.getMaxSpace(), CurrentTrip.getInstance().getEstimatedFareTotal(), CurrentTrip.getInstance().getEstimatedFareTime(), drawerActivity.addressUtils.getPickupAddress(), drawerActivity.addressUtils.getDestinationAddress(), CurrentTrip.getInstance().getCurrencyCode(), CurrentTrip.getInstance().getEstimatedFareDistance(), cityType.getTax(), Utils.showUnit(drawerActivity, CurrentTrip.getInstance().getUnit()), cityType.getCancellationFee(), cityType.getTypeDetails().getTypeImageUrl(), isDestinationSelect, surgeResult.getSurgeMultiplier(), isSurgePricing, CurrentTrip.getInstance().getTripType());
            customDialog.show();
        }
    }

    /**
     * use for get duration and distance between user_pin and destination_pin.
     * @param srcLatLng
     * @param destLatLng
     */
    private void getDistanceMatrixForDriverEta(LatLng srcLatLng, LatLng destLatLng) {
        if (drawerActivity.preferenceHelper.getIsShowEstimation()) {
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
                        HashMap<String, String> map = drawerActivity.parseContent.parsDistanceMatrix(response);
                        if (map != null) {
                            String timeSecond = map.get(Const.google.DURATION);
                            Double timeMinute = Double.valueOf(timeSecond) / 60;
                            tvMapEta.setText(Utils.validateTimeUnit(drawerActivity, new DecimalFormat("##").format(timeMinute)));
                        }
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppLog.handleThrowable(MapFragment.class.getSimpleName(), t);
                }
            });
        }

    }

    /**
     * use for get duration and distance between user_pin and destination_pin.
     *
     * @param srcLatLng
     * @param destLatLng
     */
    private void getDistanceMatrix(LatLng srcLatLng, LatLng destLatLng) {
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
                    HashMap<String, String> map = drawerActivity.parseContent.parsDistanceMatrix(response);
                    if (map != null) {
                        String distance = map.get(Const.google.DISTANCE);
                        String timeSecond = map.get(Const.google.DURATION);
                        Double timeMinute = Double.valueOf(timeSecond) / 60;
                        Double tripDistance = Double.valueOf(distance);
                        fareEstDistance = tripDistance;
                        fareEstTime = Double.valueOf(timeSecond);
                        getFareEstimate(tripDistance, fareEstTime, cityType.getId());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                AppLog.handleThrowable(TAG, t);
            }
        });
    }


    /**
     * this method called a webservice for get distance and time witch is provided by Google
     */
    private void getDirection(LatLng srcLatLng, LatLng destLatLng) {
        Utils.showCustomProgressDialog(drawerActivity, "", false, null);
        HashMap<String, String> hashMap = new HashMap<>();
        String origins = srcLatLng.latitude + "," + srcLatLng.longitude;
        hashMap.put(Const.google.ORIGIN, origins);
        String destination = destLatLng.latitude + "," + destLatLng.longitude;
        hashMap.put(Const.google.DESTINATION, destination);
        StringBuilder wayPoints = new StringBuilder();

        if (!drawerActivity.addressUtils.getTripStopAddressesArrayList().isEmpty()) {
            for (int i = 0; i < (drawerActivity.addressUtils.getTripStopAddressesArrayList().size()); i++) {
                wayPoints.append(drawerActivity.addressUtils.getTripStopAddressesArrayList().get(i).getLocation().get(0)).append(",").append(drawerActivity.addressUtils.getTripStopAddressesArrayList().get(i).getLocation().get(1)).append("|");
            }
        }
        hashMap.put(Const.google.WAYPOINTS, wayPoints.toString());
        hashMap.put(Const.google.KEY, drawerActivity.preferenceHelper.getGoogleServerKey());

        ApiInterface apiInterface = new ApiClient().changeApiBaseUrl(Const.GOOGLE_API_URL).create

                (ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getGoogleDirection(hashMap);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                HashMap<String, String> map = drawerActivity.parseContent.parseDirection(response);
                if (map != null) {
                    String distance = map.get(Const.google.DISTANCE);
                    String timeSecond = map.get(Const.google.DURATION);
                    Double timeMinute = Double.valueOf(timeSecond) / 60;
                    Double tripDistance = Double.valueOf(distance);
                    fareEstDistance = tripDistance;
                    fareEstTime = Double.valueOf(timeSecond);
                    getFareEstimate(tripDistance, fareEstTime, cityType.getId());
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.hideCustomProgressDialog();
                AppLog.handleThrowable(Const.Tag.MAP_FRAGMENT, t);

            }
        });

    }

    /**
     * Use to get nearby provider.
     * This method call every 10 sec by the scheduler.
     * It also call when map move.
     */
    private void getNearByProvider() {
        if (drawerActivity.addressUtils.getPickupLatLng() != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
                jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
                jsonObject.put(Const.Params.LATITUDE, String.valueOf(drawerActivity.addressUtils.getPickupLatLng().latitude));
                jsonObject.put(Const.Params.LONGITUDE, String.valueOf(drawerActivity.addressUtils.getPickupLatLng().longitude));


                if (drawerActivity.addressUtils.getDestinationLatLng() != null) {
                    jsonObject.put(Const.Params.DESTINATION_LATITUDE, String.valueOf(drawerActivity.addressUtils.getDestinationLatLng().latitude));
                    jsonObject.put(Const.Params.DESTINATION_lONGITUDE, String.valueOf(drawerActivity.addressUtils.getDestinationLatLng().longitude));
                }/*else {
                    jsonObject.put(Const.Params.DESTINATION_LATITUDE, "0.0");
                    jsonObject.put(Const.Params.DESTINATION_lONGITUDE, "0.0");
                }*/

                if (isShowAddressView() && cityType != null) {
                    jsonObject.put(Const.Params.SERVICE_TYPE, cityType.getId());
                    jsonObject.put(Const.Params.PAYMENT_MODE, drawerActivity.currentTrip.getPaymentMode());
                    jsonObject.put(Const.Params.ACCESSIBILITY, new JSONArray(selectedAccessibility));
                    jsonObject.put(Const.Params.RECEIVED_TRIP_FROM_GENDER, new JSONArray(selectedGender));
                    jsonObject.put(Const.Params.PROVIDER_LANGUAGE, new JSONArray(selectedLanguage));
                }
                if (vehicleTypeList.size() > 0 || poolVehicleTypeList.size() > 0) {
                    Call<ProviderDetailResponse> call = ApiClient.getClient().create(ApiInterface.class).getNearbyProviders(ApiClient.makeJSONRequestBody(jsonObject));
                    call.enqueue(new Callback<ProviderDetailResponse>() {
                        @Override
                        public void onResponse(Call<ProviderDetailResponse> call, Response<ProviderDetailResponse> response) {
                            if (ParseContent.getInstance().isSuccessful(response)) {
                                if (response.body().isSuccess()) {
                                    if (!response.body().getProviders().isEmpty()) {
                                        enableRentButton(true);
                                        btnRideNow.setEnabled(true);
                                        btnRideNow.setAlpha(1.0f);
                                        Provider provider = response.body().getProviders().get(0);
                                        if (isGetDriverETA) {
                                            getDistanceMatrixForDriverEta(drawerActivity.addressUtils.getPickupLatLng(), new LatLng(provider.getProviderLocation().get(0), provider.getProviderLocation().get(1)));
                                            isGetDriverETA = false;
                                        }
                                        googleMap.clear();
                                        driverMarkerList.clear();
                                        setMarkerOnLocation(drawerActivity.addressUtils.getPickupLatLng(), drawerActivity.addressUtils.getDestinationLatLng(), true);
                                        unRegisterAllProviderSocket();
                                        nearByProviderMarkerHashMap.clear();
                                        nearByProvider.clear();
                                        nearByProvider.addAll(response.body().getProviders());
                                        registerAllProviderSocket();
                                    } else {
                                        googleMap.clear();
                                        driverMarkerList.clear();
                                        enableRentButton(false);
                                        if (drawerActivity.getResources().getString(R.string.text_ride_now).equals(btnRideNow.getText().toString())) {
                                            btnRideNow.setEnabled(false);
                                            btnRideNow.setAlpha(.5f);
                                        }
                                        setMarkerOnLocation(drawerActivity.addressUtils.getPickupLatLng(), drawerActivity.addressUtils.getDestinationLatLng(), true);
                                        tvMapEta.setText(drawerActivity.getResources().getString(R.string.text_eta) + "" + " " + ": 0 " + drawerActivity.getResources().getString(R.string.text_unit_min));
                                    }

                                } else {
                                    googleMap.clear();
                                    driverMarkerList.clear();
                                    enableRentButton(false);
                                    if (drawerActivity.getResources().getString(R.string.text_ride_now).equals(btnRideNow.getText().toString())) {
                                        btnRideNow.setEnabled(false);
                                        btnRideNow.setAlpha(.5f);
                                    }
                                    setMarkerOnLocation(drawerActivity.addressUtils.getPickupLatLng(), drawerActivity.addressUtils.getDestinationLatLng(), true);
                                    tvMapEta.setText(drawerActivity.getResources().getString(R.string.text_eta) + "" + " " + ": 0 " + drawerActivity.getResources().getString(R.string.text_unit_min));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProviderDetailResponse> call, Throwable t) {
                            AppLog.handleThrowable(TAG, t);
                        }
                    });
                }
            } catch (Exception e) {
                AppLog.handleException(TAG, e);
            }
        }

    }

    /**
     * use to pass distance and duration between two addresses to server.
     *
     * @param distance
     * @param time
     * @param serviceType
     */
    private void getFareEstimate(double distance, double time, String serviceType) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.DISTANCE, distance);
            jsonObject.put(Const.Params.TIME, time);
            jsonObject.put(Const.Params.SERVICE_TYPE, serviceType);
            SurgeResult surgeResult = checkSurgeTimeApply(cityType.getSurgeHours(), drawerActivity.currentTrip.getServerTime(), drawerActivity.currentTrip.getCityTimeZone(), false, 0);
            jsonObject.put(Const.Params.IS_SURGE_HOURS, surgeResult.getIsSurge());
            jsonObject.put(Const.Params.SURGE_MULTIPLIER, surgeResult.getSurgeMultiplier());
            jsonObject.put(Const.Params.PICKUP_LATITUDE, String.valueOf(drawerActivity.addressUtils.getPickupLatLng().latitude));
            jsonObject.put(Const.Params.PICKUP_LONGITUDE, String.valueOf(drawerActivity.addressUtils.getPickupLatLng().longitude));
            jsonObject.put(Const.Params.DESTINATION_LATITUDE, String.valueOf(drawerActivity.addressUtils.getDestinationLatLng().latitude));
            jsonObject.put(Const.Params.DESTINATION_lONGITUDE, String.valueOf(drawerActivity.addressUtils.getDestinationLatLng().longitude));
            if (!drawerActivity.addressUtils.getTripStopAddressesArrayList().isEmpty()) {
                jsonObject.put(Const.Params.IS_MULTIPLE_STOP, Const.TRUE);
            } else {
                jsonObject.put(Const.Params.IS_MULTIPLE_STOP, Const.FALSE);
            }
            Call<ETAResponse> call = ApiClient.getClient().create(ApiInterface.class).getETAForeTrip(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<ETAResponse>() {
                @Override
                public void onResponse(Call<ETAResponse> call, Response<ETAResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                         etaResponse = response.body();
                        if (etaResponse.isSuccess()) {
                            CurrentTrip currentTrip = CurrentTrip.getInstance();
                            currentTrip.setEstimatedFareTotal(etaResponse.getEstimatedFare());
                            currentTrip.setEstimatedFareDistance(Double.valueOf(etaResponse.getDistance()));
                            currentTrip.setEstimatedFareTime(etaResponse.getTime());
                            currentTrip.setTripType(Integer.valueOf(etaResponse.getTripType()));
                            currencyFormat = drawerActivity.currencyHelper.getCurrencyFormat(CurrentTrip.getInstance().getCurrencyCode());
                             tvFareEst.setText(currencyFormat.format(etaResponse.getEstimatedFare()));

                            Log.e("Error----->",String.valueOf(etaResponse.getEstimatedFare()));
//                            tvFareEst.setText("Null");



                            Utils.hideCustomProgressDialog();
                        } else {
                            Utils.hideCustomProgressDialog();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ETAResponse> call, Throwable t) {
                    AppLog.handleThrowable(TAG, t);
                }
            });
        } catch (Exception e) {
            AppLog.handleException(TAG, e);
        }

    }

    /**
     * Use for initialize vehicle type recyclerview.
     */
    private void initVehicleRecycleView() {
        vehicleTypeList = new ArrayList<>();
        poolVehicleTypeList = new ArrayList<>();
        vehicleSelectAdapter = new VehicleSelectAdapter(drawerActivity, vehicleTypeList, poolVehicleTypeList);
        LinearLayoutManager vehicleLinearLayoutManager = new LinearLayoutManager(drawerActivity);
//        vehicleLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                vehicleLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvMapVehicleTyp.setLayoutManager(vehicleLinearLayoutManager);
        rcvMapVehicleTyp.addOnItemTouchListener(new RecyclerTouchListener(drawerActivity, rcvMapVehicleTyp, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position >= 0) {
                    selectVehicleType(position, CurrentTrip.getInstance().getVehiclePriceType(), false);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if (position >= 0) {
                    openTypeDescriptionDialog();
                }


            }
        }));
        rcvMapVehicleTyp.setAdapter(vehicleSelectAdapter);
    }

    /***
     * this method is set vehicle data which is get at position of vehicle list
     *
     * @param position
     */
    private void setVehicleData(int position) {
        if (vehicleTypeList.size() > 0 || poolVehicleTypeList.size() > 0) {
            cityType = CurrentTrip.getInstance().getVehiclePriceType() == Const.TYPE_SHARE_TRIP_PRICE ? poolVehicleTypeList.get(position) : vehicleTypeList.get(position);
            setVehiclePinOnMap(cityType.getTypeDetails().getMapPinImageUrl());
            if (!TextUtils.isEmpty(drawerActivity.addressUtils.getDestinationAddress())) {
                Utils.showCustomProgressDialog(drawerActivity, "", false, null);
                getFareEstimate(fareEstDistance, fareEstTime, cityType.getId());
            }
            if (isShowAddressView()) {
                isGetDriverETA = true;
            }
            getNearByProvider();
        }
    }

    /*
     * This method is use for put car pin on map as per seleceted vehicle type....
     */
    private void setVehiclePinOnMap(String carPinUrl) {

        GlideApp.with(drawerActivity.getApplicationContext()).asBitmap().load(IMAGE_BASE_URL + carPinUrl).override(drawerActivity.getResources().getDimensionPixelSize(R.dimen.vehicle_pin_width), drawerActivity.getResources().getDimensionPixelSize(R.dimen.vehicle_pin_height)).placeholder(R.drawable.driver_car).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                carBitmap = BitmapDescriptorFactory.fromBitmap(Utils.vectorToBitmap(drawerActivity, R.drawable.driver_car));
                return true;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                carBitmap = BitmapDescriptorFactory.fromBitmap(resource);
                return true;
            }
        }).into(ivMapCar);

    }

    /**
     * this method is used to decide which ui of card is set at current time
     *
     * @param paymentType
     */

    private void updateCardUi(int paymentType) {
        if (paymentType == Const.CARD) {
            tvPaymentType.setText(drawerActivity.getResources().getString(R.string.text_card));
            ivPaymentIcon.setImageDrawable(AppCompatResources.getDrawable(drawerActivity, R.drawable.card));
            drawerActivity.currentTrip.setPaymentMode(Const.CARD);

        } else if (paymentType == Const.CASH) {
            tvPaymentType.setText(drawerActivity.getResources().getString(R.string.text_cash));
            ivPaymentIcon.setImageDrawable(AppCompatResources.getDrawable(drawerActivity, R.drawable.cash));
            drawerActivity.currentTrip.setPaymentMode(Const.CASH);
        } else {
            tvPaymentType.setText(drawerActivity.getResources().getString(R.string.text_pay_by));
            ivPaymentIcon.setImageDrawable(AppCompatResources.getDrawable(drawerActivity, R.drawable.card));
        }
    }

    /**
     * Used for create scheduled trip.
     *
     * @param isDestination
     */


    private synchronized void createFutureTrip(boolean isDestination, boolean isFixedRatedTrip) {
        Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getResources().getString(R.string.msg_waiting_for_request_trip), false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            if (isDestination && drawerActivity.addressUtils.getDestinationLatLng() != null) {

                if (!drawerActivity.addressUtils.getTripStopAddressesArrayList().isEmpty()) {
                    jsonObject.put(Const.Params.DESTINATION_ADDRESSES, ApiClient.JSONArray(drawerActivity.addressUtils.getTripStopAddressesArrayList()));
                }
                jsonObject.put(Const.Params.DEST_LATITUDE, drawerActivity.addressUtils.getDestinationLatLng().latitude);
                jsonObject.put(Const.Params.DEST_LONGITUDE, drawerActivity.addressUtils.getDestinationLatLng().longitude);
                jsonObject.put(Const.Params.DESTINATION_ADDRESS, drawerActivity.addressUtils.getDestinationAddress());
            } else {
                jsonObject.put(Const.Params.DESTINATION_ADDRESS, "");
                jsonObject.put(Const.Params.DEST_LATITUDE, "");
                jsonObject.put(Const.Params.DEST_LONGITUDE, "");
            }
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.PICK_UP_LATITUDE, drawerActivity.addressUtils.getPickupLatLng().latitude);
            jsonObject.put(Const.Params.PICK_UP_LONGITUDE, drawerActivity.addressUtils.getPickupLatLng().longitude);
            jsonObject.put(Const.Params.SOURCE_ADDRESS, drawerActivity.addressUtils.getPickupAddress());
            jsonObject.put(Const.Params.TIMEZONE, drawerActivity.currentTrip.getCityTimeZone());
            jsonObject.put(Const.Params.PAYMENT_MODE, drawerActivity.currentTrip.getPaymentMode());
            jsonObject.put(Const.Params.SERVICE_TYPE, cityType.getId());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone(CurrentTrip.getInstance().getCityTimeZone()));
            jsonObject.accumulate(Const.Params.START_TIME, startDateAndTimeForFutureTrip - calendar.getTimeInMillis());
            SurgeResult surgeResult = checkSurgeTimeApply(cityType.getSurgeHours(), drawerActivity.currentTrip.getServerTime(), drawerActivity.currentTrip.getCityTimeZone(), true, startDateAndTimeForFutureTrip);
            jsonObject.put(Const.Params.IS_SURGE_HOURS, surgeResult.getIsSurge());
            jsonObject.put(Const.Params.SURGE_MULTIPLIER, surgeResult.getSurgeMultiplier());
            jsonObject.put(Const.Params.IS_FIXED_FARE, isFixedRatedTrip);
            jsonObject.put(Const.Params.FIXED_PRICE, isFixedRatedTrip ? drawerActivity.currentTrip.getEstimatedFareTotal() : 0);
            jsonObject.put(Const.Params.ACCESSIBILITY, new JSONArray(selectedAccessibility));
            jsonObject.put(Const.Params.RECEIVED_TRIP_FROM_GENDER, new JSONArray(selectedGender));
            jsonObject.put(Const.Params.PROVIDER_LANGUAGE, new JSONArray(selectedLanguage));
            if ((drawerActivity.currentTrip.getPaymentMode() == Const.CARD && cityDetail.getIsPromoApplyForCard() == Const.TRUE) || (drawerActivity.currentTrip.getPaymentMode() == Const.CASH && cityDetail.getIsPromoApplyForCash() == Const.TRUE)) {
                jsonObject.put(Const.Params.PROMO_ID, promoId);
            }
            if (cbPayByCorporate.isChecked() && CurrentTrip.getInstance().getCorporateDetail() != null) {
                jsonObject.put(Const.Params.TRIP_TYPE, Const.TripType.TRIP_TYPE_CORPORATE);
                jsonObject.put(Const.Params.CORPORATE_ID, CurrentTrip.getInstance().getCorporateDetail().getId());
            }
            Call<CreateTripResponse> call = ApiClient.getClient().create(ApiInterface.class).createTrip(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<CreateTripResponse>() {
                @Override
                public void onResponse(Call<CreateTripResponse> call, Response<CreateTripResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            Utils.hideCustomProgressDialog();
                            updateUiForCreateTrip(false);
                            Utils.showMessageToast(response.body().getMessage(), drawerActivity);
                        } else {
                            isClickRideNow = false;
                            Utils.hideCustomProgressDialog();
                            if (String.valueOf(response.body().getErrorCode()).equals(Const.ERROR_CODE_YOUR_TRIP_PAYMENT_IS_PENDING)) {
                                openPendingPaymentDialog();
                            } else {
                                Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<CreateTripResponse> call, Throwable t) {
                    AppLog.handleThrowable(TAG, t);
                }
            });


        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
    }

    /**
     * Open date picker dilaog for set date and time for schedule trip..
     */
    private void openDatePickerDialog() {


        calendar.clear();
        Date trialTime = new Date();
        calendar.setTimeZone(TimeZone.getTimeZone(drawerActivity.currentTrip.getCityTimeZone()));
        calendar.setTime(trialTime);
        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDate = calendar.get(Calendar.DAY_OF_MONTH);

        if (datePickerDialog != null && datePickerDialog.isShowing()) {
            return;
        }

        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        };
        datePickerDialog = new DatePickerDialog(drawerActivity, onDateSetListener, currentYear, currentMonth, currentDate);
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, drawerActivity.getResources().getString(R.string.text_select), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectDate(datePickerDialog.getDatePicker().getYear(), datePickerDialog.getDatePicker().getMonth(), datePickerDialog.getDatePicker().getDayOfMonth());
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 2);// add 2 days in current day
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        long now = calendar2.getTimeInMillis();
        long maxDate = calendar.getTimeInMillis();
        datePickerDialog.getDatePicker().setMinDate(now);
        datePickerDialog.getDatePicker().setMaxDate(maxDate);
        datePickerDialog.show();

    }

    private void selectDate(int year, int month, int day) {
        calendar.set(year, month, day);
        String selectedDateStr;
        long maxDate = datePickerDialog.getDatePicker().getMaxDate();
        long minDate = datePickerDialog.getDatePicker().getMinDate();
        long selectedDate = calendar.getTimeInMillis();
        selectDateLong = selectedDate;
        Date dateMax, dateMin, dateSelected;
        dateMax = new Date(maxDate);
        dateMin = new Date(minDate);
        dateSelected = new Date(selectedDate);
        int adjustMonth = month + 1;
        if (dateSelected.compareTo(dateMax) != 1 && dateSelected.compareTo(dateMin) != -1) {
            textRideLaterBtn = "";
            selectedDateStr = drawerActivity.parseContent.dateFormat.format(selectedDate);
            textRideLaterBtn = drawerActivity.parseContent.dateFormatDayDate.format(selectedDate);
            openTimePicker(day);
        } else {
            Utils.showToast(drawerActivity.getResources().getString(R.string.msg_create_request_48_hrs), drawerActivity);
        }

    }

    private void openTimePicker(final int today) {
        calendar.clear();
        Date trialTime = new Date();
        calendar.setTimeZone(TimeZone.getTimeZone(drawerActivity.currentTrip.getCityTimeZone()));
        calendar.setTime(trialTime);
        final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = calendar.get(Calendar.MINUTE);


        if (timePickerDialog != null && timePickerDialog.isShowing()) {
            return;
        }
        timePickerDialog = new TimePickerDialog(drawerActivity, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (today == calendar.get(Calendar.DAY_OF_MONTH)) {
                    if (hourOfDay == currentHour && (minute - currentMinute >= drawerActivity.preferenceHelper.getScheduledMinute())) {
                        selectedTime(view, hourOfDay, minute);

                    } else if (hourOfDay > currentHour) {
                        selectedTime(view, hourOfDay, minute);

                    } else {
                        Utils.showToast(drawerActivity.getResources().getString(R.string.msg_create_trip_for_onward_time), drawerActivity);

                    }
                } else {
                    selectedTime(view, hourOfDay, minute);
                }

            }
        }, currentHour, currentMinute, true);
        if (today == calendar.get(Calendar.DAY_OF_MONTH)) {
            calendar.set(Calendar.MINUTE, currentMinute + drawerActivity.preferenceHelper.getScheduledMinute());
            timePickerDialog.updateTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        } else {
            timePickerDialog.updateTime(currentHour, currentMinute);
        }
        timePickerDialog.show();
    }

    private void selectedTime(TimePicker view, int hourOfDay, int minute) {


        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(new Date(selectDateLong));
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.setTimeZone(TimeZone.getTimeZone(drawerActivity.currentTrip.getCityTimeZone()));
        textRideLaterBtn = textRideLaterBtn + " at " + drawerActivity.parseContent.timeFormat_am.format(calendar.getTime());


        startDateAndTimeForFutureTrip = calendar.getTimeInMillis();
        SurgeResult surgeResult = checkSurgeTimeApply(cityType.getSurgeHours(), drawerActivity.currentTrip.getServerTime(), drawerActivity.currentTrip.getCityTimeZone(), true, startDateAndTimeForFutureTrip);
        if (surgeResult.getIsSurge() == Const.TRUE) {
            openSurgePricingDialog(surgeResult.getSurgeMultiplier(), true);
        } else {
            updateUiForScheduleTrip(true);
        }


    }

    /**
     * this method used to decide is user selected city is valid for trip or not
     *
     * @param address
     */
    private void validCountryAndCityTrip(Address address) {
        if (address != null) {
            String countryName = address.getCountryName();
            /*
             * This method is use for set city and country name for filter autocomplete result.
             */
            drawerActivity.addressUtils.setCurrentCountry(countryName);
            getVehicleType(countryName, address.getLatitude(), address.getLongitude(), address.getCountryCode());
            startNearProviderScheduler();

        }

    }

    /**
     * Use for set near by provider marker on map.
     *
     * @param driverLatLng
     * @param bearing
     * @return
     */
    private Marker setDriverMaker(LatLng driverLatLng, float bearing, String providerId) {
        final Marker driveMarker = googleMap.addMarker(new MarkerOptions().position(driverLatLng).rotation(bearing));


        if (!isShowAddressView()) {
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.drawableToBitmap(AppCompatResources.getDrawable(drawerActivity, R.drawable.driver_car)));
            driveMarker.setIcon(bitmapDescriptor);
        } else {
            if (carBitmap != null) {
                driveMarker.setIcon(carBitmap);
            } else {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.drawableToBitmap(ivMapCar.getDrawable()));
                driveMarker.setIcon(bitmapDescriptor);
            }
        }

        return driveMarker;
    }


    /**
     * use for check which payment mode available for particular city and type.
     */
    private void checkPaymentModeAvailable() {
        if (drawerActivity.preferenceHelper.getPaymentCardAvailable() == Const.NOT_AVAILABLE) {
            updateCardUi(Const.CASH);
        } else if (drawerActivity.preferenceHelper.getPaymentCashAvailable() == Const.NOT_AVAILABLE) {
            updateCardUi(Const.CARD);
        } else {
            updateCardUi(drawerActivity.preferenceHelper.getPaymentCardAvailable() == Const.NOT_AVAILABLE && drawerActivity.preferenceHelper.getPaymentCashAvailable() == Const.NOT_AVAILABLE ? -1 : Const.CASH);
        }
    }

    /**
     * Use for set pickup address in AutoCompleteTextView filed.
     *
     * @param pickUpAddress
     */
    private void setPickUpAddress(String pickUpAddress) {
        drawerActivity.addressUtils.setPickupAddress(pickUpAddress);
        drawerActivity.addressUtils.setTrimedPickupAddress(Utils.trimString(pickUpAddress));
    }

    /**
     * Use for show pending payment dialog if any previous payment is pending while creting trip
     * request.
     */
    private void openPendingPaymentDialog() {
        if (pendingPayment != null && pendingPayment.isShowing()) {
            return;
        }


        pendingPayment = new CustomDialogBigLabel(drawerActivity, drawerActivity.getResources().getString(R.string.text_payment_pending_title), drawerActivity.getResources().getString(R.string.text_cancellation_charge_pending), drawerActivity.getResources().getString(R.string.text_try_again), drawerActivity.getResources().getString(R.string.text_cancel)) {
            @Override
            public void positiveButton() {
                goToPaymentScreen();
                closePendingPaymentDialog();
            }

            @Override
            public void negativeButton() {
                closePendingPaymentDialog();
            }

        };
        pendingPayment.show();
    }

    private void closePendingPaymentDialog() {
        if (pendingPayment != null) {
            pendingPayment.dismiss();
            pendingPayment = null;
        }
    }

    /**
     * Use for put pickup pin in map.
     */
    private void dropPickUpPin() {

        if (pickupMarker != null) {
            pickupMarker.setPosition(drawerActivity.addressUtils.getPickupLatLng());
        } else {
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.drawableToBitmap(ResourcesCompat.getDrawable(drawerActivity.getResources(), R.drawable.current_location, null)));
            pickupMarker = googleMap.addMarker(new MarkerOptions().position(drawerActivity.addressUtils.getPickupLatLng()).title(drawerActivity.getResources().getString(R.string.text_pick_up)).icon(bitmapDescriptor).anchor(0.5f, 0.5f));
        }
    }


    /**
     * Open dialog when fixed rate trip available.
     */
    private void openFixedRateDialog(final boolean isFutureTrip) {
        if (drawerActivity.currentTrip.isAskFroFixedRate() && drawerActivity.addressUtils.getTripStopAddressesArrayList().isEmpty() && drawerActivity.currentTrip.getVehiclePriceType() != Const.TYPE_SHARE_TRIP_PRICE) {
            if (dialogFixedRate != null && dialogFixedRate.isShowing()) {
                return;
            }
            isClickRideNow = false;
            dialogFixedRate = new CustomDialogBigLabel(drawerActivity, drawerActivity.getResources().getString(R.string.text_fixed_rate_available), drawerActivity.getResources().getString(R.string.msg_fixed_rate), drawerActivity.getResources().getString(R.string.text_yes), drawerActivity.getResources().getString(R.string.text_no)) {
                @Override
                public void positiveButton() {
                    dismiss();
                    if (!TextUtils.isEmpty(drawerActivity.addressUtils.getDestinationAddress())) {
                        if (drawerActivity.currentTrip.getEstimatedFareTotal() > 0 && drawerActivity.currentTrip.getEstimatedFareDistance() > 0) {
                            if (isFutureTrip) {
                                createFutureTrip(!TextUtils.isEmpty(drawerActivity.addressUtils.getDestinationAddress()), true);
                            } else {
                                createTrip(!TextUtils.isEmpty(drawerActivity.addressUtils.getDestinationAddress()), true, null);
                            }
                        } else {
                            Utils.showToast(drawerActivity.getResources().getString(R.string.msg_eta_not_available), drawerActivity);
                        }

                    } else {
                        Utils.showToast(drawerActivity.getResources().getString(R.string.msg_add_destination_first), drawerActivity);
                    }
                }

                @Override
                public void negativeButton() {

                    dismiss();
                    if (isFutureTrip) {
                        createFutureTrip(!TextUtils.isEmpty(drawerActivity.addressUtils.getDestinationAddress()), false);
                    } else {
                        createTrip(!TextUtils.isEmpty(drawerActivity.addressUtils.getDestinationAddress()), false, null);
                    }
                    drawerActivity.closedTripDialog();
                }
            };
            dialogFixedRate.show();
        } else {
            if (isFutureTrip) {
                createFutureTrip(!TextUtils.isEmpty(drawerActivity.addressUtils.getDestinationAddress()), false);
            } else {
                createTrip(!TextUtils.isEmpty(drawerActivity.addressUtils.getDestinationAddress()), false, null);
            }
        }
    }

    /**
     * Use for register receiver of trip status push.
     */
    private void initTripStatusReceiver() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Const.ACTION_ACCEPTED);
        intentFilter.addAction(Const.ACTION_NO_PROVIDER_FOUND);
        intentFilter.addAction(Const.ACTION_NEW_CORPORATE_REQUEST);
        intentFilter.addAction(Const.ACTION_PROVIDER_CREATE_INITIAL_TRIP);
        tripStatusReceiver = new TripStatusReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(drawerActivity);
    }

    /**
     * Shows service type description when long click on vehicle type.
     */
    private void openTypeDescriptionDialog() {
        TextView typeName, typeDescription;
        ImageView typeImage;
        final Dialog dialog = new Dialog(drawerActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_type_descreption);
        typeDescription = dialog.findViewById(R.id.tvDialogMessage);
        typeName = dialog.findViewById(R.id.tvDialogLabel);
        typeImage = dialog.findViewById(R.id.ivType);
        dialog.findViewById(R.id.btnClosed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        typeName.setText(cityType.getTypeDetails().getTypename());
        typeDescription.setText(cityType.getTypeDetails().getDescription());

        GlideApp.with(drawerActivity).load(IMAGE_BASE_URL + cityType.getTypeDetails().getTypeImageUrl()).override(200, 200).fallback(R.drawable.ellipse).into(typeImage);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void openAdDialog() {

        final Dialog dialog = new Dialog(drawerActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_banner);

        dialog.findViewById(R.id.iv_closed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    /**
     * Use for show surge price dialog before create trip request.
     *
     * @param surgeMultiplier
     * @param isFutureTrip
     */
    private void openSurgePricingDialog(double surgeMultiplier, final boolean isFutureTrip) {
        isClickRideNow = false;
        CustomDialogBigLabel dialogBigLabel = new CustomDialogBigLabel(drawerActivity, drawerActivity.getResources().getString(R.string.text_surge_pricing) + "\n" + "↑" + surgeMultiplier + "x", drawerActivity.getResources().getString(R.string.msg_surge_pricing), drawerActivity.getResources().getString(R.string.text_confirm), drawerActivity.getResources().getString(R.string.text_cancel)) {
            @Override
            public void positiveButton() {
                dismiss();
                if (isFutureTrip) {
                    updateUiForScheduleTrip(true);
                } else {
                    openFixedRateDialog(false);
                }


            }

            @Override
            public void negativeButton() {
                dismiss();
            }
        };
        dialogBigLabel.show();
    }

    /**
     * Use for initialize near by provider scheduler.
     */
    private void initNearProviderHandler() {
        incomingHandler = new IncomingHandler(MapFragment.this);
    }

    /**
     * Use for start near by provider scheduler.
     */
    public void startNearProviderScheduler() {
        if (!isSchedularStart) {
            nearByProviderSchedule = Executors.newSingleThreadScheduledExecutor();
            nearByProviderSchedule.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    Message message = incomingHandler.obtainMessage();
                    incomingHandler.sendMessage(message);
                }
            }, 0, Const.NEAR_PROVIDER_SCHEDULED_SECOND, TimeUnit.SECONDS);
            isSchedularStart = true;
        }
    }

    /**
     * Use for stop near by provider scheduler.
     */
    public void stopNearProviderScheduler() {
        if (isSchedularStart) {
            nearByProviderSchedule.shutdown();
            try {
                if (!nearByProviderSchedule.awaitTermination(60, TimeUnit.SECONDS)) {
                    nearByProviderSchedule.shutdownNow();
                    if (!nearByProviderSchedule.awaitTermination(60, TimeUnit.SECONDS))
                        AppLog.Log(TAG, "Pool did not terminate");

                }
            } catch (InterruptedException e) {
                AppLog.handleException(TAG + " 5", e);
                nearByProviderSchedule.shutdownNow();
                Thread.currentThread().interrupt();
            }
            isSchedularStart = false;
        }
    }


    private void goToPaymentScreen() {
        Intent paymentIntent = new Intent(drawerActivity, PaymentActivity.class);
        paymentIntent.putExtra(Const.IS_FROM_INVOICE, true);
        startActivityForResult(paymentIntent, Const.PENDING_PAYMENT);
    }

    private void goToDestinationSelectionActivity(boolean isAddNewStop) {
        Intent intent = new Intent(drawerActivity, DestinationSelectionActivity.class);
        intent.putExtra(Const.BUNDLE, isAddNewStop);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && llWhereToGo.getVisibility() == View.VISIBLE) {
            AddressUtilsEber.getInstance().getTripStopAddressesArrayList().clear();
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(drawerActivity, tvWhereTogo, drawerActivity.getResources().getString(R.string.toolbar_transient));
            startActivityForResult(intent, Const.DESTINATION_SELECTION, options.toBundle());
        } else {
            startActivityForResult(intent, Const.DESTINATION_SELECTION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Const.DESTINATION_SELECTION:
                checkUISavedAddressButton();
                if (resultCode == Const.RESULT_OK) {
                    goWithDestinationResult();
                }
                break;

            default:
                break;
        }
    }

    private void goWithDestinationResult() {
        if (googleMap != null) {
            googleMap.clear();
            getVehicleType(drawerActivity.addressUtils.getCurrentCountry(), drawerActivity.addressUtils.getPickupLatLng().latitude, drawerActivity.addressUtils.getPickupLatLng().longitude, drawerActivity.addressUtils.getCountryCode());
            isGetDriverETA = true;
            startNearProviderScheduler();
            setMarkerOnLocation(drawerActivity.addressUtils.getPickupLatLng(), drawerActivity.addressUtils.getDestinationLatLng(), false);
            updateUiForCreateTrip(true);
            checkFareEstimation();
        }
    }


    public void updateUiForCreateTrip(boolean isShow) {
        if (isShow) {
            drawerActivity.hideDrawerToggle(true);
            llWhereToGo.setVisibility(View.GONE);
            llMapAddress.setVisibility(View.VISIBLE);
            setTripAddresses();
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            drawerActivity.setToolbarRightSideIcon(AppCompatResources.getDrawable(drawerActivity, R.drawable.ic_filter), this);
        } else {
            resetViewAfterRequest();
            stopNearProviderScheduler();
            llMapAddress.setVisibility(View.GONE);
            llWhereToGo.setVisibility(View.VISIBLE);
            drawerActivity.addressUtils.resetAddress();
            updateUiServiceType(!vehicleTypeList.isEmpty());
            llTripStops.removeAllViews();
            updateUiServiceType(!poolVehicleTypeList.isEmpty());
            googleMap.clear();
            unRegisterAllProviderSocket();
            moveCameraFirstMyLocation(true);
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            drawerActivity.setToolbarRightSideIcon(null, null);
            isClickRideNow = false;
        }
    }

    private void resetViewAfterRequest() {
        googleMap.setPadding(0, 0, 0, 0);
        pickupMarker = null;
        tvFareEst.setText(drawerActivity.getResources().getString(R.string.text_fare_est));
        updateUiForScheduleTrip(false);
        drawerActivity.hideDrawerToggle(false);
    }

    private void setTripAddresses() {
        tvMapPickupAddress.setText(drawerActivity.addressUtils.getTrimedPickupAddress());
        if (TextUtils.isEmpty(drawerActivity.addressUtils.getTrimedDestinationAddress())) {
            tvMapDestinationAddress.setText(drawerActivity.getResources().getString(R.string.text_hint_destination));
            tvMapDestinationAddress.setTextColor(ContextCompat.getColor(drawerActivity, R.color.color_app_hint_autocomplete));
        } else {
            tvMapDestinationAddress.setTextColor(ContextCompat.getColor(drawerActivity, R.color.color_app_text));
            tvMapDestinationAddress.setText(drawerActivity.addressUtils.getTrimedDestinationAddress());
        }
        //check for car pool service type
        // add trip stops location in layout and trim address

        if (drawerActivity.addressUtils.getTripStopAddressesArrayList().size() < drawerActivity.preferenceHelper.getMultipleStopsCount() && !drawerActivity.addressUtils.getDestinationAddress().isEmpty()) {
            llAddStop.setVisibility(View.VISIBLE);
        } else {
            llAddStop.setVisibility(View.GONE);
        }

        if (!drawerActivity.addressUtils.getTripStopAddressesArrayList().isEmpty()) {
            llTripStops.removeAllViews();

            for (int i = 0; i < drawerActivity.addressUtils.getTripStopAddressesArrayList().size(); i++) {
                TripStopAddresses tripStopAddresses = drawerActivity.addressUtils.getTripStopAddressesArrayList().get(i);
                View stopView = LayoutInflater.from(drawerActivity).inflate(R.layout.layout_trip_stop, llTripStops, false);
                MyFontAutocompleteView acStopLocation = stopView.findViewById(R.id.acStopLocation);
                acStopLocation.setBackground(null);
                acStopLocation.setText(Utils.trimString(tripStopAddresses.getAddress()));
                acStopLocation.setTextColor(getResources().getColor(R.color.color_app_text));
                ImageView ivClearStopTextMap = stopView.findViewById(R.id.ivClearStopTextMap);
                ivClearStopTextMap.setVisibility(View.GONE);
                acStopLocation.setInputType(InputType.TYPE_NULL);
                acStopLocation.setClickable(true);
                acStopLocation.setFocusable(false);
                acStopLocation.setOnClickListener(v -> goToDestinationSelectionActivity(false));
                llTripStops.addView(stopView);
            }
        } else {
            llTripStops.removeAllViews();
        }
    }


    private void checkFareEstimation() {
        tvFareEst.setText(drawerActivity.getResources().getString(R.string.text_fare_est));
        if (!TextUtils.isEmpty(drawerActivity.addressUtils.getDestinationAddress()) && (vehicleTypeList.size() > 0 || poolVehicleTypeList.size() > 0)) {
            if (drawerActivity.addressUtils.getTripStopAddressesArrayList().isEmpty()) {
                getDistanceMatrix(drawerActivity.addressUtils.getPickupLatLng(), drawerActivity.addressUtils.getDestinationLatLng());
            } else {
                getDirection(drawerActivity.addressUtils.getPickupLatLng(), drawerActivity.addressUtils.getDestinationLatLng());
            }
        }
    }

    private void updateUiForScheduleTrip(boolean isSchedule) {
        if (isSchedule) {
            btnRideNow.setText(drawerActivity.getResources().getString(R.string.text_schedule_ride) + "\n" + textRideLaterBtn);
            btnRideNow.setEnabled(true);

            btnRideNow.setAlpha(1.0f);
        } else {
            btnRideNow.setText(drawerActivity.getResources().getString(R.string.text_ride_now));
        }
    }

    private void openPaymentModeDialog() {
        if (addPaymentDialog != null && addPaymentDialog.isShowing()) {
            return;
        }

        addPaymentDialog = new CustomAddPaymentDialog(drawerActivity) {
            @Override
            public void onSelect(int paymentType) {
                if (paymentType != drawerActivity.currentTrip.getPaymentMode()) {
                    checkPromoAvailability(paymentType, (ivBtnPromo.getTag() != null && (boolean) ivBtnPromo.getTag()));
                }
                closePaymentModeDialog();
            }
        };
        addPaymentDialog.checkPaymentMode(drawerActivity.preferenceHelper.getPaymentCardAvailable(), drawerActivity.preferenceHelper.getPaymentCashAvailable());
        addPaymentDialog.show();
    }

    private void closePaymentModeDialog() {
        if (addPaymentDialog != null && addPaymentDialog.isShowing()) {
            addPaymentDialog.dismiss();
            addPaymentDialog = null;
        }
    }

    private void getHomeAndWorkAddress() {
        Utils.showCustomProgressDialog(drawerActivity, "", false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());

            Call<SaveAddressResponse> call = ApiClient.getClient().create(ApiInterface.class).getSaveAddress(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<SaveAddressResponse>() {
                @Override
                public void onResponse(Call<SaveAddressResponse> call, Response<SaveAddressResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            AddressUtilsEber addressUtils = AddressUtilsEber.getInstance();
                            addressUtils.clearHomeAddress();
                            addressUtils.clearWorkAddress();
                            addressUtils.setHomeAddress(response.body().getUserAddress().getHomeAddress());
                            if (!TextUtils.isEmpty(addressUtils.getHomeAddress())) {
                                addressUtils.setTrimmedHomeAddress(Utils.trimString(addressUtils.getHomeAddress()));
                                addressUtils.setHomeLatitude(response.body().getUserAddress().getHomeLocation().get(0));
                                addressUtils.setHomeLongitude((response.body().getUserAddress().getHomeLocation().get(1)));
                            }
                            addressUtils.setWorkAddress(response.body().getUserAddress().getWorkAddress());
                            if (!TextUtils.isEmpty(addressUtils.getWorkAddress())) {
                                addressUtils.setTrimmedWorkAddress(Utils.trimString(addressUtils.getWorkAddress()));
                                addressUtils.setWorkLatitude(response.body().getUserAddress().getWorkLocation().get(0));
                                addressUtils.setWorkLongitude(response.body().getUserAddress().getWorkLocation().get(1));
                            }
                            checkUISavedAddressButton();
                        }

                    }

                }

                @Override
                public void onFailure(Call<SaveAddressResponse> call, Throwable t) {
                    AppLog.handleThrowable(TAG, t);
                }
            });

        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
    }

    @SuppressLint("RestrictedApi")
    private void checkUISavedAddressButton() {
        if (TextUtils.isEmpty(drawerActivity.addressUtils.getHomeAddress())) {
            llHomeAddress.setVisibility(View.GONE);
        } else {
            scaleView(llHomeAddress);
        }
        if (TextUtils.isEmpty(drawerActivity.addressUtils.getWorkAddress())) {
            llWorkAddress.setVisibility(View.GONE);
        } else {
            scaleView(llWorkAddress);
        }
        if (TextUtils.isEmpty(drawerActivity.addressUtils.getWorkAddress()) || TextUtils.isEmpty(drawerActivity.addressUtils.getHomeAddress())) {
            scaleView(llAddAddress);
        } else {
            llAddAddress.setVisibility(View.GONE);
        }
    }

    /**
     * Use for scale animation on view.
     *
     * @param view
     */
    private void scaleView(View view) {
        if (view.getVisibility() == View.GONE) {
            Animation anim = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(500);
            view.startAnimation(anim);
            view.requestLayout();
            view.setVisibility(View.VISIBLE);
        }
    }

    private void getSpeakingLanguages() {
        if (drawerActivity.currentTrip.getSpeakingLanguages().isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            Call<LanguageResponse> call = ApiClient.getClient().create(ApiInterface.class).getLanguageForTrip(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<LanguageResponse>() {
                @Override
                public void onResponse(Call<LanguageResponse> call, Response<LanguageResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            drawerActivity.currentTrip.setSpeakingLanguages((ArrayList<Language>) response.body().getLanguages());
                            speakingLanguageAdaptor.notifyDataSetChanged();
                        }
                        llSelectLanguage.setVisibility(drawerActivity.currentTrip.getSpeakingLanguages().isEmpty() ? View.GONE : View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<LanguageResponse> call, Throwable t) {
                    AppLog.handleThrowable(TAG, t);
                }
            });
        }
    }


    private void openRequestFilter() {
        if (llRequestFilter.getVisibility() == View.GONE) {
            cbMale.setChecked(false);
            cbFemale.setChecked(false);
            cbHotspot.setChecked(false);
            cbBabySeat.setChecked(false);
            cbHandicap.setChecked(false);
            for (String s : selectedGender) {
                if (TextUtils.equals(Const.Gender.MALE, s)) {
                    cbMale.setChecked(true);
                }
                if (TextUtils.equals(Const.Gender.FEMALE, s)) {
                    cbFemale.setChecked(true);
                }
            }
            for (String s : selectedAccessibility) {
                if (TextUtils.equals(Const.Accessibility.BABY_SEAT, s)) {
                    cbBabySeat.setChecked(true);
                }
                if (TextUtils.equals(Const.Accessibility.HOTSPOT, s)) {
                    cbHotspot.setChecked(true);
                }
                if (TextUtils.equals(Const.Accessibility.HANDICAP, s)) {
                    cbHandicap.setChecked(true);
                }
            }
            for (String s : selectedLanguage) {
                for (Language language : drawerActivity.currentTrip.getSpeakingLanguages()) {
                    if (TextUtils.equals(s, language.getId())) {
                        language.setSelected(true);
                    }
                }
            }
            speakingLanguageAdaptor.notifyDataSetChanged();
            llRequestFilter.setVisibility(View.VISIBLE);
        } else {
            llRequestFilter.setVisibility(View.GONE);
        }

    }

    private void saveRequestFilter() {
        selectedGender.clear();
        selectedAccessibility.clear();
        selectedLanguage.clear();
        if (cbMale.isChecked()) {
            selectedGender.add(Const.Gender.MALE);
        }
        if (cbFemale.isChecked()) {
            selectedGender.add(Const.Gender.FEMALE);
        }
        if (cbHandicap.isChecked()) {
            selectedAccessibility.add(Const.Accessibility.HANDICAP);
        }
        if (cbHotspot.isChecked()) {
            selectedAccessibility.add(Const.Accessibility.HOTSPOT);
        }
        if (cbBabySeat.isChecked()) {
            selectedAccessibility.add(Const.Accessibility.BABY_SEAT);
        }
        for (Language language : drawerActivity.currentTrip.getSpeakingLanguages()) {
            if (language.isSelected()) {
                selectedLanguage.add(language.getId());
            }
        }
        llRequestFilter.setVisibility(View.GONE);
    }

    /**
     * this method is used for get formatted address from location using Geocode Api from Google
     *
     * @param location
     */
    private void getGeocodeSourcesAddressFromLocation(Location location) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Const.google.LAT_LNG, location.getLatitude() + "," + location.getLongitude());
        hashMap.put(Const.google.KEY, drawerActivity.preferenceHelper.getGoogleServerKey());
        ApiInterface apiInterface = new ApiClient().changeApiBaseUrl(Const.GOOGLE_API_URL).create(ApiInterface.class);
        Call<ResponseBody> bodyCall = apiInterface.getGoogleGeocode(hashMap);
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (ParseContent.getInstance().isSuccessful(response)) {
                    Utils.hideCustomProgressDialog();
                    HashMap<String, String> hashMapDest = null;
                    try {
                        hashMapDest = drawerActivity.parseContent.parsGeocode(response.body().string());
                        if (hashMapDest != null) {
                            Address address = new Address(new Locale("en_US"));
                            address.setLocality(hashMapDest.get(Const.google.LOCALITY));
                            address.setCountryName(hashMapDest.get(Const.google.COUNTRY));
                            address.setLatitude((Double.parseDouble(hashMapDest.get(Const.google.LAT))));
                            address.setLongitude(Double.parseDouble(hashMapDest.get(Const.google.LNG)));
                            address.setAdminArea(hashMapDest.get(Const.google.FORMATTED_ADDRESS));
                            address.setSubAdminArea(hashMapDest.get(Const.google.ADMINISTRATIVE_AREA_LEVEL_2));
                            String sourcesAddress = hashMapDest.get(Const.google.FORMATTED_ADDRESS);
                            validCountryAndCityTrip(address);
                            setPickUpAddress(sourcesAddress);
                            AddressUtilsEber.getInstance().setCountryCode(hashMapDest.get(Const.google.COUNTRY_CODE));
                            drawerActivity.setPlaceFilter(AddressUtilsEber.getInstance().getCountryCode());
                        }
                    } catch (IOException e) {
                        AppLog.handleThrowable(TAG, e);
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                AppLog.handleThrowable(TAG, t);
            }
        });
    }


    @Override
    public void onCancelTrip(CancelTripResponse response) {
        startNearProviderScheduler();
        if (response.isSuccess()) {
            drawerActivity.closedTripDialog();
            Utils.showMessageToast(response.getMessage(), drawerActivity);
        } else {
            drawerActivity.closedTripDialog();
            Utils.showErrorToast(response.getErrorCode(), drawerActivity);
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
                        TripResponse tripResponse = response.body();
                        if (drawerActivity.parseContent.parseTripStatus(tripResponse)) {
                            stopNearProviderScheduler();
                            switch (drawerActivity.currentTrip.getIsProviderAccepted()) {
                                case Const.ProviderStatus.PROVIDER_STATUS_ACCEPTED:
                                    drawerActivity.closedTripDialog();
                                    goToTripFragment();
                                    break;
                                case Const.ProviderStatus.PROVIDER_STATUS_REJECTED:
                                    drawerActivity.openTripDialog(MapFragment.this);
                                    break;
                                case Const.ProviderStatus.PROVIDER_STATUS_RESPONSE_PENDING:
                                    if (TextUtils.isEmpty(AddressUtilsEber.getInstance().getPickupAddress())) {
                                        setTripData();
                                    }
                                    drawerActivity.openTripDialog(MapFragment.this);
                                    break;
                                default:

                                    break;
                            }

                        } else {
                            drawerActivity.closedTripDialog();
                            openCorporateRequestDialog();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TripResponse> call, Throwable t) {
                    AppLog.handleThrowable(MainDrawerActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
    }

    private void initVehicleTypeBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(llRequestView);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (llWhereToGo.getVisibility() == View.GONE) {
                        updateUiForCreateTrip(false);
                    }
                    setMapPadding(false);
                    llHomeAddress.setClickable(true);
                    llWorkAddress.setClickable(true);
                    llAddAddress.setClickable(true);
                    tvCheckOurDelivery.setClickable(true);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    setMapPadding(true);
                    llHomeAddress.setClickable(false);
                    llWorkAddress.setClickable(false);
                    llAddAddress.setClickable(false);
                    tvCheckOurDelivery.setClickable(false);
                }
                ivTargetLocation.setEnabled(BottomSheetBehavior.STATE_COLLAPSED == newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float offset = 1 - slideOffset;
                llAddAddress.setScaleX(offset);
                llAddAddress.setScaleY(offset);
                llHomeAddress.setScaleX(offset);
                llHomeAddress.setScaleY(offset);
                llWorkAddress.setScaleX(offset);
                llWorkAddress.setScaleY(offset);
                ivTargetLocation.setScaleY(offset);
                ivTargetLocation.setScaleX(offset);
                tvCheckOurDelivery.setScaleY(offset);
            }
        });
    }

    private void setMapPadding(boolean isEnable) {
        if (isEnable) {
            int[] location = new int[2];
            llRequestView.getLocationOnScreen(location);
            int mapPadding = getResources().getDisplayMetrics().heightPixels - location[1];
            this.googleMap.setPadding(0, mapPadding, 0, mapPadding);
        } else {
            this.googleMap.setPadding(0, 0, 0, 0);
        }

    }

    private SurgeResult checkSurgeTimeApply(List<SurgeTime> surgeTime, String serverTime, String timeZoneString, boolean isScheduleBooking, long scheduleBookingTime) {

        SurgeResult surgeResult = new SurgeResult();
        if (surgeTime != null) {

            Calendar serverTimeCalendar = Calendar.getInstance();
            if (isScheduleBooking) {
                serverTimeCalendar.setTimeInMillis(scheduleBookingTime);
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Const.DATE_TIME_FORMAT_WEB, Locale.US);
                Date date = null;
                try {
                    date = simpleDateFormat.parse(serverTime); // NOTE: if server times comes null then use current time to process continue.
                } catch (Exception e) {
                    AppLog.handleException(TAG, e);
                    date = new Date();
                }
                TimeZone timeZone = TimeZone.getTimeZone(timeZoneString);
                serverTimeCalendar.setTimeInMillis(date.getTime() + (timeZone.getOffset(date.getTime())));
            }
            boolean isSurge = false;
            int dayOfWeek = serverTimeCalendar.get(Calendar.DAY_OF_WEEK) - 1;
            for (SurgeTime timeItem : surgeTime) {
                if (timeItem.getDay() == dayOfWeek) {
                    if (timeItem.isSurge()) {
                        if (timeItem.getDayTime().isEmpty()) {
                            isSurge = false;
                        } else {
                            for (DayTime dayTime : timeItem.getDayTime()) {

                                String[] open = dayTime.getStartTime().split(":");
                                String[] closed = dayTime.getEndTime().split(":");

                                Calendar openCalendar = Calendar.getInstance();
                                openCalendar.setTimeInMillis(serverTimeCalendar.getTimeInMillis());
                                openCalendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(open[0]));
                                openCalendar.set(Calendar.MINUTE, Integer.valueOf(open[1]));
                                openCalendar.set(Calendar.SECOND, 0);
                                Calendar closedCalendar = Calendar.getInstance();
                                closedCalendar.setTimeInMillis(serverTimeCalendar.getTimeInMillis());
                                closedCalendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(closed[0]));
                                closedCalendar.set(Calendar.MINUTE, Integer.valueOf(closed[1]));
                                closedCalendar.set(Calendar.SECOND, 0);
                                if (serverTimeCalendar.after(openCalendar) && serverTimeCalendar.before(closedCalendar)) {
                                    surgeResult.setSurgeMultiplier(dayTime.getMultiplier());
                                    isSurge = true;
                                    break;
                                } else {
                                    isSurge = false;
                                }
                            }
                        }
                    } else {
                        isSurge = false;
                        break;
                    }
                }

            }
            if (isSurge) {
                surgeResult.setSurgeMultiplier(cityType.getRichAreaSurgeMultiplier() > surgeResult.getSurgeMultiplier() ? cityType.getRichAreaSurgeMultiplier() : surgeResult.getSurgeMultiplier());
                surgeResult.setIsSurge(surgeResult.getSurgeMultiplier() != 1.0 && surgeResult.getSurgeMultiplier() > 0.0 ? Const.TRUE : Const.FALSE);
            } else if (cityType.getRichAreaSurgeMultiplier() != 1.0 && cityType.getRichAreaSurgeMultiplier() > 0.0) {
                surgeResult.setSurgeMultiplier(cityType.getRichAreaSurgeMultiplier());
                surgeResult.setIsSurge(Const.TRUE);
            } else {
                surgeResult.setIsSurge(Const.FALSE);
            }

            if (CurrentTrip.getInstance().getTripType() == Const.TripType.AIRPORT || CurrentTrip.getInstance().getTripType() == Const.TripType.CITY || CurrentTrip.getInstance().getTripType() == Const.TripType.ZONE) {
                surgeResult.setIsSurge(Const.FALSE);
            }
        }
        return surgeResult;
    }

    private void selectVehiclePriceType(int vehiclePriceType) {
        if (vehiclePriceType == Const.TYPE_NORMAL_PRICE) {
            llFareEstimate.setVisibility(View.VISIBLE);
            llRentPackages.setVisibility(View.GONE);
            llRideNow.setVisibility(View.VISIBLE);
            ivRideLater.setVisibility(View.VISIBLE);
            btnSelectRent.setVisibility(View.GONE);
            tvEberNow.setTextColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_black, null));
            tvEberRental.setTextColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_label, null));
            tvEberRideShare.setTextColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_label, null));
        } else if (vehiclePriceType == Const.TYPE_RENTAL_PRICE) {
            llFareEstimate.setVisibility(View.GONE);
            llRentPackages.setVisibility(View.VISIBLE);
            llRideNow.setVisibility(View.GONE);
            btnSelectRent.setVisibility(View.VISIBLE);
            tvEberNow.setTextColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_label, null));
            tvEberRental.setTextColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_black, null));
            tvEberRideShare.setTextColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_label, null));
        } else {
            llFareEstimate.setVisibility(View.VISIBLE);
            llRentPackages.setVisibility(View.GONE);
            llRideNow.setVisibility(View.VISIBLE);
            ivRideLater.setVisibility(View.GONE);
            btnSelectRent.setVisibility(View.GONE);
            tvEberNow.setTextColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_label, null));
            tvEberRental.setTextColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_label, null));
            tvEberRideShare.setTextColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_black, null));
            updateUiForScheduleTrip(false);
        }
    }

    private int selectVehicleType(int position, int vehiclePriceType, boolean selectDefault) {
        if (vehiclePriceType == Const.TYPE_SHARE_TRIP_PRICE) {
            if (!poolVehicleTypeList.isEmpty()) {
                int rentalCount = 0;
                int size = poolVehicleTypeList.size();
                int defaultSelect = -1;
                for (int i = 0; i < size; i++) {
                    CityType type = poolVehicleTypeList.get(i);
                    if (type.getRentalTypes() != null && !type.getRentalTypes().isEmpty()) {
                        type.setVehiclePriceType(Const.TYPE_RENTAL_PRICE);
                        rentalCount++;
                    } else {
                        type.setVehiclePriceType(Const.TYPE_SHARE_TRIP_PRICE);
                    }
                    type.isSelected = false;

                    if (type.getTypeDetails().isDefaultSelected()) {
                        defaultSelect = i;
                    }
                }
                if (selectDefault) {
                    if (defaultSelect != -1) {
                        position = defaultSelect;
                    } else {
                        if (vehiclePriceType == Const.TYPE_SHARE_TRIP_PRICE) {
                            position = poolVehicleTypeList.size() / 2;
                        } else {
                            position = rentalCount / 2;
                        }
                    }
                }
                poolVehicleTypeList.get(position).isSelected = true;
                setVehicleData(position);
                vehicleSelectAdapter.setShowRentalTypeOnly(vehiclePriceType == Const.TYPE_RENTAL_PRICE);
                CityType cityType = poolVehicleTypeList.get(position);
                if (cityType.getRentalTypes() != null && !cityType.getRentalTypes().isEmpty() && vehiclePriceType == Const.TYPE_RENTAL_PRICE) {
                    tvRentalPackage.setText(String.valueOf(cityType.getRentalTypes().size()));
                } else {
                    tvRentalPackage.setText("0");
                }

                vehicleSelectAdapter.notifyDataSetChanged();
                int finalPosition = position;
                rcvMapVehicleTyp.post(() -> rcvMapVehicleTyp.smoothScrollToPosition(finalPosition));
                return rentalCount;
            }
        } else {
            if (!vehicleTypeList.isEmpty()) {
                int rentalCount = 0;
                int size = vehicleTypeList.size();
                int defaultSelect = -1;
                for (int i = 0; i < size; i++) {
                    CityType type = vehicleTypeList.get(i);
                    if (type.getRentalTypes() != null && !type.getRentalTypes().isEmpty()) {
                        type.setVehiclePriceType(Const.TYPE_RENTAL_PRICE);
                        rentalCount++;
                    } else {
                        type.setVehiclePriceType(Const.TYPE_NORMAL_PRICE);
                    }
                    type.isSelected = false;

                    if (type.getTypeDetails().isDefaultSelected()) {
                        defaultSelect = i;
                    }
                }
                if (selectDefault) {
                    if (defaultSelect != -1) {
                        position = defaultSelect;
                    } else {
                        if (vehiclePriceType == Const.TYPE_NORMAL_PRICE) {
                            position = vehicleTypeList.size() / 2;
                        } else {
                            position = rentalCount / 2;
                        }
                    }
                }
                vehicleTypeList.get(position).isSelected = true;
                setVehicleData(position);
                vehicleSelectAdapter.setShowRentalTypeOnly(vehiclePriceType == Const.TYPE_RENTAL_PRICE);
                CityType cityType = vehicleTypeList.get(position);
                if (cityType.getRentalTypes() != null && !cityType.getRentalTypes().isEmpty() && vehiclePriceType == Const.TYPE_RENTAL_PRICE) {
                    tvRentalPackage.setText(String.valueOf(cityType.getRentalTypes().size()));
                } else {
                    tvRentalPackage.setText("0");
                }

                vehicleSelectAdapter.notifyDataSetChanged();
                int finalPosition = position;
                rcvMapVehicleTyp.post(() -> rcvMapVehicleTyp.smoothScrollToPosition(finalPosition));
                return rentalCount;
            }
        }
        return 0;
    }

    private void openVehicleTypeRentalPackagesDialog(final ArrayList<CityTypeRental> rentalList) {
        vehicleTypeRentalDialog = new Dialog(drawerActivity);
        vehicleTypeRentalDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        vehicleTypeRentalDialog.setContentView(R.layout.dialog_rental_packages);
        RecyclerView recyclerView = vehicleTypeRentalDialog.findViewById(R.id.rcvRentalPackages);
        final RentalPackagesAdapter rentalPackagesAdapter = new RentalPackagesAdapter(rentalList);
        recyclerView.setLayoutManager(new LinearLayoutManager(vehicleTypeRentalDialog.getContext()));
        recyclerView.setAdapter(rentalPackagesAdapter);
        vehicleTypeRentalDialog.findViewById(R.id.btnRentNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rentalPackagesAdapter.getRentSelectedPosition() == RentalPackagesAdapter.INVALID_POSITION) {
                    Utils.showToast(drawerActivity.getResources().getString(R.string.msg_plz_select_package), drawerActivity);
                } else {
                    vehicleTypeRentalDialog.dismiss();
                    createTrip(!TextUtils.isEmpty(drawerActivity.addressUtils.getDestinationAddress()), false, rentalList.get(rentalPackagesAdapter.getRentSelectedPosition()).getId());

                }
            }
        });
        vehicleTypeRentalDialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vehicleTypeRentalDialog.dismiss();
            }
        });
        WindowManager.LayoutParams params = vehicleTypeRentalDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        vehicleTypeRentalDialog.getWindow().setAttributes(params);
        vehicleTypeRentalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        vehicleTypeRentalDialog.show();
        enableRentButton(btnRideNow.isEnabled());
    }

    private void enableRentButton(boolean enable) {
        if (vehicleTypeRentalDialog != null && vehicleTypeRentalDialog.isShowing()) {
            Button btnRentNow = vehicleTypeRentalDialog.findViewById(R.id.btnRentNow);
            btnRentNow.setEnabled(enable);
            btnRentNow.setAlpha(enable ? 1.0f : 0.5f);
        }

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
        if (isVisible() && tripDetailOnSocket.isTripUpdated()) {
            getTripStatus();
        }
    }

    private void openCorporateRequestDialog() {
        final CorporateDetail corporateDetail = CurrentTrip.getInstance().getCorporateDetail();
        if (corporateRequestDialog != null && corporateRequestDialog.isShowing()) {
            return;
        }
        if (corporateDetail != null && corporateDetail.getStatus() == Const.FALSE) {
            corporateRequestDialog = new Dialog(drawerActivity);
            corporateRequestDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            corporateRequestDialog.setContentView(R.layout.dialog_corporate_request);
            TextView tvCorporateName = corporateRequestDialog.findViewById(R.id.tvCorporateName);
            TextView tvCorporateNumber = corporateRequestDialog.findViewById(R.id.tvCorporatePhone);
            tvCorporateName.setText(corporateDetail.getName());
            tvCorporateNumber.setText(corporateDetail.getPhone());
            tvCorporateNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.openCallChooser(drawerActivity, corporateDetail.getPhone());
                }
            });
            corporateRequestDialog.findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    respondsCorporateRequest(true, corporateDetail.getId());
                }
            });
            corporateRequestDialog.findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    respondsCorporateRequest(false, corporateDetail.getId());
                }
            });
            corporateRequestDialog.setCancelable(false);
            Window window = corporateRequestDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = corporateRequestDialog.getWindow().getAttributes();
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(layoutParams);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            if (!drawerActivity.isFinishing()) {
                corporateRequestDialog.show();
            }
        }
    }

    private void closedCorporateRequestDialog() {
        if (corporateRequestDialog != null && corporateRequestDialog.isShowing()) {
            corporateRequestDialog.dismiss();
        }
    }

    private void respondsCorporateRequest(final boolean isAccepted, String corporateId) {

        JSONObject jsonObject = new JSONObject();
        try {
            Utils.showCustomProgressDialog(drawerActivity, "", false, null);
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.IS_ACCEPTED, isAccepted);
            jsonObject.put(Const.Params.CORPORATE_ID, corporateId);

            Call<IsSuccessResponse> responseCall = ApiClient.getClient().create(ApiInterface.class).respondsCorporateRequest(ApiClient.makeJSONRequestBody(jsonObject));
            responseCall.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (drawerActivity.parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            CurrentTrip.getInstance().getCorporateDetail().setStatus(isAccepted ? Const.TRUE : Const.FALSE);
                            closedCorporateRequestDialog();
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
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
            AppLog.handleException(TAG, e);
            Utils.hideCustomProgressDialog();
        }

    }

    private void updateCorporateUi(boolean isCorporateAvailable) {
        cbPayByCorporate.setChecked(false);
        cbPayByCorporate.setVisibility(isCorporateAvailable ? View.VISIBLE : View.GONE);
    }

    private void goToPromotionActivity() {
        Intent intent = new Intent(drawerActivity, PromotionActivity.class);
        startActivity(intent);
        drawerActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void registerProviderSocket(String providerId) {
        providerId = String.format("'%s'", providerId);
        SocketHelper.getInstance().getSocket().on(providerId, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args != null) {
                    final JSONObject jsonObject = (JSONObject) args[0];
                    final ProviderLocation providerLocation = ApiClient.getGsonInstance().fromJson(jsonObject.toString(), ProviderLocation.class);
                    drawerActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Marker marker = nearByProviderMarkerHashMap.get(providerLocation.getProviderId());
                            if (providerLocation.getLocation() != null && !providerLocation.getLocation().isEmpty() && providerLocation.getLocation().get(0) != 0 && providerLocation.getLocation().get(1) != 0) {
                                animateMarkerToGB(marker, new LatLng(providerLocation.getLocation().get(0), providerLocation.getLocation().get(1)), new LatLngInterpolator.Linear(), (float) providerLocation.getBearing());
                            }
                        }
                    });
                }
            }
        });


    }

    private void unRegisterProviderSocket(String providerId) {
        providerId = String.format("'%s'", providerId);
        SocketHelper.getInstance().getSocket().off(providerId);
    }

    private void registerAllProviderSocket() {
        for (Provider provider : nearByProvider) {
            registerProviderSocket(provider.getId());
            List<Double> providerLocation = provider.getProviderLocation();
            LatLng latLng = new LatLng(providerLocation.get(0), providerLocation.get(1));
            Marker marker = setDriverMaker(latLng, (float) provider.getBearing(), provider.getId());
            driverMarkerList.add(marker);
            nearByProviderMarkerHashMap.put(provider.getId(), marker);
        }
    }

    private void unRegisterAllProviderSocket() {
        for (Provider provider : nearByProvider) {
            unRegisterProviderSocket(provider.getId());
        }
    }

    private void animateMarkerToGB(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator, final float bearing) {
        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(finalPosition.latitude, finalPosition.longitude);

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            float rotation = getBearing(startPosition, endPosition);
            if (!Float.isNaN(rotation)) {
                marker.setRotation(rotation);
            }
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

    private boolean isShowAddressView() {
        return llMapAddress.getVisibility() == View.VISIBLE;
    }

    private void openPromoApplyDialog() {
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
                closePromoApplyDialog();
            }

            @Override
            public void clickOnText() {
            }
        };

        promoDialog.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        promoDialog.show();
    }

    private void closePromoApplyDialog() {
        if (promoDialog != null && promoDialog.isShowing()) {
            InputMethodManager imm = (InputMethodManager) drawerActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = promoDialog.getCurrentFocus();
            if (view == null) {
                view = new View(drawerActivity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            promoDialog.dismiss();
        }
    }

    /**
     * Call service when promocode enter.
     *
     * @param promoCode
     */
    private void promoCodeApply(String promoCode) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.PROMO_CODE, promoCode);
            jsonObject.put(Const.Params.USER_ID, drawerActivity.preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, drawerActivity.preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.PAYMENT_MODE, drawerActivity.currentTrip.getPaymentMode());

            if (cityType != null) {
                jsonObject.put(Const.Params.CITY_ID, cityType.getCityid());
                jsonObject.put(Const.Params.COUNTRY_ID, cityType.getCountryid());
            } else {
                Utils.showToast(drawerActivity.getResources().getString(R.string.msg_plz_select_type), drawerActivity);
                return;
            }
            Utils.showCustomProgressDialog(drawerActivity, drawerActivity.getResources().getString(R.string.msg_waiting_for_apply_promo), false, null);
            Call<PromoResponse> call = ApiClient.getClient().create(ApiInterface.class).applyPromoCode(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<PromoResponse>() {
                @Override
                public void onResponse(Call<PromoResponse> call, Response<PromoResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            closePromoApplyDialog();
                            Utils.showMessageToast(response.body().getMessage(), drawerActivity);
                            updateUiAfterPromo(true);
                            promoId = response.body().getPromoId();
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), drawerActivity);
                        }
                        Utils.hideCustomProgressDialog();
                    }
                }

                @Override
                public void onFailure(Call<PromoResponse> call, Throwable t) {
                    AppLog.handleThrowable(TripFragment.class.getSimpleName(), t);
                }
            });

        } catch (JSONException e) {
            AppLog.handleException(TAG, e);
        }
    }

    private void updateUiAfterPromo(boolean isPromoApplied) {
        if (cityDetail != null) {
            ivBtnPromo.setVisibility(View.VISIBLE);
            if (drawerActivity.currentTrip.getPaymentMode() == Const.CASH && cityDetail.getIsPromoApplyForCash() == Const.FALSE) {
                ivBtnPromo.setVisibility(View.GONE);
                ivBtnPromo.setTag(false);
                promoId = null;
            } else if (drawerActivity.currentTrip.getPaymentMode() == Const.CARD && cityDetail.getIsPromoApplyForCard() == Const.FALSE) {
                ivBtnPromo.setVisibility(View.GONE);
                ivBtnPromo.setTag(false);
                promoId = null;
            } else if (drawerActivity.currentTrip.getPaymentMode() == Const.CASH && cityDetail.getIsPromoApplyForCash() == Const.TRUE && isPromoApplied) {
                ivBtnPromo.setText(String.format("%s %s", drawerActivity.getResources().getString(R.string.text_promo_applied), drawerActivity.getResources().getString(R.string.text_tap_to_remove)));
                ivBtnPromo.setTag(true);
            } else if (drawerActivity.currentTrip.getPaymentMode() == Const.CARD && cityDetail.getIsPaymentModeCard() == Const.TRUE && isPromoApplied) {
                ivBtnPromo.setText(String.format("%s %s", drawerActivity.getResources().getString(R.string.text_promo_applied), drawerActivity.getResources().getString(R.string.text_tap_to_remove)));
                ivBtnPromo.setTag(true);
            } else {
                ivBtnPromo.setText(drawerActivity.getResources().getString(R.string.text_have_promocode));
                ivBtnPromo.setTag(false);
                promoId = null;
            }
        } else {
            ivBtnPromo.setVisibility(View.GONE);
            ivBtnPromo.setTag(false);
            promoId = null;
        }
    }

    private void checkPromoAvailability(int paymentType, boolean isPromoApplied) {
        if (paymentType == Const.CASH && cityDetail.getIsPromoApplyForCash() == Const.FALSE && isPromoApplied) {
            openPromoVoidDialog(paymentType);
        } else if (paymentType == Const.CARD && cityDetail.getIsPromoApplyForCard() == Const.FALSE && isPromoApplied) {
            openPromoVoidDialog(paymentType);
        } else {
            drawerActivity.currentTrip.setPaymentMode(paymentType);
            updateUiAfterPromo(isPromoApplied);
            updateCardUi(paymentType);
        }
    }

    private void openPromoVoidDialog(int paymentType) {
        CustomDialogBigLabel promoVoidDialog = new CustomDialogBigLabel(drawerActivity, drawerActivity.getResources().getString(R.string.text_attention), drawerActivity.getResources().getString(R.string.msg_promo_void), drawerActivity.getResources().getString(R.string.text_continue_or_next), drawerActivity.getResources().getString(R.string.text_cancel)) {
            @Override
            public void positiveButton() {
                dismiss();
                drawerActivity.currentTrip.setPaymentMode(paymentType);
                updateUiAfterPromo(false);
                updateCardUi(paymentType);
            }

            @Override
            public void negativeButton() {
                dismiss();
            }
        };
        promoVoidDialog.show();
    }

    private void updateUiDriverETA(boolean isShow) {
        if (isShow) {
            llETA.setVisibility(View.VISIBLE);
            divETA.setVisibility(View.VISIBLE);
        } else {
            llETA.setVisibility(View.GONE);
            divETA.setVisibility(View.GONE);
        }

    }

    private void goToTripFragment() {
        if (isAdded()) {
            drawerActivity.goToTripFragment();
        }
    }

    private static class IncomingHandler extends Handler {

        MapFragment mapFragment;

        IncomingHandler(MapFragment mapFragment) {
            this.mapFragment = mapFragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mapFragment.getNearByProvider();

        }
    }

    /**
     * This class is used to get pickup address from location or LatLng  on map and set
     * address on
     * AutoCompleteTextView filed
     */
    private class GetPickUpAddressFromLocation extends AsyncTask<LatLng, Integer, Address> {

        @Override
        protected Address doInBackground(LatLng... latLngs) {
            Geocoder gCoder = new Geocoder(drawerActivity, new Locale("en_US"));
            LatLng latLng = latLngs[0];
            try {
                final List<Address> list = gCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (list != null && !list.isEmpty()) {
                    return list.get(0);
                }
            } catch (IOException e) {
                AppLog.handleException(TAG, e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Address address) {
            super.onPostExecute(address);
            StringBuilder sb = new StringBuilder();
            if (address != null) {
                if (address.getMaxAddressLineIndex() > 0) {
                    int addressLineSize = address.getMaxAddressLineIndex();
                    for (int i = 0; i < addressLineSize; i++) {
                        sb.append(address.getAddressLine(i)).append(",").append("\n");
                    }
                    sb.append(address.getCountryName());
                } else {
                    sb.append(address.getAddressLine(0));
                }
                String strAddress = sb.toString();
                strAddress = strAddress.replace(",null", "");
                strAddress = strAddress.replace("null", "");
                strAddress = strAddress.replace("Unnamed", "");
                if (!TextUtils.isEmpty(strAddress)) {
                    setPickUpAddress(strAddress);
                    validCountryAndCityTrip(address);
                }
                AddressUtilsEber.getInstance().setCountryCode(address.getCountryCode());
                drawerActivity.setPlaceFilter(AddressUtilsEber.getInstance().getCountryCode());
            } else {
                getGeocodeSourcesAddressFromLocation(drawerActivity.currentLocation);
            }
        }
    }

    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private final GestureDetector gestureDetector;
        private final com.elluminati.eber.interfaces.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    /**
     * Use for handle push after create trip.
     */
    public class TripStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!drawerActivity.isFinishing() && isAdded()) {
                switch (intent.getAction()) {
                    case Const.ACTION_ACCEPTED:
                        drawerActivity.closedTripDialog();
                        goToTripFragment();
                        break;
                    case Const.ACTION_NO_PROVIDER_FOUND:
                        drawerActivity.closedTripDialog();
                        Utils.showToast(drawerActivity.getResources().getString(R.string.message_no_provider_found), drawerActivity);
                        //openRequestAgainDialog();
                        break;
                    case Const.ACTION_NEW_CORPORATE_REQUEST:
                        if (intent.getExtras() != null) {
                            String data = intent.getStringExtra(Const.Params.EXTRA_PARAM);
                            CorporateDetail corporateDetail = new Gson().fromJson(data, CorporateDetail.class);
                            if (corporateDetail != null) {
                                CurrentTrip.getInstance().setCorporateDetail(corporateDetail);
                                if (!TextUtils.isEmpty(corporateDetail.getId())) {
                                    openCorporateRequestDialog();
                                }
                            }
                        }
                        break;
                    case Const.ACTION_PROVIDER_CREATE_INITIAL_TRIP:
                        goToTripFragment();
                        break;
                    default:
                        break;
                }
            }
        }
    }

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
}
