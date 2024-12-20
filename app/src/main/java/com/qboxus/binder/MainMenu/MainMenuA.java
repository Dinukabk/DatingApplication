package com.qboxus.binder.MainMenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.qboxus.binder.ActivitiesFragments.Chat.ChatF;
import com.qboxus.binder.ActivitiesFragments.Inbox.InboxF;
import com.qboxus.binder.ActivitiesFragments.Profile.ProfileF;
import com.qboxus.binder.ActivitiesFragments.SplashA;
import com.qboxus.binder.ActivitiesFragments.UserLikes.UserlikesF;
import com.qboxus.binder.ActivitiesFragments.Users.UsersF;
import com.qboxus.binder.ActivitiesFragments.inapppurchases.InAppSubscriptionA;
import com.qboxus.binder.Adapters.ViewPagerAdapter;
import com.qboxus.binder.ApiClasses.ApiLinks;
import com.qboxus.binder.BuildConfig;
import com.qboxus.binder.SimpleClasses.AppCompatLocaleActivity;
import com.qboxus.binder.SimpleClasses.DebounceClickHandler;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Constants;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.interfaces.FragmentCallback;
import com.qboxus.binder.SimpleClasses.PermissionUtils;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import games.moisoni.google_iab.BillingConnector;
import games.moisoni.google_iab.BillingEventListener;
import games.moisoni.google_iab.enums.ProductType;
import games.moisoni.google_iab.models.BillingResponse;
import games.moisoni.google_iab.models.ProductInfo;
import games.moisoni.google_iab.models.PurchaseInfo;


public class MainMenuA extends AppCompatLocaleActivity{


    long mBackPressed;
    public static String userPic;
    DatabaseReference rootref;
    public static String actionType ="none";
    public static String receiverid="none";
    public static String title="none";
    public static String receiverPic ="none";
    public static MainMenuA mainMenuA;
    PermissionUtils takePermissionUtils;
    Snackbar snackbar;

    protected ViewPager2 pager;

