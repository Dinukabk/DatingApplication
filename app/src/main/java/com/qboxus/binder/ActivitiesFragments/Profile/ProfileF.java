package com.qboxus.binder.ActivitiesFragments.Profile;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.qboxus.binder.ApiClasses.ApiLinks;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.binder.ActivitiesFragments.inapppurchases.InAppSubscriptionA;
import com.qboxus.binder.Adapters.ProfileSliderAdapter;
import com.qboxus.binder.Adapters.ProfileSlidingAdapter;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Constants;
import com.qboxus.binder.Models.ProfileSliderModel;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.SpacesItemDecoration;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.Models.LoginSliderModel;

import com.qboxus.binder.ActivitiesFragments.Profile.EditProfile.EditProfileA;
import com.qboxus.binder.R;
import com.qboxus.binder.ActivitiesFragments.Settings.SettingA;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileF extends Fragment implements View.OnClickListener {

    View view;
    Context context;

    SimpleDraweeView profileImage;
    TextView userName;
    TextView age, schoolTv, boostPriceTv, statusTv;
    LinearLayout getCreditsView,getBoostView;
    LinearLayout settingLayout, addMedia, editProfileLayout;
    RelativeLayout purchaseLayout;

    RecyclerView sliderRv;
    ProfileSliderAdapter adapter;
    List<ProfileSliderModel> list = new ArrayList<>();

    private ArrayList<LoginSliderModel> data_list;
    ViewPager viewPager;
    WormDotsIndicator dotsIndicator;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 5000; //delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;


    public ProfileF() {
        // Required empty public constructor
    }

    public static ProfileF newInstance() {
        ProfileF fragment = new ProfileF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_profile, container, false);
        context=getContext();

        initializeViews();

        return view;
    }


    @SuppressLint("SetTextI18n")
    private void initializeViews() {
        purchaseLayout = view.findViewById(R.id.purchaseLayout);
        viewPager = view.findViewById(R.id.viewPager);
        editProfileLayout = view.findViewById(R.id.editProfileLayout);
        editProfileLayout.setOnClickListener(this);

        addMedia = view.findViewById(R.id.addMedia);
        addMedia.setOnClickListener(this);

        settingLayout = view.findViewById(R.id.setting_layout);
        settingLayout.setOnClickListener(this);

        userName = view.findViewById(R.id.user_name);
        userName.setText(Functions.getSharedPreference(context).getString(Variables.fName,""));

        schoolTv = view.findViewById(R.id.schoolTv);
        if(Functions.getSharedPreference(context).getString(Variables.school, "").equals("null")){
            schoolTv.setVisibility(View.GONE);
        }else {
            schoolTv.setText(Functions.getSharedPreference(context).getString(Variables.school, ""));
            schoolTv.setVisibility(View.VISIBLE);
        }

        age = view.findViewById(R.id.age);
        if(!Functions.getSharedPreference(context).getString(Variables.birthDay, "").isEmpty()){
            age.setText(", "+Functions.getSharedPreference(context).getString(Variables.birthDay,""));
        }else {
            age.setText("");
        }

        profileImage = view.findViewById(R.id.profile_image);
        // show the picture age and username of the user
        if(Functions.getSharedPreference(context).getString(Variables.uPic,"") !=null
                && !Functions.getSharedPreference(context).getString(Variables.uPic,"").equals("") ){
            String imageUrl=Functions.getSharedPreference(context).getString(Variables.uPic,"");
            profileImage.setController(com.qboxus.binder.SimpleClasses.Functions.frescoImageLoad(imageUrl,R.drawable.ic_user_icon,profileImage,false));

        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileDetail();
            }
        });

        statusTv = view.findViewById(R.id.statusTv);
        statusTv.setOnClickListener(this);
        dotsIndicator = view.findViewById(R.id.dots_indicator);

        boostPriceTv = view.findViewById(R.id.boostPriceTv);
        boostPriceTv.setText(context.getString(R.string.from)+" "+Constants.boostamount3);
        getBoostView = view.findViewById(R.id.getBoostView);
        getBoostView.setOnClickListener(this);
        getCreditsView = view.findViewById(R.id.getCreditsView);
        getCreditsView.setOnClickListener(this);

        sliderRv = view.findViewById(R.id.sliderView);
        setAdapterToRecyclerView();

    }


    private void setAdapterToRecyclerView(){
        list.clear();

        ProfileSliderModel model = new ProfileSliderModel();
        model.setHeaderTv(context.getString(R.string.binder_Plus));
        model.setDescriptionTv(context.getString(R.string.binder_Plus_saves_time));
        model.setPriceTv(context.getString(R.string.upgrade_from)+Constants.subscriptionIdamount);

        ProfileSliderModel model1 = new ProfileSliderModel();
        model1.setHeaderTv(context.getString(R.string.binder_Boost));
        model1.setDescriptionTv(context.getString(R.string.binder_Boost_allows_you_to));
        model1.setPriceTv(context.getString(R.string.buy_Now));

        ProfileSliderModel model2 = new ProfileSliderModel();
        model2.setHeaderTv(context.getString(R.string.binder_Credits));
        model2.setDescriptionTv(context.getString(R.string.binder_Credits_are_an));
        model2.setPriceTv(context.getString(R.string.buy_Now));

        list.add(model);
        list.add(model1);
        list.add(model2);

        sliderRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        sliderRv.setHasFixedSize(false);

        adapter = new ProfileSliderAdapter(context, list, new ProfileSliderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ProfileSliderModel item, View view) {
                ProfileSliderModel model = (ProfileSliderModel) item;
                if(model.getHeaderTv().equalsIgnoreCase(context.getString(R.string.binder_Plus))){
                    openSubscriptionView();
                }else if(model.getHeaderTv().equalsIgnoreCase(context.getString(R.string.binder_Boost))){
                    openBoost();
                }else if(model.getHeaderTv().equalsIgnoreCase(context.getString(R.string.binder_Credits))){
                    openPurchaseCoinsScreen();
                }
            }
        });

        sliderRv.setAdapter(adapter);

        int space = (int) getContext().getResources().getDimension(R.dimen._10sdp);
        int space1 = (int) getContext().getResources().getDimension(R.dimen._5sdp);
        sliderRv.addItemDecoration(new SpacesItemDecoration(space,space1));

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        sliderRv.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(sliderRv);
    }


    private void setupViewPager() {
        purchaseLayout.setVisibility(View.VISIBLE);
        data_list = new ArrayList<>();
        data_list.add(new LoginSliderModel(context.getString(R.string.GETBINDERPLUS), context.getString(R.string.Seewholikesyoumore), R.drawable.ic_binder_color));
        data_list.add(new LoginSliderModel(context.getString(R.string.Getmatchesfaster), "", R.drawable.ic_boost_profile));
        data_list.add(new LoginSliderModel(context.getString(R.string.StandoutwithSuperLikes), context.getString(R.string.Youre3timesmorelikelyogetamatch), R.drawable.ic_super_like));
        data_list.add(new LoginSliderModel(context.getString(R.string.Likeprofilesaroundtheworld), context.getString(R.string.PassporttoanywherewithBinderPlus), R.drawable.ic_location_global));
        data_list.add(new LoginSliderModel(context.getString(R.string.ControlYourProfile), context.getString(R.string.LimitwhatotherseewithBinderPlus), R.drawable.ic_control));
        data_list.add(new LoginSliderModel(context.getString(R.string.RewindyourLikeorNope), context.getString(R.string.GetunlimitedRewindswithBinderPlus), R.drawable.ic_rewind));
        data_list.add(new LoginSliderModel(context.getString(R.string.Increaseyourchances), context.getString(R.string.GetunlimitedlikeswithBinderPlus), R.drawable.ic_likes));
        viewPager.setAdapter(new ProfileSlidingAdapter(context, data_list));

        dotsIndicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        dotsIndicator.setDotIndicatorColor(ContextCompat.getColor(context, R.color.pink_color));
                        break;
                    case 1:
                        dotsIndicator.setDotIndicatorColor(ContextCompat.getColor(context, R.color.newBoostColor));
                        break;
                    case 2:
                        dotsIndicator.setDotIndicatorColor(ContextCompat.getColor(context, R.color.newSuperLikeColor));
                        break;
                    case 3:
                        dotsIndicator.setDotIndicatorColor(ContextCompat.getColor(context, R.color.newLocationColor));
                        break;
                    case 4:
                        dotsIndicator.setDotIndicatorColor(ContextCompat.getColor(context, R.color.newControlColor));
                        break;
                    case 5:
                        dotsIndicator.setDotIndicatorColor(ContextCompat.getColor(context, R.color.newRewindColor));
                        break;
                    case 6:
                        dotsIndicator.setDotIndicatorColor(ContextCompat.getColor(context, R.color.newLikeColor));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void handler() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == data_list.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }


    // below two method is used get the user pictures and about text from our server
    private void callApiShowUserDetail() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.uid,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(context, ApiLinks.showUserDetail, parameters, resp ->
                parseUserInfo(resp));
    }


    public void parseUserInfo(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONObject msg = jsonObject.optJSONObject("msg");
                JSONObject schoolObject = msg.optJSONObject("School");
                if(schoolObject.optString("name").equals("null")){
                    schoolTv.setVisibility(View.GONE);
                }else {
                    schoolTv.setVisibility(View.VISIBLE);
                    schoolTv.setText(schoolObject.optString("name"));
                }

                JSONArray userImagesArray = msg.optJSONArray("UserImage");

                if(userImagesArray != null && userImagesArray.length()>0){
                    for(int i = 0; i<userImagesArray.length(); i++){
                        if(userImagesArray.optJSONObject(i).optString("order_sequence").equals("0")){
                            profileImage.setController(com.qboxus.binder.SimpleClasses.Functions.frescoImageLoad(userImagesArray.optJSONObject(i).optString("image"),R.drawable.ic_user_icon,profileImage,false));
                            Functions.getSharedPreference(context).edit()
                                    .putString(Variables.uPic,
                                            userImagesArray.optJSONObject(i).optString("image")).commit();
                            break;
                        }
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editProfileLayout:
            case R.id.addMedia:
                openEditProfile();
                break;

            case R.id.setting_layout:
                openSettings();
                break;

            case R.id.statusTv:
                openSubscriptionView();
                break;

            case R.id.getCreditsView:
                openPurchaseCoinsScreen();
                break;

            case R.id.getBoostView:
                openBoost();
                break;
        }
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible && view != null){
            if(Functions.getSharedPreference(context).getBoolean(Variables.isProductPurchase,false)){
                purchaseLayout.setVisibility(View.GONE);
                statusTv.setVisibility(View.GONE);
            }else {
                setupViewPager();
                if(timer != null){
                    timer.cancel();
                }
                handler();
            }
        }
    }



    // open the view of Edit profile where 6 pic is visible
    public void openProfileDetail() {
        startActivity(new Intent(getActivity(), ProfileDetailsA.class));
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


    // open the view of Edit profile where 6 pic is visible
    public void openEditProfile(){
        Intent intent = new Intent(getActivity(), EditProfileA.class);
        editProfileResultCallback.launch(intent);
    }

    ActivityResultLauncher<Intent> editProfileResultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        callApiShowUserDetail();
                    }
                }
            });


    // open the view of Edit profile where 6 pic is visible
    public void openSettings(){
        startActivity(new Intent(getActivity(), SettingA.class));
    }


    // when user will click the refresh btn  then this view will be open for subscribe it in our app
    public void openSubscriptionView(){
        if(!Functions.getSharedPreference(context).getBoolean(Variables.isProductPurchase,false)){
            startActivity(new Intent(getActivity(), InAppSubscriptionA.class));
        }
    }


    public void openPurchaseCoinsScreen(){
        try {
            Fragment fragment = (Fragment) Class.forName("com.qboxus.binder.ActivitiesFragments.inapppurchases.PurchaseCoinsF").newInstance();
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
}
