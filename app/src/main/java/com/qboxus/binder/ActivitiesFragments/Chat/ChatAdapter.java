package com.qboxus.binder.ActivitiesFragments.Chat;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.R;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by qboxus on 4/3/2018.
 */

class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatModel> mDataSet;
    String myID;
    private static final int MY_CHAT = 1;
    private static final int FRIEND_CHAT = 2;
    private static final int MYCHAT_IMAGE = 3;
    private static final int OTHER_CHAT_IMAGE = 4;
    private static final int MY_GIF_IMAGE = 5;
    private static final int OTHER_GIF_IMAGE = 6;
    private static final int ALERT_MESSAGE = 7;

    private static final int MY_AUDIO_MESSAGE = 8;
    private static final int OTHER_AUDIO_MESSAGE = 9;


    Context context;
    Integer today_day = 0;

    private ChatAdapter.OnItemClickListener listener;
    private ChatAdapter.OnLongClickListener longListener;

    public interface OnItemClickListener {
        void onItemClick(ChatModel item, View view);
    }

    public interface OnLongClickListener {
        void onLongClick(ChatModel item, View view);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param dataSet Message list
     *                Device id
     */

    ChatAdapter(List<ChatModel> dataSet, String id, Context context, ChatAdapter.OnItemClickListener listener, ChatAdapter.OnLongClickListener longListener) {
        mDataSet = dataSet;
        this.myID = id;
        this.context = context;
        this.listener = listener;
        this.longListener = longListener;
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);

    }


    // this is the all types of view that is used in the chat
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View v = null;
        switch (viewtype) {
            // we have 4 type of layout in chat activity text chat of my and other and also
            // image layout of my and other
            case MY_CHAT:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_my, viewGroup, false);
                Chatviewholder mychatHolder = new Chatviewholder(v);
                return mychatHolder;
            case FRIEND_CHAT:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_other, viewGroup, false);
                Chatviewholder friendchatHolder = new Chatviewholder(v);
                return friendchatHolder;
            case MYCHAT_IMAGE:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_my, viewGroup, false);
                Chatimageviewholder mychatimageHolder = new Chatimageviewholder(v);
                return mychatimageHolder;
            case OTHER_CHAT_IMAGE:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_other, viewGroup, false);
                Chatimageviewholder otherchatimageHolder = new Chatimageviewholder(v);
                return otherchatimageHolder;

            case MY_AUDIO_MESSAGE:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_audio_my, viewGroup, false);
                Chataudioviewholder chataudioviewholder = new Chataudioviewholder(v);
                return chataudioviewholder;

            case OTHER_AUDIO_MESSAGE:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_audio_other, viewGroup, false);
                Chataudioviewholder other = new Chataudioviewholder(v);
                return other;

            case MY_GIF_IMAGE:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_gif_my, viewGroup, false);
                Chatimageviewholder mychatgigHolder = new Chatimageviewholder(v);
                return mychatgigHolder;
            case OTHER_GIF_IMAGE:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_gif_other, viewGroup, false);
                Chatimageviewholder otherchatgifHolder = new Chatimageviewholder(v);
                return otherchatgifHolder;
            case ALERT_MESSAGE:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_alert, viewGroup, false);
                Alertviewholder alertviewholder = new Alertviewholder(v);
                return alertviewholder;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatModel chat = mDataSet.get(position);
        if (chat.getType().equals("text")) {
            Chatviewholder chatviewholder = (Chatviewholder) holder;
            // check if the message is from sender or receiver
            if (chat.getSender_id().equals(myID)) {
                if (chat.getStatus().equals("1")) {
                    chatviewholder.message_seen.setText("Seen at " + ChangeDate_to_time(chat.getTime()));
                }else
                    chatviewholder.message_seen.setText("Sent");

            } else {
                chatviewholder.message_seen.setText("");
            }
            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                    chatviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatviewholder.datetxt.setVisibility(View.VISIBLE);
                    chatviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }
                chatviewholder.message.setText(chat.getText());
            } else {
                chatviewholder.datetxt.setVisibility(View.VISIBLE);
                chatviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                chatviewholder.message.setText(chat.getText());
            }

            chatviewholder.bind(chat, longListener);

        }
        else
            if (chat.getType().equals("image")) {
            final Chatimageviewholder chatimageholder = (Chatimageviewholder) holder;
            // check if the message is from sender or receiver
            if (chat.getSender_id().equals(myID)) {
                if (chat.getStatus().equals("1"))
                    chatimageholder.message_seen.setText("Seen at " + ChangeDate_to_time(chat.getTime()));
                else
                    chatimageholder.message_seen.setText("Sent");

            } else {
                chatimageholder.message_seen.setText("");
            }
            if (chat.getPic_url().equals("none")) {
                if (ChatF.uploadingImageId.equals(chat.getChat_id())) {
                    chatimageholder.pBar.setVisibility(View.VISIBLE);
                    chatimageholder.message_seen.setText("");
                } else {
                    chatimageholder.pBar.setVisibility(View.GONE);
                    chatimageholder.notSendMessageIcon.setVisibility(View.VISIBLE);
                    chatimageholder.message_seen.setText("Not delivered. ");
                }
            } else {
                chatimageholder.notSendMessageIcon.setVisibility(View.GONE);
                chatimageholder.pBar.setVisibility(View.GONE);
            }

            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                    chatimageholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatimageholder.datetxt.setVisibility(View.VISIBLE);
                    chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }
                chatimageholder.chatimage.setController(Functions.frescoImageLoad(chat.getPic_url(),R.drawable.image_placeholder,chatimageholder.chatimage,false));
            } else {
                chatimageholder.datetxt.setVisibility(View.VISIBLE);
                chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                chatimageholder.chatimage.setController(Functions.frescoImageLoad(chat.getPic_url(),R.drawable.image_placeholder,chatimageholder.chatimage,false));
            }

            chatimageholder.bind(mDataSet.get(position), listener, longListener);
        }
            else
                if (chat.getType().equals("audio")) {
            final Chataudioviewholder chataudioviewholder = (Chataudioviewholder) holder;
            // check if the message is from sender or receiver
            if (chat.getSender_id().equals(myID)) {
                if (chat.getStatus().equals("1"))
                    chataudioviewholder.message_seen.setText("Seen at " + ChangeDate_to_time(chat.getTime()));
                else
                    chataudioviewholder.message_seen.setText("Sent");

            } else {
                chataudioviewholder.message_seen.setText("");
            }
            if (chat.getPic_url().equals("none")) {
                if (ChatF.uploadingAudioId.equals(chat.getChat_id())) {
                    chataudioviewholder.p_bar.setVisibility(View.VISIBLE);
                    chataudioviewholder.message_seen.setText("");
                } else {
                    chataudioviewholder.p_bar.setVisibility(View.GONE);
                    chataudioviewholder.not_send_message_icon.setVisibility(View.VISIBLE);
                    chataudioviewholder.message_seen.setText("Not delivered. ");
                }
            } else {
                chataudioviewholder.not_send_message_icon.setVisibility(View.GONE);
                chataudioviewholder.p_bar.setVisibility(View.GONE);
            }

            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                    chataudioviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    chataudioviewholder.datetxt.setVisibility(View.VISIBLE);
                    chataudioviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }
            } else {
                chataudioviewholder.datetxt.setVisibility(View.VISIBLE);
                chataudioviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));

            }

            if (ChatF.uploadingAudioId.equals("none")) {

            }

            chataudioviewholder.seekBar.setEnabled(false);

            File fullpath = new File(Environment.getExternalStorageDirectory() + "/" + context.getResources().getString(R.string.app_name) + "/" + chat.getChat_id() + ".mp3");
            if (fullpath.exists()) {
                chataudioviewholder.total_time.setText(getfileduration(Uri.parse(fullpath.getAbsolutePath())));

            } else {
                chataudioviewholder.total_time.setText(null);
            }


            chataudioviewholder.bind(mDataSet.get(position), listener, longListener);

        }
                else
                    if (chat.getType().equals("gif")) {
            final Chatimageviewholder chatimageholder = (Chatimageviewholder) holder;
            // check if the message is from sender or receiver
            if (chat.getSender_id().equals(myID)) {
                if (chat.getStatus().equals("1"))
                    chatimageholder.message_seen.setText("Seen at " + ChangeDate_to_time(chat.getTime()));
                else
                    chatimageholder.message_seen.setText("Sent");

            } else {
                chatimageholder.message_seen.setText("");
            }
            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                    chatimageholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatimageholder.datetxt.setVisibility(View.VISIBLE);
                    chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }

                String gifURL=Variables.gifFirstpartChat + chat.getPic_url() + Variables.gifSecondpartChat;
                chatimageholder.chatimage.setController(Functions.frescoImageLoad(gifURL,R.drawable.image_placeholder,chatimageholder.chatimage,true));

            } else {
                chatimageholder.datetxt.setVisibility(View.VISIBLE);
                chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));

                String gifURL=Variables.gifFirstpartChat + chat.getPic_url() + Variables.gifSecondpartChat;
                chatimageholder.chatimage.setController(Functions.frescoImageLoad(gifURL,R.drawable.image_placeholder,chatimageholder.chatimage,true));

            }

            chatimageholder.bind(mDataSet.get(position), listener, longListener);
        } else if (chat.getType().equals("delete")) {
            Alertviewholder alertviewholder = (Alertviewholder) holder;
            alertviewholder.message.setTextColor(context.getResources().getColor(R.color.delete_message_text));
            alertviewholder.message.setBackground(context.getResources().getDrawable(R.drawable.d_round_gray_background_2));

            alertviewholder.message.setText("This message is deleted by " + chat.getSender_name());

            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(11, 13).equals(chat.getTimestamp().substring(11, 13))) {
                    alertviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    alertviewholder.datetxt.setVisibility(View.VISIBLE);
                    alertviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }

            } else {
                alertviewholder.datetxt.setVisibility(View.VISIBLE);
                alertviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));

            }

        }


    }

    @Override
    public int getItemViewType(int position) {
        // get the type it view ( given message is from sender or receiver)
        if (mDataSet.get(position).getType().equals("text")) {
            if (mDataSet.get(position).getSender_id().equals(myID)) {
                return MY_CHAT;
            }
            return FRIEND_CHAT;
        } else if (mDataSet.get(position).getType().equals("image")) {
            if (mDataSet.get(position).getSender_id().equals(myID)) {
                return MYCHAT_IMAGE;
            }

            return OTHER_CHAT_IMAGE;

        } else if (mDataSet.get(position).getType().equals("audio")) {
            if (mDataSet.get(position).getSender_id().equals(myID)) {
                return MY_AUDIO_MESSAGE;
            }
            return OTHER_AUDIO_MESSAGE;
        } else if (mDataSet.get(position).getType().equals("gif")) {
            if (mDataSet.get(position).getSender_id().equals(myID)) {
                return MY_GIF_IMAGE;
            }

            return OTHER_GIF_IMAGE;
        } else {
            return ALERT_MESSAGE;
        }
    }

    /**
     * Inner Class for a recycler view
     */

    // this is the all the viewholder in which first is for the text
    class Chatviewholder extends RecyclerView.ViewHolder {
        TextView message, datetxt, message_seen;
        View view;

        public Chatviewholder(View itemView) {
            super(itemView);
            view = itemView;
            this.message = view.findViewById(R.id.msgtxt);
            this.datetxt = view.findViewById(R.id.datetxt);
            message_seen = view.findViewById(R.id.message_seen);
        }

        public void bind(final ChatModel item, final ChatAdapter.OnLongClickListener long_listener) {
            message.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long_listener.onLongClick(item, v);
                    return false;
                }
            });
        }
    }

    // second is for the chat image
    class Chatimageviewholder extends RecyclerView.ViewHolder {
        SimpleDraweeView chatimage;
        TextView datetxt, message_seen;
        ProgressBar pBar;
        ImageView notSendMessageIcon;
        View view;

        public Chatimageviewholder(View itemView) {
            super(itemView);
            view = itemView;
            this.chatimage = view.findViewById(R.id.chatimage);
            this.datetxt = view.findViewById(R.id.datetxt);
            message_seen = view.findViewById(R.id.message_seen);
            notSendMessageIcon = view.findViewById(R.id.not_send_messsage);
            pBar = view.findViewById(R.id.p_bar);
        }

        public void bind(final ChatModel item, final ChatAdapter.OnItemClickListener listener, final ChatAdapter.OnLongClickListener long_listener) {

            chatimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, v);
                }
            });

            chatimage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long_listener.onLongClick(item, v);
                    return false;
                }
            });
        }

    }

    // third is for the audio message view that show in the chat
    class Chataudioviewholder extends RecyclerView.ViewHolder {
        TextView datetxt, message_seen;
        ProgressBar p_bar;
        ImageView not_send_message_icon;
        ImageView play_btn;
        SeekBar seekBar;
        TextView total_time;
        LinearLayout audio_bubble;


        View view;

        public Chataudioviewholder(View itemView) {
            super(itemView);
            view = itemView;
            audio_bubble = view.findViewById(R.id.audio_bubble);
            datetxt = view.findViewById(R.id.datetxt);
            message_seen = view.findViewById(R.id.message_seen);
            not_send_message_icon = view.findViewById(R.id.not_send_messsage);
            p_bar = view.findViewById(R.id.p_bar);
            this.play_btn = view.findViewById(R.id.play_btn);
            this.seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
            this.total_time = (TextView) view.findViewById(R.id.total_time);

        }

        public void bind(final ChatModel item, final ChatAdapter.OnItemClickListener listener, final ChatAdapter.OnLongClickListener long_listener) {


            audio_bubble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, v);
                }
            });

            audio_bubble.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long_listener.onLongClick(item, v);
                    return false;
                }
            });


        }


    }

    // forth is for the alert type view
    class Alertviewholder extends RecyclerView.ViewHolder {
        TextView message, datetxt;
        View view;

        public Alertviewholder(View itemView) {
            super(itemView);
            view = itemView;
            this.message = view.findViewById(R.id.message);
            this.datetxt = view.findViewById(R.id.datetxt);
        }

    }

    // change the date into (today ,yesterday and date)
    public String ChangeDate(String date) {

        try {
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
            long difference = currenttime - databasedate;
            if (difference < 86400000) {
                int chatday = Integer.parseInt(date.substring(0, 2));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                if (today_day == chatday)
                    return "Today " + sdf.format(d);
                else if ((today_day - chatday) == 1)
                    return "Yesterday " + sdf.format(d);
            } else if (difference < 172800000) {
                int chatday = Integer.parseInt(date.substring(0, 2));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                if ((today_day - chatday) == 1)
                    return "Yesterday " + sdf.format(d);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy hh:mm a");
            return sdf.format(d);
        } catch (Exception e) {
            return date;
        }

    }

    // change the date into (today ,yesterday and date)
    public String ChangeDate_to_time(String date) {
        try {
            Date d = null;
            try {
                d = Variables.df2.parse(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }


            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            return sdf.format(d);
        } catch (Exception e) {
            return date;
        }

    }

    // get the audio file duration that is store in our directory
    public String getfileduration(Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            long second = (file_duration / 1000) % 60;
            long minute = (file_duration / (1000 * 60)) % 60;

            return String.format("%02d:%02d", minute, second);
        } catch (Exception e) {

        }
        return null;
    }

}
