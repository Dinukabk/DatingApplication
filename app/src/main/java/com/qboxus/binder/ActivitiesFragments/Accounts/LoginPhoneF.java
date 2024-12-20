package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.qboxus.binder.ApiClasses.ApiLinks;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.auth.api.credentials.Credential;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.Models.UserModel;
import com.qboxus.binder.R;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import org.json.JSONException;
import org.json.JSONObject;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

import static android.app.Activity.RESULT_OK;


public class LoginPhoneF extends Fragment implements View.OnClickListener {

    public final static int RESOLVE_HINT = 1011;

    View view;
    TextView tv_country_code;
    RelativeLayout main_rlt;
    FrameLayout container;

    RelativeLayout continueButton;
    TextView continueTv;
    String country_dialing_code = "", countryCodeValue, mPhoneNumber, fromWhere;
    EditText phone_edit;
    UserModel userModel;

    String phoneNo;
    CountryCodePicker ccp;

    public LoginPhoneF() {
    }

    public LoginPhoneF(UserModel userModel, String fromWhere) {
        this.userModel = userModel;
        this.fromWhere = fromWhere;
    }

    public static LoginPhoneF newInstance(UserModel userModel, String fromWhere) {
        LoginPhoneF fragment = new LoginPhoneF(userModel,fromWhere);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_phone, container, false);

        initView();
        clickListener();

        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(getActivity().TELEPHONY_SERVICE);
        if (tm != null) {
            countryCodeValue = tm.getNetworkCountryIso().toUpperCase();
            int cc = PhoneNumberUtil.createInstance(getActivity()).getCountryCodeForRegion(countryCodeValue);
            if(cc == 0){
                String cc1 = "+1";
                ccp.setCountryForNameCode("US");
                ccp.setCountryForPhoneCode(Integer.parseInt("+1"));
                tv_country_code.setText("US" + " " + cc1);
                country_dialing_code = cc1;
            }else {
                tv_country_code.setText(countryCodeValue.toUpperCase() + " " + cc);
                country_dialing_code = String.valueOf(cc);
            }
        }

        return view;
    }

    private void initView() {
        tv_country_code = view.findViewById(R.id.country_code);
        container = view.findViewById(R.id.container);
        main_rlt = view.findViewById(R.id.main_rlt);
        phone_edit = view.findViewById(R.id.phone_edit);

        continueButton = view.findViewById(R.id.continueButton);
        continueTv = view.findViewById(R.id.continue_tv);

        phone_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                String txtName = phone_edit.getText().toString();
                if (txtName.length() > 0 ) {
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

        ccp = view.findViewById(R.id.ccp);
        ccp.registerPhoneNumberTextView(phone_edit);
    }


    private void clickListener() {
        tv_country_code.setOnClickListener(this);
        continueButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.country_code:
                Opencountry();
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

                    callApiForOtp();
                }
                break;
        }
    }

    public boolean Check_Validation() {
        final String st_phone = phone_edit.getText().toString();

        if (TextUtils.isEmpty(st_phone)) {
            return false;
        }

        return true;
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
        ApiRequest.callApi(getActivity(), ApiLinks.verifyPhoneNo, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();
                parse_login_data(resp);
            }
        });
    }

    public void parse_login_data(String loginData) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                PhoneOtpF phoneOtp_f =PhoneOtpF.newInstance(true);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left,
                        R.anim.in_from_left, R.anim.out_to_right);
                Bundle bundle = new Bundle();
                String phone_no = phone_edit.getText().toString();
                bundle.putString("phone_number", phoneNo);
                userModel.phone_no = phone_no;
                bundle.putSerializable("user_data", userModel);
                phoneOtp_f.setArguments(bundle);
                transaction.addToBackStack(null);
                transaction.replace(R.id.sign_up_fragment, phoneOtp_f).commit();

            } else {
                Toast.makeText(getActivity(), jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("WrongConstant")
    public void Opencountry() {
        final CountryPicker picker = CountryPicker.newInstance(getString(R.string.select_country));
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {

                // Implement your code here
                ccp.setCountryForNameCode(code);
                ccp.setCountryForPhoneCode(Integer.parseInt(dialCode));

                country_dialing_code = dialCode;
                tv_country_code.setText(code + " " + dialCode);
                picker.dismiss();
            }
        });
        picker.setStyle(R.style.countrypicker_style,R.style.countrypicker_style);
        picker.show(getParentFragmentManager(), "Select Country");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RESOLVE_HINT) {
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            if (credential != null) {
                mPhoneNumber = credential.getId();
                if (mPhoneNumber != null) {
                    mPhoneNumber = mPhoneNumber.replace(country_dialing_code, "");
                    if (phone_edit == null)
                        phone_edit = view.findViewById(R.id.phone_edit);

                    phone_edit.setText(mPhoneNumber);
                }

            } else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }

    }


}