package com.elluminati.eber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.R;
import com.elluminati.eber.models.datamodels.Language;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by elluminati on 20-Jun-17.
 */

public class SpeakingLanguageAdaptor extends RecyclerView.Adapter<SpeakingLanguageAdaptor.LanguageViewHolder> {
    private final List<Language> languageList;
    private final ArrayList<String> selectedLanguage;

    public SpeakingLanguageAdaptor(Context context, List<Language> languageList, ArrayList<String> selectedLanguage) {
        this.languageList = languageList;
        this.selectedLanguage = selectedLanguage;
    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LanguageViewHolder holder, final int position) {
        final Language language = languageList.get(position);
        holder.cbSelectLanguage.setText(language.getName());
        holder.cbSelectLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                language.setSelected(!language.isSelected());
            }
        });
        holder.cbSelectLanguage.setChecked(selectedLanguage.contains(language.getId()));
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelectLanguage;

        LanguageViewHolder(View itemView) {
            super(itemView);
            cbSelectLanguage = itemView.findViewById(R.id.cbSelectLanguage);
        }

    }
}
