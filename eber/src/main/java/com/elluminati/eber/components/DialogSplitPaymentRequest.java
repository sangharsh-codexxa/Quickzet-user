package com.elluminati.eber.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.elluminati.eber.R;
import com.elluminati.eber.adapter.PaymentAdapter;
import com.elluminati.eber.models.datamodels.PaymentGateway;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.Utils;

import java.util.ArrayList;

public abstract class DialogSplitPaymentRequest extends Dialog implements View.OnClickListener {
    private final Context context;
    private final MyAppTitleFontTextView tvDescription;
    private final MyFontButton btnYes;
    private final MyFontButton btnNo;
    private final View llPaymentOption;
    private final Spinner spPaymentMode;
    private SplitPaymentRequest splitPaymentRequest;

    public DialogSplitPaymentRequest(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_split_payment_request);
        this.context = context;
        splitPaymentRequest = CurrentTrip.getInstance().getSplitPaymentRequest();

        tvDescription = findViewById(R.id.tvDescription);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);
        llPaymentOption = findViewById(R.id.llPaymentOption);
        spPaymentMode = findViewById(R.id.spPaymentMode);

        notifyDataSetChange(splitPaymentRequest);

        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0.5f);
        this.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnYes) {
            if (splitPaymentRequest.getStatus() == Const.SplitPaymentStatus.ACCEPTED) {
                if (spPaymentMode.getSelectedItemPosition() == 0) {
                    CurrentTrip.getInstance().getSplitPaymentRequest().setPaymentMode(String.valueOf(Const.CASH));
                } else {
                    CurrentTrip.getInstance().getSplitPaymentRequest().setPaymentMode(String.valueOf(Const.CARD));
                }
            }
            positiveButton();
        } else if (id == R.id.btnNo) {
            negativeButton();
        }
    }

    public abstract void positiveButton();

    public abstract void negativeButton();

    public void notifyDataSetChange(SplitPaymentRequest splitPaymentRequest) {
        this.splitPaymentRequest = splitPaymentRequest;

        if (splitPaymentRequest != null) {
            tvDescription.setText(String.format("%s %s\n%s %s",
                    splitPaymentRequest.getFirstName(), splitPaymentRequest.getLastName(),
                    splitPaymentRequest.getCountryPhoneCode(), splitPaymentRequest.getPhone()));

            if (splitPaymentRequest.getStatus() == Const.SplitPaymentStatus.WAITING) {
                llPaymentOption.setVisibility(View.GONE);
            } else if (splitPaymentRequest.getStatus() == Const.SplitPaymentStatus.ACCEPTED
                    && TextUtils.isEmpty(splitPaymentRequest.getPaymentMode())) {
                initPaymentModeSpinner();
                llPaymentOption.setVisibility(View.VISIBLE);
                btnYes.setText(context.getString(R.string.text_confirm));
                btnNo.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnYes.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, (int) context.getResources().getDimension(R.dimen.activity_horizontal_margin));
            }
        }
    }

    private void initPaymentModeSpinner() {
        ArrayList<PaymentGateway> paymentGatewaysList = new ArrayList<>();
        PaymentGateway paymentGateway = new PaymentGateway();
        paymentGateway.setName(context.getString(R.string.text_cash));
        paymentGatewaysList.add(paymentGateway);
        paymentGateway = new PaymentGateway();
        paymentGateway.setName(context.getString(R.string.text_card));
        paymentGatewaysList.add(paymentGateway);

        PaymentAdapter paymentAdapter = new PaymentAdapter(context, paymentGatewaysList);
        spPaymentMode.setAdapter(paymentAdapter);

        spPaymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    btnNo.setVisibility(View.VISIBLE);
                    btnNo.setText(context.getString(R.string.text_go_to_payment));
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnYes.getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, 0);
                } else {
                    btnNo.setVisibility(View.GONE);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnYes.getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, (int) context.getResources().getDimension(R.dimen.activity_horizontal_margin));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}