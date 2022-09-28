package com.elluminati.eber.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.elluminati.eber.R;
import com.elluminati.eber.utils.Const;

/**
 * Created by Elluminati on 13-Jul-17.
 */

public abstract class CustomAddPaymentDialog extends Dialog {

    private final LinearLayout llCard;
    private final LinearLayout llCash;


    public CustomAddPaymentDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_payment);

        llCard = findViewById(R.id.llCard);
        llCash = findViewById(R.id.llCash);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        llCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelect(Const.CARD);
            }
        });


        llCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelect(Const.CASH);
            }
        });
    }


    public void checkPaymentMode(int card, int cash) {

        if (cash == Const.NOT_AVAILABLE) {
            llCash.setVisibility(View.GONE);
        }
        if (card == Const.NOT_AVAILABLE) {
            llCard.setVisibility(View.GONE);
        }
    }

    public abstract void onSelect(int type);
}
