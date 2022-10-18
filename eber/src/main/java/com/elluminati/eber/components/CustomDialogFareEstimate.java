package com.elluminati.eber.components;

import static com.elluminati.eber.utils.Const.IMAGE_BASE_URL;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elluminati.eber.R;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.CurrencyHelper;
import com.elluminati.eber.utils.GlideApp;
import com.elluminati.eber.utils.Utils;

import java.text.NumberFormat;

/**
 * Created by elluminati on 12-07-2016.
 * <p>
 * this dialog is show a fare eta and other things.
 */
public abstract class CustomDialogFareEstimate extends Dialog implements View.OnClickListener {
    private final MyFontButton btnGetFareEstimate;
    private final MyFontButton btnCancel;
    private final MyFontButton btnProceed;
    private final MyFontTextViewMedium tvMinFare;
    private final MyFontTextViewMedium tvPerMile;
    private final MyFontTextViewMedium tvTimeCost;
    private final MyFontTextViewMedium tvMaxSize;
    private final MyFontTextViewMedium tvEta;
    private final MyFontTextViewMedium tvMainFareEta;
    private final MyFontTextViewMedium tvFareDistance;
    private final MyFontTextViewMedium tvTax;
    private final MyFontTextViewMedium tvCancelFee;
    private final MyFontTextViewMedium tvSurgePricing;
    private final MyFontTextView tvFareDest;
    private final MyFontTextView tvFareSrc;
    private final MyFontTextView tvVehicleType;
    private final MyFontTextView tvEtaMessage;
    private final MyFontTextView tvFareUnit;
    private final ImageView ivVehicleImage;
    private final LinearLayout llTax;
    private final LinearLayout llCancelFee;
    private final LinearLayout llSurgePricing;
    private final Context context;
    private final LinearLayout llBaseFareLayout;
    private final LinearLayout llFixedFareLayout;
    private final MyFontTextViewMedium tvTripType;
    private final MyFontTextViewMedium tvFixedRateAmount;
    private final MyFontTextViewMedium tvFixedRateMaxSize;
    private final CurrencyHelper currencyHelper;
    private final TextView tvLabelMinFare;
    private MyFontTextView tvTimeUnits;
    private boolean isDestinationSelect;

    public CustomDialogFareEstimate(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_fare_est_detail);
        this.context = context;

        btnGetFareEstimate = findViewById(R.id.btnGetFareEstimate);
        btnGetFareEstimate.setOnClickListener(this);
        tvMinFare = findViewById(R.id.tvMinFare);
        tvPerMile = findViewById(R.id.tvPerMile);
        tvTimeCost = findViewById(R.id.tvFareTimeCost);
        tvMaxSize = findViewById(R.id.tvMaxSize);
        tvEta = findViewById(R.id.tvEta);
        tvMainFareEta = findViewById(R.id.tvMainFareEta);
        tvFareDest = findViewById(R.id.tvFareDest);
        tvFareDest.setOnClickListener(this);
        tvFareSrc = findViewById(R.id.tvFareSrc);
        btnCancel = findViewById(R.id.btnCancel);

        tvTax = findViewById(R.id.tvTax);
        tvEtaMessage = findViewById(R.id.tvEtaMessage);
        btnCancel.setOnClickListener(this);
        btnProceed = findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(this);
        tvVehicleType = findViewById(R.id.tvVehicleTypeName);
        tvFareDistance = findViewById(R.id.tvFareDistance);
        tvFareUnit = findViewById(R.id.tvFareUnit);
        tvCancelFee = findViewById(R.id.tvCancelFee);
        ivVehicleImage = findViewById(R.id.ivVehicleImage);
        llTax = findViewById(R.id.llTax);
        llCancelFee = findViewById(R.id.llCancelFee);
        llSurgePricing = findViewById(R.id.llSurgePricing);
        tvSurgePricing = findViewById(R.id.tvSurgePricing);
        llBaseFareLayout = findViewById(R.id.llBaseFareLayout);
        llFixedFareLayout = findViewById(R.id.llFixedFareLayout);
        tvTripType = findViewById(R.id.tvTripType);
        tvFixedRateAmount = findViewById(R.id.tvFixedRateAmount);
        tvFixedRateMaxSize = findViewById(R.id.tvFixedRateMaxSize);
        tvLabelMinFare = findViewById(R.id.tvLabelMinFare);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setAttributes(params);
        currencyHelper = CurrencyHelper.getInstance(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvEtaMessage.setMovementMethod(new ScrollingMovementMethod());
    }

