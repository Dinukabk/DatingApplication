package com.qboxus.binder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.binder.ActivitiesFragments.inapppurchases.PurchaseCoinsF;
import com.qboxus.binder.R;
import com.qboxus.binder.Constants;
import com.qboxus.binder.interfaces.AdapterClickListener;
import com.qboxus.binder.Models.CreditModel;
import java.util.List;

/**
 * Created by qboxus on 3/20/2018.
 */
public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.CustomViewHolder> {
    public Context context;
    List<CreditModel> list;

    AdapterClickListener adapterClickListener;

    public CreditsAdapter(Context context, List<CreditModel> list, AdapterClickListener adapterClickListener) {
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
        TextView creditsTv, creditsPriceTv;
        ImageView coinsImage;
        ProgressBar pb;

        public CustomViewHolder(View view) {
            super(view);
            parentView = itemView.findViewById(R.id.parentView);
            videoView = itemView.findViewById(R.id.videoView);
            creditsTv = itemView.findViewById(R.id.creditsTv);
            creditsPriceTv = itemView.findViewById(R.id.creditsPriceTv);

            coinsImage = itemView.findViewById(R.id.creditIv);

            pb = itemView.findViewById(R.id.pb);
        }

        public void bind(final int pos, final CreditModel item, final AdapterClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(pos,item,v);
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        final CreditModel item = list.get(i);

        holder.videoView.setVisibility(View.GONE);
        holder.pb.setVisibility(View.GONE);
        holder.creditsPriceTv.setVisibility(View.VISIBLE);

        if(Constants.GET_COINS_FROM_VIDEOS && i == 0){
            holder.creditsTv.setText(item.getCoinsNumber());
        }else {
            holder.creditsTv.setText(item.getCoinsNumber()+" "+context.getString(R.string.credits));
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 0);
        holder.creditsTv.setLayoutParams(params);
        holder.creditsPriceTv.setText(item.getCoinsAmount());
        holder.creditsPriceTv.setLayoutParams(params);
        holder.coinsImage.setImageResource(item.getCoinsImage());

        if(!PurchaseCoinsF.selectedCoins.equals("") && PurchaseCoinsF.selectedCoins.equals(item.getCoinsNumber())){
            holder.parentView.setBackground(ContextCompat.getDrawable(context, R.drawable.d_round_pink_border_pink_bkg));
        }else {
            holder.parentView.setBackground(ContextCompat.getDrawable(context, R.drawable.d_round_gray_border_white_bkg));
        }

        holder.bind(i,item,adapterClickListener);

   }

}