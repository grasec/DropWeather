package com.homework.agene.DropWeather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherFragment extends Fragment {
    Typeface weatherFont;
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    ConstraintLayout weatherFrgment;

    public static String extraCity;
    Handler handler;

    public WeatherFragment() {
        handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if (extraCity != null) {
                updateWeatherData(extraCity);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = rootView.findViewById(R.id.cityField);
        updatedField = rootView.findViewById(R.id.updatedField);
        detailsField = rootView.findViewById(R.id.detailsField);
        currentTemperatureField = rootView.findViewById(R.id.currentTemperatureField);
        weatherIcon = rootView.findViewById(R.id.weatherIcon);
        weatherFrgment = (ConstraintLayout) rootView;
        weatherIcon.setTypeface(weatherFont);
        rootView.findViewById(R.id.changeCityButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        updateWeatherData(new CityPreference(getActivity()).getCity());
        return rootView;
    }

    private void updateWeatherData(final String city) {
        new Thread() {
            @Override
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
                if (json == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getActivity()
                                            .getString(R.string.place_not_found),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    CityPreference cityPreference = new CityPreference(getActivity());
                    cityPreference.setCity(city);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change city");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setCancelable(false);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity(input.getText().toString());
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });
        builder.show();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void renderWeather(JSONObject json) {
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) + ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(details.getString("description").toUpperCase(Locale.US) +
                    "\n" + "Humidity: " + main.getString("humidity") + "%" + "\n" +
                    "Pressure: " + main.getString("pressure") + "hPa");

            currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp")) + " ℃");
            Double tempNow = main.getDouble("temp");
            setTheme(tempNow);
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            String updatedOn = dateFormat.format(new Date(json.getLong("dt") * 1000));
            updatedField.setText(updatedOn);
            setWeatherIcon(details.getInt("id"), json.getJSONObject("sys").getLong
                    ("sunrise") * 1000, json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setTheme(double currentTemp){
        if (currentTemp <= 0.0){
            weatherFrgment.setBackgroundColor(getContext().getColor(R.color.gradientBelowZero));
        } else if ( currentTemp <= 15.0){
            weatherFrgment.setBackgroundColor(getContext().getColor(R.color.gradientCold));
        } else if (currentTemp <= 22.0){
            weatherFrgment.setBackgroundColor(getContext().getColor(R.color.gradientEasy));
        } else if (currentTemp <= 28.0){
            weatherFrgment.setBackgroundColor(getContext().getColor(R.color.gradientWarm));
        } else {
            weatherFrgment.setBackgroundColor(getContext().getColor(R.color.gradientHot));
        }

    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String city) {
        updateWeatherData(city);
    }
}