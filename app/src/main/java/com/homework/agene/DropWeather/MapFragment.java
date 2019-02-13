package com.homework.agene.DropWeather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment {

    public static final int REQUEST_CODE = 123;
    private OnHaveCityForWeatherFragment mListener;
    private GoogleMap mMap;
    MapView mMapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                getMyLocation();
                mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                    @Override
                    public void onMyLocationClick(@NonNull Location location) {
                        getCityFromGeocoder(location);
                    }
                });
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Location location = new Location("click location");
                        location.setLatitude(latLng.latitude);
                        location.setLongitude(latLng.longitude);
                        getCityFromGeocoder(location);
                    }
                });
            }
        });
        return view;
    }

    private void getMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (permissions.length == 1 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMyLocation();
            }
        }
    }

    private void getCityFromGeocoder(Location location) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                String city = addresses.get(0).getLocality();
                if (city != null) {
                    mListener.onCity(city);
                } else {
                    Toast.makeText(getContext(), "City unknown", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                // do your stuff
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHaveCityForWeatherFragment) {
            mListener = (OnHaveCityForWeatherFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHaveCityForWeatherFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnHaveCityForWeatherFragment {
        void onCity(String city);
    }
}


