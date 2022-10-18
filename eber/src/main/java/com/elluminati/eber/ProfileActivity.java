package com.elluminati.eber;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.elluminati.eber.components.CustomCircularProgressView;
import com.elluminati.eber.components.CustomCountryDialog;
import com.elluminati.eber.components.CustomDialogBigLabel;
import com.elluminati.eber.components.CustomDialogEnable;
import com.elluminati.eber.components.CustomDialogVerifyAccount;
import com.elluminati.eber.components.CustomDialogVerifyDetail;
import com.elluminati.eber.components.CustomPhotoDialog;
import com.elluminati.eber.components.MyFontEdittextView;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.components.MyFontTextViewMedium;
import com.elluminati.eber.interfaces.OTPListener;
import com.elluminati.eber.models.datamodels.Country;
import com.elluminati.eber.models.responsemodels.CountriesResponse;
import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.models.responsemodels.UserDataResponse;
import com.elluminati.eber.models.responsemodels.VerificationResponse;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.models.validations.Validator;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.FieldValidation;
import com.elluminati.eber.utils.GlideApp;
import com.elluminati.eber.utils.ImageCompression;
import com.elluminati.eber.utils.ImageHelper;
import com.elluminati.eber.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseAppCompatActivity implements OTPListener {

    private MyFontEdittextView etProfileFirstName, etProfileLastName, etProfileAddress, etProfileZipCode, etProfileContectNumber, etProfileEmail, etNewPassword, etProfileCity;
    private LinearLayout llEmail, llNewPassword;
    private ImageView ivProfileImage;
    private String currentPassword, tempContactNumber, otpForSMS;
    private Uri picUri;
    private boolean isUpdate;
    private MyFontTextViewMedium tvProfileCountryCode;
    private CustomCountryDialog customCountryDialog;
    private CustomPhotoDialog customPhotoDialog;
    private CustomDialogEnable customDialogEnable;
    private CustomDialogVerifyAccount verifyDialog;
    private CustomCircularProgressView ivProgressBarProfile;
    private ImageHelper imageHelper;
    private CustomDialogVerifyDetail customDialogVerifyDetail;
    private String uploadImageFilePath = "";
    private LinearLayout llSelectGender, llGender;
    private Spinner selectGender;
    private String gender = "";
    private OtpReader otpReader;
    private Button btnChangePassword;
    private ArrayList<Country> codeArrayList;
    private TextInputLayout tilPassword;
    private MyFontTextView tvDeleteAccount;
    private CustomDialogBigLabel customDialogConfirmation;
    String msg = null;
    private Country country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initToolBar();
        setTitleOnToolbar(getResources().getString(R.string.text_profile));
        setToolbarRightSideIcon(AppCompatResources.getDrawable(this, R.drawable.ic_mode_edit_black_24dp), this);

        imageHelper = new ImageHelper(this);
        isUpdate = false;
        etProfileEmail = findViewById(R.id.etProfileEmail);
        etProfileFirstName = findViewById(R.id.etProfileFirstName);
        etProfileLastName = findViewById(R.id.etProfileLastName);
        etProfileAddress = findViewById(R.id.etProfileAddress);
        etProfileZipCode = findViewById(R.id.etProfileZipCode);
        etProfileContectNumber = findViewById(R.id.etProfileContactNumber);
        etNewPassword = findViewById(R.id.etNewPassword);
        etProfileCity = findViewById(R.id.etProfileCity);
        tvProfileCountryCode = findViewById(R.id.tvProfileCountryCode);
        tvProfileCountryCode.setOnClickListener(this);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivProgressBarProfile = findViewById(R.id.ivProgressBarProfile);
        llEmail = findViewById(R.id.llEmail);
        llNewPassword = findViewById(R.id.llNewPassword);
        llSelectGender = findViewById(R.id.llSelectGender);
        llGender = findViewById(R.id.llGender);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        tilPassword = findViewById(R.id.tilPassword);
        btnChangePassword.setOnClickListener(this);
        ivProfileImage.setOnClickListener(this);
        tvDeleteAccount = findViewById(R.id.tvDeleteAccount);
        tvDeleteAccount.setOnClickListener(this);
        etNewPassword.setEnabled(false);
        etNewPassword.getText().clear();
        setProfileData();
        setEditable(false);
        codeArrayList = parseContent.getRawCountryCodeList();
        getServicesCountry();

        if (!TextUtils.isEmpty(preferenceHelper.getSocialId())) {
            upDateUI();
        }

        llSelectGender.setVisibility(View.GONE);
        llGender.setVisibility(View.GONE);
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tilPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.MULTIPLY);
            }
        });


        etProfileContectNumber.setLongClickable(false);
        etProfileContectNumber.setInputType(InputType.TYPE_CLASS_NUMBER);

