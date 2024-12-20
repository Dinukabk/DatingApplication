package com.qboxus.binder.ActivitiesFragments.Chat;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.R;
import java.util.ArrayList;

/**
 * Created by qboxus on 3/20/2018.
 */

public class GifAdapter extends RecyclerView.Adapter<GifAdapter.CustomViewHolder >{
    public Context context;
    ArrayList<String> gifList = new ArrayList<>();
    private GifAdapter.OnItemClickListener listener;

public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public GifAdapter(Context context, ArrayList<String> urllist, GifAdapter.OnItemClickListener listener) {
        this.context = context;
        this.gifList =urllist;
        this.listener = listener;

    }


    @Override
    public GifAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gif_layout,viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
       return gifList.size();
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView gif_image;

        public CustomViewHolder(View view) {
            super(view);
            gif_image=view.findViewById(R.id.gif_image);
        }

        public void bind(final String item, final GifAdapter.OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });


        }

    }

    @Override
    public void onBindViewHolder(final GifAdapter.CustomViewHolder holder, final int i) {
        holder.bind(gifList.get(i),listener);

        String gifURL=Variables.gifFirstpart + gifList.get(i)+ Variables.gifSecondpart;
        holder.gif_image.setController(Functions.frescoImageLoad(gifURL,R.drawable.image_placeholder,holder.gif_image,true));


        Log.d("resp", Variables.gifFirstpart + gifList.get(i)+ Variables.gifSecondpart);

   }


}