package com.elluminati.eber.utils;

import android.os.Build;

import com.elluminati.eber.BuildConfig;

/**
 * Created by elluminati on 29-03-2016.
 */
public class Const {

    /***
     * Google url
     */
    public static final String GOOGLE_API_URL = BuildConfig.FLAVOR.equalsIgnoreCase("local")
            ? "https://maps.googleapis.com/maps/" : ServerConfig.BASE_URL + "gmapsapi/maps/";

    /**
     * Default font scale for used when app font scale change
     */
    public static final float DEFAULT_FONT_SCALE = 1.0f;

    /**
     * Timer Scheduled in Second
     */
    public static final long SCHEDULED_SECONDS = 5;//seconds

    /**
     * Timer Scheduled in Second for getNear provider
     */
    public static final long NEAR_PROVIDER_SCHEDULED_SECOND = 180; //seconds

    /**
     * set LatLngBounce padding
     */
    public static final int MAP_BOUNDS = 180;

    /**
     * general const
     */
    public static final int PUSH_NOTIFICATION_ID = 2688;
    public static final int USER_UNIQUE_NUMBER = 10;
    public static final int PROVIDER_UNIQUE_NUMBER = 11;
    public static final int RQS_PICK_CONTACT = 10;
    public static final int ERROR_CODE_YOUR_TRIP_PAYMENT_IS_PENDING = 464;
    public static final int ERROR_CODE_INVALID_TOKEN = 451;
    public static final int ERROR_CODE_USER_DETAIL_NOT_FOUND = 474;
    public static final int ERROR_CODE_TRIP_ALREADY_RUNNING = 995;
    public static final int ERROR_CODE_ADD_CARD_FIRST = 435;
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static final int CASH = 1;
    public static final int CARD = 0;
    public static final int USER = 1;
    public static final int IS_UPLOADED = 1;
    public static final int IS_APPROVED = 1;
    public static final int IS_DECLINE = 0;
    public static final int NOT_AVAILABLE = 0;
    public static final String MESSAGE_CODE_PAID_FROM_WALLET = "109";
    public static final String SOCIAL_FACEBOOK = "facebook";
    public static final String SOCIAL_GOOGLE = "google";
    public static final String MANUAL = "manual";
    public static final String DEVICE_TYPE_ANDROID = "android";
    public static final String IS_CLICK_INSIDE_DRAWER = "is_click_inside_drawer";
    public static final String IS_FROM_INVOICE = "is_from_invoice";
    public static final String UNIT_PREFIX = "unit_code_";
    public static final String ERROR_CODE_PREFIX = "error_code_";
    public static final String MESSAGE_CODE_PREFIX = "message_code_";
    public static final String PUSH_MESSAGE_PREFIX = "PUSH_MESSAGE_";
    public static final String HTTP_ERROR_CODE_PREFIX = "http_error_";
    public static final String UTF_8 = "utf-8";
    public static final String DATE_TIME_FORMAT_WEB = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String TIME_FORMAT_AM = "h:mm a";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DAY_DATE_FORMAT = "EE, dd MMM";
    public static final String DATE_FORMAT_MONTH = "MMMM yyyy";
    public static final String DATE_FORMAT_EARNING = "dd MMM yyyy";
    public static final String DAY = "d";
    public static final String SLASH = "/";
    public static final String PERCENTAGE = "%";
    public static final String STRING = "string";
    public static final String PIC_URI = "picUri";
    public static final String XIAOMI = "Xiaomi";
    public static final int PENDING_PAYMENT = 1001;
    public static final int DESTINATION_SELECTION = 1002;
    public static final int RESULT_OK = 1;
    public static final String ERROR_CODE_USER_DECLINE = "899";
    public static final String MESSAGE_CODE_USER_EXIST = "102";
    public static final int LOCATION_SETTING_REQUEST = 1080;
    public static final int PICK_UP_ADDRESS = 1;
    public static final int DESTINATION_ADDRESS = 2;
    public static final int HOME_ADDRESS = 3;
    public static final int WORK_ADDRESS = 4;
    public static final int TRIP_STOP_ADDRESS = 5;
    public static final int TYPE_NORMAL_PRICE = 0;
    public static final int TYPE_RENTAL_PRICE = 1;
    public static final int TYPE_SHARE_TRIP_PRICE = 2;

