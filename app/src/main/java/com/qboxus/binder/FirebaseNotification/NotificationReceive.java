package com.qboxus.binder.FirebaseNotification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.FragmentCallback;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.ActivitiesFragments.Chat.ChatF;
import com.qboxus.binder.MainMenu.MainMenuA;
import com.qboxus.binder.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by qboxus on 5/22/2018.
 */

public class NotificationReceive extends FirebaseMessagingService {

    String userId;
    String pic;
    String title;
    String message;
    String senderId;
    String senderName;
    String receiverId;
    String type;
    SharedPreferences sharedPreferences;

    Handler handler=new Handler();
    Runnable runnable;
    Snackbar snackbar;


    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            sharedPreferences=getSharedPreferences(Variables.prefName, MODE_PRIVATE);
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("body");
            pic = remoteMessage.getData().get("icon");
            senderId = remoteMessage.getData().get("sender_id");
            senderName = remoteMessage.getData().get("sender_name");
            receiverId = remoteMessage.getData().get("receiver_id");
            type= remoteMessage.getData().get("type");

            // it is the notification from user to user chat
            if(!ChatF.senderDdForCheckNotification.equals(senderId) ){
                // if the user does not open the chat
                // if all the notification is on
                sendNotification sendNotification = new sendNotification(this);
                sendNotification.execute(pic);
            }
        }
    }



    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sharedPreferences= getSharedPreferences(Variables.prefName,MODE_PRIVATE);
        userId = Functions.getSharedPreference(this).getString(Variables.uid,"");

        if(s==null){

        }else if(s.equals("null")){

        }
        else if(s.equals("")){

        }
        else if(s.length()<6){

        }
        else {
            sharedPreferences.edit().putString(Variables.deviceToken, s).commit();
        }
    }


    private class sendNotification extends AsyncTask<String, Void, Bitmap> {
        Context ctx;
        public sendNotification(Context context) {
            super();
            this.ctx = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // in notification first we will get the image of the user and then we will show the notification to user
            // in onPostExecute
            InputStream in;
            try {
                URL url = new URL(params[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @SuppressLint("WrongConstant")
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if(MainMenuA.mainMenuA !=null){
                if(snackbar != null){
                    snackbar.getView().setVisibility(View.INVISIBLE);
                    snackbar.dismiss();
                }

                if(handler!=null && runnable!=null) {
                    handler.removeCallbacks(runnable);
                }

                View layout = MainMenuA.mainMenuA.getLayoutInflater().inflate(R.layout.item_layout_custom_notification,null);
                TextView titleText = layout.findViewById(R.id.username);
                TextView messageText = layout.findViewById(R.id.message);
                ImageView imageView = layout.findViewById(R.id.user_image);
                titleText.setText("New Message");
                messageText.setText(message);
                imageView.setImageBitmap(result);


                snackbar = Snackbar.make(MainMenuA.mainMenuA
                        .findViewById(R.id.MainMenuFragment), "", Snackbar.LENGTH_LONG);

                Snackbar.SnackbarLayout snackbarLayout= (Snackbar.SnackbarLayout) snackbar.getView();
                TextView textView = snackbarLayout.findViewById(R.id.snackbar_text);
                textView.setVisibility(View.INVISIBLE);

                final ViewGroup.LayoutParams params = snackbar.getView().getLayoutParams();
                if (params instanceof CoordinatorLayout.LayoutParams) {
                    ((CoordinatorLayout.LayoutParams) params).gravity = Gravity.TOP;
                } else {
                    ((FrameLayout.LayoutParams) params).gravity = Gravity.TOP;
                }

                snackbarLayout.setPadding(0,0,0,0);
                snackbarLayout.addView(layout, 0);

                snackbar.getView().setVisibility(View.INVISIBLE);
                snackbar.addCallback(new Snackbar.Callback(){
                    @Override
                    public void onShown(Snackbar sb) {
                        super.onShown(sb);
                        snackbar.getView().setVisibility(View.VISIBLE);
                    }

                });

                runnable= () -> snackbar.getView().setVisibility(View.INVISIBLE);
                handler.postDelayed(runnable, 2750);

                snackbar.setDuration(Snackbar.LENGTH_LONG);
                snackbar.show();
                layout.setOnClickListener(v -> {
                    snackbar.dismiss();
                    snackbar.getView().setVisibility(View.INVISIBLE);

                    chatFragment(Functions.getSharedPreference(ctx)
                            .getString(Variables.uid,""),
                            senderId,
                            senderName,false);
                });
            }
        }
    }

    public void chatFragment(String senderId, String receiverId, String name, boolean isMatchExits){
        ChatF chat_activity =ChatF.newInstance((FragmentCallback) bundle -> {

        });
        FragmentTransaction transaction = MainMenuA.mainMenuA.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle args = new Bundle();
        args.putString("Sender_Id", senderId);
        args.putString("Receiver_Id", receiverId);
        args.putString("picture", pic);
        args.putString("name", name);
        args.putBoolean("is_match_exits", isMatchExits);
        chat_activity.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
    }

}
