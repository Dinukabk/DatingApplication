package com.qboxus.binder.Adapters;

import android.content.Context;
import android.graphics.Typeface;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.qboxus.binder.Models.InboxModel;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.interfaces.AdapterClickListener;
import com.qboxus.binder.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by qboxus on 3/20/2018.
 */

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.CustomViewHolder> implements Filterable{
    public Context context;
    ArrayList<InboxModel> inboxDataList = new ArrayList<>();
    ArrayList<InboxModel> inboxDataListFilter = new ArrayList<>();

    AdapterClickListener adapterClickListener;

    Integer todayDay =0;

    public InboxAdapter(Context context, ArrayList<InboxModel> user_dataList, AdapterClickListener adapterClickListener) {
        this.context = context;
        this.inboxDataList =user_dataList;
        this.inboxDataListFilter =user_dataList;

        // get the today as a integer number to make the dicision the chat date is today or yesterday
        Calendar cal = Calendar.getInstance();
        todayDay = cal.get(Calendar.DAY_OF_MONTH);

        this.adapterClickListener=adapterClickListener;

    }

    @Override
    public InboxAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_inbox_list,viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
       return inboxDataListFilter.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView username, lastMessage, dateCreated;
        SimpleDraweeView userImage;

        public CustomViewHolder(View view) {
            super(view);
            userImage =itemView.findViewById(R.id.user_image);
            username=itemView.findViewById(R.id.username);
            lastMessage =itemView.findViewById(R.id.message);
            dateCreated =itemView.findViewById(R.id.datetxt);
        }

        public void bind(final int pos, final InboxModel item, final AdapterClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(pos,item,v);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongItemClick(pos,item,v);
                    return true;
                }
            });

        }

    }

    @Override
    public void onBindViewHolder(final InboxAdapter.CustomViewHolder holder, final int i) {

        final InboxModel item= inboxDataListFilter.get(i);
        holder.username.setText(item.getName());
        holder.lastMessage.setText(item.getMessage());
        holder.dateCreated.setText(changeDate(item.getTimestamp()));

        holder.userImage.setController(Functions.frescoImageLoad(item.getPicture(),R.drawable.ic_user_icon,holder.userImage,false));


        // check the status like if the message is seen by the receiver or not
        String status = "" + item.getStatus();
        Functions.printLog( "Adapter Value: "+item.getStatus());
        if (status.equals("0")) {
            holder.lastMessage.setTypeface(null, Typeface.BOLD);
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.black));
        } else if(status.equals("1")){
            holder.lastMessage.setTypeface(null, Typeface.NORMAL);
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.dark_gray));
        }


        holder.bind(i,item,adapterClickListener);

   }


    // this method will cahnge the date to  "today", "yesterday" or date
    public String changeDate(String date){
        //current date in millisecond
        long currenttime = System.currentTimeMillis();

        //database date in millisecond
        long databasedate = 0;
        Date d = null;
        try {
            d = Variables.df.parse(date);
            databasedate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference=currenttime-databasedate;
        if(difference<86400000){
            int chatday=Integer.parseInt(date.substring(0,2));
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            if(todayDay ==chatday)
                return sdf.format(d);
            else if((todayDay -chatday)==1)
                return "Yesterday";
        }
        else if(difference<172800000){
            int chatday=Integer.parseInt(date.substring(0,2));
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            if((todayDay -chatday)==1)
                return "Yesterday";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");

        if(d!=null)
        return sdf.format(d);
        else
            return "";

    }


    // that function will filter the result
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    inboxDataListFilter = inboxDataList;
                } else {
                    ArrayList<InboxModel> filteredList = new ArrayList<>();
                    for (InboxModel row : inboxDataList) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    inboxDataListFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = inboxDataListFilter;
                return filterResults;

            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                inboxDataListFilter = (ArrayList<InboxModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void filterList(ArrayList<InboxModel> filterList){
        inboxDataListFilter = filterList;
        notifyDataSetChanged();
    }

}