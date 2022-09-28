package com.elluminati.eber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.R;
import com.elluminati.eber.models.datamodels.CityTypeRental;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.CurrencyHelper;
import com.elluminati.eber.utils.Utils;

import java.text.NumberFormat;
import java.util.ArrayList;

public class RentalPackagesAdapter extends RecyclerView.Adapter<RentalPackagesAdapter.RentalPackagesViewHolder> {

    public static final int INVALID_POSITION = -1;
    private final ArrayList<CityTypeRental> rentalList;
    private int rentSelectedPosition = INVALID_POSITION;
    private Context context;
    private CurrencyHelper currencyHelper;

    public RentalPackagesAdapter(ArrayList<CityTypeRental> rentalList) {
        this.rentalList = rentalList;
        clearSelection();
    }

    public int getRentSelectedPosition() {
        return rentSelectedPosition;
    }

    private void setRentSelectedPosition(int position) {
        rentalList.get(position).setSelected(true);
        rentSelectedPosition = position;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RentalPackagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        currencyHelper = CurrencyHelper.getInstance(context);
        return new RentalPackagesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_packages, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RentalPackagesViewHolder holder, final int position) {
        CityTypeRental cityTypeRental = rentalList.get(position);
        NumberFormat currencyFormat = currencyHelper.getCurrencyFormat(CurrentTrip.getInstance().getCurrencyCode());
        String basePrice = currencyFormat.format(cityTypeRental.getBasePrice());
        String baseTimeAndDistance = ParseContent.getInstance().twoDigitDecimalFormat.format(cityTypeRental.getBasePriceTime()) + context.getResources().getString(R.string.text_unit_min) + " & " + ParseContent.getInstance().twoDigitDecimalFormat.format(cityTypeRental.getBasePriceDistance()) + Utils.showUnit(context, CurrentTrip.getInstance().getUnit());
        String extraTimePrice = currencyFormat.format(cityTypeRental.getPriceForTotalTime()) + context.getResources().getString(R.string.text_unit_per_min);
        String extraDistancePrice = currencyFormat.format(cityTypeRental.getPricePerUnitDistance()) + "/" + Utils.showUnit(context, CurrentTrip.getInstance().getUnit());
        String packageInfo = context.getResources().getString(R.string.msg_for_extra_charge, extraDistancePrice, extraTimePrice);
        holder.tvPackageName.setText(cityTypeRental.getTypeName());
        holder.tvPackagePrice.setText(basePrice);
        holder.tvPackageUnitPrice.setText(baseTimeAndDistance);
        holder.tvPackageInfo.setText(packageInfo);
        holder.cbSelectedPackages.setChecked(cityTypeRental.isSelected());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSelection();
                setRentSelectedPosition(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return rentalList == null ? 0 : rentalList.size();
    }

    private void clearSelection() {
        for (CityTypeRental cityTypeRental : rentalList) {
            cityTypeRental.setSelected(false);
        }
    }

    class RentalPackagesViewHolder extends RecyclerView.ViewHolder {
        TextView tvPackageName, tvPackagePrice, tvPackageUnitPrice, tvPackageInfo;
        RadioButton cbSelectedPackages;

        RentalPackagesViewHolder(View itemView) {
            super(itemView);
            tvPackageName = itemView.findViewById(R.id.tvPackageName);
            tvPackagePrice = itemView.findViewById(R.id.tvPackagePrice);
            tvPackageUnitPrice = itemView.findViewById(R.id.tvPackageUnitPrice);
            tvPackageInfo = itemView.findViewById(R.id.tvPackageInfo);
            cbSelectedPackages = itemView.findViewById(R.id.cbSelectedPackages);
            cbSelectedPackages.setClickable(false);
        }
    }
}
