package com.elluminati.eber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.R;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.EmergencyContact;
import com.elluminati.eber.utils.Const;

import java.util.ArrayList;

/**
 * Created by elluminati on 17-Nov-16.
 */
public abstract class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ContactViewHolder> {
    private final Context context;
    private final ArrayList<EmergencyContact> contactList;

    public EmergencyContactAdapter(Context context, ArrayList<EmergencyContact> contactList) {
        this.context = context;
        this.contactList = contactList;

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emergency_contact, parent, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {

        holder.tvContactName.setText(contactList.get(position).getName());
        holder.tvContactNumber.setText(contactList.get(position).getPhone());
        holder.switchShareDetails.setChecked(contactList.get(position).getIsAlwaysShareRideDetail() == Const.TRUE);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public abstract void onClickRemove(int position);

    public abstract void onToggleSwitch(int position, boolean isChecked);

    protected class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MyFontTextView tvContactName, tvContactNumber;
        ImageView ivDeleteContact;
        SwitchCompat switchShareDetails;

        public ContactViewHolder(View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactNumber = itemView.findViewById(R.id.tvContactNumber);
            ivDeleteContact = itemView.findViewById(R.id.ivDeleteContact);
            switchShareDetails = itemView.findViewById(R.id.switchShareDetails);
            ivDeleteContact.setOnClickListener(this);
            switchShareDetails.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivDeleteContact:
                    onClickRemove(getAdapterPosition());
                    break;
                case R.id.switchShareDetails:
                    onToggleSwitch(getAdapterPosition(), switchShareDetails.isChecked());
                    break;
            }
        }


    }
}
