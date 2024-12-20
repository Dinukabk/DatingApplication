package com.qboxus.binder.GoogleMap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qboxus.binder.SimpleClasses.AppCompatLocaleActivity;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.PermissionUtils;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MapsA extends AppCompatLocaleActivity
        implements OnMapReadyCallback {

    public static boolean saveLocation, saveLocationAddress;

    private static final String TAG = "Current Location";

    GoogleMap mGoogleMap;

    private LatLng mDefaultLocation;
    private static final int DEFAULT_ZOOM = 15;
    String lat,long_;
    PermissionUtils takePermissionUtils;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Location mLastKnownLocation;
    private SupportMapFragment mapFragment;


    private SharedPreferences sPredMap;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;


    ImageView closeCountry;
    TextView currentTextTv;
    RelativeLayout currentAddressDiv, saveLocDiv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.selectedLanguage,Variables.defultLanguage)
                , this, getClass(),false);

        sPredMap = getSharedPreferences(Variables.prefName,MODE_PRIVATE);
        lat = sPredMap.getString(Variables.seletedLat,"");
        long_ = sPredMap.getString(Variables.selectedLon,"");
        takePermissionUtils=new PermissionUtils(MapsA.this, mLocationPermissionResult);

        if(lat.isEmpty()&&long_.isEmpty()){
            lat =  sPredMap.getString(Variables.currentLat,"");
            long_ =sPredMap.getString(Variables.currentLon,"");
        }


        mDefaultLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(long_));
        setContentView(R.layout.activity_main_maps);
        currentTextTv = findViewById(R.id.current_text_tv);
        closeCountry = findViewById(R.id.close_country);
        currentAddressDiv = findViewById(R.id.current_address_div);
        saveLocDiv = findViewById(R.id.save_loc_div);

        saveLocDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLocation = true;
                saveLocationAddress = true;

                Intent data = new Intent();
                data.putExtra("lat", String.valueOf(lat));
                data.putExtra("lng", String.valueOf(long_));
                data.putExtra("location_string", currentTextTv.getText());
                setResult(RESULT_OK, data);
                finish();

            }
        });
        currentAddressDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapsA.this, SearchPlacesA.class);
                searchResultCallback.launch(i);
            }
        });
        closeCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        setupMapIfNeeded();

        configureCameraIdle();

        mapFragment.setRetainInstance(true);

    }

    @Override
    public void onResume(){
        super.onResume();

        setupMapIfNeeded();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        updateLocationUI();
        getDeviceLocation();

        MapStateManager mgr = new MapStateManager(this);
        CameraPosition position = mgr.getSavedCameraPosition();
        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            mGoogleMap.moveCamera(update);

            mGoogleMap.setMapType(mgr.getSavedMapType());
        }

        if (mGoogleMap != null) {
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
            if (ActivityCompat.checkSelfPermission(getApplicationContext()
                    , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext()
                    , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        }
        mGoogleMap.setOnCameraIdleListener(onCameraIdleListener);

    }



    private ActivityResultLauncher<String[]> mLocationPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(MapsA.this,key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(MapsA.this, MapsA.this.getString(R.string.we_need_storage_and_recording_permission_for_voice_message));
                    }
                    else
                    if (allPermissionClear)
                    {
                        updateLocationUI();
                    }

                }
            });

    private void updateLocationUI() {
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (takePermissionUtils.isLocationPermissionGranted()) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                 mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                mLastKnownLocation = null;
                takePermissionUtils.takeLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {

        try {
            if (takePermissionUtils.isLocationPermissionGranted()) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(Double.parseDouble(lat), Double.parseDouble(long_)), DEFAULT_ZOOM));
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

                            SharedPreferences.Editor editor = sPredMap.edit();
                            editor.putString(Variables.currentLat, String.valueOf(lat));
                            editor.putString(Variables.currentLon, String.valueOf(long_));
                            editor.apply();

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                             }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng latLng = mGoogleMap.getCameraPosition().target;
                Geocoder geocoder = new Geocoder(MapsA.this, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        String locality = addressList.get(0).getAddressLine(0);
                        String country = addressList.get(0).getCountryName();
                        if (locality != null && country != null)
                            lat = ""+latLng.latitude;
                        long_ = ""+latLng.longitude;
                        currentTextTv.setText(locality + "  " + country);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private void setupMapIfNeeded(){
        // Build the map.
        if(mGoogleMap==null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        MapStateManager mgr = new MapStateManager(this);
        mgr.saveMapState(mGoogleMap);
    }




    ActivityResultLauncher<Intent> searchResultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String latSearch = data.getStringExtra("lat");
                        String longSearch = data.getStringExtra("lng");
                        lat = latSearch;
                        long_ = longSearch;
                        mDefaultLocation = new LatLng(Double.parseDouble(latSearch), Double.parseDouble(longSearch));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                    }
                }
            });



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