    private ViewPagerAdapter adapter;
    Context context;
    String userId;
    ImageButton profileBtn, starBtn, binderBtn, messageBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.selectedLanguage,Variables.defultLanguage)
                , this, getClass(),false);
        setContentView(R.layout.activity_main_menu);
        mainMenuA =this;
        userPic = Functions.getSharedPreference(this)
                .getString(Variables.uPic,"null");
        if(userPic != null || userPic.equals("")) {
            userPic = "null";
        }
        rootref = FirebaseDatabase.getInstance().getReference();
        callApiShowLicense();
        if(getIntent().hasExtra("action_type")){
            actionType = getIntent().getExtras().getString("action_type");
            receiverid = getIntent().getExtras().getString("receiverid");
            title = getIntent().getExtras().getString("title");
            receiverPic = getIntent().getExtras().getString("icon");
        }
        takeNecessaryPermission();
        getPublicIP();
        Functions.registerConnectivity(this, response -> {
            if(response.equalsIgnoreCase("disconnected")) {
                snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.no_internet, Snackbar.LENGTH_INDEFINITE);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                snackbar.show();
            }else {
                if(snackbar!=null)
                    snackbar.dismiss();
            }
        });
        initControl();
        CheckSubscriptionBuy();

        int usedCoins = Functions.getSharedPreference(this).getInt(Variables.usedCoins, 0);
        String callType = Functions.getSharedPreference(this).getString(Variables.callType, "");
        if(usedCoins > 0){
            callApiForUseCoins(usedCoins, callType);
        }
    }

    private void CheckSubscriptionBuy() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(getApplicationContext()).getString(Variables.uid,""));
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }

        ApiRequest.callApi(MainMenuA.this, ApiLinks.showSubscription, parameters, new Callback() {
            @Override
            public void response(String resp) {
                try {
                    JSONObject response=new JSONObject(resp);
                    String code=response.optString("code");
                    if(code.equals("200")) {
                        JSONObject msgObj=response.getJSONObject("msg");
                        JSONObject subscriptionObj=msgObj.getJSONObject("SubscriptionPackage");
                        String duration="1";
                        String created=subscriptionObj.optString("created");
                        if (!(checkDateIsExpire(duration,created)))
                        {
                            Log.d(Constants.tag,"checkDateIsExpire: false");
                            Functions.getSharedPreference(getApplicationContext()).edit().putBoolean(Variables.isProductPurchase, true).commit();
                        }
                        else
                        {
                            Log.d(Constants.tag,"checkDateIsExpire: true");
                            Functions.getSharedPreference(getApplicationContext()).edit().putBoolean(Variables.isProductPurchase, false).commit();
                        }
                    }
                    else
                    {
                        Functions.getSharedPreference(getApplicationContext()).edit().putBoolean(Variables.isProductPurchase, false).commit();
                    }

                } catch (Exception e) {
                    Functions.getSharedPreference(getApplicationContext()).edit().putBoolean(Variables.isProductPurchase, false).commit();
                    Log.d(Constants.tag,"Exception: "+e);
                }



            }
        });

    }

    private boolean checkDateIsExpire(String duration, String created) {

        int month=Integer.parseInt(duration);
        String subscriptionEndDate=Functions.ChangeDateFormatWithAdditionalMonth("yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss",created, month);
        Log.d(Constants.tag,"Subscription End Date: "+subscriptionEndDate);
        String todayDate=Functions.getCurrentDate("yyyy-MM-dd HH:mm:ss");
        Log.d(Constants.tag,"Today Date: "+todayDate);
        return Functions.isDateExpiredInDays("yyyy-MM-dd HH:mm:ss",todayDate,subscriptionEndDate);
    }


    private void initControl() {
        context= MainMenuA.this;
        userId = Functions.getSharedPreference(context).getString(Variables.uid,"");

        profileBtn =findViewById(R.id.profile_btn);
        profileBtn.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickProfile();
                pager.setCurrentItem(3);
            }
        }));

        binderBtn =findViewById(R.id.binder_btn);
        binderBtn.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(0);
                clickBinder();
            }
        }));

        starBtn =findViewById(R.id.star_btn);
        starBtn.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(1);
                clickStar();
            }
        }));

        messageBtn =findViewById(R.id.message_btn);
        messageBtn.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickMessage();
                pager.setCurrentItem(2);
            }
        }));


        SetTabs();
    }

    public void SetTabs() {
        adapter = new ViewPagerAdapter(this);
        pager = findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(4);
        registerFragmentWithPager();
        pager.setAdapter(adapter);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
        pager.setUserInputEnabled(false);


        if(MainMenuA.actionType.equals("message")){
            pager.setCurrentItem(2);
            chatFragment();
        }else if(MainMenuA.actionType.equals("match")){
            pager.setCurrentItem(2);
        }else {
            pager.setCurrentItem(0);
        }
    }



    private void registerFragmentWithPager() {
        adapter.addFrag(UsersF.newInstance());
        adapter.addFrag(UserlikesF.newInstance());
        adapter.addFrag(InboxF.newInstance());
        adapter.addFrag(ProfileF.newInstance());
    }


    public void clickProfile(){

        profileBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_profile_color));
        binderBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_binder_gray));
        starBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mylikes_gray));
        messageBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_message_gray));

    }


    public void clickBinder(){

        profileBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_profile_gray));
        binderBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_binder_color));
        starBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mylikes_gray));
        messageBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_message_gray));

    }


    public void clickStar(){

        starBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mylikes_color));
        profileBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_profile_gray));
        binderBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_binder_gray));
        messageBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_message_gray));

    }


    public void clickMessage(){

        profileBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_profile_gray));
        binderBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_binder_gray));
        starBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mylikes_gray));
        messageBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_message_color));

    }

    public void chatFragment(){
        ChatF chatF =ChatF.newInstance(new FragmentCallback() {
            @Override
            public void responce(Bundle bundle) {

            }
        });
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle args = new Bundle();
        args.putString("Sender_Id", userId);
        args.putString("Receiver_Id", MainMenuA.receiverid);
        args.putString("name", MainMenuA.title);
        args.putString("picture", MainMenuA.receiverPic);
        args.putBoolean("is_match_exits",false);
        chatF.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, chatF).commit();
    }

    private void takeNecessaryPermission() {
        takePermissionUtils=new PermissionUtils(MainMenuA.this, mAllPermissionResult);
        if (!(takePermissionUtils.isStorageCameraRecordingPermissionGranted()))
        {
            showPermissionDialog();
        }
    }

    private void showPermissionDialog() {
        final Dialog dialog = new Dialog(MainMenuA.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_permission_dialog1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView okayTv,cancelTv;
        cancelTv=dialog.findViewById(R.id.cancelTv);
        okayTv=dialog.findViewById(R.id.okayTv);

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        okayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                takePermissionUtils.takeStorageCameraRecordingPermission();
            }
        });
        dialog.show();
    }


    private ActivityResultLauncher<String[]> mAllPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(MainMenuA.this,key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(MainMenuA.this, MainMenuA.this.getString(R.string.we_need_storage_camera_and_recording_permission_for_use_all_feature_of_app));
                    }
                    else
                    if (allPermissionClear)
                    {

                    }

                }
            });

    public void getPublicIP() {
        ApiRequest.callApiGetRequest(MainMenuA.this, "https://api.ipify.org/?format=json", resp -> {
            try {
                JSONObject responce = new JSONObject(resp);
                String ip = responce.optString("ip");

                addFirebaseToken(ip);
            } catch (Exception e) {
                Log.d(Constants.tag,"Exception: "+e);
            }
        });
    }

    public void addFirebaseToken(String ip) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Functions.getSharedPreference(MainMenuA.this)
                            .edit().putString(Variables.deviceToken, token).apply();

                    JSONObject params = new JSONObject();
                    try {
                        params.put("user_id", Functions.getSharedPreference(MainMenuA.this).getString(Variables.uid,""));
                        params.put("device_token", token);
                        params.put("device", getString(R.string.device));
                        params.put("version", BuildConfig.VERSION_NAME);
                        params.put("ip", "" + ip);
                        params.put("lat", Functions
                                .getSharedPreference(MainMenuA.this)
                                .getString(Variables.currentLat,""));
                        params.put("long", Functions
                                .getSharedPreference(MainMenuA.this)
                                .getString(Variables.currentLon,""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ApiRequest.callApi(MainMenuA.this, ApiLinks.addDeviceData, params, null);

                });

    }




    // on start we will save the latest token into the firebase
    @Override
    protected void onStart() {
        super.onStart();
        rootref.child("Users")
                .child(Functions.getSharedPreference(MainMenuA.this).getString(Variables.uid,""))
                .child("token")
                .setValue(Functions.getSharedPreference(MainMenuA.this).getString(Variables.deviceToken,""));
    }


    private void callApiForUseCoins(int usedCoins, String type) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions
                    .getSharedPreference(MainMenuA.this)
                    .getString(Variables.uid,""));
            parameters.put("coin", ""+usedCoins);
            if(type.equals("voice_call")){
                parameters.put("feature", "audio");
            }else {
                parameters.put("feature", "video");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(MainMenuA.this, ApiLinks.useCoin, parameters, resp -> {
            try {
                JSONObject jsonObject = new JSONObject(resp);

                String code = jsonObject.optString("code");
                if(code.equals("200")){
                    JSONObject userObject = jsonObject.optJSONObject("msg").optJSONObject("User");
                    Functions.getSharedPreference(MainMenuA.this)
                            .edit().putInt(Variables.usedCoins, 0).apply();
                    Functions.getSharedPreference(MainMenuA.this).edit()
                            .putString(Variables.uWallet, userObject.getString("wallet")).apply();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void callApiEndSubscription() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(MainMenuA.this).getString(Variables.uid,""));
            parameters.put("package","gold");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(this, ApiLinks.endSubscription, parameters, null);

    }

    private void callApiShowLicense() {
        ApiRequest.callApi(MainMenuA.this, ApiLinks.showLicense, new JSONObject(), resp -> {
            try {
                JSONObject jsonObject = new JSONObject(resp);

                String code = jsonObject.optString("code");
                if(code.equals("200")){
                    Functions.getSharedPreference(MainMenuA.this).edit().
                            putBoolean(Variables.isCodePurchase, true).commit();
                }
                else {
                    Functions.getSharedPreference(MainMenuA.this).edit().
                            putBoolean(Variables.isCodePurchase, false).commit();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Functions.getSharedPreference(this).edit().putBoolean(Variables.userLikeLimit, false).commit();
        Functions.unRegisterConnectivity(this);
    }

    @Override
    public void onBackPressed() {
        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (pager.getCurrentItem() != 0) {
                pager.setCurrentItem(0);
                clickBinder();
                return;
            }

            if (mBackPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(MainMenuA.this, getString(R.string.tap_to_exist), Toast.LENGTH_SHORT).show();
                mBackPressed = System.currentTimeMillis();

            }
        }
        else {
            Fragment frag = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size()-1);
            if(frag!=null){
                int childCount = frag.getChildFragmentManager().getBackStackEntryCount();
                if(childCount==0){
                    super.onBackPressed();
                }
                else {
                    frag.getChildFragmentManager().popBackStack();
                }
            }
            else {
                super.onBackPressed();
            }
        }


    }
}
