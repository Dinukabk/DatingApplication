package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.Functions;

public class GenderF extends Fragment implements View.OnClickListener {

    Context context;
    TextView womenText,manText,everyoneText,showMeText;
    ImageView checkbox;
    LinearLayout check;
    Boolean isCheck = true;
    Boolean isFromDob = false;

    RelativeLayout continueButton;
    TextView continueTv;

    View view;

    public GenderF() {
        // Required empty public constructor
    }

    public GenderF(Boolean isFromDob) {
        this.isFromDob = isFromDob;
    }

    public static GenderF newInstance(Boolean isFromDob) {
        GenderF fragment = new GenderF(isFromDob);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gender, container, false);

        context = getActivity();

        view.findViewById(R.id.Goback).setOnClickListener(this);

        showMeText = view.findViewById(R.id.tv_showMe);
        womenText = view.findViewById(R.id.tv_woman);
        womenText.setOnClickListener(this);
        manText = view.findViewById(R.id.tv_man);
        manText.setOnClickListener(this);
        everyoneText = view.findViewById(R.id.tv_everyone);
        everyoneText.setOnClickListener(this);

        if(isFromDob){
            showMeText.setText(context.getString(R.string.i_am_a));
            everyoneText.setVisibility(View.GONE);
        }else {
            showMeText.setText(context.getString(R.string.show_me));
            everyoneText.setVisibility(View.VISIBLE);
        }

        checkbox = view.findViewById(R.id.checkbox);
        check = view.findViewById(R.id.checkLayout);
        check.setOnClickListener(this);
        if(isFromDob){
            check.setVisibility(View.VISIBLE);
        }else {
            check.setVisibility(View.GONE);
        }

        continueButton = view.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);
        continueTv = view.findViewById(R.id.continue_tv);

        return  view;
    }


    void changeTextStyle(TextView selectedTextView, TextView tv1, TextView tv2){
        selectedTextView.setTextColor(ContextCompat.getColor(context,R.color.pink_color));
        tv1.setTextColor(ContextCompat.getColor(context,R.color.dark_gray));
        tv2.setTextColor(ContextCompat.getColor(context,R.color.dark_gray));
        selectedTextView.setBackground(ContextCompat.getDrawable(context, R.drawable.d_max_round_pink_border));
        tv1.setBackground(ContextCompat.getDrawable(context, R.drawable.d_max_round_gray_border));
        tv2.setBackground(ContextCompat.getDrawable(context, R.drawable.d_max_round_gray_border));
        continueButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pink_background));
        continueTv.setTextColor(ContextCompat.getColor(context, R.color.white));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Goback:
                SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                        SignupA.pager.getCurrentItem(),
                        SignupA.pager.getOffscreenPageLimit()));
                SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()-1);
                break;
            case R.id.tv_woman:
                if(isFromDob){
                    SignupA.userModel.gender = "Female";
                }else {
                    SignupA.userModel.show_me_gender = "Female";
                }
                changeTextStyle(womenText,manText,everyoneText);
                break;
            case R.id.tv_man:
                if(isFromDob){
                    SignupA.userModel.gender = "Male";
                }else {
                    SignupA.userModel.show_me_gender = "Male";
                }
                changeTextStyle(manText,everyoneText,womenText);
                break;
            case R.id.tv_everyone:
                SignupA.userModel.show_me_gender = "all";
                changeTextStyle(everyoneText,manText,womenText);
                break;
            case R.id.checkLayout:
                if(isCheck){
                    SignupA.userModel.show_gender = "1";
                    checkbox.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_fill));
                    isCheck = false;
                }else {
                    SignupA.userModel.show_gender = "0";
                    checkbox.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_empty));
                    isCheck = true;
                }
                break;
            case R.id.continueButton:
                if(isFromDob && !SignupA.userModel.gender.equals("")){
                    SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()+1);
                    SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                            SignupA.pager.getCurrentItem() + 1,
                            SignupA.pager.getOffscreenPageLimit()));

                }else if(!SignupA.userModel.show_me_gender.equals("")){
                    SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()+1);
                    SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                            SignupA.pager.getCurrentItem() + 1,
                            SignupA.pager.getOffscreenPageLimit()));
                }
                break;
        }
    }
}