    /**
     * Permission requestCode
     */
    public static final int REQUEST_ADD_DRIVER = 31;
    public static final int REQUEST_ADD_CARD = 33;
    public static final int REQUEST_PAYU = 334;
    public static final int REQUEST_CHECK_SETTINGS = 32;
    public static final int REQUEST_CHECK_LOCATION_SETTINGS = 38;
    public static final int PERMISSION_FOR_LOCATION = 2;
    public static final int PERMISSION_FOR_CONTACTS = 5;
    public static final int PERMISSION_FOR_CAMERA_AND_EXTERNAL_STORAGE = 3;
    public static final int PERMISSION_FOR_CALL = 4;
    public static final int PERMISSION_FOR_SMS = 6;
    public static final int REQUEST_UPDATE_APP = 9;

    /**
     * Broadcast action
     */
    public static final String NETWORK_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String ACTION_ACCEPTED = "eber.client.ACCEPTED";
    public static final String ACTION_PROVIDER_STARTED = "eber.client.PROVIDER_STARTED";
    public static final String ACTION_TRIP_START = "eber.client.TRIP_START";
    public static final String ACTION_PROVIDER_ARRIVED = "eber.client.PROVIDER_ARRIVED";
    public static final String ACTION_TRIP_END = "eber.client.TRIP_END";
    public static final String ACTION_TRIP_CANCEL_BY_PROVIDER = "eber.client" + "" + ".TRIP_CANCEL_BY_PROVIDER";
    public static final String ACTION_NO_PROVIDER_FOUND = "eber.client" + ".NO_PROVIDER_FOUND";
    public static final String ACTION_DECLINE = "eber.client.CLIENT_DECLINE";
    public static final String ACTION_APPROVED = "eber.client.CLIENT_APPROVED";
    public static final String ACTION_WAITING_FOR_TIP = "eber.client.WAITING_FOR_TIP";
    public static final String BUNDLE = "BUNDLE";
    public static final String ACTION_OTP_SMS = "android.provider.Telephony.SMS_RECEIVED";
    public static final String ACTION_NEW_CORPORATE_REQUEST = "eber.client" + ".ACTION_NEW_CORPORATE_REQUEST";
    public static final String ACTION_TRIP_CANCEL_BY_ADMIN = "eber.client" + ".ACTION_TRIP_CANCEL_BY_ADMIN";
    public static final String ACTION_PROVIDER_CREATE_INITIAL_TRIP = "eber.client.PROVIDER_CREATE_INITIAL_TRIP";
    public static final String ACTION_SPLIT_PAYMENT_REQUEST = "eber.client" + ".SPLIT_PAYMENT_REQUEST";
    public static final String ACTION_REMOVE_SPLIT_PAYMENT_REQUEST = "eber.client" + ".REMOVE_SPLIT_PAYMENT_REQUEST";
    public static final String ACTION_SPLIT_PAYMENT_PAY_REQUEST = "eber.client" + ".ACTION_SPLIT_PAYMENT_PAY_REQUEST";

    /**
     * Image server URL
     */
    public static String IMAGE_BASE_URL = ServerConfig.BASE_URL;

