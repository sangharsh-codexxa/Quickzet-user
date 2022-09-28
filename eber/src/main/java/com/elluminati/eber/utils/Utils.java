package com.elluminati.eber.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.elluminati.eber.MainActivity;
import com.elluminati.eber.R;
import com.elluminati.eber.components.CustomCircularProgressView;
import com.elluminati.eber.interfaces.OnProgressCancelListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.TimeZone;


/**
 * Created by elluminati on 31-03-2016.
 */
public class Utils {

    public static final String TAG = "Utils";
    private static Dialog dialog;
    private static CustomCircularProgressView ivProgressBar;

    @Deprecated
    public static boolean isInternetConnected(Context context) {

        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return isConnected;

    }

    public static void showErrorToast(int code, Context context) {
        String msg;
        String errorCode = Const.ERROR_CODE_PREFIX + code;
        try {
            msg = context.getResources().getString(context.getResources().getIdentifier(errorCode, Const.STRING, context.getPackageName()));
            showToast(msg, context);

            if (code == Const.ERROR_CODE_INVALID_TOKEN || code == Const.ERROR_CODE_USER_DETAIL_NOT_FOUND) {
                PreferenceHelper.getInstance(context).putSessionToken(null);// clear session token
                Intent sigInIntent = new Intent(context, MainActivity.class);
                sigInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sigInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sigInIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                sigInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(sigInIntent);
            }
        } catch (Resources.NotFoundException e) {
            msg = errorCode;
        }
    }

    public static void showMessageToast(String code, Context context) {
        String msg;
        String messageCode = Const.MESSAGE_CODE_PREFIX + code;
        try {
            msg = context.getResources().getString(context.getResources().getIdentifier(messageCode, Const.STRING, context.getPackageName()));
            showToast(msg, context);
        } catch (Resources.NotFoundException e) {
            msg = code;
            AppLog.handleException(TAG, e);
        }
    }

    public static void showHttpErrorToast(int code, Context context) {
        String msg;
        String errorCode = Const.HTTP_ERROR_CODE_PREFIX + code;
        try {
            msg = context.getResources().getString(context.getResources().getIdentifier(errorCode, Const.STRING, context.getPackageName()));
            showToast(msg, context);
        } catch (Resources.NotFoundException e) {
            msg = errorCode;
            AppLog.handleException(TAG, e);
        }
    }

    public static void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void hideCustomProgressDialog() {
        try {
            if (dialog != null && ivProgressBar != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            AppLog.handleException(TAG, e);
        } finally {
            dialog = null;
            ivProgressBar = null;
        }
    }

    public static void showCustomProgressDialog(AppCompatActivity activity, String title, boolean isCancel, OnProgressCancelListener onProgressCancelListener) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (!activity.isFinishing()) {
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.circuler_progerss_bar_two);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ivProgressBar = dialog.findViewById(R.id.ivProgressBarTwo);
            ivProgressBar.startAnimation();
            dialog.setCancelable(isCancel);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setDimAmount(0);
            dialog.show();
        }
    }

    public static void showCustomProgressDialog(Context context, String title, boolean isCancel, OnProgressCancelListener onProgressCancelListener) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.circuler_progerss_bar_two);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ivProgressBar = dialog.findViewById(R.id.ivProgressBarTwo);
        ivProgressBar.startAnimation();
        dialog.setCancelable(isCancel);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setDimAmount(0);
        dialog.show();
    }

    public static String getTimeZoneName() {
        return TimeZone.getDefault().getID();
    }

    public static Bitmap vectorToBitmap(Context ctx, @DrawableRes int resVector) {
        Drawable drawable = AppCompatResources.getDrawable(ctx, resVector);
        Bitmap b = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        drawable.setBounds(0, 0, c.getWidth(), c.getHeight());
        drawable.draw(c);
        return b;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable != null) {
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }

    }

    public static String trimString(String address) {
        boolean isInteger;
        String original = "";
        if (!TextUtils.isEmpty(address)) {
            String[] strings = address.split(",");
            String s = address.substring(0, 1);
            try {
                Integer.valueOf(s);
                isInteger = true;


            } catch (NumberFormatException e) {
                isInteger = false;
            }

            int stringLength = strings.length;
            if (isInteger) {
                switch (stringLength) {
                    case 1:
                    case 2:
                        original = address;
                        break;
                    default:
                        original += strings[0] + "," + strings[1];
                        break;
                }


            } else {
                switch (stringLength) {
                    case 1:
                        original = address;
                        break;
                    case 2:
                        original = strings[0];
                        break;
                    default:
                        original = strings[0] + "," + strings[1];
                        break;
                }
            }

        }


        return original.trim();
    }

    public static String showUnit(Context context, int code) {
        String codeString = Const.UNIT_PREFIX + code;
        return context.getResources().getString(context.getResources().getIdentifier(codeString, Const.STRING, context.getPackageName()));

    }

    public static String validBasePriceSuffix(Context context, double value, String unit) {
        if (value < 1) {
            return "";
        } else if (value == 1) {
            return " " + context.getResources().getString(R.string.text_for_first) + " " + unit;
        } else {
            return " " + context.getResources().getString(R.string.text_for_first) + " " + value + " " + unit + "s";
        }
    }


    public static String validateTimeUnit(Context context, String value) {
        if (Integer.valueOf(value) < 2) {
            return context.getResources().getString(R.string.text_eta) + " : " + value + " " + context.getResources().getString(R.string.text_unit_min);
        } else {
            return context.getResources().getString(R.string.text_eta) + " : " + value + " " + context.getResources().getString(R.string.text_unit_min) + "s";
        }
    }

    public static String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return n + "th";
        }
        switch (n % 10) {
            case 1:
                return n + "st";
            case 2:
                return n + "nd";
            case 3:
                return n + "rd";
            default:
                return n + "th";
        }
    }

    public static boolean isNougat() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M;
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static boolean isValidPhoneNumber(String mobileNumber, String countryCodeISO2) {
        if (Patterns.PHONE.matcher(mobileNumber.trim()).matches()) {
            PhoneNumberUtil phoneNumber = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber parseNumber = phoneNumber.parse(mobileNumber, countryCodeISO2);
                return phoneNumber.isValidNumber(parseNumber);
            } catch (NumberParseException e) {
                return false;
            }

        } else {
            return false;
        }
    }

    public static boolean isValidPhoneNumber(String mobileNumber, int minimumPhoneNumberLength, int maximumPhoneNumberLength) {
        return mobileNumber.trim().length() >= minimumPhoneNumberLength && mobileNumber.trim().length() <= maximumPhoneNumberLength;
    }

    /**
     * open call chooser
     *
     * @param context context
     * @param phone   phone
     */
    public static void openCallChooser(Context context, String phone) {
        if (!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.text_call_via)));
        }
    }
}