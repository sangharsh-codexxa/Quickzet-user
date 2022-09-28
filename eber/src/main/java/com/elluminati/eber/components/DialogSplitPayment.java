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
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.Const;

public abstract class DialogSplitPayment extends Dialog implements View.OnClickListener {
    private final Context context;
    private final MyFontTextView tvInvoiceTotal;
    private final MyFontButton btnPay;

    public DialogSplitPayment(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_split_payment);
        this.context = context;

        tvInvoiceTotal = findViewById(R.id.tvInvoiceTotal);
        btnPay = findViewById(R.id.btnPay);

        notifyDataSetChange(CurrentTrip.getInstance().getSplitPaymentRequest());
        btnPay.setOnClickListener(this);

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
        if (id == R.id.btnPay) {
            positiveButton();
        }
    }

    public abstract void positiveButton();

    public void notifyDataSetChange(SplitPaymentRequest splitPaymentRequest) {
        if (splitPaymentRequest != null) {
            String total = ParseContent.getInstance().twoDigitDecimalFormat.format(splitPaymentRequest.getTotal());
            tvInvoiceTotal.setText(String.format("%s %s", splitPaymentRequest.getCurrency(), total));
        }
    }
}