    /**
     * service parameters
     */
    public class Params {
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String PICTURE = "picture";
        public static final String PHONE = "phone";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String DEVICE_TYPE = "device_type";
        public static final String BIO = "bio";
        public static final String ADDRESS = "address";
        public static final String COUNTRY = "country";
        public static final String ZIPCODE = "zipcode";
        public static final String LOGIN_BY = "login_by";
        public static final String SOCIAL_UNIQUE_ID = "social_unique_id";
        public static final String COUNTRY_PHONE_CODE = "country_phone_code";
        public static final String CITY = "city";
        public static final String DEVICE_TIMEZONE = "device_timezone";
        public static final String USER_ID = "user_id";
        public static final String SOCIAL_ID = "social_id";
        public static final String CARD_ID = "card_id";
        public static final String TOKEN = "token";
        public static final String STATUS = "status";
        public static final String REFERRAL_CODE = "referral_code";
        public static final String PAYMENT_TOKEN = "payment_token";
        public static final String LAST_FOUR = "last_four";
        public static final String CARD_TYPE = "card_type";
        public static final String PICK_UP_LATITUDE = "latitude";
        public static final String PICK_UP_LONGITUDE = "longitude";
        public static final String DEST_LATITUDE = "d_latitude";
        public static final String DEST_LONGITUDE = "d_longitude";
        public static final String DESTINATION_ADDRESS = "destination_address";
        public static final String SERVICE_TYPE = "service_type_id";
        public static final String PAYMENT_MODE = "payment_mode";
        public static final String PAYMENT_TYPE = "payment_type";
        public static final String TRIP_ID = "trip_id";
        public static final String PROVIDER_ID = "provider_id";
        public static final String IS_SKIP = "is_skip";
        public static final String NEW_PASSWORD = "new_password";
        public static final String OLD_PASSWORD = "old_password";
        public static final String SOURCE_ADDRESS = "source_address";
        public static final String TIME = "time";
        public static final String DISTANCE = "distance";
        public static final String REVIEW = "review";
        public static final String RATING = "rating";
        public static final String MAP = "map";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String PROMO_CODE = "promocode";
        public static final String CURRENCY = "currency";
        public static final String START_TIME = "start_time";
        public static final String TYPE = "type";
        public static final String UNIT = "unit";
        public static final String CANCEL_REASON = "cancel_reason";
        public static final String OTP_FOR_SMS = "otpForSMS";
        public static final String OTP_FOR_EMAIL = "otpForEMAIL";
        public static final String PICTURE_DATA = "pictureData";
        public static final String IS_SURGE_HOURS = "is_surge_hours";
        public static final String TIMEZONE = "timezone";
        public static final String IS_ALWAYS_SHARE_RIDE_DETAIL = "is_always_share_ride_detail";
        public static final String EMERGENCY_CONTACT_DETAIL_ID = "emergency_contact_detail_id";
        public static final String NAME = "name";
        public static final String WALLET = "wallet";
        public static final String IS_USE_WALLET = "is_use_wallet";
        public static final String TIP_AMOUNT = "tip_amount";
        public static final String APP_VERSION = "app_version";
        public static final String DOCUMENT_ID = "document_id";
        public static final String UNIQUE_CODE = "unique_code";
        public static final String EXPIRED_DATE = "expired_date";
        public static final String PICKUP_LATITUDE = "pickup_latitude";
        public static final String PICKUP_LONGITUDE = "pickup_longitude";
        public static final String DESTINATION_LATITUDE = "destination_latitude";
        public static final String DESTINATION_lONGITUDE = "destination_longitude";
        public static final String HOME_ADDRESS = "home_address";
        public static final String WORK_ADDRESS = "work_address";
        public static final String HOME_LATITUDE = "home_latitude";
        public static final String HOME_LONGITUDE = "home_longitude";
        public static final String WORK_LATITUDE = "work_latitude";
        public static final String WORK_LONGITUDE = "work_longitude";
        public static final String IS_FIXED_FARE = "is_fixed_fare";
        public static final String FIXED_PRICE = "fixed_price";
        public static final String RECEIVED_TRIP_FROM_GENDER = "received_trip_from_gender";
        public static final String PROVIDER_LANGUAGE = "provider_language";
        public static final String ACCESSIBILITY = "accessibility";
        public static final String IS_VERIFIED = "is_verified";
        public static final String IS_OPEN_FOR_RESULT = "isOpenForResult";
        public static final String IS_FROM_FORGET_PASSWORD = "isFromForgetPassword";
        public static final String CAR_RENTAL_ID = "car_rental_id";
        public static final String IS_ACCEPTED = "is_accepted";
        public static final String CORPORATE_ID = "corporate_id";
        public static final String TRIP_TYPE = "trip_type";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String SURGE_MULTIPLIER = "surge_multiplier";
        public static final String EXTRA_PARAM = "extraParam";
        public static final String SEARCH_VALUE = "search_value";
        public static final String SEARCH_USER = "search_user";
        public static final String COUNTRY_ID = "country_id";
        public static final String CITY_ID = "city_id";
        public static final String PROMO_ID = "promo_id";
        public static final String GOOGLE_PATH_START_LOCATION_TO_PICKUP_LOCATION = "googlePathStartLocationToPickUpLocation";
        public static final String GOOGLE_PICKUP_LOCATION_TO_DESTINATION_LOCATION = "googlePickUpLocationToDestinationLocation";
        public static final String PAGE = "page";
        public static final String PAYMENT_METHOD = "payment_method";
        public static final String AMOUNT = "amount";
        public static final String PAYMENT_INTENT_ID = "payment_intent_id";
        public static final String IS_PAYMENT_FOR_TIP = "is_payment_for_tip";
        public static final String PAYMENT_GATEWAY_TYPE = "payment_gateway_type";
        public static final String AUTHORIZATION_URL = "authorization_url";
        public static final String REFERENCE = "reference";
        public static final String REQUIRED_PARAM = "required_param";
        public static final String PIN = "pin";
        public static final String OTP = "otp";
        public static final String BIRTHDAY = "birthday";
        public static final String PAYU_HTML = "payu_html";
        public static final String SPLIT_REQUEST_USER_ID = "split_request_user_id";
        public static final String IS_MULTIPLE_STOP = "is_multiple_stop";
        public static final String DESTINATION_ADDRESSES = "destination_addresses";
    }

