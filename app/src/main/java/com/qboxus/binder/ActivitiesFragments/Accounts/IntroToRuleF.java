package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;
import com.qboxus.binder.R;
import com.qboxus.binder.Constants;
import com.qboxus.binder.SimpleClasses.Functions;

import java.util.ArrayList;
import java.util.List;


public class IntroToRuleF extends Fragment {

    View view;
    Context context;
    RelativeLayout tabAgree;
    TextView tvTitle,tvDataSafely;
    List<Link> links = new ArrayList<>();

    public IntroToRuleF() {
    }

    public static IntroToRuleF newInstance() {
        IntroToRuleF fragment = new IntroToRuleF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_intro_to_rule, container, false);
        context = getActivity();



        view.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(context.getString(R.string.welcome_to)+" "+context.getString(R.string.app_name)+".");
        tvDataSafely = view.findViewById(R.id.tvDataSafely);
        SetupLinkClickable();


        tabAgree = view.findViewById(R.id.tabAgree);
        tabAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(requireActivity());
                SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()+1);
                SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                        SignupA.pager.getCurrentItem() + 1,
                        SignupA.pager.getOffscreenPageLimit()));
            }
        });

        return view;
    }


    private void SetupLinkClickable() {

        Link link = new Link(view.getContext().getString(R.string.date_safely));
        link.setTextColor(ContextCompat.getColor(view.getContext(),R.color.coloraccent));
        link.setTextColorOfHighlightedLink(ContextCompat.getColor(view.getContext(),R.color.coloraccent));
        link.setUnderlined(true);
        link.setBold(false);
        link.setHighlightAlpha(.20f);
        link.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String clickedText) {
                openWebUrl(view.getContext().getString(R.string.date_safely),Constants.DATE_SAFELY_URL);
            }
        });

        links.add(link);
        CharSequence sequence = LinkBuilder.from(view.getContext(), tvDataSafely.getText().toString())
                .addLinks(links)
                .build();
        tvDataSafely.setText(sequence);
        tvDataSafely.setMovementMethod(TouchableMovementMethod.getInstance());
    }


    public void openWebUrl(String title, String url) {
        Intent intent=new Intent(view.getContext(), WebviewA.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

}