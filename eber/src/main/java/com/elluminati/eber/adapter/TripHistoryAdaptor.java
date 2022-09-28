package com.elluminati.eber.adapter;

import static com.elluminati.eber.utils.Const.IMAGE_BASE_URL;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.R;
import com.elluminati.eber.TripHistoryActivity;
import com.elluminati.eber.TripHistoryDetailActivity;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.TripHistory;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.CurrencyHelper;
import com.elluminati.eber.utils.GlideApp;
import com.elluminati.eber.utils.Utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

/**
 * Created by elluminati on 02-07-2016.
 */
public class TripHistoryAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "TripHistoryAdaptor";
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private final TripHistoryActivity historyActivity;
    private final ArrayList<TripHistory> tripHistoryList;
    private final TreeSet<Integer> separatorsSet;
    private final SimpleDateFormat dateFormat;
    private final CurrencyHelper currencyHelper;

    public TripHistoryAdaptor(TripHistoryActivity historyActivity, ArrayList<TripHistory> tripHistoryList, TreeSet<Integer> separatorsSet) {
        this.historyActivity = historyActivity;
        this.tripHistoryList = tripHistoryList;
        this.separatorsSet = separatorsSet;
        dateFormat = historyActivity.parseContent.dateFormat;
        currencyHelper = CurrencyHelper.getInstance(historyActivity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_history, parent, false);
            return new ViewHolderHistory(v);
        } else if (viewType == TYPE_SEPARATOR) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history_date, parent, false);
            return new ViewHolderSeparator(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TripHistory history = tripHistoryList.get(position);
        if (holder instanceof ViewHolderHistory) {
            ViewHolderHistory viewHolder = (ViewHolderHistory) holder;
            NumberFormat currencyFormat = currencyHelper.getCurrencyFormat(history.getCurrencycode());

            viewHolder.tvHistoryDriverName.setText(history.getFirstName() + " " + history.getLastName());
            viewHolder.tvHistoryTotalCost.setText(currencyFormat.format(history.getTotal()));
            try {
                Date date = ParseContent.getInstance().webFormat.parse(history.getUserCreateTime());
                viewHolder.tvHistoryTripTime.setText(ParseContent.getInstance().timeFormat_am.format(date));
            } catch (ParseException e) {
                AppLog.handleException(TripHistoryAdaptor.class.getSimpleName(), e);
            }

            if (history.getIsTripCancelled() == Const.TRUE) {
                if (history.getIsTripCancelledByUser() == Const.TRUE) {
                    viewHolder.tvCanceledBy.setText(historyActivity.getResources().getString(R.string.text_you_canceled_by_user));
                    viewHolder.tvCanceledBy.setVisibility(View.VISIBLE);
                } else if (history.getIsTripCancelledByProvider() == Const.TRUE) {
                    viewHolder.tvCanceledBy.setText(historyActivity.getResources().getString(R.string.text_you_canceled_by_provider));
                    viewHolder.tvCanceledBy.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tvCanceledBy.setText(historyActivity.getResources().getString(R.string.text_you_canceled_by_admin));
                    viewHolder.tvCanceledBy.setVisibility(View.VISIBLE);
                }
            } else {
                viewHolder.tvCanceledBy.setText("");
                viewHolder.tvCanceledBy.setVisibility(View.GONE);
            }

            GlideApp.with(historyActivity).load(IMAGE_BASE_URL + history.getPicture()).fallback(R.drawable.ellipse).override(200, 200).placeholder(R.drawable.ellipse).into(viewHolder.ivDriverPhotoDialog);

        } else {
            ViewHolderSeparator viewHolderSeparator = (ViewHolderSeparator) holder;
            Date currentDate = new Date();
            String date = dateFormat.format(currentDate);
            String historyDate = "";
            Date parseHistoryDate = new Date();
            try {
                parseHistoryDate = ParseContent.getInstance().webFormat.parse(history.getUserCreateTime());
                historyDate = dateFormat.format(parseHistoryDate);
            } catch (ParseException e) {
                AppLog.handleException(TripHistoryAdaptor.class.getSimpleName(), e);
            }
            if (historyDate.equals(date)) {
                viewHolderSeparator.tvDateSeparator.setText(historyActivity.getString(R.string.text_today));
            } else if (historyDate.equals(getYesterdayDateString())) {
                viewHolderSeparator.tvDateSeparator.setText(historyActivity.getString(R.string.text_yesterday));
            } else {
                String daySuffix = Utils.getDayOfMonthSuffix(Integer.valueOf(historyActivity.parseContent.day.format(parseHistoryDate)));
                viewHolderSeparator.tvDateSeparator.setText(daySuffix + " " + historyActivity.parseContent.dateFormatMonth.format(parseHistoryDate));
            }
        }
    }


    @Override
    public int getItemCount() {
        return tripHistoryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return separatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    private String getYesterdayDateString() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    private void goToTripHistoryDetail(String tripId, int unit, String currency) {
        Intent tripDetailIntent = new Intent(historyActivity, TripHistoryDetailActivity.class);
        tripDetailIntent.putExtra(Const.Params.TRIP_ID, tripId);
        tripDetailIntent.putExtra(Const.Params.UNIT, unit);
        tripDetailIntent.putExtra(Const.Params.CURRENCY, currency);
        historyActivity.startActivity(tripDetailIntent);
    }

    private class ViewHolderHistory extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvHistoryDriverName, tvHistoryTripTime, tvHistoryTotalCost, tvCanceledBy;
        LinearLayout llHistory;
        ImageView ivDriverPhotoDialog;

        public ViewHolderHistory(View itemView) {
            super(itemView);
            tvHistoryDriverName = itemView.findViewById(R.id.tvHistoryDriverName);
            tvHistoryTotalCost = itemView.findViewById(R.id.tvHistoryTripCost);
            tvHistoryTripTime = itemView.findViewById(R.id.tvHistoryTripTime);
            tvCanceledBy = itemView.findViewById(R.id.tvCanceledBy);
            ivDriverPhotoDialog = itemView.findViewById(R.id.ivDriverPhotoDialog);
            llHistory = itemView.findViewById(R.id.llHistory);
            llHistory.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.llHistory:
                    int position = getAdapterPosition();
                    if (tripHistoryList.get(position).getIsTripCompleted() == Const.TRUE) {
                        goToTripHistoryDetail(tripHistoryList.get(position).getTripId(),
                                tripHistoryList.get(position).getUnit(), tripHistoryList.get(position).getCurrency());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class ViewHolderSeparator extends RecyclerView.ViewHolder {

        MyFontTextView tvDateSeparator;

        public ViewHolderSeparator(View itemView) {
            super(itemView);
            tvDateSeparator = itemView.findViewById(R.id.tvDateSeparator);
        }
    }
}