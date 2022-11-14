package com.elluminati.eber;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.adapter.PlaceAutocompleteAdapter;
import com.elluminati.eber.adapter.RecentTripHistoryAdapter;
import com.elluminati.eber.components.CustomAddressChooseDialog;
import com.elluminati.eber.components.MyFontAutocompleteView;
import com.elluminati.eber.components.MyFontButton;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.TripHistory;
import com.elluminati.eber.models.datamodels.TripStopAddresses;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.models.responsemodels.TripHistoryResponse;
import com.elluminati.eber.models.singleton.AddressUtilsEber;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.LocationHelper;
import com.elluminati.eber.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DestinationSelectionActivity extends BaseAppCompatActivity implements LocationHelper.OnLocationReceived {

    private MyFontAutocompleteView acPickupAddress, acDestinationAddress, selectedTripStop;
    private ImageView ivClearPickUpText, ivClearDestTextMap;
    private PlaceAutocompleteAdapter autocompleteAdapter;
    private LocationHelper locationHelper;
    private LinearLayout llSetLocation, llDestinationLater, llHome, llWork, llAddStop, llTripStops;
    private CustomAddressChooseDialog dialogFavAddress;
    private MyFontTextView tvHomeAddress, tvWorkAddress, tvAddStop;
    private MyFontTextView tvDeleteHome, tvDeleteWork;
    private int addressRequestCode = Const.PICK_UP_ADDRESS;
    private int stopCount = 1, selectedStopPosition;
    private TripStopAddresses stopAddresses;
    private final ArrayList<TripStopAddresses> tripStopAddresses = new ArrayList<>();
    private MyFontButton btnContinue;
    private RecyclerView rvTripHistory;
    CardView cardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_selection);
        initToolBar();
        setToolbarBackgroundWithElevation(false, R.color.color_white, 0);
        setTitleOnToolbar(getResources().getString(R.string.text_where_to_go));
        locationHelper = new LocationHelper(this);
        locationHelper.setLocationReceivedLister(this);
        initViews();
        setUpData();
        autocompleteAdapter = new PlaceAutocompleteAdapter(this);
        initPickupAutoComplete();
        initDestinationAutoComplete();
        locationHelper.getLastLocation(location1 -> {
            if (location1 != null) {
                setPlaceFilter(AddressUtilsEber.getInstance().getCountryCode(), location1);
                if (TextUtils.isEmpty(acPickupAddress.getText().toString())) {
                    LatLng currentLatLng = new LatLng(location1.getLatitude(), location1.getLongitude());
                    new GetPickUpAddressFromLocation().executeOnExecutor(Executors.newSingleThreadExecutor(), currentLatLng);
                    addressUtils.setPickupLatLng(currentLatLng);
                }
            } else {
                Utils.showToast(getResources().getString(R.string.text_location_not_found), DestinationSelectionActivity.this);
            }
        });

        if (preferenceHelper.getAllowMultipleStops()) {
            llAddStop.setVisibility(View.VISIBLE);
        } else {
            llAddStop.setVisibility(View.GONE);
        }
        if (!addressUtils.getTripStopAddressesArrayList().isEmpty()) {
            tripStopAddresses.addAll(addressUtils.getTripStopAddressesArrayList());
            for (TripStopAddresses addresses : addressUtils.getTripStopAddressesArrayList()) {
                addTripStops(addresses.getAddress());
            }
        }

        if (preferenceHelper.getAllowMultipleStops()) {
            btnContinue.setVisibility(View.VISIBLE);
        }

        if (getIntent() != null) {
            if (getIntent().getBooleanExtra(Const.BUNDLE, false)) {
                addTripStops("");
            }
        }
    }

    private void initViews() {
        rvTripHistory = findViewById(R.id.rv_recentPlace);
         cardView = findViewById(R.id.card_HistoryList);

        acDestinationAddress = findViewById(R.id.acDestinationLocation);
        acPickupAddress = findViewById(R.id.acPickupLocation);
        ivClearPickUpText = findViewById(R.id.ivClearPickUpTextMap);
        ivClearDestTextMap = findViewById(R.id.ivClearTextDestMap);
        llSetLocation = findViewById(R.id.llSetLocation);
        llDestinationLater = findViewById(R.id.llDestinationLater);
        llHome = findViewById(R.id.llHome);
        llWork = findViewById(R.id.llWork);
        tvHomeAddress = findViewById(R.id.tvHomeAddress);
        tvWorkAddress = findViewById(R.id.tvWorkAddress);
        tvDeleteHome = findViewById(R.id.tvDeleteHome);
        tvDeleteWork = findViewById(R.id.tvDeleteWork);
        llAddStop = findViewById(R.id.llAddStop);
        llTripStops = findViewById(R.id.llTripStops);
        tvAddStop = findViewById(R.id.tvAddStop);
        btnContinue = findViewById(R.id.btnContinue);

        ivClearPickUpText.setOnClickListener(this);
        ivClearDestTextMap.setOnClickListener(this);
        llSetLocation.setOnClickListener(this);
        llDestinationLater.setOnClickListener(this);
        llHome.setOnClickListener(this);
        llWork.setOnClickListener(this);
        tvDeleteWork.setOnClickListener(this);
        tvDeleteHome.setOnClickListener(this);
        tvAddStop.setOnClickListener(this);
        btnContinue.setOnClickListener(this);

        acDestinationAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    addressRequestCode = Const.DESTINATION_ADDRESS;
                } else {
                    addressRequestCode = Const.PICK_UP_ADDRESS;
                }

            }
        });


        acPickupAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    addressRequestCode = Const.PICK_UP_ADDRESS;
                } else {
                    addressRequestCode = Const.DESTINATION_ADDRESS;
                }

            }
        });
        acPickupAddress.requestFocus();

        getTripHistory("","",true);

    }
    private void getTripHistory(String startDate, String endDate,boolean clearHistory) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTripHistory.setLayoutManager(linearLayoutManager);


        RecentTripHistoryAdapter tripHistoryAdaptor;

        List<TripHistory> tripHistoriesList = new ArrayList<>();
        final int[] pageNumber = {0};

        if (clearHistory) {
            pageNumber[0] = 1;
            tripHistoriesList.clear();
        }
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_waiting_for_history), false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            String startDateEn = startDate;
            String endDateEn = endDate;
            jsonObject.put(Const.Params.START_DATE, startDateEn);
            jsonObject.put(Const.Params.END_DATE, endDateEn);
            jsonObject.put(Const.Params.PAGE, pageNumber[0]);
            Call<TripHistoryResponse> call = ApiClient.getClient().create(ApiInterface.class).getTripHistory(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<TripHistoryResponse>() {
                @Override
                public void onResponse(Call<TripHistoryResponse> call, Response<TripHistoryResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            if (response.body().getTrips() != null && !response.body().getTrips().isEmpty()) {
//                                pageNumber[0]++;

//                                if(response.body().getTrips().)
                                    tripHistoriesList.addAll(response.body().getTrips());

                                Collections.reverse(tripHistoriesList);
                            }
//                            getShortHistoryList(tripHistoriesList);
//                            if (tripHistoryAdaptor == null) {
//                                tripHistoryAdaptor = new TripHistoryAdaptor(TripHistoryActivity.this, tripHistoryShortList, separatorSet);
//                                rcvHistory.setAdapter(tripHistoryAdaptor);
//                            } else {
//                            }
//                          updateUi(tripHistoryShortList.size() > 0);
                            Utils.hideCustomProgressDialog();


                            RecentTripHistoryAdapter.ItemClickListener itemClickListener = place -> {

                                acDestinationAddress.setText(place);
//                                setDestinationAddress(place);
//                                updateCityData(address.getCountryName());
                                if (!preferenceHelper.getAllowMultipleStops() && !TextUtils.isEmpty(acDestinationAddress.getText().toString().trim()) && AddressUtilsEber.getInstance().getDestinationLatLng() != null) {
                                    goToMapFragment(Const.RESULT_OK);
                                }

                            };


                            RecentTripHistoryAdapter recentTripHistory = new RecentTripHistoryAdapter(tripHistoriesList,itemClickListener);
                            rvTripHistory.setAdapter(recentTripHistory);

                        } else {
//                            updateUi(false);
                            Utils.hideCustomProgressDialog();
                        }
                    }

                }

                @Override
                public void onFailure(Call<TripHistoryResponse> call, Throwable t) {
                    AppLog.handleThrowable(TripHistoryActivity.class.getSimpleName(), t);

                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.TRIP_HISTORY_ACTIVITY, e);
        }
    }

    private void setUpData() {
        acPickupAddress.setText(addressUtils.getTrimedPickupAddress());
        acDestinationAddress.setText(addressUtils.getTrimedDestinationAddress());
        acDestinationAddress.requestFocus();
        if (!TextUtils.isEmpty(addressUtils.getTrimedPickupAddress())) {
            ivClearPickUpText.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(addressUtils.getTrimedDestinationAddress())) {
            ivClearDestTextMap.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(addressUtils.getHomeAddress())) {
            tvHomeAddress.setText(addressUtils.getTrimmedHomeAddress());
            tvDeleteHome.setVisibility(View.VISIBLE);
            tvHomeAddress.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        } else {
            tvDeleteHome.setVisibility(View.GONE);
            tvHomeAddress.setText(getString(R.string.text_add_home));
            tvHomeAddress.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(this, R.drawable.plus), null);
        }
        if (!TextUtils.isEmpty(addressUtils.getWorkAddress())) {
            tvWorkAddress.setText(addressUtils.getTrimmedWorkAddress());
            tvDeleteWork.setVisibility(View.VISIBLE);
            tvWorkAddress.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        } else {
            tvDeleteWork.setVisibility(View.GONE);
            tvWorkAddress.setText(getString(R.string.text_add_work));
            tvWorkAddress.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(this, R.drawable.plus), null);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        locationHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationHelper.onStop();
    }


    /**
     * Use for initialize pickup auto complete .
     */
    private void initPickupAutoComplete() {

        acPickupAddress.setAdapter(autocompleteAdapter);
        acPickupAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeybord();
                setPickUpAddress(acPickupAddress.getText().toString());
                getLocationFromPlaceId(autocompleteAdapter.getPlaceId(position), Const.PICK_UP_ADDRESS);
            }
        });
        acPickupAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    ivClearPickUpText.setVisibility(View.VISIBLE);
                } else {
                    ivClearPickUpText.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Used for initialize destination_pin autocomplete.
     * use for set autocomplete adapter.
     */
    private void initDestinationAutoComplete() {
        acDestinationAddress.setAdapter(autocompleteAdapter);

        acDestinationAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeybord();
                setDestinationAddress(acDestinationAddress.getText().toString());
                getLocationFromPlaceId(autocompleteAdapter.getPlaceId(position), Const.DESTINATION_ADDRESS);
            }
        });
        acDestinationAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    cardView.setVisibility(View.GONE);
                    ivClearDestTextMap.setVisibility(View.VISIBLE);
                } else {
                    ivClearDestTextMap.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * Use for set pickup address in AutoCompleteTextView filed.
     *
     * @param pickUpAddress pickUpAddress
     */
    private void setPickUpAddress(String pickUpAddress) {
        addressUtils.setPickupAddress(pickUpAddress);
        addressUtils.setTrimedPickupAddress(Utils.trimString(pickUpAddress));
        acPickupAddress.setFocusable(false);
        acPickupAddress.setFocusableInTouchMode(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            acPickupAddress.setText(pickUpAddress, false);
        } else {
            acPickupAddress.setText(pickUpAddress);
        }
        acPickupAddress.setFocusable(true);
        acPickupAddress.setFocusableInTouchMode(true);
    }


    private void updateCityData(String countryName) {
        addressUtils.setCurrentCountry(countryName);
    }

    private void setDestinationAddress(String destinationAddress) {
        addressUtils.setDestinationAddress(destinationAddress);
        addressUtils.setTrimedDestinationAddress(Utils.trimString(destinationAddress));
        acDestinationAddress.setFocusable(false);
        acDestinationAddress.setFocusableInTouchMode(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            acDestinationAddress.setText(destinationAddress, false);
        } else {
            acDestinationAddress.setText(destinationAddress);
        }
        acDestinationAddress.setFocusable(true);
        acDestinationAddress.setFocusableInTouchMode(true);
    }

    private void setTripStopAddress(String destinationAddress, MyFontAutocompleteView acStopLocation) {
        stopAddresses.setAddress(destinationAddress);
        //acStopLocation.setFocusable(false);
        //acStopLocation.setFocusableInTouchMode(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            acStopLocation.setText(destinationAddress, false);
        } else {
            acStopLocation.setText(destinationAddress);
        }
        acStopLocation.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.address_bg, null));
        //acStopLocation.setFocusable(true);
        //acStopLocation.setFocusableInTouchMode(true);
    }


    /**
     * It is use for get Location from address using placeId through PlaceSDK Google Play Service
     *
     * @param placeId
     */
    private void getLocationFromPlaceId(String placeId, final int addressRequestCode) {
        if (!TextUtils.isEmpty(placeId) && autocompleteAdapter != null) {
            autocompleteAdapter.getFetchPlaceRequest(placeId, new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                    Place place = fetchPlaceResponse.getPlace();
                    if (addressRequestCode == Const.PICK_UP_ADDRESS) {
                        addressUtils.setPickupLatLng(place.getLatLng());
                        new GetCountryFromLocation().execute(place.getLatLng());
                    } else if (addressRequestCode == Const.DESTINATION_ADDRESS) {
                        addressUtils.setDestinationLatLng(place.getLatLng());
                        if (!preferenceHelper.getAllowMultipleStops()) {
                            if (!TextUtils.isEmpty(acPickupAddress.getText().toString().trim()) && addressUtils.getPickupLatLng() != null) {
                                goToMapFragment(Const.RESULT_OK);
                            } else {
                                Utils.showToast(getResources().getString(R.string.msg_plz_select_pic_up_add), DestinationSelectionActivity.this);
                            }
                        }
                    } else {
                        List<Double> stopLatLng = new ArrayList<>();
                        stopLatLng.add(place.getLatLng().latitude);
                        stopLatLng.add(place.getLatLng().longitude);
                        stopAddresses.setLocation(stopLatLng);
                        addStopToList(stopAddresses);
                    }
                    CurrentTrip.getInstance().setAutocompleteSessionToken(AutocompleteSessionToken.newInstance());
                }
            });
        } else {
            Utils.showToast(getResources().getString(R.string.error_place_id), this);
        }
    }

    private void hideKeybord() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(acDestinationAddress.getWindowToken(), 0);
    }

    @Override
    protected boolean isValidate() {
        return false;
    }

    @Override
    public void goWithBackArrow() {
        hideKeybord();
        onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivClearPickUpTextMap:
                acPickupAddress.getText().clear();
                acPickupAddress.requestFocus();
                break;
            case R.id.ivClearTextDestMap:
                acDestinationAddress.getText().clear();
                cardView.setVisibility(View.VISIBLE);
                acDestinationAddress.requestFocus();
                break;
            case R.id.llSetLocation:
                if (addressRequestCode == Const.DESTINATION_ADDRESS) {
                    openFavAddressDialog(addressRequestCode, addressUtils.getDestinationLatLng(), addressUtils.getDestinationAddress());
                } else if (addressRequestCode == Const.PICK_UP_ADDRESS) {
                    openFavAddressDialog(addressRequestCode, addressUtils.getPickupLatLng(), addressUtils.getPickupAddress());
                } else if (addressRequestCode == Const.TRIP_STOP_ADDRESS) {
                    if (tripStopAddresses.get(selectedStopPosition) != null) {
                        LatLng latLng = new LatLng(tripStopAddresses.get(selectedStopPosition).getLocation().get(0), tripStopAddresses.get(selectedStopPosition).getLocation().get(1));
                        openFavAddressDialog(addressRequestCode, latLng, tripStopAddresses.get(selectedStopPosition).getAddress());
                    } else {
                        openFavAddressDialog(addressRequestCode, addressUtils.getPickupLatLng(), addressUtils.getPickupAddress());
                    }
                } else {
                    openFavAddressDialog(addressRequestCode, addressUtils.getPickupLatLng(), addressUtils.getPickupAddress());
                }
                break;
            case R.id.llDestinationLater:
                if (TextUtils.isEmpty(addressUtils.getPickupAddress()) && addressUtils.getPickupLatLng() == null) {
                    Utils.showToast(getResources().getString(R.string.msg_plz_select_pic_up_add), this);
                } else {
                    addressUtils.clearDestination();
                    addressUtils.getTripStopAddressesArrayList().clear();
                    tripStopAddresses.clear();
                    if (!tripStopAddresses.isEmpty()) {
                        addressUtils.setTripStopAddressesArrayList(tripStopAddresses);
                    }
                    goToMapFragment(Const.RESULT_OK);
                }
                break;
            case R.id.llHome:
                if (tvDeleteHome.getVisibility() == View.VISIBLE) {
                    selectSavedAddress(AddressUtilsEber.getInstance().getHomeAddress(), new LatLng(AddressUtilsEber.getInstance().getHomeLatitude(), AddressUtilsEber.getInstance().getHomeLongitude()), addressRequestCode);
                } else {
                    openFavAddressDialog(Const.HOME_ADDRESS, null, null);
                }
                break;
            case R.id.llWork:
                if (tvDeleteWork.getVisibility() == View.VISIBLE) {
                    selectSavedAddress(AddressUtilsEber.getInstance().getWorkAddress(), new LatLng(AddressUtilsEber.getInstance().getWorkLatitude(), AddressUtilsEber.getInstance().getWorkLongitude()), addressRequestCode);
                } else {
                    openFavAddressDialog(Const.WORK_ADDRESS, null, null);
                }
                break;
            case R.id.tvDeleteWork:
                deleteAddress(false);
                break;
            case R.id.tvDeleteHome:
                deleteAddress(true);
                break;
            case R.id.tvAddStop:
                addTripStops("");
                break;
            case R.id.btnContinue:
                goWithConfirm();
                break;
            default:
                //default case here...
                break;
        }
    }

    private void goWithConfirm() {
        if (!addressUtils.getPickupAddress().isEmpty() && addressUtils.getPickupLatLng() != null &&
                !addressUtils.getDestinationAddress().isEmpty() && addressUtils.getDestinationLatLng() != null) {
            addressUtils.getTripStopAddressesArrayList().clear();
            checkMultipleAddressValidOrNot();
        } else {
            Utils.showToast(getResources().getString(R.string.text_select_valid_address), DestinationSelectionActivity.this);
        }
    }

    private void checkMultipleAddressValidOrNot() {
        if (!tripStopAddresses.isEmpty()) {
            ArrayList<TripStopAddresses> tripStopAddressList = new ArrayList<>();

            //Check for address is selected from map, or places sdk or favourite address
            for (int i = 0; i < tripStopAddresses.size(); i++) {
                if (tripStopAddresses.get(i) != null && !TextUtils.isEmpty(tripStopAddresses.get(i).getAddress())
                        && !tripStopAddresses.get(i).getLocation().isEmpty()) {
                    tripStopAddressList.add(tripStopAddresses.get(i));
                } else {
                    try {
                        View stopView = llTripStops.getChildAt(i);
                        MyFontAutocompleteView acStopLocation = stopView.findViewById(R.id.acStopLocation);
                        acStopLocation.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.address_bg_error, null));
                        Utils.showToast(getResources().getString(R.string.text_select_valid_address), DestinationSelectionActivity.this);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Check for duplicate address (compare latitude and longitude)
                if (i != 0 && tripStopAddresses.get(i) != null && tripStopAddresses.get(i - 1) != null
                        && Objects.equals(tripStopAddresses.get(i).getLocation(), tripStopAddresses.get(i - 1).getLocation())) {
                    try {
                        View stopView = llTripStops.getChildAt(i);
                        MyFontAutocompleteView acStopLocation = stopView.findViewById(R.id.acStopLocation);
                        acStopLocation.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.address_bg_error, null));
                        Utils.showToast(getResources().getString(R.string.mag_duplicate_stop_added), DestinationSelectionActivity.this);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            addressUtils.setTripStopAddressesArrayList(tripStopAddressList);
        }

        goToMapFragment(Const.RESULT_OK);
    }

    private void addTripStops(String address) {
        View stopView = LayoutInflater.from(this).inflate(R.layout.layout_trip_stop, llTripStops, false);
        MyFontAutocompleteView acStopLocation = stopView.findViewById(R.id.acStopLocation);
        ImageView ivClearStopTextMap = stopView.findViewById(R.id.ivClearStopTextMap);
        stopAddresses = new TripStopAddresses();
        initTripStopAutoComplete(acStopLocation);
        if (!address.isEmpty()) {
            setTripStopAddress(Utils.trimString(address), acStopLocation);
        }
        llTripStops.addView(stopView);

        stopView.setTag(stopCount - 1);
        if (address.isEmpty()) {
            tripStopAddresses.add(null);
            acStopLocation.requestFocus();
        }

        ivClearStopTextMap.setOnClickListener(v -> {
            try {
                LinearLayout linearParent = (LinearLayout) v.getParent().getParent();
                LinearLayout linearChild = (LinearLayout) v.getParent();
                if (!tripStopAddresses.isEmpty() && tripStopAddresses.size() >= Integer.parseInt(linearParent.getTag().toString())) {
                    tripStopAddresses.remove(Integer.parseInt(linearParent.getTag().toString()));
                } else {
                    tripStopAddresses.clear();
                }
                linearParent.removeView(linearChild);
                stopCount -= 1;
                llAddStop.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        selectedTripStop = acStopLocation;

        acStopLocation.setOnFocusChangeListener((v, hasFocus) -> {
            selectedTripStop = v.findViewById(R.id.acStopLocation);
            addressRequestCode = Const.TRIP_STOP_ADDRESS;
            LinearLayout linearParent = (LinearLayout) v.getParent().getParent();
            selectedStopPosition = Integer.parseInt(linearParent.getTag().toString());
        });

        stopCount += 1;
        addressRequestCode = Const.TRIP_STOP_ADDRESS;

        if (stopCount > preferenceHelper.getMultipleStopsCount()) {
            llAddStop.setVisibility(View.GONE);
        }
    }

    private void addStopToList(TripStopAddresses stopAddresses) {
        if (!tripStopAddresses.isEmpty()) {
            for (int i = 0; i < llTripStops.getChildCount(); i++) {
                View stopView = llTripStops.getChildAt(i);
                MyFontAutocompleteView acStopLocation = stopView.findViewById(R.id.acStopLocation);
                if (acStopLocation != null && acStopLocation.hasFocus() && tripStopAddresses.size() > i) {
                    tripStopAddresses.set(i, stopAddresses);
                    break;
                }
            }
        }
    }

    /**
     * Used for initialize destination_pin autocomplete.
     * use for set autocomplete adapter.
     *
     * @param acStopLocation
     */
    private void initTripStopAutoComplete(MyFontAutocompleteView acStopLocation) {
        acStopLocation.setAdapter(autocompleteAdapter);
        acStopLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeybord();
                stopAddresses = new TripStopAddresses();
                setTripStopAddress(acStopLocation.getText().toString(), acStopLocation);
                getLocationFromPlaceId(autocompleteAdapter.getPlaceId(position), Const.TRIP_STOP_ADDRESS);
            }
        });
    }

    private void goWithConfirmAddress() {
        if (!preferenceHelper.getAllowMultipleStops()) {
            if (!TextUtils.isEmpty(addressUtils.getPickupAddress()) && !TextUtils.isEmpty(addressUtils.getDestinationAddress()) && addressUtils.getPickupLatLng() != null && addressUtils.getDestinationLatLng() != null) {
                goToMapFragment(Const.RESULT_OK);
            } else if (TextUtils.isEmpty(addressUtils.getPickupAddress()) && addressUtils.getPickupLatLng() == null) {
                Utils.showToast(getResources().getString(R.string.msg_plz_select_pic_up_add), this);
            }
        }
    }

    private void openFavAddressDialog(final int addressRequestCode, LatLng addressLatLng, String address) {
        hideKeybord();
        if (dialogFavAddress != null && dialogFavAddress.isShowing()) {
            return;
        }

        dialogFavAddress = new CustomAddressChooseDialog(DestinationSelectionActivity.this, addressLatLng, address, addressRequestCode) {

            @Override
            public void setSavedData(String address, LatLng latLng, int addressRequestCode) {
                switch (addressRequestCode) {
                    case Const.PICK_UP_ADDRESS:
                        setPickUpAddress(address);
                        addressUtils.setPickupLatLng(latLng);
                        goWithConfirmAddress();
                        break;
                    case Const.DESTINATION_ADDRESS:
                        setDestinationAddress(address);
                        addressUtils.setDestinationLatLng(latLng);
                        goWithConfirmAddress();
                        break;
                    case Const.TRIP_STOP_ADDRESS:
                        stopAddresses = new TripStopAddresses();
                        setTripStopAddress(address, selectedTripStop);
                        List<Double> stopLatLng = new ArrayList<>();
                        stopLatLng.add(latLng.latitude);
                        stopLatLng.add(latLng.longitude);
                        stopAddresses.setLocation(stopLatLng);
                        addStopToList(stopAddresses);
                        break;
                    default:
                        saveFavouriteAddress(addressRequestCode, address, latLng);
                        break;
                }
            }
        };

        dialogFavAddress.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
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

    private void goToMapFragment(int resultCode) {
        hideKeybord();
        setResult(resultCode);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setPlaceFilter(String countryCode, Location location) {
        if (autocompleteAdapter != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            RectangularBounds latLngBounds = RectangularBounds.newInstance(latLng, latLng);
            autocompleteAdapter.setBounds(latLngBounds);
            autocompleteAdapter.setPlaceFilter(countryCode);
        }
    }

    private void deleteAddress(final boolean isHomeAddress) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            if (isHomeAddress) {
                jsonObject.put(Const.Params.HOME_ADDRESS, "");
                jsonObject.put(Const.Params.HOME_LATITUDE, 0);
                jsonObject.put(Const.Params.HOME_LONGITUDE, 0);
            } else {
                jsonObject.put(Const.Params.WORK_ADDRESS, "");
                jsonObject.put(Const.Params.WORK_LATITUDE, 0);
                jsonObject.put(Const.Params.WORK_LONGITUDE, 0);
            }

            Utils.showCustomProgressDialog(this, "", false, null);
            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).saveAddress(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        Utils.hideCustomProgressDialog();
                        if (response.body().isSuccess()) {
                            if (response.body().isSuccess()) {
                                if (isHomeAddress) {
                                    tvDeleteHome.setVisibility(View.GONE);
                                    tvHomeAddress.setText(getString(R.string.text_add_home));
                                    tvHomeAddress.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(DestinationSelectionActivity.this, R.drawable.plus), null);
                                    addressUtils.clearHomeAddress();
                                } else {
                                    tvDeleteWork.setVisibility(View.GONE);
                                    tvWorkAddress.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(DestinationSelectionActivity.this, R.drawable.plus), null);
                                    tvWorkAddress.setText(getString(R.string.text_add_work));
                                    addressUtils.clearWorkAddress();
                                }
                            }
                        }
                    }


                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(SettingActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException("Setting Activity", e);
        }
    }

    private void saveFavouriteAddress(final int addressRequestCode, final String longFavAddress, final LatLng addressLatlng) {
        Utils.showCustomProgressDialog(this, "", false, null);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            if (addressRequestCode == Const.HOME_ADDRESS) {
                jsonObject.put(Const.Params.HOME_ADDRESS, longFavAddress);
                jsonObject.put(Const.Params.HOME_LATITUDE, addressLatlng.latitude);
                jsonObject.put(Const.Params.HOME_LONGITUDE, addressLatlng.longitude);
            } else {
                jsonObject.put(Const.Params.WORK_ADDRESS, longFavAddress);
                jsonObject.put(Const.Params.WORK_LATITUDE, addressLatlng.latitude);
                jsonObject.put(Const.Params.WORK_LONGITUDE, addressLatlng.longitude);
            }


            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).saveAddress(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        Utils.hideCustomProgressDialog();
                        if (response.body().isSuccess()) {
                            if (addressRequestCode == Const.HOME_ADDRESS) {
                                addressUtils.setHomeAddress(longFavAddress);
                                addressUtils.setTrimmedHomeAddress(Utils.trimString(longFavAddress));
                                addressUtils.setHomeLatitude(addressLatlng.latitude);
                                addressUtils.setHomeLongitude(addressLatlng.longitude);
                                tvHomeAddress.setText(addressUtils.getTrimmedHomeAddress());
                                tvDeleteHome.setVisibility(View.VISIBLE);
                                tvHomeAddress.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                            } else {
                                addressUtils.setWorkAddress(longFavAddress);
                                addressUtils.setTrimmedWorkAddress(Utils.trimString(longFavAddress));
                                addressUtils.setWorkLatitude(addressLatlng.latitude);
                                addressUtils.setWorkLongitude(addressLatlng.longitude);
                                tvWorkAddress.setText(addressUtils.getTrimmedWorkAddress());
                                tvDeleteWork.setVisibility(View.VISIBLE);
                                tvWorkAddress.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                            }
                        }
                    }


                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(CustomAddressChooseDialog.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException("DialogFavAddress", e);
        }
    }

    private void selectSavedAddress(String address, LatLng latLng, int addressRequestCode) {
        switch (addressRequestCode) {
            case Const.PICK_UP_ADDRESS:
                setPickUpAddress(address);
                addressUtils.setPickupLatLng(latLng);
                goWithConfirmAddress();
                break;
            case Const.DESTINATION_ADDRESS:
                setDestinationAddress(address);
                addressUtils.setDestinationLatLng(latLng);
                goWithConfirmAddress();
                break;
            case Const.TRIP_STOP_ADDRESS:
                setTripStopAddress(address, selectedTripStop);
                List<Double> stopLatLng = new ArrayList<>();
                stopLatLng.add(latLng.latitude);
                stopLatLng.add(latLng.longitude);
                stopAddresses.setLocation(stopLatLng);
                addStopToList(stopAddresses);
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }



//    @Override
//    public void onClick(String position) {
//        acDestinationAddress.setText(position);
//    }

    private class GetCountryFromLocation extends AsyncTask<LatLng, Integer, Address> {

        @Override
        protected Address doInBackground(LatLng... latLngs) {
            Geocoder gCoder = new Geocoder(getBaseContext(), new Locale("en_US"));
            LatLng latLng = latLngs[0];
            try {
                final List<Address> list = gCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (list != null && list.size() > 0) {
                    return list.get(0);
                }
            } catch (IOException e) {
                AppLog.handleException(DestinationSelectionActivity.class.getSimpleName(), e);
            }
            return null;
        }


        @Override
        protected void onPostExecute(Address address) {
            super.onPostExecute(address);
            if (address != null) {
                updateCityData(address.getCountryName());
                if (!preferenceHelper.getAllowMultipleStops() && !TextUtils.isEmpty(acDestinationAddress.getText().toString().trim()) && AddressUtilsEber.getInstance().getDestinationLatLng() != null) {
                    goToMapFragment(Const.RESULT_OK);
                }
            }
        }
    }

    private class GetPickUpAddressFromLocation extends AsyncTask<LatLng, Integer, Address> {

        @Override
        protected Address doInBackground(LatLng... latLngs) {
            Geocoder gCoder = new Geocoder(getBaseContext(), new Locale("en_US"));
            LatLng latLng = latLngs[0];
            try {
                final List<Address> list = gCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (list != null && !list.isEmpty()) {
                    return list.get(0);
                }
            } catch (Exception e) {
                AppLog.handleException(DestinationSelectionActivity.class.getSimpleName(), e);
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
                    acPickupAddress.setText(Utils.trimString(strAddress));
                    setPickUpAddress(strAddress);
                    updateCityData(address.getCountryName());
                }
                AddressUtilsEber.getInstance().setCountryCode(address.getCountryCode());
            }
        }
    }
}