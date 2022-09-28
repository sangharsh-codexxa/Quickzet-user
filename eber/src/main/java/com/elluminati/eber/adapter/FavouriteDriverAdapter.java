package com.elluminati.eber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.R;
import com.elluminati.eber.models.datamodels.FavouriteDriver;
import com.elluminati.eber.utils.GlideApp;

import java.util.ArrayList;

import static com.elluminati.eber.utils.Const.IMAGE_BASE_URL;

public abstract class FavouriteDriverAdapter extends RecyclerView.Adapter<FavouriteDriverAdapter.FavouriteDriverItemHolder> {

    private final ArrayList<FavouriteDriver> favouriteDriverArrayList;
    private Context context;
    private boolean isAddDriver;

    public FavouriteDriverAdapter(ArrayList<FavouriteDriver> favouriteDriverArrayList) {
        this.favouriteDriverArrayList = favouriteDriverArrayList;
    }

    public void enableAddDriver(boolean addDriver) {
        isAddDriver = addDriver;
    }

    @NonNull
    @Override
    public FavouriteDriverItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new FavouriteDriverItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite_driver, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteDriverItemHolder holder, int position) {

        FavouriteDriver favouriteDriver = favouriteDriverArrayList.get(position);
        GlideApp.with(context).load(IMAGE_BASE_URL + favouriteDriver.getPicture()).fallback(R.drawable.ellipse).override(200, 200).placeholder(R.drawable.ellipse).into(holder.ivDriverImage);
        holder.tvDriverName.setText(String.format("%s %s", favouriteDriver.getFirstName(), favouriteDriver.getLastName()));

    }

    @Override
    public int getItemCount() {
        return favouriteDriverArrayList.size();
    }

    public abstract void onClick(int position);

    protected class FavouriteDriverItemHolder extends RecyclerView.ViewHolder {
        ImageView ivDriverImage, ivAction;
        TextView tvDriverName;

        public FavouriteDriverItemHolder(View itemView) {
            super(itemView);
            ivDriverImage = itemView.findViewById(R.id.ivDriverImage);
            ivAction = itemView.findViewById(R.id.ivAction);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            ivAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FavouriteDriverAdapter.this.onClick(getAdapterPosition());
                }
            });
            if (isAddDriver) {
                ivAction.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.add_black_btn));
            } else {
                ivAction.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.cross_balck_icon));
            }

        }
    }
}
