package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.qboxus.binder.ApiClasses.ApiLinks;
import androidx.core.content.ContextCompat;
import com.chaos.view.PinView;
import com.qboxus.binder.SimpleClasses.AppCompatLocaleActivity;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.Variables;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassA extends AppCompatLocaleActivity implements View.OnClickListener {

    ViewFlipper viewFlipper;
    ImageView go_back, go_back2, go_back3;
    EditText recover_email, ed_new_pass;
    RelativeLayout emailBtn, otpBtn, changePasswordBtn;
    TextView emailBtnText, otpBtnText, changePasswordBtnTxt;
    String otp, user_id, user_email;
    RelativeLayout rl1;
    TextView tv1,edit_num,resend_code;
    PinView et_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.selectedLanguage,Variables.defultLanguage)
                , this, getClass(),false);
        setContentView(R.layout.activity_forgot_pass);

        viewFlipper = findViewById(R.id.viewflliper);
        go_back = findViewById(R.id.Goback);
        go_back2 = findViewById(R.id.Goback2);
        go_back3 = findViewById(R.id.Goback3);
        otpBtnText = findViewById(R.id.otpBtnText);
        otpBtn = findViewById(R.id.otpBtn);
        ed_new_pass = findViewById(R.id.ed_new_pass);
        rl1 = findViewById(R.id.rl1_id);
        tv1 = findViewById(R.id.tv1_id);
        recover_email = findViewById(R.id.recover_email);
        emailBtnText = findViewById(R.id.emailBtnText);
        emailBtn = findViewById(R.id.btn_next);
        changePasswordBtnTxt = findViewById(R.id.changePasswordBtnTxt);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        changePasswordBtn.setOnClickListener(this);
        otpBtn.setOnClickListener(this);
        emailBtn.setOnClickListener(this);
        go_back.setOnClickListener(this);
        go_back2.setOnClickListener(this);
        go_back3.setOnClickListener(this);
        edit_num = findViewById(R.id.edit_email);


        resend_code = findViewById(R.id.resend_code);
        resend_code.setOnClickListener(this);

        et_code = findViewById(R.id.et_code);
        recover_email.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (charSequence.length()>0 && isEmailValid(charSequence.toString())) {
                    emailBtn.setBackground(ContextCompat
                            .getDrawable(getApplicationContext(), R.drawable.ic_pink_background));
                    emailBtnText.setTextColor(ContextCompat
                            .getColor(getApplicationContext(), R.color.white));
                } else {
                    emailBtn.setBackground(ContextCompat
                            .getDrawable(getApplicationContext(), R.drawable.ic_google_background));
                    emailBtnText.setTextColor(ContextCompat
                            .getColor(getApplicationContext(), R.color.gray));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        et_code.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (charSequence.length()>3) {
                    otpBtn.setBackground(ContextCompat
                            .getDrawable(getApplicationContext(), R.drawable.ic_pink_background));
                    otpBtnText.setTextColor(ContextCompat
                            .getColor(getApplicationContext(), R.color.white));
                } else {
                    otpBtn.setBackground(ContextCompat
                            .getDrawable(getApplicationContext(), R.drawable.ic_google_background));
                    otpBtnText.setTextColor(ContextCompat
                            .getColor(getApplicationContext(), R.color.gray));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        ed_new_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (charSequence.length()>5) {
                    changePasswordBtn.setBackground(ContextCompat
                            .getDrawable(getApplicationContext(), R.drawable.ic_pink_background));
                    changePasswordBtnTxt.setTextColor(ContextCompat
                            .getColor(getApplicationContext(), R.color.white));
                } else {
                    changePasswordBtn.setBackground(ContextCompat
                            .getDrawable(getApplicationContext(), R.drawable.ic_google_background));
                    changePasswordBtnTxt.setTextColor(ContextCompat
                            .getColor(getApplicationContext(), R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    public void oneMinuteTimer(){
        rl1.setVisibility(View.VISIBLE);

        new CountDownTimer(60000,1000){
            @Override
            public void onTick(long l) {
                tv1.setText(getApplicationContext().getString(R.string.resend_code)+ l/1000);
            }

            @Override
            public void onFinish() {
                rl1.setVisibility(View.GONE);
            }

        }.start();

    }

    public boolean isEmailValid(String email) {
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        final Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onClick(View v) {
        if (v == go_back) {
           onBackPressed();
        }
        if (v == go_back2) {
            viewFlipper.showPrevious();
        }
        if (v == go_back3) {
            viewFlipper.showPrevious();
        }
        if (v == emailBtn) {
            if (validateEmail()) {
                Functions.showLoader(this, false, false);
                checkEmail(recover_email.getText().toString());
                user_email = recover_email.getText().toString();
            }
        }
        if (v == otpBtn) {
            otp = et_code.getText().toString();
            if (otp != null && otp.length() == 4) {
                Functions.showLoader(this, false, false);
                checkOtp(otp, user_email);
            } else {
                Toast.makeText(ForgotPassA.this, getApplicationContext().getString(R.string.code_is_invalid), Toast.LENGTH_SHORT).show();
            }
        }
        if (v == changePasswordBtn) {
            if (validateNewPassword()) {
                Functions.showLoader(this, false, false);
                changePassword(ed_new_pass.getText().toString(), user_email);
            }
        }

        if(v == resend_code) {
            checkOtp(otp, user_email);
            oneMinuteTimer();
        }
    }

    private void changePassword(String newPass, String email) {
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("password", newPass);
        } catch (JSONException e) {
            Functions.cancelLoader();
            e.printStackTrace();
        }


        ApiRequest.callApi(this, ApiLinks.changePasswordForgot, params, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();
                try {
                    JSONObject response=new JSONObject(resp);
                    String code=response.optString("code");
                    JSONArray msg=response.optJSONArray("msg");
                    if(code.equals("200")) {
                        startActivity(new Intent(ForgotPassA.this, LoginA.class));
                        finish();
                    }else {
                        String msg_txt =  response.getString("msg");
                        Toast.makeText(ForgotPassA.this, msg_txt, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void checkOtp(String otp, String email) {
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("code", otp);
        } catch (JSONException e) {
            Functions.cancelLoader();
            e.printStackTrace();
        }

        ApiRequest.callApi(this, ApiLinks.verifyforgotPasswordCode, params, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();
                try {
                    JSONObject response=new JSONObject(resp);
                    String code=response.optString("code");

                    if(code.equals("200")) {
                        JSONObject json = new JSONObject(response.toString());
                        JSONObject msgObj = json.getJSONObject("msg");
                        JSONObject json1 = new JSONObject(msgObj.toString());
                        JSONObject user_obj = json1.getJSONObject("User");
                        user_id = user_obj.optString("id");
                        viewFlipper.showNext();
                        Functions.cancelLoader();
                    }else {
                        String msg_txt =  response.getString("msg");
                        Toast.makeText(ForgotPassA.this, ""+msg_txt, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void checkEmail(final String email) {
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
        } catch (JSONException e) {
            Functions.cancelLoader();
            e.printStackTrace();
        }

        ApiRequest.callApi(this, ApiLinks.forgotPassword, params, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();

                try {
                    JSONObject response=new JSONObject(resp);
                    String code=response.optString("code");
                    JSONArray msg=response.optJSONArray("msg");
                    if(code.equals("200")) {
                        viewFlipper.showNext();
                        edit_num.setText(getApplicationContext().getString(R.string.your_code_was_emailed_to)+recover_email.getText().toString());
                        oneMinuteTimer();
                    }else {
                        String msg_txt =  response.getString("msg");
                        Toast.makeText(ForgotPassA.this, ""+msg_txt, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public boolean validateEmail() {
        String email = recover_email.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(ForgotPassA.this, getApplicationContext().getString(R.string.login_cancel), Toast.LENGTH_SHORT).show();

            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(ForgotPassA.this, getApplicationContext().getString(R.string.login_cancel), Toast.LENGTH_SHORT).show();

            return false;
        } else {

            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (viewFlipper.getDisplayedChild() == 0) {
            finish();
        } else if (viewFlipper.getDisplayedChild() == 1) {
            viewFlipper.showPrevious();
        } else
            viewFlipper.showPrevious();
    }


    public boolean validateNewPassword() {
        String newpass = ed_new_pass.getText().toString();
        if (newpass.isEmpty()) {
            Toast.makeText(ForgotPassA.this, getApplicationContext().getString(R.string.please_enter_valid_new_password), Toast.LENGTH_SHORT).show();
            return false;
        } if (newpass.length() <= 5 || newpass.length() >= 12) {
            Toast.makeText(ForgotPassA.this, getApplicationContext().getString(R.string.please_enter_valid_password), Toast.LENGTH_SHORT).show();
            return false;
        }   else {

            return true;
        }
    }
}