package com.example.jwtasks2.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.example.jwtasks2.R;
import com.example.jwtasks2.model.NoteDTO;

import java.util.Comparator;

import static com.example.jwtasks2.services.Constants.DATE_COMPARATOR_CODE;

public class ResourcesAndSettings{
    private static final String CODE_CURRENT_COMPARATOR_FOR_PREFERENCES = "CODE_CURRENT_COMPARATOR_FOR_PREFERENCES";

    private Context context;
    private SharedPreferences containerOfSettings;
    private String[] defaultTypes;
    private String[] defaultTypesDefLang;

    public ResourcesAndSettings(Context context) {
        this.context = context;
        containerOfSettings = PreferenceManager.getDefaultSharedPreferences(context);
        Resources resources = context.getResources();
        defaultTypes = resources.getStringArray(R.array.default_types);
        defaultTypesDefLang = resources.getStringArray(R.array.default_types_def_lang);
    }

    public Comparator<NoteDTO> readComparator(){
        return  Comparators.getComparatorOnId(containerOfSettings.getInt(CODE_CURRENT_COMPARATOR_FOR_PREFERENCES, DATE_COMPARATOR_CODE));
    }

    public void writeComparator(int idOfComparator){
        containerOfSettings.edit().putInt(CODE_CURRENT_COMPARATOR_FOR_PREFERENCES, idOfComparator).apply();
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public SharedPreferences getContainerOfSettings() {
        return containerOfSettings;
    }

    public void setContainerOfSettings(SharedPreferences containerOfSettings) {
        this.containerOfSettings = containerOfSettings;
    }

    public String[] getDefaultTypes() {
        return defaultTypes;
    }

    public void setDefaultTypes(String[] defaultTypes) {
        this.defaultTypes = defaultTypes;
    }

    public String[] getDefaultTypesDefLang() {
        return defaultTypesDefLang;
    }

    public void setDefaultTypesDefLang(String[] defaultTypesDefLang) {
        this.defaultTypesDefLang = defaultTypesDefLang;
    }
}
