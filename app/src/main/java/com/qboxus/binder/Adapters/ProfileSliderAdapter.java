package com.qboxus.binder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.binder.Models.ProfileSliderModel;
import com.qboxus.binder.R;

import java.util.List;

public class ProfileSliderAdapter extends RecyclerView.Adapter<ProfileSliderAdapter.CustomViewHolder> {

    public Context context;
    private OnItemClickListener listener;
    private List<ProfileSliderModel> dataList;


    // make the on item click listener interface and this interface is implement in Chat inbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(int position, ProfileSliderModel item, View view);
    }


    public ProfileSliderAdapter(Context context, List<ProfileSliderModel> dataList, OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_slider_profile, viewGroup,false);
        return new CustomViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        ProfileSliderModel item = dataList.get(i);

        if(dataList.size() == 1){
            holder.cardView.setLayoutParams(new LinearLayout.LayoutParams((int) context.getResources().getDimension(R.dimen._280sdp), (int) context.getResources().getDimension(R.dimen._110sdp)));
        }else {
            if(i != 0){
                holder.cardView.setLayoutParams(new LinearLayout.LayoutParams((int) context.getResources().getDimension(R.dimen._255sdp), (int) context.getResources().getDimension(R.dimen._110sdp)));
            }
            if(i+1 == dataList.size()){
                holder.cardView.setLayoutParams(new LinearLayout.LayoutParams((int) context.getResources().getDimension(R.dimen._270sdp), (int) context.getResources().getDimension(R.dimen._110sdp)));
            }
        }

        holder.headerTv.setText(item.getHeaderTv());
        holder.descriptionTv.setText(item.getDescriptionTv());
        holder.paymentTv.setText(item.getPriceTv());

        holder.bind(i, item, listener);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView headerTv,descriptionTv,paymentTv;
        CardView cardView;

        public CustomViewHolder(View view) {
            super(view);

            headerTv = view.findViewById(R.id.headerTv);
            descriptionTv = view.findViewById(R.id.descriptionTv);
            paymentTv = view.findViewById(R.id.paymentTv);

            cardView = view.findViewById(R.id.cardView);
        }

        public void bind(final int postion, final ProfileSliderModel item, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> {
                listener.onItemClick(postion, item, v);
            });
        }
    }
}
