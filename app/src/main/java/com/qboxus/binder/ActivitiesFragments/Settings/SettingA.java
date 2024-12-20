package com.qboxus.binder.ActivitiesFragments.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.qboxus.binder.ApiClasses.ApiLinks;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.appyvet.materialrangebar.RangeBar;
import com.facebook.login.LoginManager;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.qboxus.binder.ActivitiesFragments.Accounts.LoginA;
import com.qboxus.binder.ActivitiesFragments.Accounts.WebviewA;
import com.qboxus.binder.ActivitiesFragments.BlockedUsers.BlockedUserA;
import com.qboxus.binder.ActivitiesFragments.SplashA;
import com.qboxus.binder.SimpleClasses.AppCompatLocaleActivity;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.BuildConfig;
import com.qboxus.binder.Constants;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.GoogleMap.MapsA;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.R;
import com.zhouyou.view.seekbar.SignSeekBar;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */

public class SettingA extends AppCompatLocaleActivity implements View.OnClickListener{

    SwitchCompat currentLocationSwitch, selectedLocationSwitch;
    SwitchCompat menSwitch, womenSwitch, showMeOnBinder, hideAge, hideDistance;

    SignSeekBar distanceBar;
    RangeBar ageSeekBar;

    TextView ageRangeTxt, distanceTxt;
    TextView newLocationTxt, selectedLanguageTxt;
    TextView kmBtn, mileBtn, distanceTypeTxt;

    Context context;

