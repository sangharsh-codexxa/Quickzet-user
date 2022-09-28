package com.elluminati.eber.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.elluminati.eber.R;

/**
 * Created by elluminati on 22-07-2016.
 * <p/>
 * this dialog is ued to confirmation of user ,and this dialog is show label and message with two
 * button.
 */
public abstract class CustomDialogPaymentFailed extends Dialog implements View.OnClickListener {
    private final MyFontTextView tvDialogMessage;
    private final MyFontButton btnYes;
    private final MyFontButton btnNo;
    private final MyFontButton btnPayByOtherPayment;
    private final MyAppTitleFontTextView tvDialogLabel;

    public CustomDialogPaymentFailed(Context context, String dialogLabel, String dialogMessage, String positiveBtnLabel, String negativeBtnLabel, String payByOtherPayment) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_payment_failed);
        tvDialogLabel = findViewById(R.id.tvDialogLabel);
        tvDialogMessage = findViewById(R.id.tvDialogMessage);
        btnYes = findViewById(R.id.btnYes);
        btnPayByOtherPayment = findViewById(R.id.btnPayByOtherPayment);
        btnYes.setText(positiveBtnLabel);
        btnNo = findViewById(R.id.btnNo);
        btnPayByOtherPayment.setText(payByOtherPayment);

        if (TextUtils.isEmpty(negativeBtnLabel)) {
            btnNo.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnYes.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, (int) context.getResources().getDimension(R.dimen.activity_horizontal_margin));
        } else {
            btnNo.setText(negativeBtnLabel);
            btnNo.setOnClickListener(this);
        }
        btnYes.setOnClickListener(this);
        btnPayByOtherPayment.setOnClickListener(this);
        tvDialogLabel.setText(dialogLabel);
        tvDialogMessage.setText(dialogMessage);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        getWindow().setDimAmount(0);
        this.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnYes:
                positiveButton();
                break;
            case R.id.btnNo:
                negativeButton();
                break;
            case R.id.btnPayByOtherPayment:
                payByOtherPaymentButton();
                break;
            default:
                break;
        }
    }

    public void notifyDataSetChanged(String data) {
        tvDialogMessage.setText(data);
    }

    public abstract void positiveButton();

    public abstract void negativeButton();

    public abstract void payByOtherPaymentButton();
}
