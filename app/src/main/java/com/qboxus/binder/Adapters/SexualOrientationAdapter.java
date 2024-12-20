package com.qboxus.binder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.binder.interfaces.AdapterClickListener;
import com.qboxus.binder.Models.SexualOrientationModel;
import com.qboxus.binder.R;

import java.util.ArrayList;

public class SexualOrientationAdapter extends RecyclerView.Adapter<SexualOrientationAdapter.CustomViewHolder > {
    public Context context;
    ArrayList<SexualOrientationModel> list = new ArrayList<>();

    AdapterClickListener adapterClickListener;

    public SexualOrientationAdapter(Context context, ArrayList<SexualOrientationModel> list, AdapterClickListener adapterClickListener) {
        this.context = context;
        this.list=list;
        this.adapterClickListener=adapterClickListener;
    }

    @Override
    public SexualOrientationAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sexual_orientation,viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
       return list.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView sexualOrientation;
        ImageView icCheck;

        public CustomViewHolder(View view) {
            super(view);
            sexualOrientation=view.findViewById(R.id.tv);
            icCheck = view.findViewById(R.id.check);
        }

        public void bind(final int pos, final SexualOrientationModel item, final AdapterClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(pos,item,v);
                }
            });
        }

    }

    @Override
    public void onBindViewHolder(final SexualOrientationAdapter.CustomViewHolder holder, final int i) {
        final SexualOrientationModel item=list.get(i);

        holder.sexualOrientation.setText(item.getSexualOrientation());

        if(item.getCheck()){
            holder.icCheck.setVisibility(View.VISIBLE);
        }else if(!item.getCheck()){
            holder.icCheck.setVisibility(View.GONE);
        }

        holder.bind(i,item,adapterClickListener);
   }

}