package com.elluminati.eber.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.R;
import com.elluminati.eber.models.datamodels.Country;

import java.util.ArrayList;

/**
 * Created by elluminati on 05-08-2016.
 */
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> implements Filterable {

    private ArrayList<Country> countryList;
    private Filter filter;


    public CountryAdapter(ArrayList<Country> countryList) {
        if (countryList == null) {
            countryList = new ArrayList<>();
        }
        this.countryList = countryList;
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country_code, parent, false);
        CountryViewHolder countryViewHolder = new CountryViewHolder(view);

        return countryViewHolder;
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        holder.tvCountryCodeDigit.setText(countryList.get(position).getCountryPhoneCode());
        holder.tvCountryName.setText(countryList.get(position).getCountryName());
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) filter = new AppFilter(countryList);
        return filter;
    }

    public ArrayList<Country> getFilterResult() {
        return countryList;
    }

    protected class CountryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCountryCodeDigit, tvCountryName;
        View viewDive;

        public CountryViewHolder(View itemView) {
            super(itemView);
            tvCountryCodeDigit = itemView.findViewById(R.id.tvCountryCodeDigit);
            tvCountryName = itemView.findViewById(R.id.tvCountryName);
            viewDive = itemView.findViewById(R.id.viewDiveCountry);
        }
    }

    private class AppFilter extends Filter {

        private final ArrayList<Country> sourceObjects;

        public AppFilter(ArrayList<Country> objects) {

            sourceObjects = new ArrayList<Country>();
            synchronized (this) {
                sourceObjects.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            String filterSeq = chars.toString();
            FilterResults result = new FilterResults();
            if (filterSeq != null && filterSeq.length() > 0) {
                ArrayList<Country> filter = new ArrayList<Country>();
                for (Country countryCode : sourceObjects) {
                    // the filtering itself:
                    if (countryCode.getCountryName().toUpperCase().startsWith(filterSeq.toUpperCase())) filter.add(countryCode);
                }
                result.count = filter.size();
                result.values = filter;
            } else {
                // add all objects
                synchronized (this) {
                    result.values = sourceObjects;
                    result.count = sourceObjects.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.

            if (results.count != 0) {
                countryList = (ArrayList<Country>) results.values;
                notifyDataSetChanged();
            }


        }

    }
}
