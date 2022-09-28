package com.elluminati.eber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.elluminati.eber.interfaces.OTPListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtpReader extends BroadcastReceiver {

    private final String receiverString;
    private final OTPListener otpListener;
    public String SMS_BUNDLE = "pdus";
    public String OTP_REGEX = "[0-9]{1,6}";

    public OtpReader(OTPListener otpListener, String sender) {
        receiverString = sender;
        this.otpListener = otpListener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        SmsMessage currentMessage;
        if (intentExtras != null) {
            Object[] sms_bundle = (Object[]) intentExtras.get(SMS_BUNDLE);
            for (int i = 0; i < sms_bundle.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    currentMessage = SmsMessage.createFromPdu((byte[]) sms_bundle[i], intent.getStringExtra("format"));

                } else {
                    currentMessage = SmsMessage.createFromPdu((byte[]) sms_bundle[i]);
                }
                String senderNum = currentMessage.getDisplayOriginatingAddress();
                String message = currentMessage.getDisplayMessageBody();
                String TAG = "OtpReader";
                Log.i(TAG, "senderNum: " + senderNum + " message: " + message);

                if (!TextUtils.isEmpty(receiverString) && senderNum.contains(receiverString)) {
                    //If message received is from required number.
                    //If bound a listener interface, callback the overriden method.
                    if (otpListener != null) {
                        otpListener.otpReceived(getOtpFormMassage(message));
                    }
                }
            }

        }
    }

    public String getOtpFormMassage(String message) {
        String otp = "";
        Pattern pattern = Pattern.compile(OTP_REGEX);
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            otp = matcher.group();
        }
        return otp;
    }
}