    @SuppressLint("SetTextI18n")
    public void notifyDataSetChange(String vehicleTypeName, double basePrice, double basePriceDistance, double pricePerUnitDistance, double priceForTotalTime, int maxSpace, double minFare, double eta, String srcAddress, String destAddress, String currencySign, Double distance, Double tax, String unit, double cancelFee, String vehicleImage, boolean isDestinationSelect, double surgeMultiPier, boolean isSurgePricing, int tripType) {
        NumberFormat currencyFormat;
        currencyFormat = currencyHelper.getCurrencyFormatFractionDigits(currencySign);
        this.isDestinationSelect = isDestinationSelect;
        tvVehicleType.setText(vehicleTypeName);

        switch (tripType) {
            case Const.TripType.AIRPORT:
                goWithTripType(currencyFormat, minFare, maxSpace, context.getResources().getString(R.string.text_airport_trip));
                break;
            case Const.TripType.ZONE:
                goWithTripType(currencyFormat, minFare, maxSpace, context.getResources().getString(R.string.text_zone_trip));
                break;
            case Const.TripType.CITY:
                goWithTripType(currencyFormat, minFare, maxSpace, context.getResources().getString(R.string.text_city_trip));
                break;
            case Const.TripType.NORMAL:
                goWithNormalTrip();
                break;
            default:
                //Default case here..
                break;
        }

        tvMinFare.setText(currencyFormat.format(basePrice) + Utils.validBasePriceSuffix(getContext(), basePriceDistance, unit));
        tvPerMile.setText(currencyFormat.format(pricePerUnitDistance) + context.getResources().getString(R.string.text_unit_per_kms) + unit);
        tvTimeCost.setText(currencyFormat.format(priceForTotalTime) + context.getResources().getString(R.string.text_unit_per_min));
        tvMaxSize.setText(maxSpace + " " + context.getResources().getString(R.string.text_person));
        tvMainFareEta.setText(currencyFormat.format(minFare));

        tvFareSrc.setText(Utils.trimString(srcAddress));
        tvFareDest.setText(Utils.trimString(destAddress));
        if (TextUtils.isEmpty(destAddress)) {
            tvFareDistance.setText("0.00");
            tvEta.setText("0");
            tvMainFareEta.setText("0.00");
        } else {
            tvFareDistance.setText(ParseContent.getInstance().twoDigitDecimalFormat.format(distance));
            tvEta.setText(ParseContent.getInstance().twoDigitDecimalFormat.format(eta));
        }

        tvTax.setText(ParseContent.getInstance().twoDigitDecimalFormat.format(tax) + " " + Const.PERCENTAGE);

        if (cancelFee > 0) {
            llCancelFee.setVisibility(View.VISIBLE);
            tvCancelFee.setText(currencyFormat.format(cancelFee));
        }

        if (isDestinationSelect) {
            btnGetFareEstimate.setText(context.getResources().getString(R.string.text_continue));
            tvFareDest.setClickable(true);
        } else {
            tvFareDest.setClickable(false);
        }

        if (isSurgePricing) {
            llSurgePricing.setVisibility(View.VISIBLE);
            tvSurgePricing.setText(ParseContent.getInstance().twoDigitDecimalFormat.format(surgeMultiPier) + "x");
        } else {
            llSurgePricing.setVisibility(View.GONE);
            tvSurgePricing.setText("1.0x");
        }

        tvFareUnit.setText(unit);
        GlideApp.with(context).load(IMAGE_BASE_URL + vehicleImage).override(200, 200).fallback(R.drawable.ellipse).into(ivVehicleImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetFareEstimate:
                onClickFareEstimate(isDestinationSelect);
                break;
            case R.id.btnCancel:
                onBack();
                break;
            case R.id.btnProceed:
                onProceed();
                break;
            case R.id.tvFareDest:
                selectDestination();
                break;
            default:
                //Do something with default
                break;
        }
    }

    public abstract void onClickFareEstimate(boolean isDestinationSelect);

    public abstract void onBack();
    public abstract void onProceed();

    public abstract void selectDestination();

    private void goWithTripType(NumberFormat currencyFormat, double fixedPrice, int maxSpace, String type) {
        llBaseFareLayout.setVisibility(View.GONE);
        llFixedFareLayout.setVisibility(View.VISIBLE);
        tvTripType.setText(type);
        tvFixedRateMaxSize.setText(maxSpace + " " + context.getResources().getString(R.string.text_person));
        tvFixedRateAmount.setText(currencyFormat.format(fixedPrice));
        llSurgePricing.setVisibility(View.GONE);
        tvEtaMessage.setText(context.getResources().getString(R.string.msg_fixed_fare));
        tvLabelMinFare.setText(context.getResources().getString(R.string.text_fixed_rate));
    }

    private void goWithNormalTrip() {
        llBaseFareLayout.setVisibility(View.VISIBLE);
        llFixedFareLayout.setVisibility(View.GONE);
        llSurgePricing.setVisibility(View.VISIBLE);
        tvEtaMessage.setText(context.getResources().getString(R.string.msg_estimate_fare));
        tvLabelMinFare.setText(context.getResources().getString(R.string.text_min_fare_caps));
    }
}