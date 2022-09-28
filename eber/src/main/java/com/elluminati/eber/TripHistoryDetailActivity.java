package com.elluminati.eber;

import static com.elluminati.eber.utils.Const.IMAGE_BASE_URL;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.adapter.InvoiceAdapter;
import com.elluminati.eber.adapter.UserAdapter;
import com.elluminati.eber.components.CustomEventMapView;
import com.elluminati.eber.components.MyFontAutocompleteView;
import com.elluminati.eber.components.MyFontButton;
import com.elluminati.eber.components.MyFontEdittextView;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.components.MyFontTextViewMedium;
import com.elluminati.eber.fragments.FeedbackFragment;
import com.elluminati.eber.models.datamodels.CityType;
import com.elluminati.eber.models.datamodels.Provider;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.datamodels.Trip;
import com.elluminati.eber.models.datamodels.TripStopAddresses;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.models.responsemodels.TripHistoryDetailResponse;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.GlideApp;
import com.elluminati.eber.utils.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripHistoryDetailActivity extends BaseAppCompatActivity implements OnMapReadyCallback {

    private static boolean isMapTouched = false;
    private final int[] mapLocation = new int[2];
    private TextView tvHistoryDetailSrc, tvHistoryDetailDest, tvHistoryDetailTripTime, tvHistoryDetailDistance, tvHistoryDetailCost, tvHistoryTripDate, tvHistoryTripCreateTime, tvHistoryDetailDriverName, tvHistoryTripNumber;
    private ImageView ivHistoryDetailPhotoDialog, ivFullScreen;
    private String tripId;
    private String unit;
    private LinearLayout llHistoryRate, llDetails, llFromAndTo, llTimeAndDistance;
    private CustomEventMapView mapView;
    private GoogleMap googleMap;
    private ArrayList<LatLng> markerList;
    private Marker pickUpMarker, destinationMarker;
    private PolylineOptions currentPathPolylineOptions;
    private FrameLayout mapFrameLayout;
    /*
     * FeedBack Dialog components
     */
    private Dialog feedBackDialog;
    private MyFontTextView tvDriverNameDialog;
    private RatingBar feedbackDialogRatingBar;
    private MyFontEdittextView etDialogComment;
    private MyFontButton btnDialogSubmitFeedback;
    private CityType tripService;
    private Trip trip;
    private NumberFormat currencyFormat;
    private LinearLayout llTripStops;

    private static void setMapTouched(boolean isTouched) {
        isMapTouched = isTouched;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history_detail);
        initToolBar();
        setTitleOnToolbar(getResources().getString(R.string.text_trip_history_detail));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tripId = bundle.getString(Const.Params.TRIP_ID);
            unit = Utils.showUnit(this, bundle.getInt(Const.Params.UNIT));
        }
        mapFrameLayout = findViewById(R.id.mapFrameLayout);
        mapView = findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        llTimeAndDistance = findViewById(R.id.llTimeAndDistance);
        tvHistoryDetailCost = findViewById(R.id.tvHistoryDetailCost);
        tvHistoryDetailDest = findViewById(R.id.tvFareDest);
        tvHistoryDetailTripTime = findViewById(R.id.tvHistoryDetailTripTime);
        tvHistoryDetailDistance = findViewById(R.id.tvHistoryDetailDistance);
        tvHistoryTripCreateTime = findViewById(R.id.tvHistoryTripCreateTime);
        tvHistoryDetailSrc = findViewById(R.id.tvFareSrc);
        tvHistoryTripDate = findViewById(R.id.tvHistoryDetailDate);
        tvHistoryDetailDriverName = findViewById(R.id.tvHistoryDetailDriverName);
        ivHistoryDetailPhotoDialog = findViewById(R.id.ivHistoryDetailPhotoDialog);
        tvHistoryTripNumber = findViewById(R.id.tvHistoryTripNumber);
        llHistoryRate = findViewById(R.id.llHistoryRate);
        llDetails = findViewById(R.id.llDetails);
        llFromAndTo = findViewById(R.id.llFromAndTo);
        ivFullScreen = findViewById(R.id.ivFullScreen);
        ivFullScreen.setOnClickListener(this);
        markerList = new ArrayList<>();
        llHistoryRate.setOnClickListener(this);
        tvHistoryTripNumber.setOnClickListener(this);

        mapView.onCreate(savedInstanceState);
        mapFrameLayout.getLocationOnScreen(mapLocation);
        llTripStops = findViewById(R.id.llTripStops);
    }

    @SuppressLint("PotentialBehaviorOverride")
    private void setUpMap() {
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            final boolean doNotMoveCameraToCenterMarker = true;

            public boolean onMarkerClick(@NonNull Marker marker) {
                return doNotMoveCameraToCenterMarker;
            }
        });

        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (!isMapTouched) {

                }
                setMapTouched(false);
            }
        });
        getUserTripDetail(tripId);
    }

    private void setMarkerOnLocation(LatLng pickUpLatLng, LatLng destLatLng) {
        BitmapDescriptor bitmapDescriptor;
        markerList.clear();

        if (pickUpLatLng != null) {
            if (pickUpMarker == null) {
                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.drawableToBitmap(ResourcesCompat.getDrawable(this.getResources(), R.drawable.user_pin, null)));
                pickUpMarker = googleMap.addMarker(new MarkerOptions().position(pickUpLatLng).title(this.getResources().getString(R.string.text_from)).icon(bitmapDescriptor).anchor(0.5f, 0.5f));
            } else {
                pickUpMarker.setPosition(pickUpLatLng);
            }
            markerList.add(pickUpLatLng);
        }
        if (destLatLng != null) {
            if (destinationMarker == null) {
                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.drawableToBitmap(ResourcesCompat.getDrawable(this.getResources(), R.drawable.destination_pin, null)));
                destinationMarker = googleMap.addMarker(new MarkerOptions().position(destLatLng).title(this.getResources().getString(R.string.text_to)).icon(bitmapDescriptor).anchor(0.5f, 0.5f));
            } else {
                destinationMarker.setPosition(destLatLng);
            }
            markerList.add(destLatLng);
        }

        if (!trip.getActualTripStopAddress().isEmpty()) {
            for (TripStopAddresses tripStopAddresses : trip.getActualTripStopAddress()) {
                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.vectorToBitmap(this, R.drawable.ic_dot_pin));
                if (!tripStopAddresses.getLocation().isEmpty()) {
                    LatLng stops = new LatLng(tripStopAddresses.getLocation().get(0), tripStopAddresses.getLocation().get(1));
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(stops).title(getResources().getString(R.string.text_destination)).icon(bitmapDescriptor).anchor(0.5f, 0.5f));
                    markerList.add(stops);
                }
            }
        }

        setLocationBounds(false, markerList);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setUpMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void initCurrentPathDraw() {
        currentPathPolylineOptions = new PolylineOptions();
        currentPathPolylineOptions.color(ResourcesCompat.getColor(this.getResources(), R.color.color_app_red_path, null));
        currentPathPolylineOptions.width(15);
    }

    private void drawCurrentPath() {
        googleMap.addPolyline(currentPathPolylineOptions);
    }

    private void setLocationBounds(boolean isCameraAnim, ArrayList<LatLng> markerList) {
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        int driverListSize = markerList.size();
        if (driverListSize > 0) {
            for (int i = 0; i < driverListSize; i++) {
                bounds.include(markerList.get(i));
            }
            CameraUpdate cu;
            //Change the padding as per needed
            if (getResources().getDisplayMetrics().density > DisplayMetrics.DENSITY_HIGH) {
                cu = CameraUpdateFactory.newLatLngBounds(bounds.build(), Const.MAP_BOUNDS);
            } else {
                cu = CameraUpdateFactory.newLatLngBounds(bounds.build(), 0);
            }
            if (isCameraAnim) {
                googleMap.animateCamera(cu);
            } else {
                googleMap.moveCamera(cu);
            }
            drawCurrentPath();
        }


    }

    private void getUserTripDetail(String tripId) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_waiting_for_detail_history), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.TRIP_ID, tripId);

            Call<TripHistoryDetailResponse> call = ApiClient.getClient().create(ApiInterface.class).getTripHistoryDetail(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<TripHistoryDetailResponse>() {
                @Override
                public void onResponse(Call<TripHistoryDetailResponse> call, Response<TripHistoryDetailResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            // init model for set invoice data
                            tripService = response.body().getTripService();
                            trip = response.body().getTrip();
                            currencyFormat = currencyHelper.getCurrencyFormat(trip.getCurrencycode());
                            setTripData(response.body());
                            setTripProviderData(response.body().getProvider());
                            setTripMapData(response.body());
                            Utils.hideCustomProgressDialog();
                        } else {
                            Utils.hideCustomProgressDialog();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TripHistoryDetailResponse> call, Throwable t) {
                    AppLog.handleThrowable(TripHistoryDetailActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.TRIP_HISTORY_DETAIL_ACTIVITY, e);
        }

    }

    private void setTripProviderData(Provider provider) {
        tvHistoryDetailDriverName.setText(provider.getFirstName() + " " + provider.getLastName());
        GlideApp.with(TripHistoryDetailActivity.this).load(IMAGE_BASE_URL + provider.getPicture()).override(200, 200).placeholder(R.drawable.ellipse).fallback(R.drawable.ellipse).into(ivHistoryDetailPhotoDialog);
    }

    private void setTripMapData(TripHistoryDetailResponse response) {
        if (response.getStartTripToEndTripLocations() != null) {
            List<List<Double>> pathWayPoints = response.getStartTripToEndTripLocations();
            int size = pathWayPoints.size();
            for (int i = 0; i < size; i++) {
                List<Double> location = pathWayPoints.get(i);
                LatLng latLng = new LatLng(location.get(0), location.get(1));
                currentPathPolylineOptions.add(latLng);
            }
        }
        Trip trip = response.getTrip();
        setMarkerOnLocation(new LatLng(trip.getSourceLocation().get(0), trip.getSourceLocation().get(1)), new LatLng(trip.getDestinationLocation().get(0), trip.getDestinationLocation().get(1)));
    }

    private void setTripData(TripHistoryDetailResponse response) {
        Trip trip = response.getTrip();
        initCurrentPathDraw();
        tvHistoryDetailCost.setText(currencyFormat.format(trip.getTotal()));
        tvHistoryDetailDest.setText(trip.getDestinationAddress());
        tvHistoryDetailSrc.setText(trip.getSourceAddress());
        if (!trip.getTripStopAddresses().isEmpty()) {
            llTripStops.removeAllViews();
            for (int i = 0; i < trip.getTripStopAddresses().size(); i++) {
                TripStopAddresses tripStopAddresses = trip.getTripStopAddresses().get(i);
                View stopView = LayoutInflater.from(this).inflate(R.layout.layout_trip_stop, llTripStops, false);
                MyFontAutocompleteView acStopLocation = stopView.findViewById(R.id.acStopLocation);
                acStopLocation.setText(tripStopAddresses.getAddress());
                acStopLocation.setBackground(null);
                acStopLocation.setEnabled(false);
                ImageView ivClearStopTextMap = stopView.findViewById(R.id.ivClearStopTextMap);
                ivClearStopTextMap.setVisibility(View.GONE);
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
        tvHistoryDetailTripTime.setText(parseContent.twoDigitDecimalFormat.format(trip.getTotalTime()) + " " + getResources().getString(R.string.text_unit_mins));
        tvHistoryDetailDistance.setText(parseContent.twoDigitDecimalFormat.format(trip.getTotalDistance()) + " " + "" + unit);
        Date date = new Date();
        try {
            date = parseContent.webFormat.parse(trip.getUserCreateTime());
        } catch (ParseException e) {
            AppLog.handleException(TripHistoryDetailActivity.class.getSimpleName(), e);
        }
        tvHistoryTripCreateTime.setText(parseContent.timeFormat_am.format(date));
        setTripDate(parseContent.dateFormat.format(date));
        tvHistoryTripNumber.setText(getResources().getString(R.string.text_trip_id) + trip.getUniqueId());
        if (trip.getIsProviderRated() == Const.FALSE && trip.getIsTripCancelled() == Const.FALSE) {
            llHistoryRate.setVisibility(View.VISIBLE);
        } else {
            llHistoryRate.setVisibility(View.GONE);
        }

        if (trip.getIsTripCompleted() == Const.TRUE) {
            setToolbarRightSideButton(getResources().getString(R.string.text_invoice), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openInvoiceDialog();
                }
            });
        } else {
            if (trip.getIsCancellationFee() == Const.TRUE) {
                setToolbarRightSideButton(getResources().getString(R.string.text_invoice), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openInvoiceDialog();
                    }
                });
            }

        }

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
        switch (v.getId()) {
            case R.id.llHistoryRate:
                openFeedbackDialog();
                break;

            case R.id.btnDialogSubmitFeedback:
                if (feedbackDialogRatingBar.getRating() != 0) {
                    giveFeedBack();
                } else {
                    Utils.showToast(this.getResources().getString(R.string.msg_give_ratting), this);
                }
                break;
            case R.id.ivFullScreen:
                expandMap();
            default:
                //default here....
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private String getYesterdayDateString() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return parseContent.dateFormat.format(cal.getTime());
    }

    private void setTripDate(String tripDate) {
        Date currentDate = new Date();
        String date = parseContent.dateFormat.format(currentDate);
        if (tripDate.equals(date)) {
            tvHistoryTripDate.setText(getResources().getString(R.string.text_today));
        } else if (tripDate.equals(getYesterdayDateString())) {
            tvHistoryTripDate.setText(getResources().getString(R.string.text_yesterday));
        } else {
            try {
                Date returnDate = parseContent.dateFormat.parse(tripDate);
                String daySuffix = Utils.getDayOfMonthSuffix(Integer.valueOf(parseContent.day.format(returnDate)));
                tvHistoryTripDate.setText(daySuffix + " " + parseContent.dateFormatMonth.format(returnDate));

            } catch (ParseException e) {
                AppLog.handleException(Const.Tag.TRIP_HISTORY_DETAIL_ACTIVITY, e);
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void openInvoiceDialog() {
        TextView tvPaymentWith, tvInvoiceNumber, tvInvoiceDistance, tvInvoiceTripTime, tvInvoiceTotal, tvTotalText;
        String unit;
        ImageView ivPaymentImage;
        MyFontTextViewMedium tvInvoiceTripType;
        MyFontTextView tvInvoiceMinFareApplied;
        RecyclerView rcvInvoice, rcvSplitPaymentUsers;
        LinearLayout llInvoiceInfo;
        CardView cvInvoiceDetails;
        int margin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
        Dialog invoiceDialog = new Dialog(this);
        invoiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        invoiceDialog.setContentView(R.layout.fragment_invoice);

        ivPaymentImage = invoiceDialog.findViewById(R.id.ivPaymentImage);
        tvPaymentWith = invoiceDialog.findViewById(R.id.tvPaymentWith);
        tvInvoiceNumber = invoiceDialog.findViewById(R.id.tvInvoiceNumber);
        tvInvoiceTripType = invoiceDialog.findViewById(R.id.tvInvoiceTripType);
        tvInvoiceMinFareApplied = invoiceDialog.findViewById(R.id.tvInvoiceMinFareApplied);
        tvInvoiceDistance = invoiceDialog.findViewById(R.id.tvInvoiceDistance);
        tvInvoiceTripTime = invoiceDialog.findViewById(R.id.tvInvoiceTripTime);
        rcvInvoice = invoiceDialog.findViewById(R.id.rcvInvoice);
        rcvSplitPaymentUsers = invoiceDialog.findViewById(R.id.rcvSplitPaymentUsers);

        cvInvoiceDetails = invoiceDialog.findViewById(R.id.cvInvoiceDetails);
        cvInvoiceDetails.setRadius(getResources().getDimensionPixelOffset(R.dimen.dimen_address_padding));
        ViewGroup.MarginLayoutParams cardViewMarginParams = (ViewGroup.MarginLayoutParams) cvInvoiceDetails.getLayoutParams();
        cardViewMarginParams.setMargins(margin, margin, margin, margin);
        cvInvoiceDetails.requestLayout();

        rcvInvoice.setLayoutManager(new LinearLayoutManager(this));
        rcvInvoice.setNestedScrollingEnabled(false);
        rcvSplitPaymentUsers.setLayoutManager(new LinearLayoutManager(this));
        rcvSplitPaymentUsers.setNestedScrollingEnabled(false);
        tvInvoiceTotal = invoiceDialog.findViewById(R.id.tvInvoiceTotal);
        tvTotalText = invoiceDialog.findViewById(R.id.tvTotalText);
        tvTotalText.setVisibility(View.GONE);
        tvInvoiceTotal.setVisibility(View.GONE);
        invoiceDialog.findViewById(R.id.viewDiv).setVisibility(View.VISIBLE);
        llInvoiceInfo = invoiceDialog.findViewById(R.id.llInvoiceInfo);
        llInvoiceInfo.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.color_white, null));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            llInvoiceInfo.setElevation(0);
        }

        if (trip != null && tripService != null) {
            unit = Utils.showUnit(this, trip.getUnit());
            if (trip.getIsMinFareUsed() == Const.TRUE && trip.getTripType() == Const.TripType.NORMAL) {
                tvInvoiceMinFareApplied.setVisibility(View.VISIBLE);
                tvInvoiceMinFareApplied.setText(getResources().getString(R.string.start_min_fare) + " " + currencyFormat.format(tripService.getMinFare()) + " " + getResources().getString(R.string.text_applied));
            }
            if (trip.getPaymentMode() == Const.CASH) {
                ivPaymentImage.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.cash));
                tvPaymentWith.setText(this.getResources().getString(R.string.text_payment_with_cash));
            } else {
                ivPaymentImage.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.card));
                tvPaymentWith.setText(this.getResources().getString(R.string.text_payment_with_card));
            }

            tvInvoiceNumber.setText(trip.getInvoiceNumber());

            tvInvoiceDistance.setText(ParseContent.getInstance().twoDigitDecimalFormat.format(trip.getTotalDistance()) + " " + unit);
            tvInvoiceTripTime.setText(ParseContent.getInstance().twoDigitDecimalFormat.format(trip.getTotalTime()) + " " + this.getResources().getString(R.string.text_unit_mins));
            tvInvoiceTotal.setText(currencyFormat.format(trip.getTotal()));
            tvInvoiceTotal.setVisibility(View.VISIBLE);
            tvTotalText.setVisibility(View.VISIBLE);

            switch (trip.getTripType()) {
                case Const.TripType.AIRPORT:
                    tvInvoiceTripType.setVisibility(View.VISIBLE);
                    tvInvoiceTripType.setText(this.getResources().getString(R.string.text_airport_trip));
                    break;
                case Const.TripType.ZONE:
                    tvInvoiceTripType.setVisibility(View.VISIBLE);
                    tvInvoiceTripType.setText(this.getResources().getString(R.string.text_zone_trip));
                    break;
                case Const.TripType.CITY:
                    tvInvoiceTripType.setVisibility(View.VISIBLE);
                    tvInvoiceTripType.setText(this.getResources().getString(R.string.text_city_trip));
                    break;
                default:
                    //Default case here..
                    if (trip.isFixedFare()) {
                        tvInvoiceTripType.setVisibility(View.VISIBLE);
                        tvInvoiceTripType.setText(this.getResources().getString(R.string.text_fixed_price));
                    } else {
                        tvInvoiceTripType.setVisibility(View.GONE);
                    }
                    break;
            }

            if (rcvInvoice != null) {
                InvoiceAdapter invoiceAdapter = new InvoiceAdapter(parseContent.parseInvoice(this, trip, tripService, currencyFormat));
                rcvInvoice.setAdapter(invoiceAdapter);
            }

            if (rcvSplitPaymentUsers != null) {
                UserAdapter userAdapter = new UserAdapter(this, (ArrayList<SplitPaymentRequest>) trip.getSplitPaymentUsers()) {
                    @Override
                    public void onButtonClick(int position) {

                    }

                    @Override
                    public void onStatusClick(int position) {

                    }
                };
                userAdapter.setShowPrise(true);
                rcvSplitPaymentUsers.setAdapter(userAdapter);
            }
        }

        WindowManager.LayoutParams params = invoiceDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        invoiceDialog.getWindow().setAttributes(params);
        invoiceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        invoiceDialog.show();
    }

    public String validSuffix(double value, String unit) {

        if (value <= 1) {
            return unit;
        } else {
            return Const.SLASH + value + unit;

        }
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

    private void openFeedbackDialog() {
        if (feedBackDialog != null && feedBackDialog.isShowing()) {
            return;
        }

        feedBackDialog = new Dialog(this);
        feedBackDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        feedBackDialog.setContentView(R.layout.dialog_feedback_history);

        tvDriverNameDialog = feedBackDialog.findViewById(R.id.tvDriverNameDialog);
        feedbackDialogRatingBar = feedBackDialog.findViewById(R.id.feedbackDialogRatingBar);
        etDialogComment = feedBackDialog.findViewById(R.id.etDialogComment);
        btnDialogSubmitFeedback = feedBackDialog.findViewById(R.id.btnDialogSubmitFeedback);

        btnDialogSubmitFeedback.setOnClickListener(this);
        tvDriverNameDialog.setText(tvHistoryDetailDriverName.getText().toString());

        WindowManager.LayoutParams params = feedBackDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        feedBackDialog.getWindow().setAttributes(params);
        feedBackDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        feedBackDialog.show();
    }

    private void giveFeedBack() {
        Utils.showCustomProgressDialog(this, this.getResources().getString(R.string.msg_waiting_for_ratting), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TRIP_ID, tripId);
            jsonObject.put(Const.Params.REVIEW, etDialogComment.getText().toString());
            jsonObject.put(Const.Params.RATING, feedbackDialogRatingBar.getRating());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).giveRating(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {

                    if (ParseContent.getInstance().isSuccessful(response)) {
                        Utils.hideCustomProgressDialog();
                        feedBackDialog.dismiss();
                        if (response.body().isSuccess()) {
                            Utils.showToast(getResources().getString(R.string.text_succesfully_rated), TripHistoryDetailActivity.this);
                            llHistoryRate.setVisibility(View.GONE);
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), TripHistoryDetailActivity.this);
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


    private void expandMap() {
        if (llFromAndTo.getVisibility() == View.VISIBLE) {
            llDetails.setVisibility(View.GONE);
            llFromAndTo.setVisibility(View.GONE);
            llTimeAndDistance.setVisibility(View.GONE);
        } else {
            llDetails.setVisibility(View.VISIBLE);
            llFromAndTo.setVisibility(View.VISIBLE);
            llTimeAndDistance.setVisibility(View.VISIBLE);
        }

    }
}