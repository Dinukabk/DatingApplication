package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.qboxus.binder.ApiClasses.ApiLinks;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.credentials.Credential;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Models.UserModel;
import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.Functions;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.ybs.countrypicker.CountryPicker;

import org.json.JSONException;
import org.json.JSONObject;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

import static android.app.Activity.RESULT_OK;


public class PhoneF extends Fragment implements View.OnClickListener{

    public final static int RESOLVE_HINT = 1011;

    View view;
    Context context;
    TextView tvCountryCode;
    RelativeLayout continueButton;
    TextView continueTv;

    String country_dialing_code = "", countryCodeValue, mPhoneNumber, fromWhere;
    EditText phone_edit;
    UserModel userModel;

    public static String phoneNo="";
    CountryCodePicker ccp;


    public PhoneF() {
        // Required empty public constructor
    }

    public static PhoneF newInstance() {
        PhoneF fragment = new PhoneF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_phone, container, false);
        context = getActivity();

        Bundle bundle = getArguments();
        if(bundle!=null){
            userModel = (UserModel) bundle.getSerializable("user_model");
        }

        SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                SignupA.pager.getCurrentItem() + 1,
                SignupA.pager.getOffscreenPageLimit()));

        tvCountryCode = view.findViewById(R.id.country_code);
        tvCountryCode.setOnClickListener(this);

        ccp = view.findViewById(R.id.ccp);

        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(getActivity().TELEPHONY_SERVICE);
        if (tm != null) {
            countryCodeValue = tm.getNetworkCountryIso().toUpperCase();
            int cc = PhoneNumberUtil.createInstance(getActivity()).getCountryCodeForRegion(countryCodeValue);
            if(cc == 0){
                String cc1 = "+1";
                ccp.setCountryForNameCode("US");
                ccp.setCountryForPhoneCode(Integer.parseInt("+1"));
                tvCountryCode.setText("US" + " " + cc1);
                country_dialing_code = cc1;
            }else {
                tvCountryCode.setText(countryCodeValue.toUpperCase() + " " + cc);
                country_dialing_code = String.valueOf(cc);
            }
        }

        view.findViewById(R.id.Goback).setOnClickListener(v -> requireActivity().onBackPressed());

        continueButton = view.findViewById(R.id.continueButton);
        continueTv = view.findViewById(R.id.continue_tv);
        continueButton.setOnClickListener(this);

        phone_edit = view.findViewById(R.id.phone_edit);
        phone_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                String txtName = phone_edit.getText().toString();
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

        ccp.registerPhoneNumberTextView(phone_edit);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.country_code:
                openCountry();
                break;

            case R.id.continueButton:
                if (Check_Validation()) {
                    if (!ccp.isValid()) {
                        phone_edit.setError(getString(R.string.invalid_phone_number));
                        return;
                    }

                    phoneNo = phone_edit.getText().toString();
                    if (phoneNo.charAt(0) == '0') {
                        phoneNo = phoneNo.substring(1);
                    }
                    phoneNo = ccp.getSelectedCountryCodeWithPlus() + phoneNo;
                    phoneNo.replace(" ", "");
                    phoneNo.replace("(", "");
                    phoneNo.replace(")", "");
                    phoneNo.replace("-", "");

                    callApiForOtp();
                }
                break;
        }
    }

    private void callApiForOtp() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("phone", phoneNo);
            parameters.put("verify", "0");
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getActivity(), false, false);
        ApiRequest.callApi(getActivity(), ApiLinks.verifyPhoneNo, parameters, resp -> {
            Functions.cancelLoader();
            parseLoginData(resp);
        });
    }

    public void parseLoginData(String loginData) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                SignupA.userModel.phone_no = phoneNo;
                PhoneOtpF.edit_num.setText(phoneNo);
                SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem() + 1);
                SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                        SignupA.pager.getCurrentItem() + 1,
                        SignupA.pager.getOffscreenPageLimit()));
            } else {
                Toast.makeText(getActivity(), jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean Check_Validation() {
        final String st_phone = phone_edit.getText().toString();
        return !TextUtils.isEmpty(st_phone);
    }

    @SuppressLint("WrongConstant")
    public void openCountry() {
        final CountryPicker picker = CountryPicker.newInstance(getString(R.string.select_country));
        picker.setListener((name, code, dialCode, flagDrawableResID) -> {
            // Implement your code here
            ccp.setCountryForNameCode(code);
            ccp.setCountryForPhoneCode(Integer.parseInt(dialCode));

            country_dialing_code = dialCode;
            tvCountryCode.setText(code + " " + dialCode);
            picker.dismiss();
        });
        picker.setStyle(R.style.countrypicker_style,R.style.countrypicker_style);
        picker.show(getFragmentManager(), "Select Country");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RESOLVE_HINT) {
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            if (credential != null) {
                mPhoneNumber = credential.getId();
                mPhoneNumber = mPhoneNumber.replace(country_dialing_code, "");
                if (phone_edit == null) {
                    phone_edit = view.findViewById(R.id.phone_edit);
                }
                phone_edit.setText(mPhoneNumber);
            } else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.error),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}