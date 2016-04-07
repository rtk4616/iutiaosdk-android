package com.iutiao.sdk.views.spinner;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

import com.iutiao.sdk.R;
import com.iutiao.sdk.views.spinner.adapter.CountriesListAdapter;


/**
 * Created by Ben on 16/4/6.
 */
public class IUTCountrySelector extends Spinner {

    private CountriesListAdapter adapter;
    private Context context;

    public IUTCountrySelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setupSpinner();
    }
    private void setupSpinner(){

        String[] recourseList = this.getResources().getStringArray(R.array.CountryCodes);

        adapter = new CountriesListAdapter(context, recourseList);
        setAdapter(adapter);

        int selectedCountry = adapter.getPositionForDeviceCountry();

        if (selectedCountry != -1) {
            setSelection(selectedCountry);
        }
    }
    public String getSelectedCountryCode(){
        String countryInfo= (String) getSelectedItem();
        String[] g = countryInfo.split(",");
        return g[0];
    }
    public String getSelectedCountryName(){
        String countryInfo= (String) getSelectedItem();
        String[] g = countryInfo.split(",");
        return g[1];
    }
}
