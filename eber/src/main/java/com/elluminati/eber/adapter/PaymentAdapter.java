package com.elluminati.eber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.elluminati.eber.R;
import com.elluminati.eber.components.MyFontTextView;

import java.util.ArrayList;

public class PaymentAdapter extends BaseAdapter {

    private final ArrayList<com.elluminati.eber.models.datamodels.PaymentGateway> selectedPaymentList;
    private final LayoutInflater layoutInflater;
    private ViewHolder viewHolder;

    public PaymentAdapter(Context context, ArrayList<com.elluminati.eber.models.datamodels.PaymentGateway> selectedPaymentList) {
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
            view = layoutInflater.inflate(R.layout.item_payment_list, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.tvPaymentName = view.findViewById(R.id.tvPaymentName);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvPaymentName.setText(selectedPaymentList.get(i).getName());
        return view;
    }

    private static class ViewHolder {
        MyFontTextView tvPaymentName;
    }
}
