package com.qboxus.binder.Adapters;

import android.content.Context;
import android.os.Parcelable;

import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qboxus.binder.Models.LoginSliderModel;
import com.qboxus.binder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qboxus on 3/8/2018.
 */

// this class is belong to show the login slider

public class SlidingAdapter extends PagerAdapter {

    private List<LoginSliderModel> data_list;
    private LayoutInflater inflater;

    public SlidingAdapter(Context context, List<LoginSliderModel> list) {
        inflater = LayoutInflater.from(context);
        this.data_list = list;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return data_list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.item_slider_layout, view, false);

        if (imageLayout != null) {
            final ImageView imageView = imageLayout.findViewById(R.id.image);
            if(data_list.get(position).image != null){
                imageView.setImageResource(data_list.get(position).image);
            }
            TextView titleTxt = imageLayout.findViewById(R.id.title);
            titleTxt.setText(data_list.get(position).name);

            view.addView(imageLayout, 0);
        }

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}