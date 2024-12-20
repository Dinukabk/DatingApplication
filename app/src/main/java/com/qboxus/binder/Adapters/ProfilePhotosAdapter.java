package com.qboxus.binder.Adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.ViewHolders.PhotosviewHolder;
import com.qboxus.binder.Models.UserMultiplePhotoModel;
import com.qboxus.binder.R;

import java.util.List;

/**
 * Created by qboxus on 7/16/2018.
 */

public class ProfilePhotosAdapter extends  RecyclerView.Adapter<PhotosviewHolder> {

    Context context;
    List<UserMultiplePhotoModel> datalist;
    boolean isFromRegister;

    private ProfilePhotosAdapter.OnItemClickListener listener;



    public interface OnItemClickListener {
        void onItemClick(UserMultiplePhotoModel item, int postion, View view);


    }


    public ProfilePhotosAdapter(Context context, List<UserMultiplePhotoModel> arrayList, boolean isFromRegister, ProfilePhotosAdapter.OnItemClickListener listener)  {
        this.context=context;
        datalist =arrayList;
        this.isFromRegister = isFromRegister;
        this.listener=listener;
    }

    @Override
    public PhotosviewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        return new PhotosviewHolder(LayoutInflater.from(context).inflate(R.layout.item_edit_profile_layout, viewGroup, false));
    }


    @Override
    public int getItemCount() {
        return datalist.size();
    }


    @Override
    public void onBindViewHolder(final PhotosviewHolder hol, final int position) {

        PhotosviewHolder holder = (PhotosviewHolder) hol;

        UserMultiplePhotoModel model = datalist.get(position);

        if(position == 0 && model.getImage() != null){
            if(isFromRegister){
                holder.crossButton.setVisibility(View.GONE);
                holder.addButton.setVisibility(View.VISIBLE);
                holder.addButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_edit_pink));
                Uri uri= Uri.parse("data:image/png;base64,"+model.getImage());
                holder.image.setController(Functions.frescoImageLoad(uri,R.drawable.d_round_gray_bkg,holder.image,false));
            }else if(!isFromRegister){
                holder.crossButton.setVisibility(View.GONE);
                holder.addButton.setVisibility(View.GONE);
                holder.image.setController(Functions.frescoImageLoad(model.getImage(),R.drawable.d_round_gray_bkg,holder.image,false));
            }
        }

        if(model.getImage() == null || model.getImage().equals("")){
            holder.addButton.setVisibility(View.VISIBLE);
            holder.addButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_round_add_btn));
            holder.crossButton.setVisibility(View.GONE);

            holder.image.setController(Functions.frescoImageLoad(ContextCompat.getDrawable(holder.view.getContext(),R.drawable.d_round_gray_bkg),holder.image,false));


        }else if(position != 0 && !model.getImage().equals("")){
            holder.crossButton.setVisibility(View.VISIBLE);
            holder.addButton.setVisibility(View.GONE);
            if(isFromRegister){
                Uri uri= Uri.parse("data:image/png;base64,"+model.getImage());
                holder.image.setController(Functions.frescoImageLoad(uri,R.drawable.d_round_gray_bkg,holder.image,false));
            }else if(!isFromRegister){
                holder.crossButton.setVisibility(View.VISIBLE);
                holder.addButton.setVisibility(View.GONE);
                holder.image.setController(Functions.frescoImageLoad(model.getImage(),R.drawable.d_round_gray_bkg,holder.image,false));


            }
        }

        holder.bind(model,position,listener);
     }







}

