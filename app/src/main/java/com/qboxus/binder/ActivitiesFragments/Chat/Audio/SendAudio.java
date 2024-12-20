package com.qboxus.binder.ActivitiesFragments.Chat.Audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;

import com.qboxus.binder.ActivitiesFragments.Chat.ChatF;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by qboxus on 12/5/2018.
 */

// this class will send a voice message to other user
public class SendAudio {


    DatabaseReference rootref;
    String senderid = "";
    String receiverid = "";
    String receiverName ="";
    String receiverPic ="null";
    Context context;
    boolean ismatchExits;

    private static String mFileName = null;
    private MediaRecorder mRecorder = null;

    private DatabaseReference adduserToInbox;

    EditText messageField;


    public SendAudio(Context context, EditText messageField,
                     DatabaseReference rootref, DatabaseReference adduserToInbox
            , String senderid, String receiverid, String receiverName, String receiverPic, boolean ismatchExits) {

        this.context=context;
        this.messageField = messageField;
        this.rootref=rootref;
        this.adduserToInbox = adduserToInbox;
        this.senderid=senderid;
        this.receiverid =receiverid;
        this.receiverName = receiverName;
        this.receiverPic = receiverPic;
        this.ismatchExits = ismatchExits;
        mFileName = context.getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.mp3";

    }



    // this function will start the recrding
    private void startRecording() {

        if(mRecorder!=null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder=null;
        }

        mRecorder = new MediaRecorder();

        if(mRecorder!=null)
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        if(mRecorder!=null)
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        if(mRecorder!=null)
            mRecorder.setOutputFile(mFileName);

        if(mRecorder!=null)
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            if(mRecorder!=null)
                mRecorder.prepare();
        } catch (IOException e) {
            Log.e("resp", "prepare() failed");
        }
        if(mRecorder!=null)
            mRecorder.start();


    }



    // stop the recording and then call a function to upload the audio file into database

    public void stopRecording() {
        stopTimerWithoutRecoder();
        if(mRecorder!=null ) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder=null;
            runbeep("stop");
            uploadAudio();
        }
    }

    Handler handler;
    Runnable runnable;
    public void runbeep(final String action){

        // within 700 milisecond the timer will be start
        handler=new Handler();
        if(action.equals("start")) {
            messageField.setText("00:00");
            runnable = new Runnable() {
                @Override
                public void run() {
                    startTimer();
                }
            };

            handler.postDelayed(runnable, 700);
        }


        // this will run a beep sound
        final MediaPlayer beep = MediaPlayer.create(context, R.raw.sound);
        beep.setVolume(100,100);
        beep.start();
        beep.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                beep.release();

                // if our action is start a recording the recording will start
                if(action.equals("start"))
                    startRecording();
            }
        });
    }


    // this method will upload audio  in firebase database
    public void uploadAudio(){

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        StorageReference reference= FirebaseStorage.getInstance().getReference();
        DatabaseReference dref=rootref.child("chat").child(senderid+"-"+ receiverid).push();
        final String key=dref.getKey();
        ChatF.uploadingAudioId =key;
        final String current_user_ref = "chat" + "/" + senderid + "-" + receiverid;
        final String chat_user_ref = "chat" + "/" + receiverid + "-" + senderid;

        HashMap my_dummi_pic_map = new HashMap<>();
        my_dummi_pic_map.put("receiver_id", receiverid);
        my_dummi_pic_map.put("sender_id", senderid);
        my_dummi_pic_map.put("chat_id",key);
        my_dummi_pic_map.put("text", "");
        my_dummi_pic_map.put("type","audio");
        my_dummi_pic_map.put("pic_url", receiverPic);
        my_dummi_pic_map.put("status", "0");
        my_dummi_pic_map.put("time", "");
        my_dummi_pic_map.put("sender_name", Functions.getSharedPreference(context).getString(Variables.fName,""));
        my_dummi_pic_map.put("timestamp", formattedDate);

        HashMap dummy_push = new HashMap<>();
        dummy_push.put(current_user_ref + "/" + key, my_dummi_pic_map);
        rootref.updateChildren(dummy_push);



        Uri uri = Uri.fromFile(new File(mFileName));

        final StorageReference audio_path= reference.child("Audio").child(key+".mp3");
        audio_path.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                audio_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                ChatF.uploadingAudioId ="none";

                HashMap message_user_map = new HashMap<>();
                message_user_map.put("receiver_id", receiverid);
                message_user_map.put("sender_id", senderid);
                message_user_map.put("chat_id",key);
                message_user_map.put("text", "");
                message_user_map.put("type","audio");
                message_user_map.put("pic_url",uri.toString());
                message_user_map.put("status", "0");
                message_user_map.put("time", "");
                message_user_map.put("sender_name", Functions.getSharedPreference(context).getString(Variables.fName,""));
                message_user_map.put("timestamp", formattedDate);

                HashMap user_map = new HashMap<>();

                user_map.put(current_user_ref + "/" + key, message_user_map);
                user_map.put(chat_user_ref + "/" + key, message_user_map);

                rootref.updateChildren(user_map, (databaseError, databaseReference) -> {
                    String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + receiverid;
                    String inbox_receiver_ref = "Inbox" + "/" + receiverid + "/" + senderid;

                    HashMap sendermap=new HashMap<>();
                    sendermap.put("rid",senderid);
                    sendermap.put("name",Functions.getSharedPreference(context).getString(Variables.fName,""));
                    sendermap.put("pic", Functions.getSharedPreference(context).getString(Variables.uPic,""));
                    sendermap.put("msg","Send an Audio");
                    sendermap.put("status","0");
                    sendermap.put("date",formattedDate);
                    sendermap.put("timestamp", -1*System.currentTimeMillis());

                    HashMap receivermap=new HashMap<>();
                    receivermap.put("rid", receiverid);
                    receivermap.put("name", receiverName);
                    receivermap.put("pic", receiverPic);
                    receivermap.put("msg","Send an Audio");
                    receivermap.put("status","1");
                    receivermap.put("date",formattedDate);
                    receivermap.put("timestamp", -1*System.currentTimeMillis());

                    HashMap both_user_map = new HashMap<>();
                    both_user_map.put(inbox_sender_ref , receivermap);
                    both_user_map.put(inbox_receiver_ref , sendermap);

                    adduserToInbox.updateChildren(both_user_map).addOnCompleteListener((OnCompleteListener<Void>) task -> {

                    });
                });
                    }
                });
            }
        });

    }



    CountDownTimer timer;
    public void startTimer() {

        timer=new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long time=30000-millisUntilFinished;

                int min = (int) (time/1000) / 60;
                int sec = (int) (time/1000) % 60;
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", min, sec);
                messageField.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                stopRecording();
                messageField.setText(null);
            }
        };

        timer.start();
    }


    // this  will stop timer when audio file have some data
    public void stopTimer(){

        if(mRecorder!=null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder=null;
        }

        if(handler!=null && runnable!=null) {
            handler.removeCallbacks(runnable);
        }

        messageField.setText(null);

        if(timer!=null){
            timer.cancel();
            messageField.setText(null);
        }

    }

    // this will stop timer when audio file does not have data
    public void stopTimerWithoutRecoder(){

        if(handler!=null && runnable!=null) {
            handler.removeCallbacks(runnable);
        }
        messageField.setText(null);

        if(timer!=null){
            timer.cancel();
            messageField.setText(null);
        }

    }

}

