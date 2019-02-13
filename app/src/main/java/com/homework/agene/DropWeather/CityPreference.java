package com.homework.agene.DropWeather;

import android.app.Activity;
import android.content.SharedPreferences;

class CityPreference {
    private SharedPreferences mPrefs;

    CityPreference(Activity activity){
        mPrefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity(){
        return mPrefs.getString("city","Tel Aviv, IL");
    }

    void setCity(String city){
        mPrefs.edit().putString("city",city).apply();
    }
}