    /**
     * ProviderStatus
     */
    public class ProviderStatus {
        public static final int PROVIDER_STATUS_RESPONSE_PENDING = 2;
        public static final int PROVIDER_STATUS_ACCEPTED = 1;
        public static final int PROVIDER_STATUS_REJECTED = 0;
        public static final int IS_REFERRAL_SKIP = 1;
        public static final int IS_REFERRAL_APPLY = 0;
        public static final int IS_PROVIDER_RATED = 1;
        public static final int PROVIDER_STATUS_STARTED = 2;
        public static final int PROVIDER_STATUS_ARRIVED = 4;
        public static final int PROVIDER_STATUS_TRIP_STARTED = 6;
        public static final int PROVIDER_STATUS_TRIP_END = 9;
        public static final int IS_DEFAULT = 1;
        public static final int PROVIDER_STATUS_UN_DEFINE = -1;

    }

    /**
     * app request code
     */
    public class ServiceCode {
        public static final int CHOOSE_PHOTO = 4;
        public static final int TAKE_PHOTO = 5;
        public static final int PATH_DRAW = 7;
        public static final int GET_GOOGLE_MAP_PATH = 39;
    }

    /**
     * all activity and fragment TAG for log
     */
    public class Tag {
        public static final String FCM_MESSAGING_SERVICE = "FcmMessagingService";
        public static final String MAIN_DRAWER_ACTIVITY = "MainDrawerActivity";
        public static final String REGISTER_ACTIVITY = "RegisterActivity";
        public static final String SIGN_IN_ACTIVITY = "SignInActivity";
        public static final String MAP_FRAGMENT = "MapFragmentApp";
        public static final String TRIP_FRAGMENT = "tripFragment";
        public static final String FEEDBACK_FRAGMENT = "FeedbackFragment";
        public static final String TRIP_HISTORY_ACTIVITY = "TripHistoryActivity";
        public static final String VIEW_AND_ADD_PAYMENT_ACTIVITY = "ViewPaymentActivity";
        public static final String PROFILE_ACTIVITY = "ProfileActivity";
        public static final String TRIP_HISTORY_DETAIL_ACTIVITY = "HistoryDetailActivity";
        public static final String REFERRAL_CODE_ACTIVITY = "ReferralEnterActivity";
        public static final String INVOICE_FRAGMENT = "InvoiceFragment";
        public static final String BOOKING_ACTIVITY = "BookingActivity";
        public static final String EMERGENCY_CONTACT = "EmergencyContact";
        public static final String DOCUMENT_ACTIVITY = "DOCUMENT_ACTIVITY";
        public static final String API_DISTANCE_MATRIX = "API_DISTANCE_MATRIX";
        public static final String API_GEOCODE = "API_DIRECTION";
        public static final String API_DIRECTION = "API_DIRECTION";
    }

