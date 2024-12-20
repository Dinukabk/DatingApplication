package com.qboxus.binder.ActivitiesFragments.Users;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appyvet.materialrangebar.RangeBar;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qboxus.binder.ActivitiesFragments.Accounts.LoginA;
import com.qboxus.binder.ActivitiesFragments.Matchs.MatchF;
import com.qboxus.binder.Adapters.UserAdapter;
import com.qboxus.binder.ApiClasses.ApiLinks;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Constants;
import com.qboxus.binder.Models.MatchModel;
import com.qboxus.binder.Models.NearbyUserModel;
import com.qboxus.binder.Models.UserMultiplePhotoModel;
import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.interfaces.AdapterClickListener;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.interfaces.FragmentCallback;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;
import com.zhouyou.view.seekbar.SignSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersF extends Fragment implements View.OnClickListener {

    View view;
    Context context;

    CardStackView cardStackView;

    UserAdapter adapter;
    NearbyUserModel lastUserItem = new NearbyUserModel();
    int checkLastIndext = 0;

    SimpleDraweeView profileImage;
    ImageButton refreshBtn, crossBtn, heartBtn, superLikeBtn, boostBtn, findingRefreshBtn;

    int pager = 0, wallet;

    JSONArray likedUserArray = new JSONArray();
    String userId;

    Date c;
    SimpleDateFormat df;
    int currentYear;

    Integer todayDay = 0;


    public UsersF() {
        // Required empty public constructor
    }

    public static UsersF newInstance() {
        UsersF fragment = new UsersF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    RelativeLayout userListLayout, findNearbyUser;
    DatabaseReference rootref;
    boolean isApiRunning =false;
    boolean isViewLoad =false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_users, container, false);

        context=getContext();
        userId = Functions.getSharedPreference(context).getString(Variables.uid,"");
        wallet = Integer.parseInt(Functions.getSharedPreference(context).getString(Variables.uWallet, "0"));
        Calendar cal = Calendar.getInstance();
        todayDay = cal.get(Calendar.DAY_OF_MONTH);

        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("yyyy", Locale.getDefault());
        currentYear = Integer.parseInt(df.format(c));

        adapter = new UserAdapter(context, new AdapterClickListener() {
            @Override
            public void onItemClick(int pos, Object item, View view) {
                openUserDetail();
            }

            @Override
            public void onLongItemClick(int pos, Object item, View view) {

            }
        });
        rootref= FirebaseDatabase.getInstance().getReference();

        profileImage = view.findViewById(R.id.profileimage);

        String imageUrl=Functions.getSharedPreference(context).getString(Variables.uPic,"");
        profileImage.setController(com.qboxus.binder.SimpleClasses.Functions.frescoImageLoad(imageUrl,R.drawable.ic_user_icon,profileImage,false));



        profileImage.setOnClickListener(v -> {
            if(!isApiRunning){
                isApiRunning = true;
                pager = 0;
                callApiShowNearbyUsers(false);
            }
        });

        callApiShowNearbyUsers(false);

        cardStackView =view.findViewById(R.id.card_viewstack);
        cardStackView.setAdapter(adapter);
        cardStackView.setCardEventListener(new CardStackView.CardEventListener() {
            @Override
            public void onCardDragging(float percentX, float percentY) {

            }

            @Override
            public void onCardSwiped(SwipeDirection direction) {
                // card swipe get the top user index for get the data in list
                int position = cardStackView.getTopIndex()-1;
                if(position<adapter.getCount()) {
                    final NearbyUserModel item = adapter.getItem(position);

                    // if swipe left the we will call a function for update the value in firebase
                    if (direction.equals(SwipeDirection.Left)) {
                        likeDislike("0","0", item);
                    }


                    // if swipe Right the we will call a function for update the value in firebase
                    else if (direction.equals(SwipeDirection.Right)) {
                        if(Functions.getSharedPreference(context).getBoolean(Variables.isProductPurchase,Constants.enableSubscribe)){
                            likeDislike("1","0", item);
                        }else{
                            if(!Functions.getSharedPreference(context).getBoolean(Variables.userLikeLimit, false)){
                                likeDislike("1","0", item);
                            }else if(Functions.getSharedPreference(context).getBoolean(Variables.userLikeLimit, false) && checkDate()){
                                cardStackView.reverse();
                                openSubscriptionView();
                            }else if(Functions.getSharedPreference(context).getBoolean(Variables.userLikeLimit, false) && !checkDate()){
                                likeDislike("1","0", item);
                            }
                        }
                    }


                    else if(direction.equals(SwipeDirection.Top)){

                        likeDislike("0","1", item);

//                        if(Functions.getSharedPreference(context).getBoolean(Variables.userSuperlikeLimit, false) && checkDate()){
//
//                            wallet = Integer.parseInt(Functions.getSharedPreference(context).getString(Variables.uWallet, "0"));
//
//                            if(wallet == Constants.SUPER_LIKE_COINS || wallet > Constants.SUPER_LIKE_COINS){
//                                callApiForUseCoins(item);
//                            }else {
//                                cardStackView.reverse();
//                                openSuperLikePopup();
//                            }
//                        }
//
//                        else if(Functions.getSharedPreference(context).getBoolean(Variables.userSuperlikeLimit, false) && !checkDate()){
//                            likeDislike("0","1", item);
//                        }
//
//                        else if(!Functions.getSharedPreference(context).getBoolean(Variables.userSuperlikeLimit, false)){
//                            likeDislike("0","1", item);
//                        }
//

                    }

                    // find if the swipes card is last or not
                    if (cardStackView.getTopIndex() == adapter.getCount()) {
                        if(!Functions.getSharedPreference(context).getBoolean(Variables.userLikeLimit, false)){

                            if(direction.equals(SwipeDirection.Top)){
                                checkLastIndext = 2;
                                callApiLikeDislikeUser( item);
                            }else if(direction.equals(SwipeDirection.Left)){
                                checkLastIndext = 0;
                                callApiLikeDislikeUser( item);
                            }else {
                                checkLastIndext = 1;
                                callApiLikeDislikeUser( item);
                            }
                            likedUserArray = new JSONArray();
                            // if last then we will replace the view and show the ad
                            if(mInterstitialAd != null){
                                mInterstitialAd.show(getActivity());
                            }
                            showFindingView();
                            lastUserItem = item;
                            pager++;
                            callApiShowNearbyUsers(true);
                        }else {
                            if(direction.equals(SwipeDirection.Top)){
                                if(wallet == Constants.SUPER_LIKE_COINS || wallet > Constants.SUPER_LIKE_COINS){
                                    callApiForUseCoins(item);
                                }else {
                                    cardStackView.reverse();
                                    openSuperLikePopup();
                                }
                            }else if(direction.equals(SwipeDirection.Right)){
                                cardStackView.reverse();
                                openSubscriptionView();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCardReversed() {
                // card swipe get the top user index for get the data in list
                showUserListView();
            }

            @Override
            public void onCardMovedToOrigin() {

            }

            @Override
            public void onCardClicked(int index) {
            }

        });

        userListLayout =view.findViewById(R.id.user_list_layout);
        findNearbyUser =view.findViewById(R.id.find_nearby_User);

        // initialize the bottom three buttons
        initBottomView();

        isViewLoad =true;
        return view;

    }

    // initialize the bottom three buttons
    public void initBottomView(){

        refreshBtn =view.findViewById(R.id.refresh_btn);
        crossBtn =view.findViewById(R.id.cross_btn);
        heartBtn =view.findViewById(R.id.heart_btn);
        superLikeBtn =view.findViewById(R.id.supperlike_btn);
        boostBtn =view.findViewById(R.id.boost_btn);

        refreshBtn.setOnClickListener(this);
        crossBtn.setOnClickListener(this);
        heartBtn.setOnClickListener(this);
        superLikeBtn.setOnClickListener(this);
        boostBtn.setOnClickListener(this);

    }


    private boolean checkDate(){

        try {

            int chatday = Integer.parseInt(Functions.getSharedPreference(context).getString(Variables.userLikeLimitDate, "").substring(0, 2));
            if (todayDay == chatday) {
                return true;
            }
        }catch (Exception e){

        }
        return false;
    }



    // when all the card is swiped then this mehtod will call and replace the view and show the ad
    public void showFindingView(){
        Variables.isReloadUsers = true;

        String imageUrl=Functions.getSharedPreference(context).getString(Variables.uPic,"");
        profileImage.setController(com.qboxus.binder.SimpleClasses.Functions.frescoImageLoad(imageUrl,R.drawable.ic_user_icon,profileImage,false));


        userListLayout.setVisibility(View.GONE);
        findNearbyUser.setVisibility(View.VISIBLE);

        final PulsatorLayout pulsator =  view.findViewById(R.id.pulsator);
        pulsator.start();

        view.findViewById(R.id.change_setting_btn).setOnClickListener(this);
        view.findViewById(R.id.finding_refresh_btn).setOnClickListener(this);
    }


    public void showUserListView(){
        userListLayout.setVisibility(View.VISIBLE);
        findNearbyUser.setVisibility(View.GONE);
    }


    // this method will initializae the inertial add will will show when user swipe all the users
    private AdManagerInterstitialAd mInterstitialAd;
    @Override
    public void onResume() {
        super.onResume();
        //code for interstitial add
        if(!Constants.REMOVE_ADS){
            AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
            AdManagerInterstitialAd.load(getContext(), getResources().getString(R.string.my_Interstitial_Add),
                    adRequest,
                    new AdManagerInterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                            // The mAdManagerInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            mInterstitialAd = null;
                        }
                    });

        }

    }

    @Override
    public void onPause() {
        if(likedUserArray.length()>2){
            callApiLikeDislikeUser( new NearbyUserModel());
            likedUserArray = new JSONArray();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if(likedUserArray.length()>2){
            callApiLikeDislikeUser(new NearbyUserModel());
            likedUserArray = new JSONArray();
        }
        super.onDestroyView();
    }

    // when ever a user go to the main center view then we call a api of nearby
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        if((menuVisible && Variables.isReloadUsers) && isViewLoad){
            wallet = Integer.parseInt(Functions.getSharedPreference(context).getString(Variables.uWallet, "0"));
            if(!isApiRunning){
                isApiRunning =true;
                Variables.isReloadUsers =false;
                callApiShowNearbyUsers(false);
            }
        }

        else if(!menuVisible && isViewLoad){
            if(likedUserArray.length()>0){
                callApiLikeDislikeUser(null);
            }

        }

    }

    // all the action that will perform by bottom 3 buttons
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.cross_btn:
                clearBackStack();
                com.qboxus.binder.SimpleClasses.Functions.swipeLeft(cardStackView);
                break;

            case R.id.heart_btn:
                clearBackStack();
                com.qboxus.binder.SimpleClasses.Functions.swipeRight(cardStackView);
                break;

            case R.id.boost_btn:
                openBoost();
                break;

            case R.id.supperlike_btn:
                clearBackStack();
                com.qboxus.binder.SimpleClasses.Functions.swipeTop(cardStackView);
                break;

            case R.id.refresh_btn:
                if(cardStackView.getVisibility() == View.GONE){
                    cardStackView.setVisibility(View.VISIBLE);
                }
                cardStackView.reverse();
                break;

            case R.id.finding_refresh_btn:
                if(lastUserItem.getFbId() != null && !lastUserItem.getFbId().equals("")){
                    showUserListView();
                    cardStackView.setVisibility(View.VISIBLE);
                    cardStackView.reverse();
                }
                break;

            case R.id.change_setting_btn:
                openDiscoverySettingDialog();
                break;

        }
    }



    public void clearBackStack(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }


    private void callApiShowNearbyUsers(final boolean is_show_loader) {
        if(is_show_loader){
            Functions.showLoader(context,false,false);
        }

        String lat="";
        String lng="";
        if( Functions.getSharedPreference(context).getBoolean(Variables.isSeletedLocationSelected,false)){
            lat= Functions.getSharedPreference(context).getString(Variables.seletedLat,"33.738045");
            lng= Functions.getSharedPreference(context).getString(Variables.selectedLon,"73.084488");
        }else {
            lat= Functions.getSharedPreference(context).getString(Variables.currentLat,"33.738045");
            lng= Functions.getSharedPreference(context).getString(Variables.currentLon,"73.084488");
        }

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
            parameters.put("lat", lat);
            parameters.put("long", lng);
            parameters.put("starting_point", ""+pager);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(context, ApiLinks.showNearByUsers, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();
                isApiRunning = false;
                parseUserInfo(resp);
            }
        });
    }

    String block="0";
    public void parseUserInfo(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msg = jsonObject.getJSONArray("msg");

                if(msg!=null) {
                    adapter.clear();
                    for (int i = 0; i < msg.length(); i++) {
                        if(pager>0 && i==0){
                            adapter.add(lastUserItem);
                            if(checkLastIndext == 0){
                                com.qboxus.binder.SimpleClasses.Functions.swipeLeft(cardStackView);
                            }else if(checkLastIndext == 1){
                                com.qboxus.binder.SimpleClasses.Functions.swipeRight(cardStackView);
                            }else {
                                com.qboxus.binder.SimpleClasses.Functions.swipeTop(cardStackView);
                            }
                        }else{
                            JSONObject userdata = msg.getJSONObject(i);
                            parseUserData(userdata);
                        }
                    }
                }

                if(!(msg.length()>0)){
                    showFindingView();
                } else {
                    showUserListView();
                }

                if(block.equals("1")){
                    Functions.getSharedPreference(context).edit().putBoolean(Variables.islogin,false).commit();
                    startActivity(new Intent(getActivity(), LoginA.class));
                    getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                    getActivity().finish();
                }
            }else if(code.equals("201")){
                showFindingView();
                if(lastUserItem.getFbId() != null && !lastUserItem.getFbId().equals("")){
                    cardStackView.setVisibility(View.GONE);
                    adapter.clear();
                    adapter.add(lastUserItem);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Functions.printLog( "Exception: "+e.toString());
            showFindingView();
        }
    }

    public void parseUserData(JSONObject userdata){

        JSONObject userData = userdata.optJSONObject("User");
        JSONObject distance = userdata.optJSONObject("0");
        JSONArray userImagesList = userdata.optJSONArray("UserImage");
        JSONArray userPassionsList = userdata.optJSONArray("UserPassion");
        double dist = Double.parseDouble(distance.optString("distance"));
        int dist1 = (int) Math.round(dist);

        NearbyUserModel item = new NearbyUserModel();

        item.setFbId(userData.optString("id"));
        item.setFirstName(userData.optString("first_name"));
        item.setLastName(userData.optString("last_name"));
        item.setName(userData.optString("username"));
        item.setImage(userData.optString("image"));
        item.setJobTitle(userData.optString("job_title"));
        item.setCompany(userData.optString("company"));
        item.setSchool(userdata.optJSONObject("School").optString("name"));
        if(!userData.optString("dob").equals("0000-00-00")){
            try {
                Date date = df.parse(userData.optString("dob"));
                int age = Integer.parseInt(df.format(date));
                item.setBirthday(" " + (currentYear - age));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        item.setAbout(userData.optString("bio"));
        item.setLocation(String.valueOf(dist1));
        item.setGender(""+userData.optString("gender"));
        item.setGenderShow(""+userData.optString("gender_show"));
        item.setLike(""+userData.optString("like"));
        item.setHide_location(""+userData.optString("hide_location"));
        item.setBoost(""+userData.optString("boost"));
        item.setBoostDateTime(""+userData.optString("boost_datetime"));
        item.setTotalBoost(""+userData.optString("total_boost"));

        if(userImagesList != null && userImagesList.length()>0){
            item.imagesUrl = new ArrayList<>();
            for(int i = 0; i<6; i++){
                UserMultiplePhotoModel model = new UserMultiplePhotoModel();
                if(i<userImagesList.length()){
                    model.setImage(userImagesList.optJSONObject(i).optString("image"));
                    model.setId(userImagesList.optJSONObject(i).optString("id"));
                    model.setOrderSequence(Integer.parseInt(userImagesList.optJSONObject(i).optString("order_sequence")));
                    item.imagesUrl.add(i, model);
                }else {
                    model.setOrderSequence(i);
                    item.imagesUrl.add(i, model);
                }
            }
        }

        if(userPassionsList != null && userPassionsList.length()>0){
            item.userPassion = new ArrayList<>();
            for(int i = 0; i<userPassionsList.length(); i++){
                item.userPassion.add(userPassionsList.optJSONObject(i).optJSONObject("Passion").optString("title"));
            }
        }

        item.setLastSeenDate(""+userData.optString("last_seen"));
        item.setSuperLike(""+userData.optString("super_like"));

        adapter.add(item);
        adapter.notifyDataSetChanged();
    }




    private void likeDislike(String like, String superLike, NearbyUserModel item){
        JSONObject userObject = new JSONObject();
        try {
            userObject.put("user_id", userId);
            userObject.put("other_user_id", item.fbId);
            userObject.put("super_like", superLike);
            userObject.put("like", like);
            likedUserArray.put(userObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(superLike.equals("1")){
            callApiLikeDislikeUser( item);
            likedUserArray = new JSONArray();
        }else if(likedUserArray.length()>2){
            callApiLikeDislikeUser( item);
            likedUserArray = new JSONArray();
        }

        if(like.equals("1") || superLike.equals("1")){
            if(item.getLike().equals("1") || item.getSuperLike().equals("1")){
                callApiMatchUser(item);
            }
        }


    }


    // below two method is used get the user pictures and about text from our server
    private void callApiLikeDislikeUser( NearbyUserModel item) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("like_data", likedUserArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(context, ApiLinks.likeUser, parameters, new Callback() {
            @Override
            public void response(String resp) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    String msg = jsonObject.optString("msg");

                    if (code.equals("201") && msg.contains("super like limit reached per day")) {
                        Date c = Calendar.getInstance().getTime();
                        cardStackView.reverse();
                        if(wallet >= Constants.SUPER_LIKE_COINS){
                            if(item!=null)
                            callApiForUseCoins(item);
                        }
                        else {
                            openSuperLikePopup();
                        }
                        final String formattedDate = Variables.df.format(c);
                        Functions.getSharedPreference(context).edit().putString(Variables.userLikeLimitDate, formattedDate).commit();
                    }
                   
                    else if(code.equals("201") && msg.contains("like limit reached per day")){
                        Functions.getSharedPreference(context).edit().putBoolean(Variables.userLikeLimit, true).commit();
                        openSubscriptionView();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // below two method is used get the user pictures and about text from our server
    private void callApiMatchUser(NearbyUserModel model) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
            parameters.put("other_user_id",  model.getFbId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiRequest.callApi(context, ApiLinks.matchUser, parameters, new Callback() {
            @Override
            public void response(String resp) {
                try {
                    JSONObject object = new JSONObject(resp);
                    if(object.optString("code").equals("200")){
                        MatchModel match_model =new MatchModel();
                        match_model.setU_id(model.getFbId());
                        match_model.setUsername(model.getFirstName());
                        match_model.setSuperLike(model.getSuperLike());
                        if(model.getImagesUrl() != null){
                            match_model.setPicture(model.getImagesUrl().get(0).getImage());
                        }
                        openMatch(match_model);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void callApiEditProfile() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
            parameters.put("min_age", ""+ Functions.getSharedPreference(context).getInt(Variables.minAge, Constants.MIN_DEFAULT_AGE));
            parameters.put("max_age", ""+ Functions.getSharedPreference(context).getInt(Variables.maxAge, Constants.MAX_DEFAULT_AGE));
            parameters.put("radius", ""+ Functions.getSharedPreference(context).getInt(Variables.maxDistance, Constants.DEFAULT_DISTANCE));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(context, ApiLinks.editProfile, parameters, new Callback() {
            @Override
            public void response(String resp) {
                pager = 0;
                callApiShowNearbyUsers(true);
            }
        });
    }

    private void callApiForUseCoins(NearbyUserModel item) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
            parameters.put("coin", ""+Constants.SUPER_LIKE_COINS);
            parameters.put("feature", "superlike");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(context, ApiLinks.useCoin, parameters, new Callback() {
            @Override
            public void response(String resp) {

                try {
                    JSONObject jsonObject = new JSONObject(resp);

                    String code = jsonObject.optString("code");
                    if(code.equals("200")){
                        JSONObject userObject = jsonObject.optJSONObject("msg").optJSONObject("User");

                        wallet = Integer.parseInt(userObject.getString("wallet"));
                        Functions.getSharedPreference(context).edit()
                                .putString(Variables.uWallet, userObject.getString("wallet")).apply();

                        likeDislike("0","1", item);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    // this method will show the view which will show the more detail about the user
    private void openDiscoverySettingDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_discovery_setting_dialog);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.d_top_bottom_border_line));

        TextView cancelBtn=dialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        TextView updateBtn=dialog.findViewById(R.id.update_btn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callApiEditProfile();
                dialog.cancel();
            }
        });


        SignSeekBar distanceBar=dialog.findViewById(R.id.distance_bar);
        final TextView distanceTxt=dialog.findViewById(R.id.distance_txt);
        distanceBar.setProgress( Functions.getSharedPreference(context).getInt(Variables.maxDistance, Constants.DEFAULT_DISTANCE));
        distanceTxt.setText( Functions.getSharedPreference(context).getInt(Variables.maxDistance, Constants.DEFAULT_DISTANCE)+" "+ context.getResources().getString(R.string.miles));
        distanceBar.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat,boolean fromUser) {
                distanceTxt.setText(progress+" "+ context.getResources().getString(R.string.miles));
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
                Variables.isReloadUsers =true;
                Functions.getSharedPreference(context).edit().putInt(Variables.maxDistance,progress).commit();
            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat,boolean fromUser) {

            }
        });


        // this is the age seek bar. when we change the progress of seek bar it will be locally save
        RangeBar ageSeekbar=dialog.findViewById(R.id.age_seekbar);
        final TextView ageRangeTxt=dialog.findViewById(R.id.age_range_txt);

        ageRangeTxt.setText( Functions.getSharedPreference(context).getInt(Variables.minAge, Constants.MIN_DEFAULT_AGE)+" - "+
                Functions.getSharedPreference(context).getInt(Variables.maxAge, Constants.MAX_DEFAULT_AGE)+" "+ context.getResources().getString(R.string.years));
        ageSeekbar.setRangePinsByValue( Functions.getSharedPreference(context).getInt(Variables.minAge, Constants.MIN_DEFAULT_AGE),
                Functions.getSharedPreference(context).getInt(Variables.maxAge, Constants.MAX_DEFAULT_AGE));
        ageSeekbar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                Variables.isReloadUsers =true;

                SharedPreferences.Editor editor= Functions.getSharedPreference(context).edit();
                editor.putInt(Variables.minAge,Integer.parseInt(leftPinValue));
                editor.putInt(Variables.maxAge,Integer.parseInt(rightPinValue));
                editor.commit();

                ageRangeTxt.setText(leftPinValue+" - "+rightPinValue+" "+ context.getResources().getString(R.string.years));

            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {

            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {

            }
        });


        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        layoutParams.width = dialogWindowWidth;
        dialog.getWindow().setAttributes(layoutParams);
    }

    // this method will show the view which will show the more detail about the user
    public void openUserDetail(){
        NearbyUserModel item= adapter.getItem(cardStackView.getTopIndex());
        UserDetailF userDetail_f =UserDetailF.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        getActivity().getSupportFragmentManager().setFragmentResultListener("1212",
                this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if(result != null){
                    String status = result.getString("status");
                    switch (status){
                        case "0":
                            com.qboxus.binder.SimpleClasses.Functions.swipeLeft(cardStackView);
                            break;
                        case "1":
                            com.qboxus.binder.SimpleClasses.Functions.swipeRight(cardStackView);
                            break;
                        case "2":
                            com.qboxus.binder.SimpleClasses.Functions. swipeTop(cardStackView);
                            break;
                    }
                }
            }
        });

        Bundle args = new Bundle();
        args.putSerializable("data", item);
        userDetail_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, userDetail_f,"User_detail_F").commit();
    }



    // when a match is build between user then this method is call and open the view if match screen
    public void openMatch(MatchModel item){
        MatchF matchF =MatchF.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("data",item);
        matchF.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, matchF).commit();
    }

    public void openBoost(){
        try {
            Fragment fragment = (Fragment) Class.forName("com.qboxus.binder.ActivitiesFragments.inapppurchases.BoostF").newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null).replace(R.id.MainMenuFragment, fragment).commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void openSuperLikePopup(){
        SuperLikePopupF superLike_popup_f =SuperLikePopupF.newInstance(new FragmentCallback() {
            @Override
            public void responce(Bundle bundle) {

            }
        });
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null).replace(R.id.MainMenuFragment, superLike_popup_f).commit();
    }

    public void openSubscriptionView(){
        try {
            startActivity(new Intent(getActivity(), Class.forName("com.qboxus.binder.ActivitiesFragments.inapppurchases.InAppSubscriptionA")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
