package com.elluminati.eber;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.adapter.EmergencyContactAdapter;
import com.elluminati.eber.components.CustomDialogEnable;
import com.elluminati.eber.components.CustomLanguageDialog;
import com.elluminati.eber.components.MyFontButton;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.EmergencyContact;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.responsemodels.AllEmergencyContactsResponse;
import com.elluminati.eber.models.responsemodels.EmergencyContactResponse;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.PreferenceHelper;
import com.elluminati.eber.utils.ServerConfig;
import com.elluminati.eber.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingActivity extends BaseAppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    SwitchCompat switchTripStatusSound, switchDriverArrivedSound, switchPushNotificationSound;
    private MyFontTextView tvVersion, tvLanguage;
    private CustomLanguageDialog customLanguageDialog;
    private MyFontButton btnAddContact;
    private RecyclerView rcvEmergencyContact;
    private ArrayList<EmergencyContact> contactList;
    private MyFontTextView tvMaxContacts;
    private EmergencyContactAdapter emergencyContactAdapter;
    private CustomDialogEnable customDialogEnable;
    private LinearLayout llNotification;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initToolBar();
        setTitleOnToolbar(getResources().getString(R.string.text_settings));
        tvVersion = findViewById(R.id.tvVersion);
        switchTripStatusSound = findViewById(R.id.switchTripStatusSound);
        switchDriverArrivedSound = findViewById(R.id.switchDriverArrivedSound);
        switchPushNotificationSound = findViewById(R.id.switchPushNotificationSound);
        tvLanguage = findViewById(R.id.tvLanguage);
        llNotification = findViewById(R.id.llNotification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            llNotification.setVisibility(View.GONE);
        } else {
            llNotification.setVisibility(View.VISIBLE);
        }
        switchPushNotificationSound.setOnCheckedChangeListener(this);
        switchDriverArrivedSound.setOnCheckedChangeListener(this);
        switchTripStatusSound.setOnCheckedChangeListener(this);
        tvLanguage.setOnClickListener(this);
        switchTripStatusSound.setChecked(preferenceHelper.getIsTripStatusSoundOn());
        switchDriverArrivedSound.setChecked(preferenceHelper.getIsDriverArrivedSoundOn());
        switchPushNotificationSound.setChecked(preferenceHelper.getIsPushNotificationSoundOn());
        tvVersion.setText(getAppVersion());
        btnAddContact = findViewById(R.id.btnAddContact);
        tvMaxContacts = findViewById(R.id.tvMaxContacts);
        btnAddContact.setOnClickListener(this);
        findViewById(R.id.llLanguage).setOnClickListener(this);

        if (BuildConfig.APPLICATION_ID.equalsIgnoreCase("com.elluminatiinc.taxi.user")) {
            findViewById(R.id.llAppVersion).setOnTouchListener(new View.OnTouchListener() {

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

        setLanguageName();

        initRcvEmergencyContact();

        getEmergencyContact();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
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
            case R.id.llLanguage:
                openLanguageDialog();
                break;
            case R.id.btnAddContact:
                checkPermission();
                break;
            default:
                //default case here..
                break;
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, Const.PERMISSION_FOR_CONTACTS);
        } else {
            //Do the stuff that requires permission...
            if (contactList != null && contactList.size() < 5) {
                getPhoneBook(Const.RQS_PICK_CONTACT);
            } else {
                Utils.showToast(getString(R.string.msg_add_max_5_contact), this);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchTripStatusSound:
                preferenceHelper.putIsTripStatusSoundOn(isChecked);
                break;
            case R.id.switchDriverArrivedSound:
                preferenceHelper.putIsDriverArrivedSoundOn(isChecked);
                break;
            case R.id.switchPushNotificationSound:
                preferenceHelper.putIsPushNotificationSoundOn(isChecked);
                break;


        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                logOut(true);
            }
        });
        WindowManager.LayoutParams params = serverDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        serverDialog.getWindow().setAttributes(params);
        serverDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        serverDialog.setCancelable(false);
        serverDialog.show();
    }

    private void openLanguageDialog() {
        if (customLanguageDialog != null && customLanguageDialog.isShowing()) {
            return;
        }
        customLanguageDialog = new CustomLanguageDialog(this) {
            @Override
            public void onSelect(String languageName, String languageCode) {
                tvLanguage.setText(languageName);
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

    private void setLanguageName() {
        TypedArray array = getResources().obtainTypedArray(R.array.language_code);
        TypedArray array2 = getResources().obtainTypedArray(R.array.language_name);
        int size = array.length();
        for (int i = 0; i < size; i++) {
            if (TextUtils.equals(preferenceHelper.getLanguageCode(), array.getString(i))) {
                tvLanguage.setText(array2.getString(i));
                break;
            }
        }

    }

    private void initRcvEmergencyContact() {
        contactList = new ArrayList<>();
        contactList.clear();
        emergencyContactAdapter = new EmergencyContactAdapter(this, contactList) {
            @Override
            public void onClickRemove(int position) {
                deleteEmergencyContact(contactList.get(position).getId(), position);
            }

            @Override
            public void onToggleSwitch(int position, boolean isChecked) {
                if (isChecked) {
                    updateEmergencyContact(contactList.get(position).getName(), contactList.get(position).getPhone(), contactList.get(position).getId(), Const.TRUE, position);
                } else {
                    updateEmergencyContact(contactList.get(position).getName(), contactList.get(position).getPhone(), contactList.get(position).getId(), Const.FALSE, position);
                }
            }

        };
        rcvEmergencyContact = findViewById(R.id.rcvEmergencyContact);
        rcvEmergencyContact.setLayoutManager(new LinearLayoutManager(this));
        rcvEmergencyContact.setAdapter(emergencyContactAdapter);
        rcvEmergencyContact.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rcvEmergencyContact.setNestedScrollingEnabled(false);
    }

    private void deleteEmergencyContact(String id, final int position) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_loading), false, null);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.EMERGENCY_CONTACT_DETAIL_ID, id);
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).deleteEmergencyContact(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            contactList.remove(position);
                            emergencyContactAdapter.notifyDataSetChanged();
                            updateUiContact(contactList);
                            Utils.hideCustomProgressDialog();
                        } else {
                            Utils.hideCustomProgressDialog();
                        }
                    }
                }

                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(SettingActivity.class.getSimpleName(), t);
                }
            });

        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.EMERGENCY_CONTACT, e);
        }

    }

    private void addEmergencyContact(String name, String number) {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_loading), false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.PHONE, number);
            jsonObject.put(Const.Params.NAME, name);
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.IS_ALWAYS_SHARE_RIDE_DETAIL, Const.TRUE);

            Call<EmergencyContactResponse> call = ApiClient.getClient().create(ApiInterface.class).addEmergencyContact(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<EmergencyContactResponse>() {
                @Override
                public void onResponse(Call<EmergencyContactResponse> call, Response<EmergencyContactResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            contactList.add(response.body().getEmergencyContactData());
                            updateUiContact(contactList);
                            emergencyContactAdapter.notifyDataSetChanged();
                            Utils.hideCustomProgressDialog();
                        } else {
                            Utils.hideCustomProgressDialog();
                            Utils.showErrorToast(response.body().getErrorCode(), SettingActivity.this);
                        }
                    }

                }

                @Override
                public void onFailure(Call<EmergencyContactResponse> call, Throwable t) {
                    AppLog.handleThrowable(SettingActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.EMERGENCY_CONTACT, e);
        }

    }

    private void updateEmergencyContact(String name, String number, String id, int isAlwaysShareRideDetail, final int position) {
        Utils.showCustomProgressDialog(this, "", false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.PHONE, number);
            jsonObject.put(Const.Params.NAME, name);
            jsonObject.put(Const.Params.EMERGENCY_CONTACT_DETAIL_ID, id);
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.IS_ALWAYS_SHARE_RIDE_DETAIL, isAlwaysShareRideDetail);
            Call<EmergencyContactResponse> call = ApiClient.getClient().create(ApiInterface.class).updateEmergencyContact(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<EmergencyContactResponse>() {
                @Override
                public void onResponse(Call<EmergencyContactResponse> call, Response<EmergencyContactResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            EmergencyContact contact = contactList.get(position);
                            contact.setIsAlwaysShareRideDetail(response.body().getEmergencyContactData().getIsAlwaysShareRideDetail());
                            Utils.hideCustomProgressDialog();
                        } else {
                            Utils.hideCustomProgressDialog();
                            Utils.showErrorToast(response.body().getErrorCode(), SettingActivity.this);
                        }

                    }

                }

                @Override
                public void onFailure(Call<EmergencyContactResponse> call, Throwable t) {
                    AppLog.handleThrowable(SettingActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.EMERGENCY_CONTACT, e);
        }
    }

    private void getPhoneBook(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone
        // .CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, requestCode);
    }

    private void updateUiContact(ArrayList<EmergencyContact> contactList) {
        if (contactList.size() >= 5) {
            tvMaxContacts.setVisibility(View.GONE);
        } else {
            tvMaxContacts.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            switch (requestCode) {
                case Const.PERMISSION_FOR_CONTACTS:
                    goWithContactsPermission(grantResults);
                    break;
                default:

                    break;
            }
        }

    }

    private void goWithContactsPermission(int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Do the stuff that requires permission...
            getPhoneBook(Const.RQS_PICK_CONTACT);
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
                openPermissionDialog();
            } else {
                openPermissionNotifyDialog(Const.PERMISSION_FOR_CONTACTS);
            }
        }
    }

    private void openPermissionDialog() {
        if (customDialogEnable != null && customDialogEnable.isShowing()) {
            return;
        }
        customDialogEnable = new CustomDialogEnable(this, getResources().getString(R.string.msg_reason_for_permission_contacts), getString(R.string.text_i_am_sure), getString(R.string.text_re_try)) {
            @Override
            public void doWithEnable() {
                ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, Const.PERMISSION_FOR_CONTACTS);
                closedPermissionDialog();
            }

            @Override
            public void doWithDisable() {
                closedPermissionDialog();
                goToMainDrawerActivity();
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

    private void getEmergencyContact() {
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_loading), false, null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());

            Call<AllEmergencyContactsResponse> call = ApiClient.getClient().create(ApiInterface.class).getEmergencyContacts(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<AllEmergencyContactsResponse>() {
                @Override
                public void onResponse(Call<AllEmergencyContactsResponse> call, Response<AllEmergencyContactsResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            contactList.addAll(response.body().getEmergencyContactData());
                            emergencyContactAdapter.notifyDataSetChanged();
                            updateUiContact(contactList);
                            Utils.hideCustomProgressDialog();
                        } else {
                            Utils.hideCustomProgressDialog();
                        }

                    }

                }

                @Override
                public void onFailure(Call<AllEmergencyContactsResponse> call, Throwable t) {
                    AppLog.handleThrowable(SettingActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.EMERGENCY_CONTACT, e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Const.RQS_PICK_CONTACT:

                    String name = null, phone = null;
                    Uri contctDataVar = data.getData();
                    Cursor contctCursorVar = getContentResolver().query(contctDataVar, null, null, null, null);
                    if (contctCursorVar.getCount() > 0) {
                        while (contctCursorVar.moveToNext()) {
                            name = contctCursorVar.getString(contctCursorVar.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            if (Integer.parseInt(contctCursorVar.getString(contctCursorVar.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                phone = contctCursorVar.getString(contctCursorVar.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                for (int i = 0; i < phone.length(); i++) {
                                    phone = phone.replace("-", "");
                                    phone = phone.replace(" ", "");
                                }
                                addEmergencyContact(name, phone);
                            } else {
                                Utils.showToast(getResources().getString(R.string.msg_selected_contact_is_empty), this);
                            }
                        }
                    }
                    break;
                case Const.PERMISSION_FOR_CONTACTS:
                    checkPermission();
                    break;
            }
        }
    }
}