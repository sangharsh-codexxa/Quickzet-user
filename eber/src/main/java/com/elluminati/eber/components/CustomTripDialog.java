package com.elluminati.eber.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.elluminati.eber.R;
import com.elluminati.eber.utils.GlideApp;

/**
 * Created by elluminati on 25-07-2016.
 */
public abstract class CustomTripDialog extends Dialog implements View.OnClickListener {

    private final ImageView ivDriverPhotoDialog;
    private final MyFontButton btnTripCancel;
    private final Context context;
    private String profileUrl;

    public CustomTripDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_driver_detail);
        this.context = context;
        ivDriverPhotoDialog = findViewById(R.id.ivDriverPhotoDialog);
        btnTripCancel = findViewById(R.id.btnTripCancel);
        btnTripCancel.setOnClickListener(this);
        GlideApp.with(context).asGif().load(R.raw.nearby_provider).into(ivDriverPhotoDialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
        getWindow().setDimAmount(0);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setCancelable(false);
    }


    @Override
    public void onClick(View v) {
        doWithOk();
    }

    public abstract void doWithOk();
}
