package com.qboxus.binder.ActivitiesFragments.Users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.Adapters.ReportTypeAdapter;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Models.ReportTypeModel;
import com.qboxus.binder.interfaces.FragmentCallback;
import com.qboxus.binder.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import com.qboxus.binder.ApiClasses.ApiLinks;

public class ReportTypeF extends Fragment implements View.OnClickListener {

    View view;
    RecyclerView recyclerview;
    ReportTypeAdapter adapter;

    Bundle fragmentCallBack;
    String userId;

    boolean check=false;

    ArrayList<ReportTypeModel> dataList = new ArrayList<>();

    public ReportTypeF() {
        //Required Empty
    }

    public static ReportTypeF newInstance() {
        ReportTypeF fragment = new ReportTypeF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_report_type, container, false);
        view.findViewById(R.id.back_btn).setOnClickListener(this);
        recyclerview = view.findViewById(R.id.recylerview);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("user_id");
        }

        callApiForGetReportType();

        return view;
    }


    // get the types of reports
    private void callApiForGetReportType() {

        Functions.showLoader(getActivity(), false, false);
        JSONObject parameters = new JSONObject();
        ApiRequest.callApi(getActivity(), ApiLinks.showReportReasons, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();
                parseData(resp);
            }
        });
    }

    private void parseData(String resp) {
        dataList.clear();
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");

                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    JSONObject reportreason = itemdata.optJSONObject("ReportReason");

                    ReportTypeModel item = new ReportTypeModel();
                    item.id = reportreason.optString("id");
                    item.title = reportreason.optString("title");
                    dataList.add(item);
                }
                setAdapter();
                Functions.cancelLoader();

            } else {
                Functions.cancelLoader();
                Toast.makeText(getActivity(), ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Functions.cancelLoader();
            e.printStackTrace();
        }

    }


    private void setAdapter() {
        adapter = new ReportTypeAdapter(getActivity(), dataList, new ReportTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int positon, ReportTypeModel item, View view) {
                switch (view.getId()) {
                    case R.id.rlt_report:
                        SubmitReportF submit_report_f =SubmitReportF.newInstance(new FragmentCallback() {
                            @Override
                            public void responce(Bundle bundle) {
                                if(bundle != null && bundle.getBoolean("check",false)){
                                    check = bundle.getBoolean("check", false);
                                    getActivity().onBackPressed();
                                }
                            }
                        });
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
                        transaction.addToBackStack(null);
                        Bundle args = new Bundle();
                        args.putString("report_id", item.id);
                        args.putString("report_type", item.title);
                        args.putString("user_id", userId);
                        submit_report_f.setArguments(args);
                        transaction.replace(R.id.fragment_select_report, submit_report_f).commit();
                        break;

                }
            }
        });

        adapter.setHasStableIds(true);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onDetach() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("check", check);
        getParentFragmentManager().setFragmentResult("1122", bundle);
        super.onDetach();
    }

}