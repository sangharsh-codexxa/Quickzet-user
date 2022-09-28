package com.elluminati.eber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.elluminati.eber.R;
import com.elluminati.eber.components.MyAppTitleFontTextView;
import com.elluminati.eber.models.datamodels.PaymentGateway;

import java.util.ArrayList;

/**
 * Created by elluminati on 03-Jan-17.
 */
public class TripPaymentAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<PaymentGateway> selectedPaymentList;
    private final LayoutInflater layoutInflater;
    private ViewHolder viewHolder;

    public TripPaymentAdapter(Context context, ArrayList<PaymentGateway> selectedPaymentList) {
        this.context = context;
        this.selectedPaymentList = selectedPaymentList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return selectedPaymentList.size();
    }

    @Override
    public String getItem(int i) {
        return selectedPaymentList.get(i).getName();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_trip_payment_list, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.tvPaymentName = view.findViewById(R.id.tvPaymentName);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvPaymentName.setText(selectedPaymentList.get(i).getName());
        return view;
    }

    private class ViewHolder {
        MyAppTitleFontTextView tvPaymentName;

    }
}