//        otpReader = new OtpReader(this, preferenceHelper.getTwilioNumber());
//        registerReceiver(otpReader, new IntentFilter(Const.ACTION_OTP_SMS));
    }

    /**
     * @deprecated
     */
    private void initGenderSelection() {
        selectGender = findViewById(R.id.selectGender);
        String[] typedArray = getResources().getStringArray(R.array.gender);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.item_spinner, typedArray);
        selectGender.setAdapter(arrayAdapter);
        selectGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] typedArray = getResources().getStringArray(R.array.gender_english);
                gender = typedArray[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        //unregisterReceiver(otpReader);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdminApprovedListener(this);
        setConnectivityListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Const.PIC_URI, picUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        picUri = savedInstanceState.getParcelable(Const.PIC_URI);
        if (picUri != null) {
            setEditable(true);
            isUpdate = true;
            setToolbarRightSideIcon(AppCompatResources.getDrawable(this, R.drawable.ic_done_black_24dp), this);
        }


    }

    @Override
    protected boolean isValidate() {
        msg = null;
        Validator emailValidation = FieldValidation.isEmailValid(ProfileActivity.this, etProfileEmail.getText().toString().trim());
        Validator passwordValidation = FieldValidation.isPasswordValid(ProfileActivity.this, etNewPassword.getText().toString().trim());

        if (TextUtils.isEmpty(etProfileFirstName.getText().toString().trim())) {
            msg = getString(R.string.msg_enter_name);
            etProfileFirstName.requestFocus();
            etProfileFirstName.setError(msg);
        } else if (!TextUtils.isEmpty(etNewPassword.getText().toString().trim()) && !passwordValidation.isValid()) {
            msg = passwordValidation.getErrorMsg();
            etNewPassword.setError(msg);
            etNewPassword.requestFocus();
            tilPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
        } else if (TextUtils.isEmpty(etProfileContectNumber.getText().toString().trim())) {
            msg = getString(R.string.msg_enter_number);
            etProfileContectNumber.setError(msg);
            etProfileContectNumber.requestFocus();
        } else if (!Utils.isValidPhoneNumber(etProfileContectNumber.getText().toString(), preferenceHelper.getMinimumPhoneNumberLength(), preferenceHelper.getMaximumPhoneNumberLength())) {
            msg = getString(R.string.msg_enter_valid_number);
            etProfileContectNumber.requestFocus();
            etProfileContectNumber.setError(msg);
        } else if (!TextUtils.isEmpty(etProfileEmail.getText().toString().trim()) || preferenceHelper.getIsUserEmailVerification()) {
            if (!emailValidation.isValid()) {
                msg = emailValidation.getErrorMsg();
                etProfileEmail.requestFocus();
                etProfileEmail.setError(msg);
            }
        }

        return TextUtils.isEmpty(msg);
    }

    @Override
    public void goWithBackArrow() {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivProfileImage:
                openPhotoDialog();
                break;
            case R.id.tvProfileCountryCode:
                //openCountryCodeDialog();
                break;
            case R.id.btnChangePassword:
                if (etNewPassword.isEnabled()) {
                    etNewPassword.setEnabled(false);
                    btnChangePassword.setText(getResources().getString(R.string.text_change));
                    etNewPassword.getText().clear();
                } else {
                    etNewPassword.setEnabled(true);
                    btnChangePassword.setText(getResources().getString(R.string.text_cancel));
                    etNewPassword.requestFocus();
                }
                break;
            case R.id.ivToolbarIcon:
                if (isUpdate) {
                    if (isValidate()) {
                        if (preferenceHelper.getIsUserSMSVerification()) {
                            if (TextUtils.equals(etProfileContectNumber.getText().toString(), preferenceHelper.getContact()) && TextUtils.equals(tvProfileCountryCode.getText().toString(), preferenceHelper.getCountryPhoneCode())) {
                                openVerifyAccountDialog(false);
                            } else {
                                if (TextUtils.equals(tempContactNumber, tvProfileCountryCode.getText().toString() + etProfileContectNumber.getText().toString())) {
                                    openVerifyAccountDialog(false);
                                } else {
                                    OTPVerify();
                                }
                            }
                        } else {
                            openVerifyAccountDialog(false);
                        }
                    }
                } else {
                    setEditable(true);
                    isUpdate = true;
                    setToolbarRightSideIcon(AppCompatResources.getDrawable(ProfileActivity.this, R.drawable.ic_done_black_24dp), this);
                }
                break;
            case R.id.tvDeleteAccount:
                openDeleteAccountConfirmationDialog();
                break;
            default:
                break;
        }
    }

    private void openDeleteAccountConfirmationDialog() {
        if (!this.isFinishing()) {
            if (customDialogConfirmation != null && customDialogConfirmation.isShowing()) {
                return;
            }

            customDialogConfirmation = new CustomDialogBigLabel(this, getString(R.string.text_delete_account), getString(R.string.msg_are_you_sure_delete_account), getString(R.string.text_yes), getString(R.string.text_no)) {

                @Override
                public void positiveButton() {
                    closeDeleteAccountConfirmation();
                    openVerifyAccountDialog(true);
                }

                @Override
                public void negativeButton() {
                    closeDeleteAccountConfirmation();
                }
            };

            customDialogConfirmation.show();
        }
    }

    private void closeDeleteAccountConfirmation() {
        if (customDialogConfirmation != null && customDialogConfirmation.isShowing()) {
            customDialogConfirmation.dismiss();
            customDialogConfirmation = null;
        }
    }

    private void deleteUser() {
        Utils.showCustomProgressDialog(this, "", false, null);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
            jsonObject.put(Const.Params.USER_ID, preferenceHelper.getUserId());
            jsonObject.put(Const.Params.SOCIAL_ID, preferenceHelper.getSocialId());
            jsonObject.put(Const.Params.PASSWORD, currentPassword);

            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).deleteUser(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(@NonNull Call<IsSuccessResponse> call, @NonNull Response<IsSuccessResponse> response) {
                    Utils.hideCustomProgressDialog();
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                preferenceHelper.logout();
                                mAuth.signOut();
                                goToMainActivity();
                            } else {
                                Utils.showErrorToast(response.body().getErrorCode(), ProfileActivity.this);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<IsSuccessResponse> call, @NonNull Throwable t) {
                    AppLog.handleThrowable(TAG, t);
                    Utils.hideCustomProgressDialog();
                }
            });
        } catch (Exception e) {
            AppLog.handleException(TAG, e);
        }
    }

    private void setProfileData() {
        etProfileFirstName.setText(preferenceHelper.getFirstName());
        etProfileLastName.setText(preferenceHelper.getLastName());
        etProfileAddress.setText(preferenceHelper.getAddress());
        etProfileEmail.setText(preferenceHelper.getEmail());
        etProfileContectNumber.setText(preferenceHelper.getContact());
        etProfileCity.setText(preferenceHelper.getUserCity());
        GlideApp.with(this).load(preferenceHelper.getProfilePic()).dontAnimate().placeholder(R.drawable.ellipse).override(200, 200).into(ivProfileImage);
    }

    private void setCountry(Country country) {
        this.country = country;
        tvProfileCountryCode.setText(country.getCountryPhoneCode());
    }

    private void upDateUI() {
        etProfileEmail.setEnabled(false);
        llNewPassword.setVisibility(View.GONE);
    }

    private void choosePhotoFromGallery() {
        if (Build.VERSION.SDK_INT <= 19) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i, Const.ServiceCode.CHOOSE_PHOTO);
        } else if (Build.VERSION.SDK_INT > 19) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, Const.ServiceCode.CHOOSE_PHOTO);
        }
    }

    /**
     * This method is used for crop the image which selected or captured by user.
     */
    private void beginCrop(Uri sourceUri) {
//        CropImage.activity(sourceUri).setGuidelines(com.theartofdev.edmodo.cropper.CropImageView.Guidelines.ON).start(this);
    }

    private void setProfileImage(Uri imageUri) {
        GlideApp.with(this).load(imageUri).fallback(R.drawable.ellipse).into(ivProfileImage);
    }

    private void handleCrop(int resultCode, Intent result) {
//        final CropImage.ActivityResult activityResult = CropImage.getActivityResult(result);
//        if (resultCode == RESULT_OK) {
//            uploadImageFilePath = imageHelper.getRealPathFromURI(activityResult.getUri());
//            new ImageCompression(this).setImageCompressionListener(new ImageCompression.ImageCompressionListener() {
//                @Override
//                public void onImageCompression(String compressionImagePath) {
//                    setProfileImage(activityResult.getUri());
//                    uploadImageFilePath = compressionImagePath;
//
//                }
//            }).execute(uploadImageFilePath);
//
//        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//            Utils.showToast(activityResult.getError().getMessage(), this);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Const.ServiceCode.TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    onCaptureImageResult();
                }
                break;
            case Const.ServiceCode.CHOOSE_PHOTO:
                onSelectFromGalleryResult(data);
                break;
