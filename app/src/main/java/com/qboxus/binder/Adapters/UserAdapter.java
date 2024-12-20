package com.qboxus.binder.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.qboxus.binder.SimpleClasses.CustomViewPager;
import com.qboxus.binder.Models.NearbyUserModel;
import com.qboxus.binder.Models.UserMultiplePhotoModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.qboxus.binder.Constants;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.interfaces.AdapterClickListener;
import com.qboxus.binder.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by qboxus on 10/15/2018.
 */
public class UserAdapter extends ArrayAdapter<NearbyUserModel> {

    Context context;
    AdapterClickListener adapterClickListener;
    StringBuilder sb = new StringBuilder();


    public UserAdapter(Context context, AdapterClickListener adapterClickListener) {
        super(context, 0);
        this.context=context;
        this.adapterClickListener=adapterClickListener;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder;
        if (contentView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.item_user_layout, parent, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }

        NearbyUserModel spot = null;
        try{
            spot = getItem(position);
        }
        catch (Exception e){
            Log.d(Constants.tag,"Exception: "+e);
        }
        finally {
            if (spot==null)
            {
                Log.d(Constants.tag,"spot: ");
            }else
            {
                performAllAction(holder,spot,position);
            }
        }



        return contentView;
    }

    private void performAllAction(ViewHolder holder, NearbyUserModel spot, int position) {
        if( Functions.getSharedPreference(context).getString(Variables.distanceType,"mi").equalsIgnoreCase(context.getString(R.string.mi)))
            holder.distanceText.setText(spot.getLocation()+" "+context.getResources().getString(R.string.miles_away));
        else
            holder.distanceText.setText(Functions.changemiletoKm(spot.getLocation())+" "+context.getResources().getString(R.string.km_away));


        holder.name.setText(spot.getFirstName());
        holder.age.setText(spot.getBirthday());


        setSlider(holder,spot.imagesUrl);


        if(spot.getSuperLike().equals("1")) {
            holder.superLikeImage.setVisibility(View.VISIBLE);
            holder.infoLayout.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
            holder.bottomLayout.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
        }else {
            holder.superLikeImage.setVisibility(View.GONE);
            holder.infoLayout.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            holder.bottomLayout.setBackground(context.getResources().getDrawable(R.drawable.d_black_gradient));
        }

        if(spot.getUserPassion() != null && spot.getUserPassion().size()>0){
            holder.passions.setVisibility(View.VISIBLE);
            holder.passions.removeAllViews();
            for (int i = 0; i<spot.getUserPassion().size(); i++){
                Chip chip1 = (Chip) LayoutInflater.from(context).inflate(R.layout.item_passion2, null);
                chip1.setText(spot.getUserPassion().get(i));
                holder.passions.addView(chip1);
            }
        }else {
            holder.passions.removeAllViews();
            holder.passions.setVisibility(View.GONE);
        }

        holder.bind(position,spot,adapterClickListener);

        if(checkDate(spot.getLastSeenDate()) > 12){
            holder.recentlyActiveView.setVisibility(View.GONE);
        }else {
            holder.recentlyActiveView.setVisibility(View.VISIBLE);
        }
    }

    public void setSlider(ViewHolder holder,List<UserMultiplePhotoModel> imageList){

        List<UserMultiplePhotoModel> list=new ArrayList<>();
        for(UserMultiplePhotoModel item:imageList)
        {
            if (item.getImage()!=null && !(TextUtils.isEmpty(item.getImage())) && (item.getImage().contains(".png") || item.getImage().contains(".jpg")))
            {
                list.add(item);
            }
        }
        try {
            holder.mPager.setAdapter(new ImagesSlidingAdapter(getContext(), list));
            if(list.size()>1){
                holder.indicator.setVisibility(View.VISIBLE);
            }else{
                holder.indicator.setVisibility(View.GONE);
            }
        }
        catch (NullPointerException e){
            e.getCause();
        }
        holder.mPager.setCurrentItem(0);
        holder.indicator.setupWithViewPager(holder.mPager, true);

        holder.previousImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.mPager != null && holder.mPager.getChildCount()>1){
                    holder.mPager.setCurrentItem(holder.mPager.getCurrentItem()-1);
                }
            }
        });
        holder.nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.mPager != null && holder.mPager.getChildCount()>1){
                    holder.mPager.setCurrentItem(holder.mPager.getCurrentItem()+1);
                }
            }
        });
    }




    private static class ViewHolder {
        public TextView name, age, distanceText;
        public ImageView superLikeImage;
        public LinearLayout infoLayout,recentlyActiveView;
        RelativeLayout bottomLayout;
        ChipGroup passions;
        private TabLayout indicator;
        private CustomViewPager mPager;
        LinearLayout previousImage,nextImage;

        public ViewHolder(View view) {
            infoLayout =view.findViewById(R.id.info_layout);
            recentlyActiveView =view.findViewById(R.id.recentlyActiveView);
            superLikeImage =view.findViewById(R.id.superlike_image);
            name=view.findViewById(R.id.username);
            age=view.findViewById(R.id.age);

            distanceText =view.findViewById(R.id.distance_txt);
            passions = view.findViewById(R.id.chipGroup);
            bottomLayout = view.findViewById(R.id.bottomLayout);

            indicator = view.findViewById(R.id.indicator);
            mPager = view.findViewById(R.id.image_slider_pager);
            previousImage = view.findViewById(R.id.previousImage);
            nextImage = view.findViewById(R.id.nextImage);
        }



        public void bind(final int pos, final NearbyUserModel item, final AdapterClickListener listener) {
            infoLayout.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(pos,item,v);
                }
            });
        }
    }


    private int checkDate(String date){
        //database date in millisecond
        try {
            Date d = Variables.newdf.parse(date);
            Date today = Calendar.getInstance().getTime();
            String todayDate = Variables.newdf.format(today);
            Date formatedDate = Variables.newdf.parse(todayDate);
            long diff = formatedDate.getTime() - d.getTime();
            return (int) TimeUnit.MILLISECONDS.toHours(diff);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
