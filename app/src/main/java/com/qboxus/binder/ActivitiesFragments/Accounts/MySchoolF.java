package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.os.Build;
import android.os.Bundle;
import com.qboxus.binder.ApiClasses.ApiLinks;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.qboxus.binder.Adapters.MySchoolAdapter;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.AdapterClickListener;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.Models.MySchoolModel;
import com.qboxus.binder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MySchoolF extends Fragment {

    RelativeLayout mainLayout, searchSchoolLayout;
    TextView schoolTv;
    EditText searchSchoolEt;

    RelativeLayout continueButton;
    TextView continueTv;

    RecyclerView school;
    MySchoolAdapter adapter;
    ArrayList<MySchoolModel> list = new ArrayList<>();

    ProgressBar progressBar;

    String selectedSchoolId="";

    View view;

    public MySchoolF() {
        // Required empty public constructor
    }

    public static MySchoolF newInstance() {
        MySchoolF fragment = new MySchoolF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_school, container, false);

        mainLayout = view.findViewById(R.id.main);
        searchSchoolLayout = view.findViewById(R.id.searchSchool_rl);

        view.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                        SignupA.pager.getCurrentItem(),
                        SignupA.pager.getOffscreenPageLimit()));
                SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()-1);
            }
        });

        view.findViewById(R.id.cross).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSchoolLayout.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
            }
        });

        view.findViewById(R.id.skip_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupA.userModel.mySchoolId = "";
                SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()+1);
                SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                        SignupA.pager.getCurrentItem() + 1,
                        SignupA.pager.getOffscreenPageLimit()));
            }
        });

        progressBar = view.findViewById(R.id.progress_bar);

        continueButton = view.findViewById(R.id.continueButton);
        continueTv = view.findViewById(R.id.continue_tv);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectedSchoolId.equals("")){
                    SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()+1);
                    SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                            SignupA.pager.getCurrentItem() + 1,
                            SignupA.pager.getOffscreenPageLimit()));
                }
            }
        });

        schoolTv = view.findViewById(R.id.school_edit);
        schoolTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.setVisibility(View.GONE);
                searchSchoolLayout.setVisibility(View.VISIBLE);
            }
        });


        school = view.findViewById(R.id.rv_school);
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        school.setLayoutManager(layout);
        school.setHasFixedSize(false);
        adapter =new MySchoolAdapter(getContext(), list, new AdapterClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int pos, Object object, View view) {
                MySchoolModel model = (MySchoolModel) object;

                selectedSchoolId = model.getId();
                SignupA.userModel.mySchoolId = model.getId();
                schoolTv.setText(model.getSchoolName());
                schoolTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                if(selectedSchoolId.equals("")){
                    continueButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_google_background));
                    continueTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray));
                }else {
                    continueButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pink_background));
                    continueTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                }

                Functions.hideSoftKeyboard(getActivity());

                mainLayout.setVisibility(View.VISIBLE);
                searchSchoolLayout.setVisibility(View.GONE);
            }

            @Override
            public void onLongItemClick(int pos, Object item, View view) {
            }
        });
        school.setAdapter(adapter);

        searchSchoolEt = view.findViewById(R.id.searchSchool);
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
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            callApiShowSchool(s.toString());
                        }
                    }, 1000);
                }
            }
        });

        return view;
    }

    private void callApiShowSchool(String params) {
        JSONObject sendObj = new JSONObject();

        try {
            sendObj.put("keyword", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        ApiRequest.callApi(getActivity(), ApiLinks.showSchools, sendObj, new Callback() {
            @Override
            public void response(String resp) {
                progressBar.setVisibility(View.GONE);
                parseUserInfo(resp);
            }
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