//            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
//                handleCrop(resultCode, data);
//                break;
            case Const.PERMISSION_FOR_CAMERA_AND_EXTERNAL_STORAGE:
                openPhotoDialog();
                break;
            default:
                //do default
                break;

        }
    }

    /**
     * This method is used for handel result after select image from gallery .
     */

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            picUri = data.getData();
            beginCrop(picUri);
        }
    }

    /**
     * This method is used for handel result after captured image from camera .
     */
    private void onCaptureImageResult() {
        beginCrop(picUri);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Utils.isNougat()) {
            picUri = FileProvider.getUriForFile(ProfileActivity.this, this.getApplicationContext().getPackageName(), imageHelper.createImageFile());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            picUri = Uri.fromFile(imageHelper.createImageFile().getAbsoluteFile());
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        startActivityForResult(intent, Const.ServiceCode.TAKE_PHOTO);
    }

    private void updateProfile() {
        HashMap<String, RequestBody> map = new HashMap<>();
        hideKeyPad();
        Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_waiting_for_update_profile), false, null);

        if (TextUtils.isEmpty(preferenceHelper.getSocialId())) {
            map.put(Const.Params.OLD_PASSWORD, ApiClient.makeTextRequestBody(currentPassword));
        } else {
            map.put(Const.Params.OLD_PASSWORD, ApiClient.makeTextRequestBody(""));
        }
        map.put(Const.Params.FIRST_NAME, ApiClient.makeTextRequestBody(etProfileFirstName.getText().toString()));
        map.put(Const.Params.LAST_NAME, ApiClient.makeTextRequestBody(etProfileLastName.getText().toString()));
        map.put(Const.Params.NEW_PASSWORD, ApiClient.makeTextRequestBody(etNewPassword.getText().toString()));
        map.put(Const.Params.PHONE, ApiClient.makeTextRequestBody(etProfileContectNumber.getText().toString()));
        map.put(Const.Params.USER_ID, ApiClient.makeTextRequestBody(preferenceHelper.getUserId()));
        map.put(Const.Params.EMAIL, ApiClient.makeTextRequestBody(etProfileEmail.getText().toString()));
        map.put(Const.Params.COUNTRY_PHONE_CODE, ApiClient.makeTextRequestBody(tvProfileCountryCode.getText().toString()));
        map.put(Const.Params.CITY, ApiClient.makeTextRequestBody(etProfileCity.getText().toString().trim()));
        map.put(Const.Params.TOKEN, ApiClient.makeTextRequestBody(preferenceHelper.getSessionToken()));
        Call<UserDataResponse> userDataResponseCall;
        if (!TextUtils.isEmpty(uploadImageFilePath)) {
            userDataResponseCall = ApiClient.getClient().create(ApiInterface.class).updateProfile(ApiClient.makeMultipartRequestBody(this, uploadImageFilePath, Const.Params.PICTURE_DATA), map);
        } else {
            userDataResponseCall = ApiClient.getClient().create(ApiInterface.class).updateProfile(null, map);
        }

        userDataResponseCall.enqueue(new Callback<UserDataResponse>() {
            @Override
            public void onResponse(Call<UserDataResponse> call, Response<UserDataResponse> response) {
                if (parseContent.isSuccessful(response)) {
                    Utils.hideCustomProgressDialog();
                    if (parseContent.saveUserData(response.body(), false, true)) {
                        onBackPressed();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserDataResponse> call, Throwable t) {
                AppLog.handleThrowable(ProfileActivity.class.getSimpleName(), t);
            }
        });

    }

    protected void openPhotoDialog() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, Const.PERMISSION_FOR_CAMERA_AND_EXTERNAL_STORAGE);
        } else {
            //Do the stuff that requires permission...
            customPhotoDialog = new CustomPhotoDialog(this) {
                @Override
                public void clickedOnCamera() {
                    customPhotoDialog.dismiss();
                    takePhotoFromCamera();
                }

                @Override
                public void clickedOnGallery() {
                    customPhotoDialog.dismiss();
                    choosePhotoFromGallery();
                }
            };
            customPhotoDialog.show();
        }


    }

    private void setEditable(boolean isEditable) {
        etProfileFirstName.setEnabled(isEditable);
        etProfileLastName.setEnabled(isEditable);
        etProfileAddress.setEnabled(isEditable);
        etProfileContectNumber.setEnabled(isEditable);
        etProfileZipCode.setEnabled(isEditable);
        etProfileCity.setEnabled(isEditable);

        ivProfileImage.setClickable(isEditable);
        tvProfileCountryCode.setEnabled(isEditable);
        if (TextUtils.isEmpty(preferenceHelper.getSocialId())) {
            etProfileEmail.setEnabled(isEditable);
            etProfileEmail.setFocusableInTouchMode(isEditable);
        }

        etProfileFirstName.setFocusableInTouchMode(isEditable);
        etProfileLastName.setFocusableInTouchMode(isEditable);
        etProfileAddress.setFocusableInTouchMode(isEditable);
        etProfileContectNumber.setFocusableInTouchMode(isEditable);
        etProfileZipCode.setFocusableInTouchMode(isEditable);
        etProfileCity.setFocusableInTouchMode(isEditable);
        if (isEditable) {
            etProfileFirstName.requestFocus();
        }
        btnChangePassword.setEnabled(isEditable);
        tilPassword.setEnabled(isEditable);
    }

    private void setContactNoLength(int length) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        etProfileContectNumber.setFilters(FilterArray);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void hideKeyPad() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            switch (requestCode) {
                case Const.PERMISSION_FOR_CAMERA_AND_EXTERNAL_STORAGE:
                    goWithCameraAndStoragePermission(grantResults);
                    break;
                default:
                    // do default
                    break;

            }
        }
    }

    private void goWithCameraAndStoragePermission(int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Do the stuff that requires permission...
            openPhotoDialog();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                openCameraPermissionDialog();
            } else {
                closedPermissionDialog();
                openPermissionNotifyDialog(Const.PERMISSION_FOR_CAMERA_AND_EXTERNAL_STORAGE);
            }
        } else if (grantResults[1] == PackageManager.PERMISSION_DENIED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                openCameraPermissionDialog();
            } else {
                closedPermissionDialog();
                openPermissionNotifyDialog(Const.PERMISSION_FOR_CAMERA_AND_EXTERNAL_STORAGE);

            }
        }
    }

    private void openCameraPermissionDialog() {
        if (customDialogEnable != null && customDialogEnable.isShowing()) {
            return;
        }
        customDialogEnable = new CustomDialogEnable(this, getResources().getString(R.string.msg_reason_for_camera_permission), getString(R.string.text_i_am_sure), getString(R.string.text_re_try)) {
            @Override
            public void doWithEnable() {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, Const.PERMISSION_FOR_CAMERA_AND_EXTERNAL_STORAGE);
                closedPermissionDialog();
            }

            @Override
            public void doWithDisable() {
                closedPermissionDialog();
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

    private void openVerifyAccountDialog(boolean isForDeleteAccount) {
        if (TextUtils.isEmpty(preferenceHelper.getSocialId())) {
            verifyDialog = new CustomDialogVerifyAccount(this, getResources().getString(R.string.text_verify_account), getResources().getString(R.string.text_yes), getResources().getString(R.string.text_no), getResources().getString(R.string.text_pass_current_hint), false) {
                @Override
                public void doWithEnable(EditText editText) {
                    currentPassword = editText.getText().toString();
                    if (!TextUtils.isEmpty(currentPassword)) {
                        verifyDialog.dismiss();
                        if (isForDeleteAccount) {
                            deleteUser();
                        } else {
                            updateProfile();
                        }
                    } else {
                        Utils.showToast(getString(R.string.msg_enter_password), ProfileActivity.this);
                    }
                }

                @Override
                public void doWithDisable() {
                    dismiss();
                }

                @Override
                public void clickOnText() {
                    dismiss();
                }
            };
            verifyDialog.show();
        } else {
            if (isForDeleteAccount) {
                deleteUser();
            } else {
                updateProfile();
            }
        }
    }

    private void OTPVerify() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.PHONE, etProfileContectNumber.getText());
            jsonObject.put(Const.Params.COUNTRY_PHONE_CODE, tvProfileCountryCode.getText().toString());
            jsonObject.put(Const.Params.TYPE, Const.USER);
            Utils.showCustomProgressDialog(this, getResources().getString(R.string.msg_loading), false, null);

            Call<VerificationResponse> call = ApiClient.getClient().create(ApiInterface.class).verification(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<VerificationResponse>() {
                @Override
                public void onResponse(Call<VerificationResponse> call, Response<VerificationResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        VerificationResponse verificationResponse = response.body();
                        if (verificationResponse.isSuccess()) {
                            Utils.hideCustomProgressDialog();
                            otpForSMS = verificationResponse.getOtpForSMS();
                            openOTPVerifyDialog();
                        } else {
                            Utils.hideCustomProgressDialog();
                            Utils.showErrorToast(verificationResponse.getErrorCode(), ProfileActivity.this);
                        }
                    }

                }

                @Override
                public void onFailure(Call<VerificationResponse> call, Throwable t) {
                    AppLog.handleThrowable(ProfileActivity.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException(Const.Tag.REGISTER_ACTIVITY, e);
        }
    }

    private void openOTPVerifyDialog() {
        if (customDialogVerifyDetail != null && customDialogVerifyDetail.isShowing()) {
            return;
        }

        customDialogVerifyDetail = new CustomDialogVerifyDetail(this, false, true) {
            @Override
            public void doWithSubmit(EditText etEmailVerify, EditText etSMSVerify) {

                if (otpForSMS.equals(etSMSVerify.getText().toString())) {
                    dismiss();
                    openVerifyAccountDialog(false);
                    tempContactNumber = tvProfileCountryCode.getText().toString() + etProfileContectNumber.getText().toString();
                } else {
                    Utils.showToast(getResources().getString(R.string.msg_otp_wrong), ProfileActivity.this);
                    tempContactNumber = "";
                }
            }

            @Override
            public void doCancel() {
                this.dismiss();
            }
        };
        customDialogVerifyDetail.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            closedEnableDialogInternet();
        } else {
            openInternetDialog();
        }
    }


    private void getServicesCountry() {
        if (CurrentTrip.getInstance().getCountryCodes() == null) {
            Utils.showCustomProgressDialog(this, "", false, null);
            Call<CountriesResponse> call = ApiClient.getClient().create(ApiInterface.class).getCountries();
            call.enqueue(new Callback<CountriesResponse>() {
                @Override
                public void onResponse(Call<CountriesResponse> call, Response<CountriesResponse> response) {
                    if (parseContent.isSuccessful(response)) {
                        Utils.hideCustomProgressDialog();
                        if (response.body().isSuccess()) {
                            for (Country countryCodeLocal : codeArrayList) {
                                for (Country countryCodeWeb : response.body().getCountry()) {
                                    if (TextUtils.equals(countryCodeWeb.getCountryPhoneCode(), countryCodeLocal.getCountryPhoneCode())) {
                                        countryCodeLocal.setPhoneNumberLength(countryCodeWeb.getPhoneNumberLength());
                                        countryCodeLocal.setPhoneNumberMinLength(countryCodeWeb.getPhoneNumberMinLength());
                                    }
                                }
                                if (TextUtils.equals(countryCodeLocal.getCountryPhoneCode(), preferenceHelper.getCountryPhoneCode())) {
                                    setCountry(countryCodeLocal);
//                                    phoneNumberLength = countryCodeLocal.getPhoneNumberLength();
//                                    phoneNumberMinLength = countryCodeLocal.getPhoneNumberMinLength();
//                                    setContactNoLength(phoneNumberLength);
                                    break;
                                }
                            }
                            CurrentTrip.getInstance().setCountryCodes(codeArrayList);
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), ProfileActivity.this);
                        }
                    }

                }

                @Override
                public void onFailure(Call<CountriesResponse> call, Throwable t) {
                    AppLog.handleThrowable(ProfileActivity.class.getSimpleName(), t);
                }
            });
        } else {
            for (Country countryCodeLocal : CurrentTrip.getInstance().getCountryCodes()) {
                if (TextUtils.equals(countryCodeLocal.getCountryPhoneCode(), preferenceHelper.getCountryPhoneCode())) {
//                    phoneNumberLength = countryCodeLocal.getPhoneNumberLength();
//                    phoneNumberMinLength = countryCodeLocal.getPhoneNumberMinLength();
//                    setContactNoLength(phoneNumberLength);
                    break;
                }
            }
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


    @Override
    public void otpReceived(String otp) {
        if (customDialogVerifyDetail != null && customDialogVerifyDetail.isShowing()) {
            customDialogVerifyDetail.notifyDataSetChange(otp);
        }
    }
}