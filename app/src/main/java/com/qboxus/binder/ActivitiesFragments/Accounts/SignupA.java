




package com.qboxus.binder.ActivitiesFragments.Accounts;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.qboxus.binder.Adapters.ViewPagerAdapter;
import com.qboxus.binder.ApiClasses.ApiLinks;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Constants;
import com.qboxus.binder.Models.UserModel;
import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.AppCompatLocaleActivity;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.interfaces.Callback;

import org.json.JSONException;
import org.json.JSONObject;


public class SignupA extends AppCompatLocaleActivity {


    public static ViewPager2 pager;
    public static ViewPagerAdapter adapter;
    public static ProgressBar progressBar;

    public static UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.selectedLanguage, Variables.defultLanguage)
                , this, getClass(), false);
        setContentView(R.layout.activity_signup);

        try {
            userModel = (UserModel) getIntent().getSerializableExtra("user_model");
        }catch (Exception e){
            userModel=new UserModel();
            Log.d(Constants.tag,"Exception: "+e);
        }



        SetTabs();

        setContentView(R.layout.activity_signup);

        progressBar = findViewById(R.id.pb);
        viewPager = findViewById(R.id.vp);

        setupViewPager();
        updateProgress();
    }


    public void SetTabs() {
        adapter = new ViewPagerAdapter(this);
        pager = findViewById(R.id.vp);

        if(!userModel.isFromPh && userModel.isSocialLogin){
            pager.setOffscreenPageLimit(11);
        }else if(!userModel.isFromPh){
            pager.setOffscreenPageLimit(12);
        }else {
            pager.setOffscreenPageLimit(9);
        }

        registerFragmentWithPager();
        pager.setAdapter(adapter);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
        pager.setUserInputEnabled(false);
    }

    private void registerFragmentWithPager() {
        adapter.addFrag(IntroToRuleF.newInstance());
        if(!userModel.isFromPh){
            adapter.addFrag(PhoneF.newInstance());
            adapter.addFrag(PhoneOtpF.newInstance(false));
        }
        adapter.addFrag(FirstNameF.newInstance(""));
        if(!userModel.isFromPh && !userModel.isSocialLogin){
            adapter.addFrag(PasswordF.newInstance());
        }
        adapter.addFrag(DOBF.newInstance());
        adapter.addFrag(GenderF.newInstance(true));
        adapter.addFrag(SexualOrientationF.newInstance());
        adapter.addFrag(GenderF.newInstance(false));
        adapter.addFrag(MySchoolF.newInstance());
        adapter.addFrag(PassionsF.newInstance());
        adapter.addFrag(AddPhotosF.newInstance());
    }

    @Override
    public void onBackPressed() {
        switch (pager.getCurrentItem()){
            case 0:
                super.onBackPressed();
                break;
            case 1:
                pager.setCurrentItem(0);
                progressBar.setProgress(13);
                break;
            case 2:
                calculateProgress();
                break;
            case 3:
                calculateProgress();
                break;
            case 4:
                calculateProgress();
                break;
            case 5:
                calculateProgress();
                break;
            case 6:
                calculateProgress();
                break;
            case 7:
                calculateProgress();
                break;
            case 8:
                calculateProgress();
                break;
            case 9:
                calculateProgress();
                break;
            case 10:
                calculateProgress();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


    public static void calculateProgress(){
        if(SignupA.userModel.isFromPh){
            SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                    SignupA.pager.getCurrentItem(),
                    8));
        }else {
            SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                    SignupA.pager.getCurrentItem(),
                    11));
        }
        pager.setCurrentItem(pager.getCurrentItem()-1);
    }
    private ViewPager2 viewPager;

    private int currentStep = 0;


    private void setupViewPager() {
        adapter = new ViewPagerAdapter(this);

        // Add fragments to the ViewPager for different signup steps
        adapter.addFrag(new UsernameF());
        adapter.addFrag(new EmailF());
        adapter.addFrag(new PasswordF());

        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false); // Disable manual swiping
    }

    public void onNextStep(View view) {
        if (currentStep < adapter.getItemCount() - 1) {
            currentStep++;
            viewPager.setCurrentItem(currentStep);
            updateProgress();
        } else {
            handleSignUp();
        }
    }

    private void updateProgress() {
        int progress = (currentStep + 1) * (100 / adapter.getItemCount());
        progressBar.setProgress(progress);
    }

    private void handleSignUp() {
        // Retrieve username, email, password, and confirm password from fragments
        String username = ((UsernameF) adapter.getFragment(0)).getUsername();
        String email = ((EmailF) adapter.getFragment(1)).getEmail();
        PasswordF passwordFragment = (PasswordF) adapter.getFragment(2);
        String password = passwordFragment.getPassword();
        String confirmPassword = passwordFragment.getConfirmPassword();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // If all validations pass, register the user
        registerUserToDatabase(username, email, password);

        Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
        finish();

    }


    private void registerUserToDatabase(String username, String email, String password) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("username", username);
            parameters.put("email", email);
            parameters.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Functions.showLoader(this, false, false);

        ApiRequest.callApi(this, ApiLinks.registerUser, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();
                parseRegisterResponse(resp);
            }
        });

    }


    private void parseRegisterResponse(String response) {
        // Handle and parse the server's response here
    }



}