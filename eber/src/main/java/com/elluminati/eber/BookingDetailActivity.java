package com.elluminati.eber;

import static com.elluminati.eber.utils.Const.IMAGE_BASE_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

import com.elluminati.eber.adapter.InvoiceAdapter;
import com.elluminati.eber.components.CustomEventMapView;
import com.elluminati.eber.components.MyFontAutocompleteView;
import com.elluminati.eber.components.MyFontButton;
import com.elluminati.eber.components.MyFontEdittextView;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.components.MyFontTextViewMedium;
import com.elluminati.eber.fragments.FeedbackFragment;
import com.elluminati.eber.models.datamodels.CityType;
import com.elluminati.eber.models.datamodels.Provider;
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

public class BookingDetailActivity extends BaseAppCompatActivity implements OnMapReadyCallback {

    private static boolean isMapTouched = false;
    private final int[] mapLocation = new int[2];
    private TextView tvHistoryDetailSrc, tvHistoryDetailDest, tvHistoryTripDate, tvHistoryTripCreateTime, tvHistoryDetailDriverName, tvHistoryTripNumber;
    private ImageView ivHistoryDetailPhotoDialog, ivFullScreen;
    private LinearLayout llDetails, llFromAndTo;
    private CustomEventMapView mapView;
    private GoogleMap googleMap;
    private ArrayList<LatLng> markerList;
    private Marker pickUpMarker, destinationMarker;
    private PolylineOptions currentPathPolylineOptions;
    private FrameLayout mapFrameLayout;

    private Trip trip;
    private LinearLayout llTripStops;



