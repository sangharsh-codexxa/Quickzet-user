package com.elluminati.eber.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.R;
import com.elluminati.eber.models.datamodels.TripHistory;

import java.util.List;

public class RecentTripHistoryAdapter extends RecyclerView.Adapter<RecentTripHistoryAdapter.ViewHolder> {
     List<TripHistory> tripHistoryList;
    ItemClickListener itemClickListener;

    public RecentTripHistoryAdapter(List<TripHistory> tripHistoryList, ItemClickListener itemClickListener) {
        this.tripHistoryList = tripHistoryList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_trip_history,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            holder.textView.setText(tripHistoryList.get(position).getDestinationAddress());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(tripHistoryList.get(position).getDestinationAddress().toString());
                }
            });
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if(tripHistoryList.size() > 5){
            return 5;
        }else {
            return tripHistoryList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.destinationName);
        }
    }
    public interface ItemClickListener {
        void onClick(String place);
    }
}
