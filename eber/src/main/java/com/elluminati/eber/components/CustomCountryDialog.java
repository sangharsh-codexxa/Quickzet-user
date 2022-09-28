package com.elluminati.eber.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elluminati.eber.R;
import com.elluminati.eber.adapter.CountryAdapter;
import com.elluminati.eber.interfaces.ClickListener;
import com.elluminati.eber.interfaces.RecyclerTouchListener;
import com.elluminati.eber.models.datamodels.Country;

import java.util.ArrayList;

/**
 * Created by elluminati on 04-08-2016.
 */
public abstract class CustomCountryDialog extends Dialog {

    private final RecyclerView rcvCountryCode;
    private final MyFontEdittextView etCountrySearch;
    private final CountryAdapter countryAdapter;
    private final ArrayList<Country> codeArrayList;
    private final Context context;
    private SearchView searchView;

    public CustomCountryDialog(Context context, ArrayList<Country> countryList) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_country_code);
        codeArrayList = countryList;
        rcvCountryCode = findViewById(R.id.rcvCountryCode);
        etCountrySearch = findViewById(R.id.etCountrySearch);
        rcvCountryCode.setLayoutManager(new LinearLayoutManager(context));
        countryAdapter = new CountryAdapter(countryList);
        rcvCountryCode.setAdapter(countryAdapter);
        this.context = context;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setAttributes(params);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        etCountrySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (countryAdapter != null) {
                    countryAdapter.getFilter().filter(s);
                } else {
                    Log.d("filter", "no filter availible");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rcvCountryCode.addOnItemTouchListener(new RecyclerTouchListener(context, rcvCountryCode, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                onSelect(position, countryAdapter.getFilterResult());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public abstract void onSelect(int position, ArrayList<Country> filterList);


}
