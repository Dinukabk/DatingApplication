package com.qboxus.binder.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.Models.MatchModel;
import com.qboxus.binder.interfaces.AdapterClickListener;
import com.qboxus.binder.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by qboxus on 3/20/2018.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.CustomViewHolder> {
    public Context context;
    ArrayList<MatchModel> matchDataList = new ArrayList<>();
    ArrayList<MatchModel> inbox_dataList_filter = new ArrayList<>();

    AdapterClickListener adapterClickListener;
    Integer today_day=0;

    public MatchesAdapter(Context context, ArrayList<MatchModel> user_dataList, AdapterClickListener adapterClickListener) {
        this.context = context;
        this.matchDataList =user_dataList;
        this.inbox_dataList_filter=user_dataList;
        this.adapterClickListener=adapterClickListener;

        // get the today as a integer number to make the dicision the chat date is today or yesterday
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public MatchesAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_matchs_layout,viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
       return inbox_dataList_filter.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        SimpleDraweeView user_image;

        public CustomViewHolder(View view) {
            super(view);
            username=view.findViewById(R.id.username);
            user_image=view.findViewById(R.id.user_image);
        }

        public void bind(final int pos, final MatchModel item, final AdapterClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(pos,item,v);
                }
            });

        }

    }


    @Override
    public void onBindViewHolder(final MatchesAdapter.CustomViewHolder holder, final int i) {
        final MatchModel item=inbox_dataList_filter.get(i);
        holder.username.setText(item.getUsername());

        holder.user_image.setController(Functions.frescoImageLoad(item.getPicture(),R.drawable.ic_user_icon,holder.user_image,false));

        holder.bind(i,item,adapterClickListener);
   }

    // that function will filter the result
    public void filterList(ArrayList<MatchModel> filterList){
        inbox_dataList_filter = filterList;
        notifyDataSetChanged();
    }

}