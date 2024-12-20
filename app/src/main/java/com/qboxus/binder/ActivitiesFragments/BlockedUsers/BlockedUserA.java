package com.qboxus.binder.ActivitiesFragments.BlockedUsers;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.qboxus.binder.ApiClasses.ApiLinks;
import android.os.Bundle;
import android.view.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qboxus.binder.ActivitiesFragments.Chat.ChatF;
import com.qboxus.binder.Adapters.BlockedUsersAdapter;
import com.qboxus.binder.Models.BlockUsersModel;
import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.AppCompatLocaleActivity;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.AdapterClickListener;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.interfaces.FragmentCallback;
import com.qboxus.binder.SimpleClasses.Variables;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

public class BlockedUserA extends AppCompatLocaleActivity {

    RecyclerView recylerview;
    BlockedUsersAdapter adapter;
    ArrayList<BlockUsersModel> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.selectedLanguage,Variables.defultLanguage)
                , this, getClass(),false);
        setContentView(R.layout.activity_blocked_user);

        arrayList=new ArrayList<>();

        recylerview = (RecyclerView) findViewById(R.id.recylerview);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        recylerview.setLayoutManager(layout);
        recylerview.setHasFixedSize(false);
        adapter =new BlockedUsersAdapter(this, arrayList, new AdapterClickListener() {
            @Override
            public void onItemClick(int pos, Object object, View view) {
                BlockUsersModel blockUsersModel=(BlockUsersModel) object;
                if(view.getId()==R.id.unblockBtn){

                    callApiBlockUser(pos,blockUsersModel);
                }
                else {
                    chatFragment(blockUsersModel);
                }
            }

            @Override
            public void onLongItemClick(int pos, Object item, View view) {
            }
        });

        recylerview.setAdapter(adapter);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        callApiBlockUsersList();
    }


    private void callApiBlockUsersList() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(this).getString(Variables.uid,""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(this,false,false);
        ApiRequest.callApi(this, ApiLinks.showBlockedUsers, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();

                try {
                    JSONObject jsonObject=new JSONObject(resp);

                    String code=jsonObject.optString("code");
                    if(code.equals("200")) {


                        JSONArray msg=jsonObject.optJSONArray("msg");
                        for(int i=0;i<msg.length();i++){
                            JSONObject object=msg.optJSONObject(i);
                            BlockUsersModel model = null;
                            try {
                                model = new ObjectMapper().readValue(object.toString(), BlockUsersModel.class);
                                arrayList.add(model);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        adapter.notifyDataSetChanged();

                    }


                    if(arrayList.isEmpty()){
                        findViewById(R.id.nodata_found_txt).setVisibility(View.VISIBLE);

                    }
                    else {
                        findViewById(R.id.nodata_found_txt).setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }





    private void callApiBlockUser(int pos,BlockUsersModel blockUsersModel) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(this).getString(Variables.uid,""));
            parameters.put("block_user_id",blockUsersModel.blockedUserModel.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(this,false,false);
        ApiRequest.callApi(this, ApiLinks.blockUser, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();

                try {
                    JSONObject jsonObject=new JSONObject(resp);

                    String code=jsonObject.optString("code");
                    if(code.equals("200") || code.equals("201")) {
                        arrayList.remove(pos);
                        adapter.notifyDataSetChanged();
                    }

                    if(arrayList.isEmpty()){
                        findViewById(R.id.nodata_found_txt).setVisibility(View.VISIBLE);
                    }
                    else { findViewById(R.id.nodata_found_txt).setVisibility(View.GONE); }


                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        });
    }


    public void chatFragment(BlockUsersModel blockUsersModel){
        ChatF chatF =ChatF.newInstance(new FragmentCallback() {
            @Override
            public void responce(Bundle bundle) {

            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle args = new Bundle();
        args.putString("Sender_Id", Functions.getSharedPreference(this).getString(Variables.uid,""));
        args.putString("Receiver_Id", blockUsersModel.blockedUserModel.id);
        args.putString("name",blockUsersModel.blockedUserModel.username);
        args.putString("picture",blockUsersModel.blockedUserModel.image);
        args.putBoolean("is_match_exits",false);
        chatF.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.BlockF, chatF).commit();
    }

}