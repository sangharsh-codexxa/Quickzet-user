package com.elluminati.eber.adapter;

import static com.elluminati.eber.utils.Const.IMAGE_BASE_URL;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.MainDrawerActivity;
import com.elluminati.eber.R;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.CityType;
import com.elluminati.eber.models.datamodels.TypeDetails;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.GlideApp;

import java.util.ArrayList;

/**
 * Created by elluminati on 19-07-2016.
 */
public class VehicleSelectAdapter extends RecyclerView.Adapter<VehicleSelectAdapter.VehicleViewHolder> {

    private final MainDrawerActivity drawerActivity;
    private final ArrayList<CityType> vehicleTypeList, poolVehicleTypeList;
    private boolean showRentalTypeOnly;

    public VehicleSelectAdapter(MainDrawerActivity drawerActivity, ArrayList<CityType> vehicleTypeList, ArrayList<CityType> poolVehicleTypeList) {

        this.drawerActivity = drawerActivity;
        this.vehicleTypeList = vehicleTypeList;
        this.poolVehicleTypeList = poolVehicleTypeList;
    }


    public void setShowRentalTypeOnly(boolean showRentalTypeOnly) {
        this.showRentalTypeOnly = showRentalTypeOnly;
    }

    @Override
    public VehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle, parent, false);
        VehicleViewHolder vehicleViewHolder = new VehicleViewHolder(view);
        return vehicleViewHolder;
    }

    @Override
    public void onBindViewHolder(VehicleViewHolder holder, int position) {
        TypeDetails typeDetails = drawerActivity.currentTrip.getVehiclePriceType() == Const.TYPE_SHARE_TRIP_PRICE ? poolVehicleTypeList.get(position).getTypeDetails() : vehicleTypeList.get(position).getTypeDetails();
//        TypeDetails typeDetails = drawerActivity.currentTrip.getVehiclePriceType() == Const.TYPE_SHARE_TRIP_PRICE? poolVehicleTypeList.get(position).getTypeDetails():vehicleTypeList.get(position).getTypeDetails();
        Log.i("AAA"," : "+IMAGE_BASE_URL + typeDetails.getTypeImageUrl());
        GlideApp.with(drawerActivity).load(IMAGE_BASE_URL + typeDetails.getTypeImageUrl()).fallback(R.drawable.ellipse).into(holder.ivVehicleImage);
        holder.tvVehicleType.setText(typeDetails.getTypename());

        ViewGroup.LayoutParams layoutParams = holder.viewSelectDiv.getLayoutParams();
        if (drawerActivity.currentTrip.getVehiclePriceType() == Const.TYPE_SHARE_TRIP_PRICE && poolVehicleTypeList.get(position).isSelected) {
//        if (drawerActivity.currentTrip.getVehiclePriceType() == Const.TYPE_SHARE_TRIP_PRICE && poolVehicleTypeList.get(position).isSelected){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvVehicleType.setTextAppearance(R.style.TextAppearanceMedium);
            } else {
                holder.tvVehicleType.setTextAppearance(drawerActivity, R.style.TextAppearanceMedium);
            }
            holder.viewSelectDiv.setBackgroundColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_theme_dark, null));
            layoutParams.height = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_div_height_2dp);
        }
        else if (drawerActivity.currentTrip.getVehiclePriceType() == Const.TYPE_NORMAL_PRICE && vehicleTypeList.get(position).isSelected) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvVehicleType.setTextAppearance(R.style.TextAppearanceMedium);
            } else {
                holder.tvVehicleType.setTextAppearance(drawerActivity, R.style.TextAppearanceMedium);
            }
            holder.viewSelectDiv.setBackgroundColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_theme_dark, null));
            layoutParams.height = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_div_height_2dp);
        }else if (drawerActivity.currentTrip.getVehiclePriceType() == Const.TYPE_RENTAL_PRICE && vehicleTypeList.get(position).isSelected) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvVehicleType.setTextAppearance(R.style.TextAppearanceMedium);
            } else {
                holder.tvVehicleType.setTextAppearance(drawerActivity, R.style.TextAppearanceMedium);
            }
            holder.viewSelectDiv.setBackgroundColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_theme_dark, null));
            layoutParams.height = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_div_height_2dp);
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvVehicleType.setTextAppearance(R.style.TextAppearanceSmall);
            } else {
                holder.tvVehicleType.setTextAppearance(drawerActivity, R.style.TextAppearanceSmall);
            }
            holder.viewSelectDiv.setBackgroundColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_divider_horizontal, null));
            layoutParams.height = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_div_height_1dp);
        }
        if (showRentalTypeOnly) {
            if (vehicleTypeList.get(position).getVehiclePriceType() == Const.TYPE_RENTAL_PRICE) {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.getLayoutParams().width = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_vehicle_type_width);
                holder.itemView.getLayoutParams().height = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_vehicle_type_height);

            } else {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.getLayoutParams().width = 0;
                holder.itemView.getLayoutParams().height = 0;
            }
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.getLayoutParams().width = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_vehicle_type_width);
            holder.itemView.getLayoutParams().height = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_vehicle_type_height);
        }


    }

    @Override
    public int getItemCount() {
        return drawerActivity   .currentTrip.getVehiclePriceType() == Const.TYPE_SHARE_TRIP_PRICE? poolVehicleTypeList.size():vehicleTypeList.size();
    }

    public class VehicleViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView ivVehicleImage;
        MyFontTextView tvVehicleType;
        View viewSelectDiv;

        public VehicleViewHolder(View itemView) {
            super(itemView);
            ivVehicleImage = itemView.findViewById(R.id.ivVehicle);
            tvVehicleType = itemView.findViewById(R.id.tvVehicleTypeFancy);
            viewSelectDiv = itemView.findViewById(R.id.viewSelectDiv);
        }

    }
}
