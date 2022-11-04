package com.elluminati.eber.adapter;

import static com.elluminati.eber.utils.Const.IMAGE_BASE_URL;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.MainDrawerActivity;
import com.elluminati.eber.R;
import com.elluminati.eber.components.MyAppTitleFontTextView;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.Card;
import com.elluminati.eber.models.datamodels.CityType;
import com.elluminati.eber.models.datamodels.TypeDetails;
import com.elluminati.eber.models.singleton.CurrentTrip;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.GlideApp;

import java.text.NumberFormat;
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
        Log.i("AAA", " : " + IMAGE_BASE_URL + typeDetails.getTypeImageUrl());
        GlideApp.with(drawerActivity).load(IMAGE_BASE_URL + typeDetails.getTypeImageUrl()).fallback(R.drawable.ellipse).into(holder.ivVehicleImage);
        holder.tvVehicleType.setText(typeDetails.getTypename());


        ViewGroup.LayoutParams layoutParams = holder.viewSelectDiv.getLayoutParams();
        NumberFormat currencyFormat = null;
        if (drawerActivity.currentTrip.getVehiclePriceType() == Const.TYPE_SHARE_TRIP_PRICE && poolVehicleTypeList.get(position).isSelected) {
//        if (drawerActivity.currentTrip.getVehiclePriceType() == Const.TYPE_SHARE_TRIP_PRICE && poolVehicleTypeList.get(position).isSelected){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvVehicleType.setTextAppearance(R.style.TextAppearance_AppCompat_Large);
//                holder.tvCharges.setTextAppearance(R.style.TextAppearanceMedium);
            } else {
                holder.tvVehicleType.setTextAppearance(drawerActivity, R.style.TextAppearanceMedium);
//                holder.tvCharges.setTextAppearance(drawerActivity, R.style.TextAppearanceMedium);

            }
            holder.viewSelectDiv.setBackground(drawerActivity.getDrawable(R.drawable.squre_bg));


        } else if (drawerActivity.currentTrip.getVehiclePriceType() == Const.TYPE_NORMAL_PRICE && vehicleTypeList.get(position).isSelected) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                holder.tvVehicleType.setTextAppearance(R.style.TextAppearanceSmall);

                holder.tvVehicleType.setTextAppearance(R.style.TextAppearanceMedium);
//                holder.tvCharges.setTextAppearance(R.style.TextAppearanceMedium);

            } else {
                holder.tvVehicleType.setTextAppearance(drawerActivity, R.style.TextAppearanceSmall);
//                holder.tvCharges.setTextAppearance(drawerActivity, R.style.TextAppearanceMedium);

            }
            currencyFormat = drawerActivity.currencyHelper.getCurrencyFormat(CurrentTrip.getInstance().getCurrencyCode());
//        tvFareEst.setText(currencyFormat.format(etaResponse.getEstimatedFare()));


//            Log.e("Prices",String.valueOf(vehicleTypeList.get(position).getBasePriceDistance()+"\n"+vehicleTypeList.get(position).getBasePrice()+"\n"+vehicleTypeList.get(position).getBasePriceTime()+"\n"+vehicleTypeList.get(position).getPriceForTotalTime()));
            holder.viewSelectDiv.setBackground(drawerActivity.getDrawable(R.drawable.squre_bg));

//            holder.viewSelectDiv.setBackgroundColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_red, null));
//            layoutParams.height = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_div_height_2dp);
//            ViewGroup.MarginLayoutParams layoutParams =
//                    (ViewGroup.MarginLayoutParams) holder.vehicleCardView.getLayoutParams();
//            layoutParams.setMargins(5, 5, 5, 5);
//            holder.vehicleCardView.requestLayout();

        } else if (drawerActivity.currentTrip.getVehiclePriceType() == Const.TYPE_RENTAL_PRICE && vehicleTypeList.get(position).isSelected) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                holder.tvVehicleType.setTextAppearance(R.style.TextAppearanceMedium);
//                holder.tvCharges.setTextAppearance(R.style.TextAppearanceMedium);

            } else {
                holder.tvVehicleType.setTextAppearance(drawerActivity, R.style.TextAppearanceSmall);
//              holder.tvCharges.setTextAppearance(drawerActivity, R.style.TextAppearanceMedium);
            }
            holder.viewSelectDiv.setBackground(drawerActivity.getDrawable(R.drawable.squre_bg));

//            holder.viewSelectDiv.setBackgroundColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_red, null));
//            ViewGroup.MarginLayoutParams layoutParams =
//                    (ViewGroup.MarginLayoutParams) holder.vehicleCardView.getLayoutParams();
//            layoutParams.setMargins(5, 0, 5, 0);
//            holder.vehicleCardView.requestLayout();
//            layoutParams1.height = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_div_height_2dp);
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                holder.tvVehicleType.setTextAppearance(R.style.TextAppearanceSmall);
//                holder.tvCharges.setTextAppearance(R.style.TextAppearanceSmall);

            } else {
//                holder.tvVehicleType.setTextAppearance(drawerActivity, R.style.TextAppearanceSmall);
                holder.tvCharges.setTextAppearance(drawerActivity, R.style.TextAppearanceMedium);

            }
//            ViewGroup.MarginLayoutParams layoutParams =
//                    (ViewGroup.MarginLayoutParams) holder.vehicleCardView.getLayoutParams();
//            layoutParams.setMargins(5, 5, 5, 5);
//            holder.vehicleCardView.requestLayout();
            holder.viewSelectDiv.setBackgroundColor(ResourcesCompat.getColor(drawerActivity.getResources(), R.color.color_app_trans_white, null));
//            layoutParams.height = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_div_height_1dp);
        }
        if (showRentalTypeOnly) {
            if (vehicleTypeList.get(position).getVehiclePriceType() == Const.TYPE_RENTAL_PRICE) {
                holder.itemView.setVisibility(View.VISIBLE);
//                holder.itemView.getLayoutParams().width = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_vehicle_type_width);
//                holder.itemView.getLayoutParams().height = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_vehicle_type_height);

            } else {
                holder.itemView.setVisibility(View.GONE);
//                holder.itemView.getLayoutParams().width = 0;
//                holder.itemView.getLayoutParams().height = 0;
            }
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
//            holder.itemView.getLayoutParams().width = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_vehicle_type_width);
//            holder.itemView.getLayoutParams().height = drawerActivity.getResources().getDimensionPixelOffset(R.dimen.dimen_vehicle_type_height);
        }


//        holder.tvCharges.setText("₹ " + String.valueOf(vehicleTypeList.get(position).getBasePrice()));

    }

    @Override
    public int getItemCount() {
        return drawerActivity.currentTrip.getVehiclePriceType() == Const.TYPE_SHARE_TRIP_PRICE? poolVehicleTypeList.size():vehicleTypeList.size();
    }

    public class VehicleViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView ivVehicleImage;
        MyFontTextView tvVehicleType;
        MyFontTextView tvCharges;
      View viewSelectDiv;
        CardView vehicleCardView;

        public VehicleViewHolder(View itemView) {
            super(itemView);
            ivVehicleImage = itemView.findViewById(R.id.ivVehicle);
            tvVehicleType = itemView.findViewById(R.id.tvVehicleTypeFancy);
            viewSelectDiv = itemView.findViewById(R.id.viewSelectDiv);
            vehicleCardView = itemView.findViewById(R.id.card_vehicle_item);
//            tvCharges = itemView.findViewById(R.id.tvVehicleTypeCharges);
        }


    }
}
