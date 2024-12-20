package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.View;
import com.qboxus.binder.SimpleClasses.AppCompatLocaleActivity;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.LocationTracker;
import com.qboxus.binder.databinding.ActivityEnableLocationBinding;
import com.qboxus.binder.Constants;
import com.qboxus.binder.interfaces.FragmentCallback;
import com.qboxus.binder.SimpleClasses.PermissionUtils;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.MainMenu.MainMenuA;
import com.qboxus.binder.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnableLocationA extends AppCompatLocaleActivity {

    SharedPreferences sharedPreferences;
    PermissionUtils takePermissionUtils;
    ActivityEnableLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.selectedLanguage, Variables.defultLanguage)
                , this, getClass(), false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enable_location);

        sharedPreferences = getSharedPreferences(Variables.prefName, MODE_PRIVATE);

        binding.tabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCurrentLocationUpdates();
            }
        });
    }


    private void checkCurrentLocationUpdates() {
        takePermissionUtils=new PermissionUtils(EnableLocationA.this,mPermissionLocationResult);
        if (takePermissionUtils.isLocationPermissionGranted())
        {
            getLoactionLatlng();
        }
        else
        {
            getLocationPermission();
        }
    }

    private void getLoactionLatlng() {
        LocationTracker locationTracker = new LocationTracker(this);
        if (locationTracker.isGooglePlayServicesAvailable() && locationTracker.isGPSEnabled()) {
            double latitude = locationTracker.getLatitude();
            double longitude = locationTracker.getLongitude();
            locationTracker.stopUsingGPS();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Variables.currentLat, "" + latitude);
            editor.putString(Variables.currentLon, "" + longitude);
            editor.commit();

            startActivity(new Intent(EnableLocationA.this, MainMenuA.class));
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finishAffinity();

        } else {
            Log.d(Constants.tag,"You Have no services");
            // Handle the case where the necessary services are not available
        }
    }



    private ActivityResultLauncher<String[]> mPermissionLocationResult = registerForActivityResult(
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
                            blockPermissionCheck.add(Functions.getPermissionStatus(EnableLocationA.this,key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(EnableLocationA.this,EnableLocationA.this.getString(R.string.we_need_location_permission_to_show_you_nearby_contents));
                    }
                    else
                    if (allPermissionClear)
                    {
                        getLoactionLatlng();
                    }

                }
            });


    private void getLocationPermission() {
        final ShowLocationPermissionF fragment =ShowLocationPermissionF.newInstance(new FragmentCallback() {
            @Override
            public void responce(Bundle bundle) {
                if (bundle.getBoolean("isShow"))
                {
                    takePermissionUtils.showLocationPermissionDailog(getString(R.string.we_need_location_permission_to_show_you_nearby_contents));
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "ShowLocationPermissionF");
    }




    public void goNext(Location location) {
        Functions.cancelLoader();
        if (location != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Variables.currentLat, "" + location.getLatitude());
            editor.putString(Variables.currentLon, "" + location.getLongitude());
            editor.commit();

            startActivity(new Intent(EnableLocationA.this, MainMenuA.class));
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finishAffinity();

        } else {
            // else we will use the basic location
            if (sharedPreferences.getString(Variables.currentLat, "").equals("") || sharedPreferences.getString(Variables.currentLon, "").equals("")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Variables.currentLat, "33.738045");
                editor.putString(Variables.currentLon, "73.084488");
                editor.commit();
            }

            startActivity(new Intent(EnableLocationA.this, MainMenuA.class));
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finishAffinity();

        }
    }


}

