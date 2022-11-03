package com.elluminati.eber;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.elluminati.eber.models.responsemodels.IsSuccessResponse;
import com.elluminati.eber.parse.ApiClient;
import com.elluminati.eber.parse.ApiInterface;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.PreferenceHelper;
import com.elluminati.eber.utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by elluminati on 30-03-2016.
 * <p/>
 * This Class is handle a Notification which send by Google FCM server.
 */
public class FcmMessagingService extends FirebaseMessagingService {

    public static final String ID = "id";
    public static final String PROVIDER_TRIP_ACCEPTED = "203";
    public static final String PROVIDER_STARTED = "200";
    public static final String PROVIDER_TRIP_START = "206";
    public static final String PROVIDER_ARRIVED = "204";
    public static final String PROVIDER_TRIP_END = "209";
    public static final String TRIP_CANCEL_BY_PROVIDER = "214";
    public static final String NO_PROVIDER_FOUND = "213";
    public static final String LOG_OUT = "220";
    public static final String CLIENT_APPROVED = "215";
    public static final String CLIENT_DECLINED = "216";
    public static final String WAITING_FOR_TIP = "240";
    public static final String NEW_CORPORATE_REQUEST = "231";
    public static final String PROVIDER_CREATE_INITIAL_TRIP = "217";
    public static final String TRIP_CANCEL_BY_ADMIN = "218";
    public static final String SPLIT_PAYMENT_REQUEST = "239";
    public static final String ACCEPT_SPLIT_PAYMENT_REQUEST = "236";
    public static final String REJECT_SPLIT_PAYMENT_REQUEST = "237";
    public static final String REMOVE_SPLIT_PAYMENT_REQUEST = "238";
    public static final String YOUR_TRIP_END_SPLIT_PAYMENT = "243";
    private static final String CHANNEL_ID = "eberuser2019";
    private Map<String, String> data;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        if (remoteMessage != null) {
            AppLog.Log("FcmMessagingService", "From:" + remoteMessage.getFrom());
            AppLog.Log("FcmMessagingService", "Data:" + remoteMessage.getData());
            //Handle notifications for app
            data = remoteMessage.getData();
            String message = remoteMessage.getData().get(ID);
            if (message != null && !message.isEmpty()) {
                tripStatus(message);
            }
        }
    }

    private void sendNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "Trip Status", NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableVibration(true);
            mChannel.enableLights(true);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent notificationIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SplashScreenActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);

        final Notification.Builder notificationBuilder = new Notification.Builder(this).setColor(ResourcesCompat.getColor(getResources(), R.color.color_app_theme_dark, null)).setContentTitle(this.getResources().getString(R.string.app_name)).setContentText(message).setAutoCancel(true).setSmallIcon(getNotificationIcon()).setContentIntent(notificationPendingIntent).setPriority(Notification.PRIORITY_DEFAULT).setVisibility(Notification.VISIBILITY_PUBLIC).setCategory(Notification.CATEGORY_MESSAGE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(CHANNEL_ID); // Channel ID
        }
        if (PreferenceHelper.getInstance(this).getIsPushNotificationSoundOn() && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
        }
        notificationManager.notify(Const.PUSH_NOTIFICATION_ID, notificationBuilder.build());
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_stat_eber : R.mipmap.ic_launcher;
    }

    private void tripStatus(String status) {
        switch (status) {
            case PROVIDER_TRIP_ACCEPTED:
                sendNotification(getMessage(status));
                sendBroadcast(Const.ACTION_ACCEPTED);
                break;
            case PROVIDER_STARTED:
                sendNotification(getMessage(status));
                sendBroadcast(Const.ACTION_PROVIDER_STARTED);
                break;
            case PROVIDER_ARRIVED:
                sendNotification(getMessage(status));
                sendBroadcast(Const.ACTION_PROVIDER_ARRIVED);
                break;
            case PROVIDER_TRIP_START:
                sendNotification(getMessage(status));
                sendBroadcast(Const.ACTION_TRIP_START);
                break;
            case PROVIDER_TRIP_END:
                sendNotification(getMessage(status));
                sendBroadcast(Const.ACTION_TRIP_END);
                break;
            case NO_PROVIDER_FOUND:
                sendBroadcast(Const.ACTION_NO_PROVIDER_FOUND);
                sendNotification(getMessage(status));
                break;
            case TRIP_CANCEL_BY_PROVIDER:
                sendNotification(getMessage(status));
                sendBroadcast(Const.ACTION_TRIP_CANCEL_BY_PROVIDER);
                break;
            case LOG_OUT:
                sendNotification(getMessage(status));
                goToMainActivity();
                break;
            case CLIENT_APPROVED:
                sendNotification(getMessage(status));
                sendGlobalBroadcast(Const.ACTION_APPROVED);
                break;
            case CLIENT_DECLINED:
                sendNotification(getMessage(status));
                sendGlobalBroadcast(Const.ACTION_DECLINE);
                break;
            case WAITING_FOR_TIP:
                sendBroadcast(Const.ACTION_WAITING_FOR_TIP);
                break;
            case NEW_CORPORATE_REQUEST:
                sendNotification(getMessage(status));
                sendBroadcastWithData(Const.ACTION_NEW_CORPORATE_REQUEST);
                break;
            case TRIP_CANCEL_BY_ADMIN:
                sendNotification(getMessage(status));
                sendBroadcast(Const.ACTION_TRIP_CANCEL_BY_ADMIN);
                break;
            case PROVIDER_CREATE_INITIAL_TRIP:
                sendNotification(getMessage(status));
                sendBroadcast(Const.ACTION_PROVIDER_CREATE_INITIAL_TRIP);
                break;
            case SPLIT_PAYMENT_REQUEST:
                sendNotification(getMessage(status));
                sendGlobalBroadcastWithData(Const.ACTION_SPLIT_PAYMENT_REQUEST);
                break;
            case ACCEPT_SPLIT_PAYMENT_REQUEST:
            case REJECT_SPLIT_PAYMENT_REQUEST:
                sendNotification(getMessage(status));
                break;
            case REMOVE_SPLIT_PAYMENT_REQUEST:
                sendNotification(getMessage(status));
                sendGlobalBroadcast(Const.ACTION_REMOVE_SPLIT_PAYMENT_REQUEST);
            case YOUR_TRIP_END_SPLIT_PAYMENT:
                sendNotification(getMessage(status));
                sendGlobalBroadcastWithData(Const.ACTION_SPLIT_PAYMENT_PAY_REQUEST);
                break;
            default:
                sendNotification(status);
                break;
        }
    }

    private String getMessage(String code) {
        String msg = "";
        String messageCode = Const.PUSH_MESSAGE_PREFIX + code;
        try {
            Configuration conf = new Configuration(this.getApplicationContext().getResources().getConfiguration());
            Locale locale = new Locale(PreferenceHelper.getInstance(this).getLanguageCode());
            conf.setLocale(locale);
            msg = this.getApplicationContext().createConfigurationContext(conf).getResources().getString(this.getResources().getIdentifier(messageCode, Const.STRING, this.getPackageName()));

        } catch (Resources.NotFoundException e) {
            msg = code;
        }
        return msg;
    }

    private void sendGlobalBroadcast(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void sendGlobalBroadcastWithData(String action) {
        Intent intent = new Intent(action);
        intent.putExtra(Const.Params.EXTRA_PARAM, data.get(Const.Params.EXTRA_PARAM));
        sendBroadcast(intent);
    }

    private void sendBroadcast(String action) {
        Intent intent = new Intent(action);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void sendBroadcastWithData(String action) {
        Intent intent = new Intent(action);
        intent.putExtra(Const.Params.EXTRA_PARAM, data.get(Const.Params.EXTRA_PARAM));
        localBroadcastManager.sendBroadcast(intent);
    }

    public void goToMainActivity() {
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(this);
        preferenceHelper.logout();
        Intent sigInIntent = new Intent(this, MainActivity.class);
        sigInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        sigInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sigInIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        sigInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sigInIntent);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        PreferenceHelper.getInstance(this).putDeviceToken(token);
        if (PreferenceHelper.getInstance(this).getSessionToken() != null) {
            updateDeviceTokenOnServer(token);
        }
    }


    private void updateDeviceTokenOnServer(String deviceToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.Params.TOKEN, PreferenceHelper.getInstance(this).getSessionToken());
            jsonObject.put(Const.Params.USER_ID, PreferenceHelper.getInstance(this).getUserId());
            jsonObject.put(Const.Params.DEVICE_TOKEN, deviceToken);
            Call<IsSuccessResponse> call = ApiClient.getClient().create(ApiInterface.class).updateDeviceToken(ApiClient.makeJSONRequestBody(jsonObject));
            call.enqueue(new Callback<IsSuccessResponse>() {
                @Override
                public void onResponse(Call<IsSuccessResponse> call, Response<IsSuccessResponse> response) {
                    if (ParseContent.getInstance().isSuccessful(response)) {
                        if (response.body().isSuccess()) {
                            Utils.showMessageToast(response.body().getMessage(), FcmMessagingService.this);
                        } else {
                            Utils.showErrorToast(response.body().getErrorCode(), FcmMessagingService.this);
                        }
                    }
                }
                @Override
                public void onFailure(Call<IsSuccessResponse> call, Throwable t) {
                    AppLog.handleThrowable(FcmMessagingService.class.getSimpleName(), t);
                }
            });
        } catch (JSONException e) {
            AppLog.handleException("FCM Token Refresh", e);
        }
    }

}
