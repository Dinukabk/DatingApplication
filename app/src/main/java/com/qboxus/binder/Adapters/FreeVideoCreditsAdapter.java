package com.qboxus.binder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.binder.ActivitiesFragments.inapppurchases.FreeCreditsVideoF;
import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.AdapterClickListener;
import com.qboxus.binder.Models.FreeVideoCreditModel;
import com.qboxus.binder.SimpleClasses.Variables;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qboxus on 3/20/2018.
 */

public class FreeVideoCreditsAdapter extends RecyclerView.Adapter<FreeVideoCreditsAdapter.CustomViewHolder> {
    public Context context;
    List<FreeVideoCreditModel> list = new ArrayList<>();

    AdapterClickListener adapterClickListener;

    Integer viewNumberOfVideos;

    public FreeVideoCreditsAdapter(Context context, List<FreeVideoCreditModel> list, AdapterClickListener adapterClickListener) {
        this.context = context;
        this.list = list;
        this.adapterClickListener=adapterClickListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_free_video_credits,viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
       return list.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parentView,videoView;
        TextView creditsTv, videoCount;
        ProgressBar pb;

        public CustomViewHolder(View view) {
            super(view);
            parentView = itemView.findViewById(R.id.parentView);
            videoView = itemView.findViewById(R.id.videoView);
            creditsTv = itemView.findViewById(R.id.creditsTv);
            videoCount = itemView.findViewById(R.id.videoCountTv);

            pb = itemView.findViewById(R.id.pb);
        }

        public void bind(final int pos, final FreeVideoCreditModel item, final AdapterClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(pos,item,v);
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        final FreeVideoCreditModel item = list.get(i);

        holder.videoView.setVisibility(View.VISIBLE);
        holder.pb.setVisibility(View.VISIBLE);
        holder.creditsTv.setText(item.getCredits()+" "+context.getString(R.string.credits));

        viewNumberOfVideos = Functions.getSharedPreference(context)
                .getInt(item.getPackageId()+Functions
                        .getSharedPreference(context).getString(Variables.uid,""), 0);

        holder.videoCount.setText(viewNumberOfVideos+"/"+item.getNoOfVideos()+context.getString(R.string.videos));

        holder.pb.setProgress((int) Functions.calculateSegmentProgress(
                (double) viewNumberOfVideos, Double.parseDouble(item.getNoOfVideos())));

        if(!FreeCreditsVideoF.selectedId.equals("") && FreeCreditsVideoF.selectedId.equals(item.getPackageId())){
            holder.parentView.setBackground(ContextCompat.getDrawable(context, R.drawable.d_round_pink_border_pink_bkg));
        }else {
            holder.parentView.setBackground(ContextCompat.getDrawable(context, R.drawable.d_round_gray_border_white_bkg));
        }

        holder.bind(i,item,adapterClickListener);
   }

}