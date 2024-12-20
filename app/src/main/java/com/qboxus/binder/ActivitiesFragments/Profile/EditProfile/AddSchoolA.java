package com.qboxus.binder.ActivitiesFragments.Profile.EditProfile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.qboxus.binder.ApiClasses.ApiLinks;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.qboxus.binder.Adapters.MySchoolAdapter;
import com.qboxus.binder.SimpleClasses.AppCompatLocaleActivity;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.interfaces.AdapterClickListener;
import com.qboxus.binder.Models.MySchoolModel;
import com.qboxus.binder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddSchoolA extends AppCompatLocaleActivity implements View.OnClickListener{

    RelativeLayout searchSchoolRl;
    EditText searchSchoolEt;
    TextView schoolTv, toolbarTitle;
    ImageView crossButton,crossButton1;
    ImageButton backButton;

    RelativeLayout universityRl;

    RecyclerView school;
    MySchoolAdapter adapter;
    ArrayList<MySchoolModel> list = new ArrayList<>();

    String selectedSchoolId="";

    ProgressBar progressBar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.selectedLanguage,Variables.defultLanguage)
                , this, getClass(),false);
        setContentView(R.layout.activity_add_school);

        toolbarTitle = findViewById(R.id.toolbarTitle);
        schoolTv = findViewById(R.id.schoolTv);
        searchSchoolEt = findViewById(R.id.searchSchool);

        progressBar = findViewById(R.id.progress_bar);

        crossButton = findViewById(R.id.crossButton);
        crossButton1 = findViewById(R.id.cross);
        crossButton.setOnClickListener(this);
        crossButton1.setOnClickListener(this);

        Bundle bundle = getIntent().getBundleExtra("school");
        if(bundle!=null){
            if(bundle.getString(Variables.school) != null){
                schoolTv.setText(bundle.getString(Variables.school));
            }
            if(schoolTv.getText().toString().isEmpty()){
                schoolTv.setText(getString(R.string.add_university));
                crossButton.setVisibility(View.GONE);
            }else {
                crossButton.setVisibility(View.VISIBLE);
            }
        }

        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(this);
        searchSchoolRl = findViewById(R.id.searchSchool_rl);
        universityRl = findViewById(R.id.universityRl);
        universityRl.setOnClickListener(this);


        school = findViewById(R.id.rv_school);
        LinearLayoutManager layout = new LinearLayoutManager(AddSchoolA.this);
        school.setLayoutManager(layout);
        school.setHasFixedSize(false);
        adapter =new MySchoolAdapter(AddSchoolA.this, list, new AdapterClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int pos, Object object, View view) {
                MySchoolModel model = (MySchoolModel) object;

                toolbarTitle.setVisibility(View.VISIBLE);
                Functions.hideSoftKeyboard(AddSchoolA.this);

                selectedSchoolId = model.getId();
                schoolTv.setText(model.getSchoolName());
                schoolTv.setTextColor(ContextCompat.getColor(AddSchoolA.this, R.color.black));

                crossButton.setVisibility(View.VISIBLE);
                searchSchoolRl.setVisibility(View.GONE);
            }

            @Override
            public void onLongItemClick(int pos, Object item, View view) {
            }
        });
        school.setAdapter(adapter);

        searchSchoolEt = findViewById(R.id.searchSchool);
        searchSchoolEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    new Handler().postDelayed(() -> callApiShowSchool(""+s.toString()), 800);
                }
            }
        });
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;

            case R.id.universityRl:
                searchSchoolRl.setVisibility(View.VISIBLE);
                callApiShowSchool(schoolTv.getText().toString());
                toolbarTitle.setVisibility(View.GONE);
                break;

            case R.id.crossButton:
                if(!schoolTv.getText().toString().equals(getString(R.string.add_university))){
                    schoolTv.setText(getString(R.string.add_university));
                    selectedSchoolId = "";
                    crossButton.setVisibility(View.GONE);
                }
                break;

            case R.id.cross:
                toolbarTitle.setVisibility(View.VISIBLE);
                Functions.hideSoftKeyboard(AddSchoolA.this);
                crossButton.setVisibility(View.VISIBLE);
                searchSchoolRl.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("school_name", schoolTv.getText().toString());
        bundle.putString("school_id", ""+selectedSchoolId);
        intent.putExtra("bundle_school", bundle);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }


    private void callApiShowSchool(String params) {
        JSONObject sendObj = new JSONObject();
        try {
            if(params.equalsIgnoreCase(getString(R.string.add_university))){
                sendObj.put("keyword", "a");
            }else {
                sendObj.put("keyword", params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        ApiRequest.callApi(AddSchoolA.this, ApiLinks.showSchools, sendObj, resp -> {
            progressBar.setVisibility(View.GONE);
            parseUserInfo(resp);
        });
    }

    public void parseUserInfo(String data){
        try {
            JSONObject jsonObject=new JSONObject(data);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                list.clear();
                JSONArray msgArray = jsonObject.optJSONArray("msg");
                for (int i=0; i<msgArray.length();i++){
                    MySchoolModel model = new MySchoolModel();

                    model.setId(msgArray.optJSONObject(i).optJSONObject("School").optString("id"));
                    model.setSchoolName(msgArray.getJSONObject(i).optJSONObject("School").getString("name"));
                    model.setCountryId(msgArray.getJSONObject(i).optJSONObject("School").getString("country_id"));
                    model.setUrl(msgArray.getJSONObject(i).optJSONObject("School").getString("url"));

                    list.add(model);
                }
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}