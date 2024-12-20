package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.Functions;

public class PasswordF extends Fragment {

    Context context;
    EditText password, confirmPassword;
    RelativeLayout continueButton;
    TextView continueTv;

    ImageView closeEyeIv1, closeEyeIv2;
    Boolean check1=false, check2=false;

    View view;

    public PasswordF() {
        // Required empty public constructor
    }

    public static PasswordF newInstance() {
        PasswordF fragment = new PasswordF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_password, container, false);

        context = getActivity();

        view.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                        SignupA.pager.getCurrentItem() + 1,
                        SignupA.pager.getOffscreenPageLimit()));
                SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()-1);
            }
        });

        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                changeContinueButtonStyle();
            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                changeContinueButtonStyle();
            }
        });

        closeEyeIv1 = view.findViewById(R.id.closeEyeIV1);
        closeEyeIv2 = view.findViewById(R.id.openEyeIV2);

        closeEyeIv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check1) {
                    closeEyeIv1.setImageDrawable(ContextCompat.getDrawable(v.getContext(),
                            R.drawable.ic_open_eye));
                    password.setTransformationMethod(null);
                    password.setSelection(password.length());
                    check1 = true;
                } else {
                    closeEyeIv1.setImageDrawable(ContextCompat.getDrawable(v.getContext(),
                            R.drawable.ic_close_eye));
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    password.setSelection(password.length());
                    check1 = false;
                }
            }
        });

        closeEyeIv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check2) {
                    closeEyeIv2.setImageDrawable(ContextCompat.getDrawable(v.getContext(),
                            R.drawable.ic_open_eye));
                    confirmPassword.setTransformationMethod(null);
                    confirmPassword.setSelection(confirmPassword.length());
                    check2 = true;
                } else {
                    closeEyeIv2.setImageDrawable(ContextCompat.getDrawable(v.getContext(),
                            R.drawable.ic_close_eye));
                    confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    confirmPassword.setSelection(confirmPassword.length());
                    check2 = false;
                }
            }
        });

        continueButton = view.findViewById(R.id.continueButton);
        continueTv = view.findViewById(R.id.continue_tv);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSignUpVerification()){
                    SignupA.userModel.password = password.getText().toString();
                    continueButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pink_background));
                    continueTv.setTextColor(ContextCompat.getColor(context, R.color.white));
                    SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()+1);
                    SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                            SignupA.pager.getCurrentItem() + 1,
                            SignupA.pager.getOffscreenPageLimit()));
                }
            }
        });

        return view;
    }

    private boolean checkSignUpVerification() {
        final String pass = password.getText().toString();
        final String confPassword = confirmPassword.getText().toString();

        if(TextUtils.isEmpty(pass) || pass.length() < 6){
            return false;
        }

        if(TextUtils.isEmpty(confPassword) || confPassword.length() < 6){
            return false;
        }

        if(!pass.equals(confPassword)){
            return false;
        }

        return true;
    }

    private void changeContinueButtonStyle(){
        if(checkSignUpVerification()){
            continueButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pink_background));
            continueTv.setTextColor(ContextCompat.getColor(context, R.color.white));
        }else{
            continueButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_google_background));
            continueTv.setTextColor(ContextCompat.getColor(context, R.color.gray));
        }
    }

    public String getPassword() {
        return password.getText().toString().trim();
    }
    public String getConfirmPassword() {
        return confirmPassword.getText().toString().trim();
    }
}