package com.homework.agene.DropWeather;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {
    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity(){
        return prefs.getString("city","Tel Aviv, IL");
    }

    void setCity(String city){
        prefs.edit().putString("city",city).commit();
    }
}
