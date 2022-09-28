package com.elluminati.eber;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import com.elluminati.eber.components.CustomDialogEnable;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.Country;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.responsemodels.UserDataResponse;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.LocationHelper;
import com.elluminati.eber.utils.PreferenceHelper;
import com.elluminati.eber.utils.ServerConfig;
import com.elluminati.eber.utils.SocketHelper;
import com.elluminati.eber.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseAppCompatActivity implements LocationHelper.OnLocationReceived {
    public GoogleSignInClient googleSignInClient;
    private LocationHelper locationHelper;
    private MyFontTextView tvCountryCode, ivGetMovingWith;
    private LinearLayout llContactNumber;
    private ArrayList<Country> codeArrayList;
    private CustomDialogEnable customDialogEnable;
    private Location currentLocation;
    private CallbackManager callbackManager;
    private String socialId, socialEmail = "", socialPhotoUrl, socialFirstName, socialLastName, loginBy = Const.MANUAL;
    private Country country;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        SocketHelper.getInstance().socketDisconnect();
        codeArrayList = parseContent.getRawCountryCodeList();
        CurrentTrip.getInstance().setCountryCodes(codeArrayList);
        locationHelper = new LocationHelper(this);
        locationHelper.setLocationReceivedLister(this);
        tvCountryCode = findViewById(R.id.tvCountryCode);
        ivGetMovingWith = findViewById(R.id.ivGetMovingWith);
        llContactNumber = findViewById(R.id.llContactNumber);
        llContactNumber.setOnClickListener(this);
        ivGetMovingWith.setText(getString(R.string.text_get_moving_with) + " " + getString(R.string.app_name));
        LoginManager.getInstance().logOut();
        initFBLogin();
        initGoogleLogin();
        setCountry(0);
        checkPlayServices();
        findViewById(R.id.llSocialLogin).setVisibility(preferenceHelper.getIsUserSocialLogin() ? View.VISIBLE : View.GONE);
//        getServicesCountry();

        if (BuildConfig.APPLICATION_ID.equalsIgnoreCase("com.elluminatiinc.taxi.user")) {
            findViewById(R.id.ivAppLogo).setOnTouchListener(new View.OnTouchListener() {

                final Handler handler = new Handler();
                int numberOfTaps = 0;
                long lastTapTimeMs = 0;
                long touchDownMs = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchDownMs = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_UP:
                            handler.removeCallbacksAndMessages(null);
                            if (System.currentTimeMillis() - touchDownMs > ViewConfiguration.getTapTimeout()) {
                                numberOfTaps = 0;
                                lastTapTimeMs = 0;
                            }
                            if (numberOfTaps > 0
                                    && System.currentTimeMillis() - lastTapTimeMs < ViewConfiguration.getDoubleTapTimeout()
                            ) {
                                numberOfTaps += 1;
                            } else {
                                numberOfTaps = 1;
                            }
                            lastTapTimeMs = System.currentTimeMillis();

                            if (numberOfTaps == 3) {
                                showServerDialog();
                            }
                    }
                    return true;
                }
            });
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 12).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected boolean isValidate() {
        return false;
    }

    @Override
    public void goWithBackArrow() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llContactNumber:
                goToLoginActivity(true);
                break;
            default:
                break;
        }
    }

    private void getFacebookProfileDetail(AccessToken accessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Utils.hideCustomProgressDialog();
                try {
                    socialPhotoUrl = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?width=250&height=250").toString();

                    if (object.has("email")) {
                        socialEmail = object.getString("email");
                    }
                    socialId = object.getString("id");
                    socialFirstName = object.getString("first_name");
                    socialLastName = object.getString("last_name");
                    LoginManager.getInstance().logOut();
                    socialSignIn(Const.SOCIAL_FACEBOOK);

                } catch (MalformedURLException | JSONException e) {
                    e.printStackTrace();
                    socialPhotoUrl = "";
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

    }

    private void socialSignIn(String loginType) {
        loginBy = loginType;
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_waiting_for_sing_in), false, null);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Const.Params.PASSWORD, "");
            jsonObject.put(Const.Params.EMAIL, socialEmail);
            jsonObject.put(Const.Params.SOCIAL_UNIQUE_ID, socialId);
            jsonObject.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
            jsonObject.put(Const.Params.DEVICE_TOKEN, preferenceHelper.getDeviceToken());
            jsonObject.put(Const.Params.LOGIN_BY, loginType);
            jsonObject.put(Const.Params.APP_VERSION, getAppVersion());


            Call<UserDataResponse> call = ApiClient.getClient().create(ApiInterface.class).login(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<UserDataResponse>() {
                @Override
                public void onResponse(Call<UserDataResponse> call, Response<UserDataResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        CurrentTrip.getInstance().clear();
                        if (parseContent.saveUserData(response.body(), true, true)) {
                            Utils.hideCustomProgressDialog();
                            moveWithUserSpecificPreference();
                            generateFirebaseAccessToken();
                        } else {
                            Utils.hideCustomProgressDialog();
                            if (!TextUtils.equals(loginBy, Const.MANUAL)) {
                                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                                intent.putExtra(Const.Params.FIRST_NAME, socialFirstName);
                                intent.putExtra(Const.Params.LAST_NAME, socialLastName);
                                intent.putExtra(Const.Params.SOCIAL_UNIQUE_ID, socialId);
                                intent.putExtra(Const.Params.EMAIL, socialEmail);
                                intent.putExtra(Const.Params.PICTURE, socialPhotoUrl);
                                intent.putExtra(Const.Params.LOGIN_BY, loginBy);
                                intent.putExtra(Const.Params.IS_VERIFIED, false);
                                goToRegisterActivity(intent);
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<UserDataResponse> call, Throwable t) {
                    AppLog.handleThrowable(MainActivity.class.getSimpleName(), t);

                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.SIGN_IN_ACTIVITY, e);
        }

    }


    private void googleSignIn() {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, Const.google.RC_SIGN_IN);
        });
    }

    private void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        if (task.isSuccessful()) {
            try {
                GoogleSignInAccount account = task.getResult();
                socialId = account.getId();
                socialEmail = account.getEmail();
                String str = account.getDisplayName();
                if (str.trim().contains(" ")) {
                    String[] name = str.split("\\s+");
                    socialFirstName = name[0].trim();
                    socialLastName = name[1].trim();
                } else {
                    socialFirstName = str.trim();
                    socialLastName = "";
                }
                socialPhotoUrl = account.getPhotoUrl().toString();
                socialSignIn(Const.SOCIAL_GOOGLE);
            } catch (Exception e) {
                socialSignIn(Const.SOCIAL_GOOGLE);
                AppLog.handleException(Const.Tag.SIGN_IN_ACTIVITY, e);
            }
        } else {
            Utils.showToast(getString(R.string.message_can_not_signin_google), MainActivity.this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.PERMISSION_FOR_LOCATION) {
            checkPermission();
        } else if (requestCode == Const.google.RC_SIGN_IN) {
            handleGoogleSignInResult(data);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onAdminApproved() {
        //do something
    }

    @Override
    public void onAdminDeclined() {
        //do something
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
    public void onLocationChanged(Location location) {
        if (currentLocation == null) {
            currentLocation = location;
            if (currentLocation != null) {
                new GetCityAsyncTask().execute();
            } else {
                setCountry(0);
            }
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Const.PERMISSION_FOR_LOCATION);
        } else {
            locationHelper.onStart();
            loadCountryList();
        }
    }

    private void loadCountryList() {
        locationHelper.getLastLocation(location -> {
            currentLocation = location;
            if (currentLocation != null) {
                new GetCityAsyncTask().execute();
            } else {
                setCountry(0);
            }
        });
    }

    private void setCountry(int position) {
        country = codeArrayList.get(position);
        tvCountryCode.setText(country.getCountryPhoneCode());
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
                    //Do som thing
                    break;
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
    }

    private void goWithLocationPermission(int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Do the stuff that requires permission...
            loadCountryList();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                openPermissionDialog();
            } else {
                openPermissionNotifyDialog(Const.PERMISSION_FOR_LOCATION);
            }
        }
    }

    private void openPermissionDialog() {
        if (customDialogEnable != null && customDialogEnable.isShowing()) {
            return;
        }
        customDialogEnable = new CustomDialogEnable(this, getResources().getString(R.string.msg_reason_for_permission_location), getString(R.string.text_i_am_sure), getString(R.string.text_re_try)) {
            @Override
            public void doWithEnable() {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Const.PERMISSION_FOR_LOCATION);
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

    private void closedPermissionDialog() {
        if (customDialogEnable != null && customDialogEnable.isShowing()) {
            customDialogEnable.dismiss();
            customDialogEnable = null;

        }
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

    private void getCountryCodeList(String country) {
        int countryListSize = codeArrayList.size();
        for (int i = 0; i < countryListSize; i++) {
            if (codeArrayList.get(i).getCountryName().toUpperCase().startsWith(country.toUpperCase())) {
                setCountry(i);
                return;
            }
        }
        setCountry(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationHelper.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationHelper.onStart();
    }

    private void goToLoginActivity(boolean isAnimated) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Const.Params.COUNTRY, country);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isAnimated) {
            Pair<View, String> p1 = Pair.create(findViewById(R.id.llContactNumber), getResources().getString(R.string.transition_string_text));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1);
            ActivityCompat.startActivity(this, intent, options.toBundle());
        } else {
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }


    private void goToRegisterActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

   /* private void getServicesCountry() {
        Utils.showCustomProgressDialog(this, "", false, null);
        Call<CountriesResponse> call = ApiClient.getClient().create(ApiInterface.class).getCountries();
        call.enqueue(new Callback<CountriesResponse>() {
            @Override
            public void onResponse(Call<CountriesResponse> call, Response<CountriesResponse> response) {
                if (parseContent.isSuccessful(response)) {
                    Utils.hideCustomProgressDialog();
                    if (response.body().isSuccess()) {
                        for (Country countryCodeWeb : response.body().getCountry()) {
                            for (Country countryCodeLocal : codeArrayList) {
                                if (TextUtils.equals(countryCodeWeb.getCountryPhoneCode(), countryCodeLocal.getCountryPhoneCode())) {
                                    countryCodeLocal.setPhoneNumberLength(countryCodeWeb.getPhoneNumberLength());
                                    countryCodeLocal.setPhoneNumberMinLength(countryCodeWeb.getPhoneNumberMinLength());
                                }
                            }

                        }
                    } else {
                        Utils.showErrorToast(response.body().getErrorCode(), MainActivity.this);
                    }
                    checkPermission();
                }


            }

            @Override
            public void onFailure(Call<CountriesResponse> call, Throwable t) {
                Utils.hideCustomProgressDialog();
                AppLog.handleThrowable(ProfileActivity.class.getSimpleName(), t);
                checkPermission();
            }
        });
    }*/

    private void initFBLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginButton faceBookLogin = findViewById(R.id.btnFbLogin);
        faceBookLogin.setPermissions(Arrays.asList("email", "public_profile"));
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, final AccessToken currentAccessToken) {
                if (currentAccessToken != null && !currentAccessToken.isExpired()) {
                    getFacebookProfileDetail(AccessToken.getCurrentAccessToken());
                }
            }
        };
        accessTokenTracker.startTracking();

    }

    private void initGoogleLogin() {
        SignInButton btnGoogleSingIn;
        btnGoogleSingIn = findViewById(R.id.btnGoogleLogin);
        btnGoogleSingIn.setSize(SignInButton.SIZE_WIDE);
        btnGoogleSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accessTokenTracker != null) {
            accessTokenTracker.stopTracking();
        }
    }

    protected class GetCityAsyncTask extends AsyncTask<String, Void, Address> {

        @Override
        protected Address doInBackground(String... params) {

            Geocoder geocoder = new Geocoder(MainActivity.this, new Locale("en_US"));
            try {
                List<Address> addressList = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                if (addressList != null && !addressList.isEmpty()) {
                    return addressList.get(0);
                }

            } catch (IOException | IllegalArgumentException e) {
                AppLog.handleException(TAG, e);
                publishProgress();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            setCountry(0);
        }

        @Override
        protected void onPostExecute(Address address) {
            if (address != null) {
                getCountryCodeList(address.getCountryName() == null ? "" : address.getCountryName());
            }
        }
    }

    private void showServerDialog() {
        final Dialog serverDialog = new Dialog(this);
        serverDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        serverDialog.setContentView(R.layout.dialog_server);

        RadioGroup dialogRadioGroup = serverDialog.findViewById(R.id.dialogRadioGroup);
        RadioButton rbServer1 = serverDialog.findViewById(R.id.rbServer1);
        RadioButton rbServer2 = serverDialog.findViewById(R.id.rbServer2);
        RadioButton rbServer3 = serverDialog.findViewById(R.id.rbServer3);

        switch (ServerConfig.BASE_URL) {
            case "https://eber.appemporio.net/":
                rbServer1.setChecked(true);
                break;
            case "https://staging.appemporio.net/":
                rbServer2.setChecked(true);
                break;
            case "https://eberdeveloper.appemporio.net/":
                rbServer3.setChecked(true);
                break;
        }

        serverDialog.findViewById(R.id.btnCancel).setOnClickListener(v -> serverDialog.dismiss());
        serverDialog.findViewById(R.id.btnOk).setOnClickListener(v -> {
            int id = dialogRadioGroup.getCheckedRadioButtonId();
            if (id == R.id.rbServer1) {
                ServerConfig.BASE_URL = "https://eber.appemporio.net/";
            } else if (id == R.id.rbServer2) {
                ServerConfig.BASE_URL = "https://staging.appemporio.net/";
            } else if (id == R.id.rbServer3) {
                ServerConfig.BASE_URL = "https://eberdeveloper.appemporio.net/";
            }

            serverDialog.dismiss();

            if (!ServerConfig.BASE_URL.equals(PreferenceHelper.getInstance(this).getBaseUrl())) {
                PreferenceHelper.getInstance(this).putBaseUrl(ServerConfig.BASE_URL);
                goToSplashActivity();
            }
        });
        WindowManager.LayoutParams params = serverDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        serverDialog.getWindow().setAttributes(params);
        serverDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        serverDialog.setCancelable(false);
        serverDialog.show();
    }
}