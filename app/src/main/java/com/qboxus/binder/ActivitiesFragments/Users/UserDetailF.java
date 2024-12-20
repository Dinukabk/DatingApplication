package com.qboxus.binder.ActivitiesFragments.Users;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.qboxus.binder.ApiClasses.ApiLinks;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.qboxus.binder.Adapters.ImagesSlidingAdapter;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Constants;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.Models.NearbyUserModel;
import com.qboxus.binder.Models.UserMultiplePhotoModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.qboxus.binder.R;
import com.google.android.material.tabs.TabLayout;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.qboxus.binder.SimpleClasses.Variables;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class UserDetailF extends Fragment implements View.OnClickListener {

    View view;
    Context context;

    ImageButton moveDownButton,likeButton,dislikeButton,superLikeButton;

    RelativeLayout usernameLayout;
    ScrollView scrollView;
    LinearLayout previousImage,nextImage,genderLayout,passionLayout,aboutLayout;
    ChipGroup userPassions;

    TextView usernameTxt, ageTxt, jobTxt, schoolTxt, genderTxt;
    TextView reportTxt, locationText, aboutTxt;

    NearbyUserModel dataItem = new NearbyUserModel();
    ImageButton profileMenu;

    int pos;
    String fromWhere;
    Bundle backBundle;

    public UserDetailF() {
        //Required Empty
    }

    public static UserDetailF newInstance() {
        UserDetailF fragment = new UserDetailF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_detail_, container, false);
        context = getContext();

        backBundle = new Bundle();
        Bundle bundle = getArguments();
        if(bundle!=null) {
            dataItem = (NearbyUserModel) bundle.getSerializable("data");
            fromWhere = bundle.getString("from_where");
            pos = bundle.getInt("pos");
        }

        scrollView = view.findViewById(R.id.scrollView);
        usernameLayout = view.findViewById(R.id.username_layout);

        initBottomView();
        YoYo.with(Techniques.BounceInDown).duration(800).playOn(moveDownButton);

        if(fromWhere !=null && fromWhere.equals("user_list")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)scrollView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            scrollView.setLayoutParams(params);
        }

        return view;
    }


    // this method will initialize all the views and set the data in that view
    public void initBottomView() {
        profileMenu =view.findViewById(R.id.profile_menu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindow(v);
            }
        });

        previousImage = view.findViewById(R.id.previousImage);
        previousImage.setOnClickListener(this);
        nextImage = view.findViewById(R.id.nextImage);
        nextImage.setOnClickListener(this);

        usernameTxt = view.findViewById(R.id.username_txt);
        if(dataItem.getFirstName() != null && !dataItem.getFirstName().equals("")){
            usernameTxt.setText(dataItem.getFirstName());
        }
        ageTxt = view.findViewById(R.id.bottom_age);
        if(dataItem.getBirthday() != null && !dataItem.getBirthday().equals("")){
            ageTxt.setText(dataItem.getBirthday());
        }

        jobTxt = view.findViewById(R.id.bottom_job_txt);
        if(dataItem.getJobTitle().equals("") && !dataItem.getCompany().equals("")){
            jobTxt.setText(dataItem.getJobTitle());
        }else if(dataItem.getCompany().equals("") && !dataItem.getJobTitle().equals("") ){
            jobTxt.setText(dataItem.getJobTitle());
        }else if(dataItem.getCompany().equals("") && dataItem.getJobTitle().equals("") ){
            view.findViewById(R.id.job_layout).setVisibility(View.GONE);
        }else {
            jobTxt.setText(dataItem.getJobTitle());
        }

        if(dataItem.getHide_location().equals("0")){
            view.findViewById(R.id.distanceLayout).setVisibility(View.VISIBLE);
        }else {
            view.findViewById(R.id.distanceLayout).setVisibility(View.GONE);
        }

        if(dataItem.userPassion != null && dataItem.userPassion.size()>0){
            view.findViewById(R.id.passionLayout).setVisibility(View.VISIBLE);
        }else {
            view.findViewById(R.id.passionLayout).setVisibility(View.GONE);
        }

        if(dataItem.about.equals("")){
            view.findViewById(R.id.aboutLayout).setVisibility(View.GONE);
        }else {
            view.findViewById(R.id.aboutLayout).setVisibility(View.VISIBLE);
        }

        schoolTxt =view.findViewById(R.id.bottom_school_txt);
        if(dataItem.getSchool() != null){
            if(dataItem.getSchool().equals("null") || dataItem.getSchool().equals("")){
                view.findViewById(R.id.school_layout).setVisibility(View.GONE);
            }else {
                schoolTxt.setText(dataItem.getSchool());
            }
        }else {
            view.findViewById(R.id.school_layout).setVisibility(View.GONE);
        }

        genderTxt =view.findViewById(R.id.gender_txt);

        if(dataItem.getGenderShow().equals("1")){
            view.findViewById(R.id.genderLayout).setVisibility(View.VISIBLE);
            genderTxt.setText(dataItem.getGender());
        }else {
            view.findViewById(R.id.genderLayout).setVisibility(View.GONE);
        }

        if(dataItem.getLocation() == null || dataItem.getLocation().equals("")){
            view.findViewById(R.id.distanceLayout).setVisibility(View.GONE);
        }else {
            view.findViewById(R.id.distanceLayout).setVisibility(View.VISIBLE);
        }

        locationText =view.findViewById(R.id.bottom_location_txt);
        locationText.setText(dataItem.getLocation()+" "+context.getString(R.string.miles_away));

        aboutLayout = view.findViewById(R.id.aboutLayout);
        passionLayout = view.findViewById(R.id.passionLayout);
        aboutTxt =view.findViewById(R.id.aboutTv);
        if(dataItem.getAbout().equals("")){
            aboutTxt.setVisibility(View.GONE);
        }

        Functions.printLog( "About: "+dataItem.getAbout());
        aboutTxt.setText(dataItem.getAbout());
        userPassions = view.findViewById(R.id.chipGroup);
        if(dataItem.getUserPassion() != null && dataItem.getUserPassion().size()>0){
            passionLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i<dataItem.getUserPassion().size(); i++){

                Log.d(Constants.tag,"Passsion: "+dataItem.getUserPassion().get(i));

                Chip chip1 = (Chip) LayoutInflater.from(context).inflate(R.layout.item_passion1, null);
                chip1.setText(dataItem.getUserPassion().get(i));
                userPassions.addView(chip1);
            }
        }else {
            passionLayout.setVisibility(View.GONE);
        }

        moveDownButton = view.findViewById(R.id.move_downbtn);
        moveDownButton.setOnClickListener(this);

        likeButton = view.findViewById(R.id.likeButton);
        likeButton.setOnClickListener(this);
        superLikeButton = view.findViewById(R.id.superLikeButton);
        superLikeButton.setOnClickListener(this);
        dislikeButton = view.findViewById(R.id.dislikeButton);
        dislikeButton.setOnClickListener(this);


        reportTxt =view.findViewById(R.id.bottom_report_txt);
        reportTxt.setText(context.getResources().getString(R.string.report)+" "+ dataItem.getFirstName());
        reportTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportUserAlert();
            }
        });

        if(dataItem.getImagesUrl() != null){
            setSlider();
        }else{
            TabLayout indicator = view.findViewById(R.id.indicator);
            mPager = view.findViewById(R.id.image_slider_pager);
            try {
                List<UserMultiplePhotoModel> list = new ArrayList<>();
                UserMultiplePhotoModel model = new UserMultiplePhotoModel();
                list.add(model);
                mPager.setAdapter(new ImagesSlidingAdapter(getContext(), list));
                if(list.size()>1){
                    indicator.setVisibility(View.VISIBLE);
                }else{
                    indicator.setVisibility(View.GONE);
                }
            }
            catch (NullPointerException e){
                e.getCause();
            }
            mPager.setCurrentItem(0);
            indicator.setupWithViewPager(mPager, true);
        }
    }


    // when all the animation is done then we will place a data into the view
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation anim= MoveAnimation.create(MoveAnimation.UP, enter, 200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    // when animation is done then we will show the picture slide
                    // because animation in that case will show fluently

                    //setSlider();
                    //fill_data();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            return anim;

        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 200);
        }
    }


    private ViewPager mPager;
    public void setSlider(){
        TabLayout indicator = view.findViewById(R.id.indicator);
        mPager = view.findViewById(R.id.image_slider_pager);
        try {
            List<UserMultiplePhotoModel> list = new ArrayList<>();
            for(int i = 0; i<dataItem.getImagesUrl().size(); i++){
                UserMultiplePhotoModel model = dataItem.getImagesUrl().get(i);
                if(model.getImage() != null && !model.getImage().equals("")){
                    list.add(model);
                }
            }
            mPager.setAdapter(new ImagesSlidingAdapter(getContext(), list));
            if(list.size()>1){
                indicator.setVisibility(View.VISIBLE);
            }else{
                indicator.setVisibility(View.GONE);
            }
        }
        catch (NullPointerException e){
            e.getCause();
        }
        mPager.setCurrentItem(0);
        indicator.setupWithViewPager(mPager, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previousImage:
                if(mPager != null && mPager.getChildCount()>1){
                    mPager.setCurrentItem(mPager.getCurrentItem()-1);
                }
                break;

            case R.id.nextImage:
                if(mPager != null && mPager.getChildCount()>1){
                    mPager.setCurrentItem(mPager.getCurrentItem()+1);
                }
                break;

            case R.id.dislikeButton:
                backBundle.putString("status", "0");
                backBundle.putInt("pos", pos);
                backBundle.putString("id", dataItem.fbId);
                getParentFragmentManager().setFragmentResult("1212",backBundle);
                getActivity().onBackPressed();
                break;

            case R.id.superLikeButton:
                backBundle.putString("status", "2");
                backBundle.putInt("pos", pos);
                backBundle.putString("id", dataItem.fbId);
                backBundle.putSerializable("data", dataItem);
                getParentFragmentManager().setFragmentResult("1212",backBundle);
                getActivity().onBackPressed();
                break;

            case R.id.likeButton:
                backBundle.putString("status", "1");
                backBundle.putInt("pos", pos);
                backBundle.putString("id", dataItem.fbId);
                backBundle.putSerializable("data", dataItem);
                getParentFragmentManager().setFragmentResult("1212",backBundle);
                getActivity().onBackPressed();
                break;

            case R.id.move_downbtn:
                getActivity().onBackPressed();
                break;
        }
    }


    // this will show an alert will is show when we click to Report a User
    public void reportUserAlert(){
        final AlertDialog.Builder alert=new AlertDialog.Builder(context,R.style.DialogStyle);
        alert.setTitle(context.getResources().getString(R.string.report))
                .setMessage(context.getResources().getString(R.string.are_you_sure_report_user))
                .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openUserReport(dataItem.getFbId());
                    }
                });

        alert.setCancelable(true);
        alert.show();
    }

    public void blockUserAlert(){
        final AlertDialog.Builder alert=new AlertDialog.Builder(context,R.style.DialogStyle);
        alert.setTitle(context.getResources().getString(R.string.block))
                .setMessage(context.getResources().getString(R.string.are_you_sure_block_user))
                .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callApiBlockUser();
                    }
                });

        alert.setCancelable(true);
        alert.show();
    }


    PopupWindow popup;
    private void displayPopupWindow(View anchorView) {
        popup = new PopupWindow(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.item_menu_popup_option, null);

        TextView report=layout.findViewById(R.id.report);
        TextView block=layout.findViewById(R.id.blockbtn);
        popup.setContentView(layout);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                reportUserAlert();
            }
        });

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                blockUserAlert();
            }
        });

        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);

        String language=Functions.getSharedPreference(context)
                .getString(Variables.selectedLanguage,"");
        if(language.equals("ar")){
            popup.showAsDropDown(anchorView, anchorView.getWidth() -
                    (Functions.convertDpToPx(context,35)),anchorView.getHeight()
                    - (Functions.convertDpToPx(context,40)));
        }else {
            popup.showAsDropDown(anchorView,anchorView.getWidth(),anchorView.getHeight()
                    - (Functions.convertDpToPx(context,60)));
        }

    }


    public void openUserReport(String receiverId) {
        ReportTypeF _report_typeF =ReportTypeF.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);

        Bundle bundle = new Bundle();
        bundle.putString("user_id", receiverId);
        _report_typeF.setArguments(bundle);

        transaction.addToBackStack(null);
        transaction.replace(R.id.userdetail_frag, _report_typeF).commit();
        onPause();
    }



    private void callApiBlockUser() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.uid,""));
            parameters.put("block_user_id",dataItem.fbId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(context,false,false);
        ApiRequest.callApi(context, ApiLinks.blockUser, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();

                try {
                    JSONObject jsonObject=new JSONObject(resp);

                    String code=jsonObject.optString("code");
                    if(code.equals("200")) {

                        backBundle.putString("status", "0");
                        backBundle.putInt("pos", pos);
                        backBundle.putString("id", dataItem.fbId);
                        getParentFragmentManager().setFragmentResult("1212", backBundle);
                        getActivity().onBackPressed();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        });
    }

}