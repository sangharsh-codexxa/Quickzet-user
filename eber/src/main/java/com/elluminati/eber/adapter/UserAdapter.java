package com.elluminati.eber.adapter;

import static com.elluminati.eber.utils.Const.IMAGE_BASE_URL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.R;
import com.elluminati.eber.models.datamodels.SplitPaymentRequest;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.GlideApp;

import java.util.ArrayList;

public abstract class UserAdapter extends RecyclerView.Adapter<UserAdapter.FavouriteDriverItemHolder> {

    private final ArrayList<SplitPaymentRequest> list;
    private final Context context;
    private boolean isShowPrise = false;

    public UserAdapter(Context context, ArrayList<SplitPaymentRequest> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FavouriteDriverItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavouriteDriverItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteDriverItemHolder holder, int position) {
        SplitPaymentRequest splitPaymentRequest = list.get(position);
        GlideApp.with(context).load(IMAGE_BASE_URL + splitPaymentRequest.getPicture()).fallback(R.drawable.ellipse).override(200, 200).placeholder(R.drawable.ellipse).into(holder.ivUserImage);
        holder.tvUserName.setText(String.format("%s %s", splitPaymentRequest.getFirstName(), splitPaymentRequest.getLastName()));
        holder.tvPhoneNumber.setText(String.format("%s %s", splitPaymentRequest.getCountryPhoneCode(), splitPaymentRequest.getPhone()));

        if (splitPaymentRequest.getStatus() >= Const.SplitPaymentStatus.WAITING) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.ivAction.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.cross_balck_icon));
        } else {
            holder.tvStatus.setVisibility(View.GONE);
            holder.ivAction.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.add_black_btn));
        }

        if (splitPaymentRequest.getStatus() == Const.SplitPaymentStatus.WAITING) {
            holder.tvStatus.setText(context.getString(R.string.text_waiting));
            holder.tvStatus.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.color_app_button, null));
        } else if (splitPaymentRequest.getStatus() == Const.SplitPaymentStatus.ACCEPTED) {
            holder.tvStatus.setText(context.getString(R.string.text_accepted));
            holder.tvStatus.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.color_app_wallet_added, null));
        } else if (splitPaymentRequest.getStatus() == Const.SplitPaymentStatus.REJECTED) {
            holder.tvStatus.setText(context.getString(R.string.text_resend));
            holder.tvStatus.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.color_app_red, null));
        }

        if (isShowPrise) {
            String total = ParseContent.getInstance().twoDigitDecimalFormat.format(splitPaymentRequest.getTotal());
            holder.tvStatus.setText(String.format("%s %s", splitPaymentRequest.getCurrency(), total));
            holder.tvStatus.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.color_black, null));
            holder.ivAction.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public abstract void onButtonClick(int position);

    public abstract void onStatusClick(int position);

    public void setShowPrise(boolean showPrise) {
        isShowPrise = showPrise;
    }

    protected class FavouriteDriverItemHolder extends RecyclerView.ViewHolder {
        ImageView ivUserImage, ivAction;
        TextView tvUserName, tvPhoneNumber, tvStatus;

        public FavouriteDriverItemHolder(View itemView) {
            super(itemView);
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
            ivAction = itemView.findViewById(R.id.ivAction);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivAction.setOnClickListener(v -> onButtonClick(getAbsoluteAdapterPosition()));
            tvStatus.setOnClickListener(v -> onStatusClick(getAbsoluteAdapterPosition()));
        }
    }
}