    ExpandableRelativeLayout expandableLayout;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.selectedLanguage,Variables.defultLanguage)
                , this, getClass(),false);
        setContentView(R.layout.activity_setting);

        context = this;

        expandableLayout = findViewById(R.id.expandable_layout);
        findViewById(R.id.one_layout).setOnClickListener(v -> {
            if(expandableLayout.isExpanded()){
                expandableLayout.collapse();
            } else{
                expandableLayout.expand();
            }
        });


        currentLocationSwitch = findViewById(R.id.current_loction_switch);
        selectedLocationSwitch = findViewById(R.id.selected_location_switch);
        if(Functions.getSharedPreference(context).getBoolean(
                Variables.isSeletedLocationSelected,false)){
            selectedLocationSwitch.setChecked(true);
        }else {
            currentLocationSwitch.setChecked(true);
        }

        currentLocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Variables.isReloadUsers =true;
            if(isChecked){
                Functions.getSharedPreference(context).edit()
                        .putBoolean(Variables.isSeletedLocationSelected,false).apply();
                selectedLocationSwitch.setChecked(false);
            } else {
                Functions.getSharedPreference(context).edit()
                        .putBoolean(Variables.isSeletedLocationSelected,true).apply();
                selectedLocationSwitch.setChecked(true);
            }
        });

        selectedLocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Variables.isReloadUsers =true;
            if(isChecked){
                Functions.getSharedPreference(context).edit()
                        .putBoolean(Variables.isSeletedLocationSelected, true).apply();
                currentLocationSwitch.setChecked(false);
            }else {
                Functions.getSharedPreference(context).edit()
                        .putBoolean(Variables.isSeletedLocationSelected, false).apply();
                currentLocationSwitch.setChecked(true);
            }
        });



        newLocationTxt = findViewById(R.id.new_location_txt);
        newLocationTxt.setOnClickListener(this);

        if(!Functions.getSharedPreference(context).getString(Variables.selectedLocationString,"").equals("")){
            newLocationTxt.setText( Functions.getSharedPreference(context).getString(Variables.selectedLocationString,""));
        }else {
            selectedLocationSwitch.setClickable(false);
            currentLocationSwitch.setClickable(false);
        }


        menSwitch = findViewById(R.id.men_switch);
        womenSwitch = findViewById(R.id.women_switch);

        if(Functions.getSharedPreference(context).getString(Variables.showMe,"all").equals("all")){
            menSwitch.setChecked(true);
            womenSwitch.setChecked(true);
        }
        else if(Functions.getSharedPreference(context).getString(Variables.showMe,"all").equals("male")){
            menSwitch.setChecked(true);
        }
        else {
            womenSwitch.setChecked(true);
        }

        menSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Variables.isReloadUsers = true;
            if(!isChecked && !womenSwitch.isChecked()){
                womenSwitch.setChecked(true);
            }else {
                setShowMeData();
            }
            callApiEditProfile();
        });


        womenSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Variables.isReloadUsers =true;
            if(!isChecked && !menSwitch.isChecked()){
                menSwitch.setChecked(true);
            }else {
                setShowMeData();
            }
            callApiEditProfile();
        });


        distanceBar = findViewById(R.id.distance_bar);
        distanceTxt = findViewById(R.id.distance_txt);
        distanceBar.setProgress( Functions.getSharedPreference(context)
                .getInt(Variables.maxDistance, Constants.DEFAULT_DISTANCE));
        distanceTxt.setText(Functions.getSharedPreference(context).getInt(Variables.maxDistance,
                Constants.DEFAULT_DISTANCE)+" "+context.getResources().getString(R.string.miles));

        distanceBar.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat,boolean fromUser) {
                distanceTxt.setText(progress+" "+context.getResources().getString(R.string.miles));
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
                Variables.isReloadUsers =true;
                Functions.getSharedPreference(context).edit()
                        .putInt(Variables.maxDistance,progress).apply();
            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat,boolean fromUser) {
                callApiEditProfile();
            }
        });

        kmBtn = findViewById(R.id.km_btn);
        kmBtn.setOnClickListener(this);

        mileBtn = findViewById(R.id.mile_btn);
        mileBtn.setOnClickListener(this);

        distanceTypeTxt = findViewById(R.id.distance_type_txt);


        // this is the age seek bar. when we change the progress of seek bar it will be locally save
        ageSeekBar = findViewById(R.id.age_seekbar);
        ageRangeTxt = findViewById(R.id.age_range_txt);
        ageRangeTxt.setText( Functions.getSharedPreference(context).getInt(Variables.minAge, Constants.MIN_DEFAULT_AGE)+" - "+
                Functions.getSharedPreference(context).getInt(Variables.maxAge, Constants.MAX_DEFAULT_AGE)+" "+context.getResources().getString(R.string.years));
        ageSeekBar.setRangePinsByValue( Functions.getSharedPreference(context).getInt(Variables.minAge, Constants.MIN_DEFAULT_AGE),
                Functions.getSharedPreference(context).getInt(Variables.maxAge, Constants.MAX_DEFAULT_AGE));
        ageSeekBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                Variables.isReloadUsers =true;

                SharedPreferences.Editor editor=  Functions.getSharedPreference(context).edit();
                editor.putInt(Variables.minAge,Integer.parseInt(leftPinValue));
                editor.putInt(Variables.maxAge,Integer.parseInt(rightPinValue));
                editor.apply();

                ageRangeTxt.setText(leftPinValue+" - "+rightPinValue+" "+context.getResources().getString(R.string.years));

            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {
                callApiEditProfile();
            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {

            }
        });


        showMeOnBinder = findViewById(R.id.show_me_on_binder);
        showMeOnBinder.setChecked(Functions.getSharedPreference(context).getBoolean(Variables.showMeOnTinder,true));
        showMeOnBinder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Functions.getSharedPreference(context).edit()
                    .putBoolean(Variables.showMeOnTinder, isChecked).apply();
            Variables.isReloadUsers =true;
            callApiEditProfile();
        });


        hideAge = findViewById(R.id.hide_age);
        hideAge.setChecked( Functions.getSharedPreference(context).getBoolean(Variables.hideAge,false));
        hideAge.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Functions.getSharedPreference(context).edit()
                    .putBoolean(Variables.hideAge,isChecked).apply();
            callApiEditProfile();
        });

        hideDistance = findViewById(R.id.hide_distance);
        hideDistance.setChecked( Functions.getSharedPreference(context).getBoolean(Variables.hide_distance,false));
        hideDistance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Functions.getSharedPreference(context).edit()
                    .putBoolean(Variables.hide_distance,isChecked).apply();
            callApiEditProfile();
        });


        selectedLanguageTxt = findViewById(R.id.selected_language_txt);

        List<String> languageNames= Arrays.asList(getResources().getStringArray(R.array.language_names_for_show));
        List <String> languageCode= Arrays.asList(getResources().getStringArray(R.array.language_code));

        String language= Locale.getDefault().getLanguage();
        if( Functions.getSharedPreference(context).getString(Variables.selectedLanguage,null)!=null) {
            language =  Functions.getSharedPreference(context).getString(Variables.selectedLanguage, context.getResources().getString(R.string.english));
        }
        if(languageCode.contains(language)){
            selectedLanguageTxt.setText(languageNames.get(languageCode.indexOf(language)));
        }
        else {
            selectedLanguageTxt.setText("english");
        }


        TextView version_txt = findViewById(R.id.version_txt);
        version_txt.setText(getString(R.string.version)+" ("+ BuildConfig.VERSION_NAME+")");


        if( Functions.getSharedPreference(context).getString(Variables.distanceType,"mi").equals(context.getString(R.string.mi)))
            mileBtn.performClick();
        else
            kmBtn.performClick();


        findViewById(R.id.help_support_layout).setOnClickListener(this);

        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.langage_layout).setOnClickListener(this);

        findViewById(R.id.licences_txt).setOnClickListener(this);
        findViewById(R.id.privacy_policy_txt).setOnClickListener(this);
        findViewById(R.id.terms_condition_txt).setOnClickListener(this);



        findViewById(R.id.blockedList_btn).setOnClickListener(this);
        findViewById(R.id.logout_btn).setOnClickListener(this);
        findViewById(R.id.delete_account_btn).setOnClickListener(this);

    }


    public void openLanguageDialog(){
        List<String> languageNames = Arrays.asList(getResources()
                .getStringArray(R.array.language_names_for_show));
        List<String> languageCode = Arrays.asList(getResources()
                .getStringArray(R.array.language_code));

        final CharSequence[] options = getResources()
                .getStringArray(R.array.language_names_for_show);
        Functions.showOptions(context, options, resp -> {
            Functions.getSharedPreference(context).edit().putString(Variables.selectedLanguage,
                    languageCode.get(languageNames.indexOf(resp))).apply();

            Functions.setLocale(Functions.getSharedPreference(SettingA.this).getString(Variables.selectedLanguage,Variables.defultLanguage)
                    , this, SplashA.class,true);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;

            case R.id.km_btn:
                distanceTypeTxt.setText(context.getString(R.string.km));
                kmBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_rounded_background));
                mileBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.d_round_bottom));

                kmBtn.setTextColor(context.getResources().getColor(R.color.white));
                mileBtn.setTextColor(context.getResources().getColor(R.color.black));
                Variables.isReloadUsers =true;
                callApiEditProfile();
                Functions.getSharedPreference(context).edit()
                        .putString(Variables.distanceType,context.getString(R.string.km)).apply();
                break;

            case R.id.mile_btn:
                distanceTypeTxt.setText(context.getString(R.string.mi));
                kmBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.d_round_bottom));
                mileBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_rounded_background));

                kmBtn.setTextColor(context.getResources().getColor(R.color.black));
                mileBtn.setTextColor(context.getResources().getColor(R.color.white));
                Variables.isReloadUsers =true;
                callApiEditProfile();
                Functions.getSharedPreference(context).edit().putString(Variables.distanceType,context.getString(R.string.mi)).apply();
                break;

            case R.id.new_location_txt:
                openLocationPicker();
                break;

            case R.id.langage_layout:
                openLanguageDialog();
                break;

            case R.id.licences_txt:
            {
                openWebUrl(context.getString(R.string.licenses),Constants.LICENCE_URL);
            }
                break;
            case R.id.privacy_policy_txt:
            {
                openWebUrl(context.getString(R.string.privacy_policy),Constants.PRIVACY_POLICY_URL);
            }
                break;
            case R.id.terms_condition_txt:
            {
                openWebUrl(context.getString(R.string.terms_of_services),Constants.TERMS_OF_SERVICE_URL);
            }
                break;

            case R.id.help_support_layout:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + Constants.CONTACT_US_EMAIL));
                startActivity(Intent.createChooser(emailIntent, "Send feedback"));
                break;

            case R.id.delete_account_btn:
                showDeleteAccountAlert();
                break;

            case R.id.blockedList_btn:
                openBlockedUserList();
                break;

            case R.id.logout_btn:
                logoutUser();
                break;
        }
    }

    public void openWebUrl(String title, String url) {
        Intent intent=new Intent(context, WebviewA.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    // below two methods are related with "save the value in database" that he is want to show
    // as a public or keep it private
    public void setShowMeData(){
        if(menSwitch.isChecked() && womenSwitch.isChecked()){
             Functions.getSharedPreference(context).edit()
                     .putString(Variables.showMe,"all").apply();
        }else if(!menSwitch.isChecked() && womenSwitch.isChecked()){
             Functions.getSharedPreference(context).edit()
                     .putString(Variables.showMe,"female").apply();
        }else if(menSwitch.isChecked() && !womenSwitch.isChecked()){
             Functions.getSharedPreference(context).edit()
                     .putString(Variables.showMe,"male").apply();
        }
    }


    private void callApiEditProfile() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions
                    .getSharedPreference(context).getString(Variables.uid,""));

            if(showMeOnBinder.isChecked())
                parameters.put("hide_me","0");
            else
                parameters.put("hide_me","1");

            if(hideAge.isChecked())
                parameters.put("hide_age","1");
            else
                parameters.put("hide_age","0");

            if(hideDistance.isChecked())
                parameters.put("hide_location","1");
            else
                parameters.put("hide_location","0");


            parameters.put("show_me_distance_in",  Functions.getSharedPreference(context)
                    .getString(Variables.distanceType,"mi"));
            parameters.put("show_me_gender",  Functions.getSharedPreference(context)
                    .getString(Variables.showMe,"all"));
            parameters.put("min_age", ""+  Functions.getSharedPreference(context)
                    .getInt(Variables.minAge, Constants.MIN_DEFAULT_AGE));
            parameters.put("max_age", ""+  Functions.getSharedPreference(context)
                    .getInt(Variables.maxAge, Constants.MAX_DEFAULT_AGE));
            parameters.put("radius", ""+  Functions.getSharedPreference(context)
                    .getInt(Variables.maxDistance, Constants.DEFAULT_DISTANCE));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(context, ApiLinks.editProfile, parameters,null);

    }


    public void showDeleteAccountAlert(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context, R.style.DialogStyle);
        alert.setTitle(context.getResources().getString(R.string.are_you_sure))
                .setMessage(context.getResources().getString(R.string.delete_account_description))
                .setPositiveButton(context.getResources().getString(R.string.yes), (dialog, which) -> callApiToDeleteAccount())
                .setNegativeButton(context.getResources().getString(R.string.no), (dialog, id) -> dialog.dismiss());

        alert.setCancelable(true);
        alert.show();
    }


    // below two method is used get the user pictures and about text from our server
    private void callApiToDeleteAccount() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context)
                    .getString(Variables.uid,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(context,false,false);
        ApiRequest.callApi(context, ApiLinks.deleteUserAccount, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    String code=jsonObject.optString("code");
                    if(code.equals("200")){
                        logoutUser();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void openBlockedUserList(){
        startActivity(new Intent(this, BlockedUserA.class));
    }

    public void logoutUser(){
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(SettingA.this, gso);
        googleSignInClient.signOut();

        LoginManager.getInstance().logOut();
        SharedPreferences.Editor editor=  Functions.getSharedPreference(context).edit();

        editor.putBoolean(Variables.islogin, false);
        editor.putString(Variables.fName,"");
        editor.putString(Variables.lName,"");
        editor.putString(Variables.birthDay,"");
        editor.putString(Variables.uPic,"");
        editor.putString(Variables.uid,"");
        editor.putString(Variables.authToken, null);
        editor.putBoolean(Variables.isProductPurchase, Constants.enableSubscribe);

        editor.apply();
        startActivity(new Intent(SettingA.this, LoginA.class));
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finishAffinity();
    }

    public void openLocationPicker(){
        locationResultCallback.launch(new Intent(SettingA.this, MapsA.class));
    }

    ActivityResultLauncher<Intent> locationResultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String latSearch = data.getStringExtra("lat");
                        String longSearch = data.getStringExtra("lng");

                        String locationString = data.getStringExtra("location_string");

                        newLocationTxt.setText(locationString);
                        selectedLocationSwitch.setClickable(true);
                        currentLocationSwitch.setClickable(true);

                        Functions.getSharedPreference(context).edit()
                                .putString(Variables.seletedLat,latSearch).apply();
                        Functions.getSharedPreference(context).edit()
                                .putString(Variables.selectedLon,longSearch).apply();
                        Functions.getSharedPreference(context).edit()
                                .putString(Variables.selectedLocationString,locationString).apply();
                    }
                }
            });

    @Override
    public void onBackPressed() {
        //callApiEditProfile();
        super.onBackPressed();
    }
}
