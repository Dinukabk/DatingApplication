package com.qboxus.binder.Adapters;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import com.qboxus.binder.Models.UserMultiplePhotoModel;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.Functions;

import java.util.List;


/**
 * Created by qboxus on 3/8/2018.
 */

public class ImagesSlidingAdapter extends PagerAdapter {

    private LayoutInflater inflater;
    private List<UserMultiplePhotoModel> arrayList;

    public ImagesSlidingAdapter(Context context, List<UserMultiplePhotoModel> image_list) {
        inflater = LayoutInflater.from(context);
        arrayList=image_list;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.item_sliding_image_layout, view, false);
        if(imageLayout!=null) {
            final SimpleDraweeView imageView =  imageLayout.findViewById(R.id.image);
            UserMultiplePhotoModel model = arrayList.get(position);

            imageView.setController(Functions.frescoImageLoad(model.getImage(),
                    R.drawable.ic_user_icon,imageView,false));

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