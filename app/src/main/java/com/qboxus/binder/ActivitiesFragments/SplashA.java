package com.qboxus.binder.ActivitiesFragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.qboxus.binder.ActivitiesFragments.Accounts.EnableLocationA;
import com.qboxus.binder.ActivitiesFragments.Accounts.LoginA;
import com.qboxus.binder.SimpleClasses.AppCompatLocaleActivity;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.databinding.ActivitySplashBinding;
import com.qboxus.binder.SimpleClasses.PermissionUtils;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.R;
import com.qboxus.binder.MainMenu.MainMenuA;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SplashA extends AppCompatLocaleActivity {

    ActivitySplashBinding binding;
    PermissionUtils takePermissionUtils;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.selectedLanguage,Variables.defultLanguage)
                , this, getClass(),false);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_splash);

        sharedPreferences=getSharedPreferences(Variables.prefName,MODE_PRIVATE);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sharedPreferences.getBoolean(Variables.islogin, false)) {
                    // if user is already login then we get the current location of user
                    if(getIntent().hasExtra("action_type")){
                        Intent intent= new Intent(SplashA.this, MainMenuA.class);
                        String action_type=getIntent().getExtras().getString("action_type");
                        String receiverId=getIntent().getExtras().getString("senderid");
                        String title=getIntent().getExtras().getString("title");
                        String icon=getIntent().getExtras().getString("icon");

                        intent.putExtra("icon", icon);
                        intent.putExtra("action_type", action_type);
                        intent.putExtra("receiverid", receiverId);
                        intent.putExtra("title", title);

                        startActivity(intent);
                        finish();
                    } else {
                        takePermissionUtils = new PermissionUtils(SplashA.this, mPermissionLocationResult);
                        if (takePermissionUtils.isLocationPermissionGranted()) {

                            getGPSLocationData();
                        } else {
                            enableLocationByActivity();
                        }
                    }

                } else {
                    // else we will move the user to login screen
                    startActivity(new Intent(SplashA.this, LoginA.class));
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    finish();
                }
            }
        },1500);

        getScreenSize();

    }


    private ActivityResultLauncher<String[]> mPermissionLocationResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear = true;
                    List<String> blockPermissionCheck = new ArrayList<>();
                    for (String key : result.keySet()) {
                        if (!(result.get(key))) {
                            allPermissionClear = false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(SplashA.this, key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked")) {
                        Functions.showPermissionSetting(binding.getRoot().getContext(), getString(R.string.we_need_location_permission_to_give_you_better_app_experience));
                    } else if (allPermissionClear) {
                        getGPSLocationData();
                    }

                }
            });


    private void getGPSLocationData() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(binding.getRoot().getContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    try {
                        Functions.printLog("Lat "+location.getLatitude());
                        Functions.printLog("Lng "+location.getLongitude());
                        goNext(location);
                    } catch (Exception e) {
                        Functions.printLog( "Exception : " + e);
                    }
                }
                else
                {
                    enableLocationByActivity();
                }
            }
        });
    }

    private void enableLocationByActivity() {
        startActivity(new Intent(this, EnableLocationA.class));
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();
    }





    public void getScreenSize(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Variables.screenHeight = displayMetrics.heightPixels;
        Variables.screenWidth = displayMetrics.widthPixels;
    }


    public void goNext(Location location){
        if (location != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Variables.currentLat, "" + location.getLatitude());
            editor.putString(Variables.currentLon, "" + location.getLongitude());
            editor.commit();
            startActivity(new Intent(SplashA.this, MainMenuA.class));
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finish();
        } else {
            // else we will use the basic location
            if (sharedPreferences.getString(Variables.currentLat, "").equals("") || sharedPreferences.getString(Variables.currentLon, "").equals("")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Variables.currentLat, Variables.defaultLat);
                editor.putString(Variables.currentLon, Variables.defaultLon);
                editor.commit();
            }
            startActivity(new Intent(SplashA.this, MainMenuA.class));
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finish();
        }
    }


}
