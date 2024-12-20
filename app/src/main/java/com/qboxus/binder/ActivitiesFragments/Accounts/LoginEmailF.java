package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.qboxus.binder.ApiClasses.ApiLinks;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.Models.UserMultiplePhotoModel;
import com.qboxus.binder.Models.UserModel;
import com.qboxus.binder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class LoginEmailF extends Fragment implements View.OnClickListener {

    View view;
    EditText email, password;
    TextView forgotPassBtn;
    ImageView closeEyeIv1;
    RelativeLayout loginLayout, passwordRL, continueButton;
    TextView continueTv;
    SharedPreferences sharedPreferences;
    String fromWhere;
    UserModel userModel;

    Boolean check1=false;
    List<UserMultiplePhotoModel> imagesList;

    Date c;
    SimpleDateFormat df;
    int currentYear;

    public LoginEmailF() {
    }

    public LoginEmailF(UserModel userModel, String fromWhere) {
        this.userModel = userModel;
        this.fromWhere = fromWhere;
    }

    public static LoginEmailF newInstance(UserModel userModel, String fromWhere) {
        LoginEmailF fragment = new LoginEmailF(userModel,fromWhere);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_email_reg, container, false);

        sharedPreferences = getActivity().getSharedPreferences(Variables.prefName, MODE_PRIVATE);

        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("yyyy", Locale.getDefault());
        currentYear = Integer.parseInt(df.format(c));

        loginLayout = view.findViewById(R.id.loginLayout);

        email = view.findViewById(R.id.email_edit);
        password = view.findViewById(R.id.password_edt);
        passwordRL = view.findViewById(R.id.passwordRL);

        closeEyeIv1 = view.findViewById(R.id.closeEyeIV1);
        closeEyeIv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check1) {
                    closeEyeIv1.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_open_eye));
                    password.setTransformationMethod(null);
                    password.setSelection(password.length());
                    check1 = true;
                } else {
                    closeEyeIv1.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_close_eye));
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    password.setSelection(password.length());
                    check1 = false;
                }
            }
        });
        forgotPassBtn = view.findViewById(R.id.forgot_pass_btn);

        if(fromWhere != null ) {
            if (fromWhere.equals("login")) {
                forgotPassBtn.setVisibility(View.VISIBLE);
                passwordRL.setVisibility(View.VISIBLE);
                loginLayout.setVisibility(View.VISIBLE);
            }else{
                loginLayout.setVisibility(View.GONE);
                if(userModel != null){
                    email.setText(userModel.email);
                }
            }
        }

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (checkValidation()) {
                    continueButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pink_background));
                    continueTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                } else {
                    continueButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_google_background));
                    continueTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (checkValidation()) {
                    continueButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pink_background));
                    continueTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                } else {
                    continueButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_google_background));
                    continueTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        continueButton = view.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);
        continueTv = view.findViewById(R.id.continue_tv);

        forgotPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ForgotPassA.class));
            }
        });

        return view;
    }


    private void callApiForRegisterUser() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("email", email.getText().toString());
            parameters.put("password",""+password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getActivity(),false,false);
        ApiRequest.callApi(getActivity(), ApiLinks.registerUser, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();
                parseLoginData(resp);
            }
        });
    }


    public void parseLoginData(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONObject jsonArray=jsonObject.getJSONObject("msg");
                JSONObject userdata = jsonArray.getJSONObject("User");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Variables.uid,userdata.optString("id"));
                editor.putString(Variables.fName,userdata.optString("first_name"));
                editor.putString(Variables.lName,userdata.optString("last_name"));
                editor.putString(Variables.uName,userdata.optString("username"));
                editor.putString(Variables.gender,userdata.optString("gender"));

                JSONArray userImagesArray = jsonObject.optJSONObject("msg").optJSONArray("UserImage");

                imagesList = new ArrayList<>();
                for(int i = 0; i<6; i++){
                    UserMultiplePhotoModel model = new UserMultiplePhotoModel();
                    if(i<userImagesArray.length()){
                        model.setImage(userImagesArray.optJSONObject(i).optString("image"));
                        model.setId(userImagesArray.optJSONObject(i).getString("id"));
                        model.setOrderSequence(Integer.parseInt(userImagesArray.optJSONObject(i).getString("order_sequence")));
                        imagesList.add(i, model);
                    }else {
                        model.setOrderSequence(i);
                        imagesList.add(i, model);
                    }
                }

                Collections.sort(imagesList, new Comparator<UserMultiplePhotoModel>() {
                    @Override public int compare(UserMultiplePhotoModel p1, UserMultiplePhotoModel p2) {
                        return p1.getOrderSequence() - p2.getOrderSequence(); // Ascending
                    }
                });

                if(imagesList.size()>0){
                    editor.putString(Variables.uPic, imagesList.get(0).getImage());
                }

                if(!userdata.optString("dob").equals("0000-00-00")){
                    try {
                        Date date = df.parse(userdata.optString("dob"));
                        int age = Integer.parseInt(df.format(date));
                        editor.putString(Variables.birthDay, " " + (currentYear - age));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                if(userdata.optString("hide_me").equals("0")){
                    editor.putBoolean(Variables.showMeOnTinder, true);
                }else {
                    editor.putBoolean(Variables.showMeOnTinder, false);
                }

                if(userdata.optString("hide_age").equals("0")){
                    editor.putBoolean(Variables.hideAge, false);
                }else {
                    editor.putBoolean(Variables.hideAge, true);
                }

                if(userdata.optString("hide_location").equals("0")){
                    editor.putBoolean(Variables.hide_distance, false);
                }else {
                    editor.putBoolean(Variables.hide_distance, true);
                }

                editor.putString(Variables.school, ""+jsonObject.optJSONObject("msg").optJSONObject("School").optString("name"));

                editor.putString(Variables.uTotalBoost,userdata.optString("total_boost"));
                editor.putString(Variables.uBoost,userdata.optString("boost"));
                editor.putString(Variables.uWallet,userdata.optString("wallet"));
                editor.putString(Variables.authToken,userdata.optString("auth_token"));
                editor.putBoolean(Variables.islogin,true);
                editor.commit();

                enableLocation();

            }else if(code.equals("201") && jsonObject.optString("msg").contains("open registration screen")){
                UserModel model = new UserModel();

                model.email = email.getText().toString();
                model.isFromPh = false;
                model.isSocialLogin = false;

                Intent intent = new Intent(view.getContext(), SignupA.class);
                intent.putExtra("user_model", model);
                startActivity(intent);
            }else if(code.equals("201") && jsonObject.optString("msg").contains("Incorrect old password")){
                Toast.makeText(getContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void enableLocation() {
        startActivity(new Intent(getActivity(), EnableLocationA.class));
        requireActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        getActivity().finishAffinity();
    }

    // this will check the validations like none of the field can be the empty
    public boolean checkValidation(){

        final String st_email = email.getText().toString();
        final String st_password = password.getText().toString();

        if(TextUtils.isEmpty(st_email)){
            return false;
        }

        if(!isEmailValid(st_email)){
            return false;
        }

        if(TextUtils.isEmpty(st_password)){
            return false;
        }

        if(password.length() < 6){
            return false;
        }

        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.continueButton:
                if(checkValidation()){
                    callApiForRegisterUser();
                }
                break;
        }
    }


    public boolean isEmailValid(String email) {
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        final Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}