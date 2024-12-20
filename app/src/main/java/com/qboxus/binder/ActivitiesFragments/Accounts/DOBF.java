package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.qboxus.binder.Models.UserModel;
import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.Functions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class DOBF extends Fragment {

    View view;
    Context context;
    EditText dayEditText1, dayEditText2, monthEditText1, monthEditText2, yearEditText1, yearEditText2, yearEditText3, yearEditText4;
    RelativeLayout continueButton;
    TextView continueTv;
    Date c;
    SimpleDateFormat df;
    String y1,y2,y3,y4;

    String fromWhere;
    UserModel userModel = new UserModel();
    public DOBF() {

    }

    public static DOBF newInstance() {
        DOBF fragment = new DOBF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dob_fragment, container, false);

        context = getActivity();

        c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        if(!formattedDate.equals("")){
            int year = Integer.parseInt(formattedDate) - 18;
            formattedDate = String.valueOf(year);
            y1 = formattedDate.substring(0,1);
            y2 = formattedDate.substring(1,2);
            y3 = formattedDate.substring(2,3);
            y4 = formattedDate.substring(3);
        }

        view.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                        SignupA.pager.getCurrentItem(),
                        SignupA.pager.getOffscreenPageLimit()));
                SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()-1);
            }
        });

        dayEditText1 = view.findViewById(R.id.day_ET1_id);
        dayEditText2 = view.findViewById(R.id.day_ET2_id);
        monthEditText1 = view.findViewById(R.id.month_ET1_id);
        monthEditText2 = view.findViewById(R.id.month_ET2_id);
        yearEditText1 = view.findViewById(R.id.year_ET1_id);
        yearEditText2 = view.findViewById(R.id.year_ET2_id);
        yearEditText3 = view.findViewById(R.id.year_ET3_id);
        yearEditText4 = view.findViewById(R.id.year_ET4_id);

        Bundle bundle = getArguments();
        if(bundle!=null){
            userModel = (UserModel) bundle.getSerializable("user_model");
            fromWhere = bundle.getString("fromWhere");
        }

        continueButton = view.findViewById(R.id.continueButton);
        continueTv = view.findViewById(R.id.continue_tv);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(requireActivity());
                if(checkAdultAge()){
                    SignupA.userModel.date_of_birth = yearEditText1.getText().toString()+
                            yearEditText2.getText().toString()+
                            yearEditText3.getText().toString()+
                            yearEditText4.getText().toString()+"-"+
                            monthEditText1.getText().toString()+
                            monthEditText2.getText().toString()+"-"+
                            dayEditText1.getText().toString()+
                            dayEditText2.getText().toString();
                    SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()+1);
                    SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                            SignupA.pager.getCurrentItem() + 1,
                            SignupA.pager.getOffscreenPageLimit()));
                }
            }
        });


        TextWatcher day1TW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int digitEt1 = Integer.parseInt(s.toString());
                    if (digitEt1 > 3 || digitEt1 < 0) {
                        dayEditText1.setText("");
                        dayEditText1.requestFocus();
                    } else if (s.length() == 1){
                        if(dayEditText2.length() < 1){
                            dayEditText2.requestFocus();
                        }
                    }

                } catch (Exception b) {
                    b.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                ChangeContinueButtonStyle();
            }
        };

        TextWatcher day2TW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int digitEt1 = Integer.parseInt(s.toString());
                    int digitEt2 = Integer.parseInt(dayEditText1.getText().toString());
                    if (s.length() == 1) {
                        if(digitEt2 == 3 && digitEt1 >1){
                            dayEditText2.setText("");
                            dayEditText2.requestFocus();
                        }else {
                            if(monthEditText1.length() < 1){
                                monthEditText1.requestFocus();
                            }
                        }
                    }
                } catch (Exception b) {
                    b.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                ChangeContinueButtonStyle();
            }
        };

        TextWatcher month1TW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int digitEt1 = Integer.parseInt(s.toString());
                    int digitEt2 = Integer.parseInt(dayEditText1.getText().toString()+dayEditText2.getText().toString());

                    if(digitEt2 == 28 && s.toString().equals("1")){
                        monthEditText1.setText("");
                        monthEditText1.requestFocus();

                        if(monthEditText2.getText().length()>0){
                            int digitEt3 = Integer.parseInt(monthEditText2.getText().toString());
                            if(digitEt3 != 2){
                                monthEditText2.setText("");
                            }
                        }

                    }else if(digitEt1 > 1){
                        monthEditText1.setText("");
                        monthEditText1.requestFocus();
                    }else if(s.length() > 0){
                        if (monthEditText2.length() < 1) {
                            monthEditText2.requestFocus();
                        }
                    }
                } catch (Exception b) {
                    b.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                ChangeContinueButtonStyle();
            }
        };

        TextWatcher month2TW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(monthEditText1.length()>0){
                        int digitEt1 = Integer.parseInt(s.toString());
                        int digitEt2 = Integer.parseInt(dayEditText1.getText().toString()+dayEditText2.getText().toString());
                        if(!monthEditText1.getText().toString().equals("0")){
                            if(digitEt1 > 2){
                                monthEditText2.setText("");
                                monthEditText2.requestFocus();
                            }else {
                                if (yearEditText1.length() < 1) {
                                    yearEditText1.requestFocus();
                                }
                            }
                        }else {
                            if(digitEt1 == 0){
                                monthEditText2.setText("");
                                monthEditText2.requestFocus();
                            }else{
                                if (yearEditText1.length() < 1) {
                                    yearEditText1.requestFocus();
                                }
                            }
                        }
                    }else {
                        monthEditText1.requestFocus();
                    }

                } catch (Exception b) {
                    b.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                ChangeContinueButtonStyle();
            }
        };
        TextWatcher year1TW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(s.length() == 1){
                        if(yearEditText2.length()<1){
                            yearEditText2.requestFocus();
                        }
                    }
                } catch (Exception b) {
                    b.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                ChangeContinueButtonStyle();
            }
        };
        TextWatcher year2TW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(s.length() ==1){
                        if(yearEditText3.length()<1){
                            yearEditText3.requestFocus();
                        }
                    }
                } catch (Exception b) {
                    b.printStackTrace();
                } // End Catch Statement

            }

            @Override
            public void afterTextChanged(Editable s) {
                ChangeContinueButtonStyle();
            }
        };
        TextWatcher year3TW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(s.length() == 1){
                        if(yearEditText4.length()<1){
                            yearEditText4.requestFocus();
                        }
                    }

                } catch (Exception b) {
                    b.printStackTrace();
                } // End Catch Statement

            }

            @Override
            public void afterTextChanged(Editable s) {
                ChangeContinueButtonStyle();
            }
        };
        TextWatcher year4TW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ChangeContinueButtonStyle();
            }
        };


        dayEditText1.addTextChangedListener(day1TW);
        dayEditText2.addTextChangedListener(day2TW);
        monthEditText1.addTextChangedListener(month1TW);
        monthEditText2.addTextChangedListener(month2TW);
        yearEditText1.addTextChangedListener(year1TW);
        yearEditText2.addTextChangedListener(year2TW);
        yearEditText3.addTextChangedListener(year3TW);
        yearEditText4.addTextChangedListener(year4TW);

        dayEditText1.setSelectAllOnFocus(true);
        dayEditText2.setSelectAllOnFocus(true);
        monthEditText1.setSelectAllOnFocus(true);
        monthEditText2.setSelectAllOnFocus(true);
        yearEditText1.setSelectAllOnFocus(true);
        yearEditText2.setSelectAllOnFocus(true);
        yearEditText3.setSelectAllOnFocus(true);
        yearEditText4.setSelectAllOnFocus(true);

        delInSoftKey();

        return view;
    }

    private void ChangeContinueButtonStyle() {
        if(checkValidation()){
            if(checkAdultAge()){
                continueButton.setEnabled(true);
                continueButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pink_background));
                continueTv.setTextColor(ContextCompat.getColor(context, R.color.white));
            }else {
                continueButton.setEnabled(false);
            }
        }else {
            continueButton.setEnabled(false);
            continueButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_google_background));
            continueTv.setTextColor(ContextCompat.getColor(context, R.color.gray));
        }
    }

    public void delInSoftKey() {
        dayEditText1.setOnKeyListener((v1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                dayEditText1.requestFocus();
            }
            return false;
        });

        dayEditText2.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                dayEditText1.requestFocus();
            }
            return false;
        });

        monthEditText1.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                dayEditText2.requestFocus();
            }
            return false;
        });

        monthEditText2.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                monthEditText1.requestFocus();
            }
            return false;
        });

        yearEditText1.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                monthEditText2.requestFocus();
            }
            return false;
        });

        yearEditText2.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                yearEditText1.requestFocus();
            }
            return false;
        });

        yearEditText3.setOnKeyListener((v1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                yearEditText2.requestFocus();
            }
            return false;
        });

        yearEditText4.setOnKeyListener((v1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                yearEditText3.requestFocus();
            }
            return false;
        });
    }

    public int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        if(date != null){
            cal.setTime(date);
        }
        return cal;
    }

    boolean checkValidation(){
        if(dayEditText1.length() == 0){
            return false;
        }else if(dayEditText2.length() == 0){
            return false;
        }else if(monthEditText1.length() == 0){
            return false;
        }else if(monthEditText2.length() == 0){
            return false;
        }else if(yearEditText1.length() == 0){
            return false;
        }else if(yearEditText2.length() == 0){
            return false;
        }else if(yearEditText3.length() == 0){
            return false;
        }else if(yearEditText4.length() == 0){
            return false;
        }
        return true;
    }

    public boolean checkAdultAge(){
        String currentDate = yearEditText1.getText().toString()+
                yearEditText2.getText().toString()+
                yearEditText3.getText().toString()+
                yearEditText4.getText().toString();
        SimpleDateFormat df = new SimpleDateFormat("yyy", Locale.getDefault());
        String formattedDate = df.format(c);
        Date dob = null;
        Date currentdate = null;

        try {
            dob = df.parse(formattedDate);
            currentdate = df.parse(currentDate);
        } catch (ParseException e){

        }

        if(currentDate != null && dob != null){
            int value = getDiffYears(currentdate,dob);

            if(value < 18){
                return false;
            }
            return true;
        }else {
            return false;
        }

    }

}