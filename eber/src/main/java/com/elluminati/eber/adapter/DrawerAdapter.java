package com.elluminati.eber.adapter;

import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.MainDrawerActivity;
import com.elluminati.eber.R;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.utils.PreferenceHelper;


/**
 * Created by elluminati on 06-06-2016.
 */
public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 1;


    private final MainDrawerActivity activity;
    private final TypedArray drawerItemTitle;
    private final TypedArray drawerItemIcon;
    private final PreferenceHelper preferenceHelper;


    public DrawerAdapter(MainDrawerActivity activity) {
        drawerItemTitle = activity.getResources().obtainTypedArray(R.array.drawer_menu_item);
        drawerItemIcon = activity.getResources().obtainTypedArray(R.array.drawer_menu_icons);
        this.activity = activity;
        preferenceHelper = PreferenceHelper.getInstance(activity);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawer, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tvDrawerItemTitle.setText(drawerItemTitle.getString(position));
        viewHolder.ivDrawerItemIcon.setImageResource(drawerItemIcon.getResourceId(position, 0));
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return drawerItemTitle.length();
    }


    /**
     * Holder for item
     */

    protected class ViewHolder extends RecyclerView.ViewHolder {

        MyFontTextView tvDrawerItemTitle;
        ImageView ivDrawerItemIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDrawerItemTitle = itemView.findViewById(R.id.tvDrawerItemTitle);
            ivDrawerItemIcon = itemView.findViewById(R.id.ivDrawerItemIcon);

        }


    }


}
