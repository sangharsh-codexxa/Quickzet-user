package com.elluminati.eber.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.R;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.Trip;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by elluminati on 05-08-2016.
 */
public abstract class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    
    private final ArrayList<Trip> tripBookingList;
    private final SimpleDateFormat timeFormat_am;
    private final SimpleDateFormat dateFormatMonth;
    private final SimpleDateFormat day;

    public BookingAdapter(ArrayList<Trip> tripBookingList) {

        this.tripBookingList = tripBookingList;
        timeFormat_am = new SimpleDateFormat(Const.TIME_FORMAT_AM, Locale.getDefault());
        dateFormatMonth = new SimpleDateFormat(Const.DATE_FORMAT_MONTH, Locale.getDefault());
        day = new SimpleDateFormat(Const.DAY, Locale.getDefault());
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_trip, parent, false);
        BookingViewHolder bookingViewHolder = new BookingViewHolder(view);
        return bookingViewHolder;
    }

    @Override
    public void onBindViewHolder(BookingViewHolder holder, int position) {

        try {
            Date date = ParseContent.getInstance().webFormat.parse(tripBookingList.get(position).getServerStartTimeForSchedule());
            TimeZone timeZone = TimeZone.getTimeZone(tripBookingList.get(position).getTimezone());
            long dateLong = date.getTime() + timeZone.getOffset(date.getTime());
            date.setTime(dateLong);
            timeFormat_am.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateFormatMonth.setTimeZone(TimeZone.getTimeZone("UTC"));
            day.setTimeZone(TimeZone.getTimeZone("UTC"));
            String daySuffix = Utils.getDayOfMonthSuffix(Integer.valueOf(day.format(date)));
            holder.tvBookingDate.setText(daySuffix + " " + dateFormatMonth.format(date));
            holder.tvBookingTime.setText(timeFormat_am.format(date));
            holder.tvBookingAddress.setText(tripBookingList.get(position).getSourceAddress());
            holder.btnTripCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCancel(position);
                }
            });
            holder.llTripDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelect(position);
                }
            });

        } catch (ParseException e) {
            AppLog.handleException(BookingAdapter.class.getSimpleName(), e);
        }


    }

    public abstract void onCancel(int position);

    public abstract void onSelect(int position);


    @Override
    public int getItemCount() {
        return tripBookingList.size();
    }

    protected class BookingViewHolder extends RecyclerView.ViewHolder {

        MyFontTextView tvBookingDate, tvBookingAddress, tvBookingTime;
        ImageView btnTripCancel;
        LinearLayout llTripDetail;

        public BookingViewHolder(View itemView) {
            super(itemView);

            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvBookingAddress = itemView.findViewById(R.id.tvBookingAddress);
            tvBookingTime = itemView.findViewById(R.id.tvBookingTime);
            btnTripCancel = itemView.findViewById(R.id.btnTripCancel);
            llTripDetail = itemView.findViewById(R.id.llTripDetail);
        }
    }
}
