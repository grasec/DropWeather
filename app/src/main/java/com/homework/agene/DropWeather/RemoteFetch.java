package com.homework.agene.DropWeather;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetch {
    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
    public static final String API_KEY = "dfe51c58a2e4f94ee85f1e5883baeec5";


    public static JSONObject getJSON(Context context, String city) {
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("x-api-key", API_KEY);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder json = new StringBuilder();
            String temp = "";
            while ((temp = reader.readLine()) != null)
                json.append(temp).append("\n");
            reader.close();
            JSONObject data = new JSONObject(json.toString());

            //if we get 404 request has filed
            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
