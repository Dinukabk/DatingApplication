package com.qboxus.binder.SimpleClasses;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.qboxus.binder.R;

public class LocationTracker implements LocationListener {

    private LocationManager locationManager;
    private String provider;
    private Location location;
    private Activity activity;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public LocationTracker(Activity activity) {
        this.activity = activity;
        if (isGooglePlayServicesAvailable()) {
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            if (provider != null) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(provider, 1000, 1, this);
                location = locationManager.getLastKnownLocation(provider);
            }
        } else {
            Toast.makeText(activity, activity.getString(R.string.google_play_services_is_not_available), Toast.LENGTH_LONG).show();
        }
    }

    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog((Activity) activity, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e("LocationTracker", "This device is not supported.");
                ((Activity) activity).finish();
            }
            return false;
        }
        return true;
    }

    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public double getLatitude() {
        if (location != null) {
            return location.getLatitude();
        } else {
            return 0.0;
        }
    }

    public double getLongitude() {
        if (location != null) {
            return location.getLongitude();
        } else {
            return 0.0;
        }
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(LocationTracker.this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}