    /**
     * params for google
     */
    public class google {
        public static final String FORMATTED_ADDRESS = "formatted_address";
        public static final String ERROR_MESSAGE = "error_message";
        public static final String RESULTS = "results";
        public static final String GEOMETRY = "geometry";
        public static final String LOCATION = "location";
        public static final String LAT = "lat";
        public static final String LNG = "lng";
        public static final String DESTINATION_ADDRESSES = "destination_addresses";
        public static final String ORIGIN_ADDRESSES = "origin_addresses";
        public static final String ROWS = "rows";
        public static final String ELEMENTS = "elements";
        public static final String DISTANCE = "distance";
        public static final String VALUE = "value";
        public static final String DURATION = "duration";
        public static final String TEXT = "text";
        public static final String ROUTES = "routes";
        public static final String LEGS = "legs";
        public static final String STEPS = "steps";
        public static final String POLYLINE = "polyline";
        public static final String POINTS = "points";
        public static final String ORIGIN = "origin";
        public static final String ORIGINS = "origins";
        public static final String DESTINATION = "destination";
        public static final String DESTINATIONS = "destinations";
        public static final String KEY = "key";
        public static final String STATUS = "status";
        public static final String OK = "OK";
        public static final String ADDRESS_COMPONENTS = "address_components";
        public static final String TYPES = "types";
        public static final String LOCALITY = "locality";
        public static final String LONG_NAME = "long_name";
        public static final String ADMINISTRATIVE_AREA_LEVEL_2 = "administrative_area_level_2";
        public static final String ADMINISTRATIVE_AREA_LEVEL_1 = "administrative_area_level_1";
        public static final String COUNTRY = "country";
        public static final int RC_SIGN_IN = 2001;
        public static final String COUNTRY_CODE = "country_code";
        public static final String SHORT_NAME = "short_name";
        public static final String LAT_LNG = "latlng";
        public static final String WAYPOINTS = "waypoints";
    }

    public class TripType {
        public static final int AIRPORT = 11;
        public static final int ZONE = 12;
        public static final int CITY = 13;
        public static final int NORMAL = 0;
        public static final int TRIP_TYPE_CAR_RENTAL = 14;
        public static final int TRIP_TYPE_CORPORATE = 7;
    }

    public class Accessibility {
        public static final String HANDICAP = "handicap";
        public static final String BABY_SEAT = "baby_seat";
        public static final String HOTSPOT = "hotspot";
    }

    public class Gender {
        public static final String MALE = "male";
        public static final String FEMALE = "female";
    }

    public class Wallet {
        public static final int ADD_WALLET_AMOUNT = 1;
        public static final int DEDUCT_WALLET_AMOUNT = 2;
        public static final int ADDED_BY_ADMIN = 1;
        public static final int ADDED_BY_CARD = 2;
        public static final int ADDED_BY_REFERRAL = 3;
        public static final int PAID_TRIP_AMOUNT = 4;
    }

    public class PaymentStatus {
        public static final int WAITING = 0;
        public static final int COMPLETED = 1;
        public static final int FAILED = 2;
    }

    public class PaymentMethod {
        public static final int STRIPE = 10;
        public static final int PAY_STACK = 11;
        public static final int PAYU = 12;
    }

    public class VerificationParam {
        public static final String SEND_PIN = "send_pin";
        public static final String SEND_OTP = "send_otp";
        public static final String SEND_PHONE = "send_phone";
        public static final String SEND_BIRTHDATE = "send_birthdate";
        public static final String SEND_ADDRESS = "send_address";
    }

    /**
     * Phone number default length
     */
    public class PhoneNumber {
        public static final int MINIMUM_PHONE_NUMBER_LENGTH = 7;
        public static final int MAXIMUM_PHONE_NUMBER_LENGTH = 12;
    }

    /**
     * Split payment status
     */
    public class SplitPaymentStatus {
        public static final int WAITING = 0;
        public static final int ACCEPTED = 1;
        public static final int REJECTED = 2;
    }
}