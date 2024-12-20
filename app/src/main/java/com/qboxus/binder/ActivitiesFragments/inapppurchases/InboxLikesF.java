package com.qboxus.binder.ActivitiesFragments.inapppurchases;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.qboxus.binder.ApiClasses.ApiLinks;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.binder.R;
import com.qboxus.binder.Adapters.UserLikeAdapter;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Constants;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.AdapterClickListener;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.Models.MatchModel;
import com.qboxus.binder.Models.NearbyUserModel;
import com.qboxus.binder.Models.UserMultiplePhotoModel;

import com.qboxus.binder.SimpleClasses.Variables;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxLikesF extends Fragment implements View.OnClickListener{

    View view;
    Context context;

    ArrayList<NearbyUserModel> dataList;
    RecyclerView recyclerView;
    UserLikeAdapter adapter;

    String likesCount;
    int count;
    TextView likesCountTv;
    ProgressBar progressBar;

    JSONArray likedUserArray = new JSONArray();
    Boolean isViewCreated =false;

    int wallet;

    Date c;
    SimpleDateFormat df;
    int currentYear;
    Integer todayDay = 0;

    public InboxLikesF() {
        //Required Empty
    }

    public static InboxLikesF newInstance() {
        InboxLikesF fragment = new InboxLikesF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    Bundle fragmentCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_userlike_list, container, false);
        context=getContext();

        Calendar cal = Calendar.getInstance();
        todayDay = cal.get(Calendar.DAY_OF_MONTH);
        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("yyyy", Locale.getDefault());
        currentYear = Integer.parseInt(df.format(c));

        wallet = Integer.parseInt(Functions.getSharedPreference(context).getString(Variables.uWallet, "0"));

        Bundle bundle = getArguments();
        if(bundle!=null){
            likesCount = bundle.getString("like_count");
            count = Integer.parseInt(likesCount);
        }

        getScreenSize();

        progressBar = view.findViewById(R.id.progress_bar);

        likesCountTv = view.findViewById(R.id.title_txt);
        likesCountTv.setText(likesCount+" "+context.getString(R.string.likes));

        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentCallback = new Bundle();
                if(count == 0 || count < 0){
                    fragmentCallback.putInt("likes", 0);
                }else {
                    fragmentCallback.putInt("likes", count);
                }
                getParentFragmentManager().setFragmentResult("1112", fragmentCallback);
                getActivity().onBackPressed();
            }
        });


        dataList = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        recyclerView.setHasFixedSize(false);
        adapter=new UserLikeAdapter(context, dataList, new AdapterClickListener() {
            @Override
            public void onItemClick(int pos, Object item, View view) {
                if(Functions.getSharedPreference(context).getBoolean(Variables.isProductPurchase,Constants.enableSubscribe)){
                    openUserDetail((NearbyUserModel) item,pos);
                }
            }

            @Override
            public void onLongItemClick(int pos, Object item, View view) {

            }
        });

        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        view.findViewById(R.id.appbar).setVisibility(View.VISIBLE);
        view.findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        view.findViewById(R.id.top_layout).setVisibility(View.VISIBLE);

        getPeopleNearby();

        isViewCreated = true;

        return view;
    }


    public void getScreenSize(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = getActivity().getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            Variables.screenWidth = windowMetrics.getBounds().width() - insets.left - insets.right;
            Variables.screenHeight = windowMetrics.getBounds().height() - insets.top - insets.bottom;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Variables.screenHeight = displayMetrics.heightPixels;
            Variables.screenWidth = displayMetrics.widthPixels;
        }
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN |ItemTouchHelper.UP) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if(Functions.getSharedPreference(context).getBoolean(Variables.isProductPurchase,Constants.enableSubscribe)){
                return super.getSwipeDirs(recyclerView, viewHolder);
            }else {
                return 0;
            }
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();

            NearbyUserModel item= dataList.get(position);

            dataList.remove(position);
            if(swipeDir == 8){
                likeDislike("1", "0", item);
            }else if(swipeDir == 4){
                likeDislike("0", "0", item);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if(dX<0.0){
                viewHolder.itemView.findViewById(R.id.left_overlay).setVisibility(View.VISIBLE);
                viewHolder.itemView.findViewById(R.id.right_overlay).setVisibility(View.GONE);
            }else if(dX>0.0) {
                viewHolder.itemView.findViewById(R.id.left_overlay).setVisibility(View.GONE);
                viewHolder.itemView.findViewById(R.id.right_overlay).setVisibility(View.VISIBLE);
            }else {
                viewHolder.itemView.findViewById(R.id.left_overlay).setVisibility(View.GONE);
                viewHolder.itemView.findViewById(R.id.right_overlay).setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if((isViewCreated && menuVisible)){

            wallet = Integer.parseInt(Functions.getSharedPreference(context).getString(Variables.uWallet, "0"));
            getPeopleNearby();

            if(Functions.getSharedPreference(context).getBoolean(Variables.isProductPurchase,Constants.enableSubscribe)){
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);
                view.findViewById(R.id.subscribe_txt).setVisibility(View.GONE);
                view.findViewById(R.id.subscribe_btn).setVisibility(View.GONE);
            }else {
                view.findViewById(R.id.subscribe_txt).setVisibility(View.VISIBLE);
                view.findViewById(R.id.subscribe_btn).setVisibility(View.VISIBLE);
                view.findViewById(R.id.subscribe_btn).setOnClickListener(this);
            }
        }
    }


    private void getPeopleNearby() {
        if(progressBar.getVisibility() == View.GONE){
            progressBar.setVisibility(View.VISIBLE);
        }

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.uid,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(context, ApiLinks.showUserLikes, parameters, new Callback() {
            @Override
            public void response(String resp) {
                if(progressBar.getVisibility() == View.VISIBLE){
                    progressBar.setVisibility(View.GONE);
                }
                parseUserInfo(resp);
            }
        });
    }

    public void parseUserInfo(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                dataList.clear();
                JSONArray msg=jsonObject.getJSONArray("msg");

                for (int i=0; i<msg.length();i++){
                    JSONObject userobj = msg.getJSONObject(i).optJSONObject("LikeUser");
                    JSONObject userdata;
                    if(userobj.optString("user_id").equals(Functions
                            .getSharedPreference(context).getString(Variables.uid,""))){
                        userdata = msg.getJSONObject(i).optJSONObject("OtherUser");
                    }else {
                        userdata = msg.getJSONObject(i).optJSONObject("User");
                    }

                    NearbyUserModel item=new NearbyUserModel();

                    JSONArray userImagesList = userdata.optJSONArray("UserImage");
                    if(userImagesList != null && userImagesList.length()>0){
                        item.imagesUrl = new ArrayList<>();
                        for(int k = 0; k<6; k++){
                            UserMultiplePhotoModel model = new UserMultiplePhotoModel();
                            if(k<userImagesList.length()){
                                model.setImage(userImagesList.optJSONObject(k).optString("image"));
                                model.setId(userImagesList.optJSONObject(k).optString("id"));
                                model.setOrderSequence(Integer.parseInt(userImagesList.optJSONObject(k).optString("order_sequence")));
                                item.imagesUrl.add(k, model);
                            }else {
                                model.setOrderSequence(k);
                                item.imagesUrl.add(k, model);
                            }
                        }
                    }

                    item.setFbId(userdata.optString("id"));
                    item.setFirstName(userdata.optString("first_name"));
                    item.setLastName(userdata.optString("last_name"));
                    item.setName(userdata.optString("first_name")+" "+userdata.optString("last_name"));
                    item.setJobTitle(userdata.optString("job_title"));
                    item.setCompany(userdata.optString("company"));
                    item.setSchool(userdata.optString("school"));

                    if(!userdata.optString("dob").equals("0000-00-00")){
                        try {
                            Date date = df.parse(userdata.optString("dob"));
                            int age = Integer.parseInt(df.format(date));
                            item.setBirthday(" " + (currentYear - age));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    item.setHide_location(""+userdata.optString("hide_location"));
                    item.setAbout(userdata.optString("bio"));
                    item.setGender(userdata.optString("gender"));
                    item.setGenderShow(userdata.optString("gender_show"));
                    item.setLike(userdata.optString("like"));
                    item.setSuperLike(userdata.optString("super_like"));

                    if(userobj != null){
                        item.setLike(userobj.getString("like"));
                        item.setSuperLike(userobj.getString("super_like"));
                    }

                    dataList.add(item);
                }

                if(dataList.isEmpty()){
                    view.findViewById(R.id.nodata_found_txt).setVisibility(View.VISIBLE);
                }else {
                    view.findViewById(R.id.nodata_found_txt).setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            view.findViewById(R.id.nodata_found_txt).setVisibility(View.VISIBLE);
        }

    }

    public void openUserDetail(NearbyUserModel item, int pos){
        String openClassName = context.getPackageName()+".ActivitiesFragments.Users.UserDetail_F";
        try {
            Fragment fragment = (Fragment) Class.forName(openClassName).newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            getActivity().getSupportFragmentManager().setFragmentResultListener("1212",
                    this, new FragmentResultListener() {
                        @Override
                        public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                            if(result != null){
                                String status = result.getString("status");
                                int position = result.getInt("pos");
                                NearbyUserModel model = new NearbyUserModel();
                                if(result.getSerializable("data") != null){
                                    model = (NearbyUserModel) result.getSerializable("data");
                                }

                                switch (status){
                                    case "0":
                                        likeDislike("0", "0", model);
                                        manageCount();
                                        dataList.remove(position);
                                        adapter.notifyDataSetChanged();
                                        break;
                                    case "1":
                                        if(Functions.getSharedPreference(context).getBoolean(Variables.isProductPurchase,Constants.enableSubscribe)){
                                            likeDislike("1","0", model);
                                            manageCount();
                                            dataList.remove(position);
                                            adapter.notifyDataSetChanged();
                                        }
                                        else {
                                            if(Functions.getSharedPreference(context).getBoolean(Variables.userLikeLimit, false) && checkDate()){
                                                openSubscriptionView();
                                            }else if(Functions.getSharedPreference(context).getBoolean(Variables.userLikeLimit, false) && !checkDate()){
                                                likeDislike("1","0", model);
                                                manageCount();
                                                dataList.remove(position);
                                                adapter.notifyDataSetChanged();
                                            }else if(!Functions.getSharedPreference(context).getBoolean(Variables.userLikeLimit, false)){
                                                likeDislike("1","0", model);
                                                manageCount();
                                                dataList.remove(position);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                        break;
                                    case "2":

                                        likeDislike("0","1", model);

                                        break;
                                }
                            }
                        }
                    });

            Bundle args = new Bundle();
            args.putSerializable("data", item);
            args.putString("from_where", "user_list");
            args.putInt("pos", pos);
            fragment.setArguments(args);

            transaction.addToBackStack(null).replace(R.id.Users_likes_F, fragment).commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void likeDislike(String like, String superLike, NearbyUserModel item) {
        JSONObject userObject = new JSONObject();
        try {
            userObject.put("user_id", Functions.getSharedPreference(context).getString(Variables.uid,""));
            userObject.put("other_user_id", item.fbId);
            userObject.put("super_like", superLike);
            userObject.put("like", like);
            likedUserArray.put(userObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(superLike.equals("1")){
            callApiLikeDislikeUser(item);
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
    private void callApiLikeDislikeUser(NearbyUserModel item) {
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

                    if(code.equals("200")){
                        manageCount();
                        removeItem(item);
                    }

                    else if (code.equals("201") && msg.contains("super like limit reached per day")) {
                        Date c = Calendar.getInstance().getTime();
                        if(item!=null) {
                            if (wallet >= Constants.SUPER_LIKE_COINS)
                                callApiForUseCoins(item);
                            else
                                openSuperLikePopup();
                        }

                        final String formattedDate = Variables.df.format(c);
                        Functions.getSharedPreference(context).edit().putString(Variables.userLikeLimitDate, formattedDate).commit();
                    }


                    else if(code.equals("201") && msg.contains("limit reached per day. please subscribe for unlimited likes")){
                        openSubscriptionView();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void removeItem(NearbyUserModel model){
        for (int i=0;i<dataList.size();i++){
            if(dataList.get(i).fbId.equals(model.fbId)){
                dataList.remove(i);
                adapter.notifyDataSetChanged();
            }
        }
    }
    // below two method is used get the user pictures and about text from our server
    private void callApiMatchUser(NearbyUserModel item) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.uid,""));
            parameters.put("other_user_id", item.getFbId());
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
                        match_model.setU_id(item.getFbId());
                        match_model.setUsername(item.getFirstName());
                        match_model.setSuperLike(item.getSuperLike());
                        if(item.getImagesUrl() != null){
                            match_model.setPicture(item.getImagesUrl().get(0).getImage());
                        }
                        openMatch(match_model);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean checkDate(){
        long currenttime = System.currentTimeMillis();

        //database date in millisecond
        long compareDate = 0;
        Date d = null;
        try {
            d = Variables.df.parse(Functions.getSharedPreference(context).getString(Variables.userLikeLimitDate,""));
            compareDate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = currenttime - compareDate;
        if (difference < 86400000) {
            int chatday = Integer.parseInt(Functions.getSharedPreference(context).getString(Variables.userLikeLimitDate,"").substring(0, 2));
            if (todayDay == chatday){
                Functions.printLog( "Date = Today");
                return true;
            }
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.subscribe_btn) {
            openSubscriptionView();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if((isViewCreated)){
            getPeopleNearby();

            if(Functions.getSharedPreference(context).getBoolean(Variables.isProductPurchase,Constants.enableSubscribe)){
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);
                view.findViewById(R.id.subscribe_btn).setVisibility(View.GONE);
            }else {
                view.findViewById(R.id.subscribe_txt).setVisibility(View.VISIBLE);
                view.findViewById(R.id.subscribe_btn).setVisibility(View.VISIBLE);
                view.findViewById(R.id.subscribe_btn).setOnClickListener(this);
            }
        }
    }


    @Override
    public void onDetach() {
        fragmentCallback = new Bundle();
        if(count == 0 || count < 0){
            fragmentCallback.putInt("likes", 0);
        }else {
            fragmentCallback.putInt("likes", count);
        }
        getParentFragmentManager().setFragmentResult("1112", fragmentCallback);
        super.onDetach();
    }


    private void callApiForUseCoins( NearbyUserModel model) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.uid, ""));
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

                        likeDislike("0","1", model);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    // when a match is build between user then this method is call and open the view if match screen
    public void openMatch(MatchModel item){
        String openClassName = context.getPackageName()+".ActivitiesFragments.Matchs.Match_F";
        try {
            Fragment fragment = (Fragment) Class.forName(openClassName).newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top,
                    R.anim.in_from_top, R.anim.out_from_bottom);

            Bundle args = new Bundle();
            args.putSerializable("data", item);
            fragment.setArguments(args);

            transaction.addToBackStack(null).replace(R.id.MainMenuFragment, fragment).commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    public void openSubscriptionView(){
        startActivity(new Intent(getActivity(), InAppSubscriptionA.class));
    }


    public void openSuperLikePopup(){
        String openClassName = context.getPackageName()+".ActivitiesFragments.Users.SuperLike_Popup_F";
        try {
            Fragment fragment = (Fragment) Class.forName(openClassName).newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top,
                    R.anim.in_from_top, R.anim.out_from_bottom);

            transaction.addToBackStack(null).replace(R.id.MainMenuFragment, fragment).commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void manageCount(){
        count = count - 1;
        if(count == 0 || count < 0){
            likesCountTv.setText(count+" "+context.getString(R.string.likes));
        }else {
            likesCountTv.setText(count+" "+context.getString(R.string.likes));
        }
    }

}