    private static void setMapTouched(boolean isTouched) {
        isMapTouched = isTouched;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);
        initToolBar();
        setTitleOnToolbar(getResources().getString(R.string.text_my_booking));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            trip = bundle.getParcelable(Const.Params.TRIP_ID);
        }
        mapFrameLayout = findViewById(R.id.mapFrameLayout);
        mapView = findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        tvHistoryDetailDest = findViewById(R.id.tvFareDest);
        tvHistoryTripCreateTime = findViewById(R.id.tvHistoryTripCreateTime);
        tvHistoryDetailSrc = findViewById(R.id.tvFareSrc);
        tvHistoryTripDate = findViewById(R.id.tvHistoryDetailDate);
        tvHistoryDetailDriverName = findViewById(R.id.tvHistoryDetailDriverName);
        ivHistoryDetailPhotoDialog = findViewById(R.id.ivHistoryDetailPhotoDialog);
        tvHistoryTripNumber = findViewById(R.id.tvHistoryTripNumber);
        llDetails = findViewById(R.id.llDetails);
        llFromAndTo = findViewById(R.id.llFromAndTo);
        ivFullScreen = findViewById(R.id.ivFullScreen);
        ivFullScreen.setOnClickListener(this);
        markerList = new ArrayList<>();
        tvHistoryTripNumber.setOnClickListener(this);

        mapView.onCreate(savedInstanceState);
        mapFrameLayout.getLocationOnScreen(mapLocation);
        llTripStops = findViewById(R.id.llTripStops);

        setTripData();
        setUserData();
    }

    private void setUpMap() {

        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            final boolean doNotMoveCameraToCenterMarker = true;

            public boolean onMarkerClick(Marker marker) {
                return doNotMoveCameraToCenterMarker;
            }
        });

        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                if (!isMapTouched) {

                }
                setMapTouched(false);
                setTripMapData();
            }

        });

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

        if(!trip.getTripStopAddresses().isEmpty()) {
            for (TripStopAddresses tripStopAddresses: trip.getTripStopAddresses()) {
                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Utils.vectorToBitmap(this, R.drawable.ic_dot_pin));
                LatLng stops = new LatLng(tripStopAddresses.getLocation().get(0),tripStopAddresses.getLocation().get(1));
                Marker marker = googleMap.addMarker(new MarkerOptions().position(stops).title(getResources().getString(R.string.text_destination_stop)).icon(bitmapDescriptor).anchor(0.5f, 0.5f));
                markerList.add(stops);
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


    private void setUserData() {
        tvHistoryDetailDriverName.setText(preferenceHelper.getFirstName() + " " + preferenceHelper.getLastName());
        GlideApp.with(BookingDetailActivity.this).load(IMAGE_BASE_URL + preferenceHelper.getProfilePic()).override(200, 200).placeholder(R.drawable.ellipse).fallback(R.drawable.ellipse).into(ivHistoryDetailPhotoDialog);
    }

    private void setTripMapData() {
        /*if (response.getStartTripToEndTripLocations() != null) {
            List<List<Double>> pathWayPoints = response.getStartTripToEndTripLocations();
            int size = pathWayPoints.size();
            for (int i = 0; i < size; i++) {
                List<Double> location = pathWayPoints.get(i);
                LatLng latLng = new LatLng(location.get(0), location.get(1));
                currentPathPolylineOptions.add(latLng);
            }
        }*/
        setMarkerOnLocation(new LatLng(trip.getSourceLocation().get(0), trip.getSourceLocation().get(1)), new LatLng(trip.getDestinationLocation().get(0), trip.getDestinationLocation().get(1)));
    }

    private void setTripData() {
        initCurrentPathDraw();
        tvHistoryDetailDest.setText(trip.getDestinationAddress());
        tvHistoryDetailSrc.setText(trip.getSourceAddress());
        if (!trip.getTripStopAddresses().isEmpty()) {
            llTripStops.removeAllViews();
            for (int i = 0; i < trip.getTripStopAddresses().size(); i++) {
                TripStopAddresses tripStopAddresses = trip.getTripStopAddresses().get(i);
                View stopView = LayoutInflater.from(this).inflate(R.layout.layout_trip_stop, llTripStops, false);
                MyFontAutocompleteView tvMapStopAddress = stopView.findViewById(R.id.acStopLocation);
                tvMapStopAddress.setText(tripStopAddresses.getAddress());
                tvMapStopAddress.setBackground(null);
                tvMapStopAddress.setEnabled(false);
                ImageView ivClearStopTextMap = stopView.findViewById(R.id.ivClearStopTextMap);
                ivClearStopTextMap.setVisibility(View.GONE);
                llTripStops.addView(stopView);
            }
        } else {
            llTripStops.removeAllViews();
        }
        Date date = new Date();
        try {
            date = parseContent.webFormat.parse(trip.getUserCreateTime());
        } catch (ParseException e) {
            AppLog.handleException(TripHistoryDetailActivity.class.getSimpleName(), e);
        }
        tvHistoryTripCreateTime.setText(parseContent.timeFormat_am.format(date));
        setTripDate(parseContent.dateFormat.format(date));
        tvHistoryTripNumber.setText(getResources().getString(R.string.text_trip_id) + trip.getUniqueId());


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
            try {
                Date returnDate = parseContent.dateFormat.parse(tripDate);
                String daySuffix = Utils.getDayOfMonthSuffix(Integer.valueOf(parseContent.day.format(returnDate)));
                tvHistoryTripDate.setText(daySuffix + " " + parseContent.dateFormatMonth.format(returnDate) + ", " + tvHistoryTripCreateTime.getText().toString() );

            } catch (ParseException e) {
                AppLog.handleException(Const.Tag.TRIP_HISTORY_DETAIL_ACTIVITY, e);
            }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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


    private void expandMap() {
        if (llFromAndTo.getVisibility() == View.VISIBLE) {
            llDetails.setVisibility(View.GONE);
            llFromAndTo.setVisibility(View.GONE);
        } else {
            llDetails.setVisibility(View.VISIBLE);
            llFromAndTo.setVisibility(View.VISIBLE);
        }

    }

}
