package com.qboxus.binder.ActivitiesFragments.Chat.Audio;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qboxus.binder.ActivitiesFragments.Accounts.SexualOrientationF;
import com.qboxus.binder.R;

import nl.changer.audiowife.AudioWife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayAudioF extends Fragment {

    View view;
    Context context;
    ImageButton playBtn, pauseBtn;
    SeekBar seekBar;
    TextView durationTime, totalTime;

    AudioWife audioWife;


    ImageButton closeBtn;

    public PlayAudioF() {
        // Required empty public constructor
    }

    public static PlayAudioF newInstance() {
        PlayAudioF fragment = new PlayAudioF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_play_audio, container, false);

        context=getContext();

        closeBtn =view.findViewById(R.id.close_btn);

        playBtn =(ImageButton) view.findViewById(R.id.play_btn);
        pauseBtn =(ImageButton)  view.findViewById(R.id.pause_btn);

        seekBar=(SeekBar) view.findViewById(R.id.seek_bar);

        durationTime =(TextView)view.findViewById(R.id.duration_time);
        totalTime =(TextView)view.findViewById(R.id.total_time);

        String filepath=getArguments().getString("path");

        Uri uri= Uri.parse(filepath);


        // this is the third partty library that will show get the audio player view
        // and run the audio file and handle the player view itself

        audioWife=AudioWife.getInstance();
        audioWife.init(context, uri)
                .setPlayView(playBtn)
                .setPauseView(pauseBtn)
                .setSeekBar(seekBar)
                .setRuntimeView(durationTime)
                .setTotalTimeView(totalTime);

        audioWife.play();


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }



    @Override
    public void onDetach() {
        super.onDetach();
        audioWife.pause();
        audioWife.release();
    }



}
