package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.R;

import static android.content.Context.MODE_PRIVATE;

public class FirstNameF extends Fragment {

    View view;
    Context context;
    EditText firstNameEt;
    ImageButton backButton;
    RelativeLayout continueButton;
    TextView continueTv;
    SharedPreferences sharedPreferences;
    String fromWhere;

    public FirstNameF(String fromWhere) {
        this.fromWhere = fromWhere;
    }

    public FirstNameF() {
    }

    public static FirstNameF newInstance(String fromWhere) {
        FirstNameF fragment = new FirstNameF(fromWhere);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_firstname, container, false);

        context = getActivity();

        backButton = view.findViewById(R.id.Goback);
        if(SignupA.userModel.isFromPh){
            backButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cross_white));
        }else {
            backButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_back_arrow));
        }

        view.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SignupA.userModel.isFromPh){
                    requireActivity().onBackPressed();
                }else {
                    SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                            SignupA.pager.getCurrentItem(),
                            SignupA.pager.getOffscreenPageLimit()));
                    SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()-1);
                }
            }
        });

        sharedPreferences = context.getSharedPreferences(Variables.prefName, MODE_PRIVATE);

        SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                SignupA.pager.getCurrentItem() + 1,
                SignupA.pager.getOffscreenPageLimit()));

        continueButton = view.findViewById(R.id.continueButton);
        continueTv = view.findViewById(R.id.continue_tv);
        firstNameEt = view.findViewById(R.id.firstName);
        firstNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                String txtName = firstNameEt.getText().toString();
                if (txtName.length() > 0 ) {
                    continueButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pink_background));
                    continueTv.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    continueButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_google_background));
                    continueTv.setTextColor(ContextCompat.getColor(context, R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation()){
                    SignupA.userModel.fname = firstNameEt.getText().toString();
                    SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem() + 1);
                    SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                            SignupA.pager.getCurrentItem() + 1,
                            SignupA.pager.getOffscreenPageLimit()));
                }
            }
        });

        return view;
    }


    public boolean checkValidation() {
        String ufName = firstNameEt.getText().toString();
        return !TextUtils.isEmpty(ufName);
    }

}