package com.elluminati.eber.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.DocumentActivity;
import com.elluminati.eber.R;
import com.elluminati.eber.components.MyFontTextView;
import com.elluminati.eber.models.datamodels.Document;
import com.elluminati.eber.parse.ParseContent;
import com.elluminati.eber.utils.AppLog;
import com.elluminati.eber.utils.Const;
import com.elluminati.eber.utils.GlideApp;

import java.text.ParseException;
import java.util.ArrayList;

import static com.elluminati.eber.utils.Const.IMAGE_BASE_URL;


/**
 * Created by elluminati on 08-08-2016.
 */
public class DocumentAdaptor extends RecyclerView.Adapter<DocumentAdaptor.DocumentViewHolder> {

    private final DocumentActivity documentActivity;
    private final ArrayList<Document> docList;

    public DocumentAdaptor(DocumentActivity documentActivity, ArrayList<Document> docList) {
        this.docList = docList;
        this.documentActivity = documentActivity;
    }


    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);

        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DocumentViewHolder holder, int position) {

        Document document = docList.get(position);

        GlideApp.with(documentActivity).load(IMAGE_BASE_URL + document.getDocumentPicture()).dontAnimate().fallback(R.drawable.uploading).override(200, 200).placeholder(R.drawable.uploading).into(holder.ivDocumentImage);
        int maxWidth = documentActivity.getResources().getDisplayMetrics().widthPixels - (int) (2 * documentActivity.getResources().getDimensionPixelSize(R.dimen.dimen_bill_line)) - (int) (2 * documentActivity.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin)) - (int) (2 * documentActivity.getResources().getDimensionPixelSize(R.dimen.dimen_bill_margin_three)) - (int) (2 * documentActivity.getResources().getDimensionPixelSize(R.dimen.dimen_bill_margin_two)) - (int) (documentActivity.getResources().getDimensionPixelSize(R.dimen.driver_history_photo_size)) - (int) (2 * documentActivity.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin));
        holder.tvDocumentTittle.setMaxWidth(maxWidth);
        if (document.isIsExpiredDate()) {
            holder.tvExpireDate.setVisibility(View.VISIBLE);
            String date = documentActivity.getResources().getString(R.string.text_expire_date);
            try {
                if (!TextUtils.isEmpty(document.getExpiredDate())) {
                    date = date + " " + ParseContent.getInstance().dateFormat.format(ParseContent.getInstance().webFormatWithLocalTimeZone.parse(document.getExpiredDate()));
                }
            } catch (ParseException e) {
                AppLog.handleException(DocumentActivity.class.getSimpleName(), e);
            }
            holder.tvExpireDate.setText(date);
        } else {
            holder.tvExpireDate.setVisibility(View.GONE);
        }
        if (document.isIsUniqueCode()) {
            holder.tvIdNumber.setVisibility(View.VISIBLE);
            String date = documentActivity.getResources().getString(R.string.text_id_number) + " " + "" + document.getUniqueCode();
            holder.tvIdNumber.setText(date);
        } else {
            holder.tvIdNumber.setVisibility(View.GONE);
        }
        holder.tvDocumentTittle.setText(document.getName());
        if (document.getOption() == Const.TRUE) {
            holder.tvOption.setVisibility(View.VISIBLE);
        } else {
            holder.tvOption.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return docList.size();
    }


    protected class DocumentViewHolder extends RecyclerView.ViewHolder {

        ImageView ivDocumentImage;
        MyFontTextView tvIdNumber, tvExpireDate, tvDocumentTittle, tvOption;
        LinearLayout llDocumentUpload;

        public DocumentViewHolder(View itemView) {
            super(itemView);
            ivDocumentImage = itemView.findViewById(R.id.ivDocumentImage);
            tvDocumentTittle = itemView.findViewById(R.id.tvDocumentTittle);
            llDocumentUpload = itemView.findViewById(R.id.llDocumentUpload);
            tvIdNumber = itemView.findViewById(R.id.tvIdNumber);
            tvExpireDate = itemView.findViewById(R.id.tvExpireDate);
            tvOption = itemView.findViewById(R.id.tvOption);
        }


    }
}
