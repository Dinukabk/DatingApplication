package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.qboxus.binder.Models.UserModel;
import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.Functions;

import static com.qboxus.binder.ActivitiesFragments.Accounts.LoginPhoneF.RESOLVE_HINT;

public class EmailPhoneF extends Fragment {

    View view;
    protected TabLayout tabLayout;
    TextView signupTxt;
    protected ViewPager pager;
    ViewPagerAdapter adapter;
    LoginPhoneF fragment1;
    String fromWhere;
    UserModel userModel = new UserModel();

    public EmailPhoneF(String fromWhere) {
        this.fromWhere = fromWhere;
    }

    public EmailPhoneF() {
        //empty constructor
    }

    public static EmailPhoneF newInstance(String fromWhere) {
        EmailPhoneF fragment = new EmailPhoneF(fromWhere);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        init();

        view.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
            }
        });

        signupTxt = view.findViewById(R.id.signup_txt);
        Bundle bundle = getArguments();
        if(bundle!=null){
            userModel = (UserModel) bundle.getSerializable("user_model");
        }

        return view;
    }

    private void init() {
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        pager = view.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);

        adapter = new ViewPagerAdapter(getResources(), getChildFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

        fragment1 = (LoginPhoneF) adapter.getItem(pager.getCurrentItem());

        setupTabIcons();
    }

    private void setupTabIcons() {
        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.item_tabs_signup, null);
        TextView text_history = view1.findViewById(R.id.text_history);
        text_history.setText(getActivity().getString(R.string.phone));
        tabLayout.getTabAt(0).setCustomView(view1);

        View view2 = LayoutInflater.from(getActivity()).inflate(R.layout.item_tabs_signup, null);
        TextView text_history1 = view2.findViewById(R.id.text_history);
        text_history1.setText(getActivity().getString(R.string.email));
        tabLayout.getTabAt(1).setCustomView(view2);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                TextView text_history = v.findViewById(R.id.text_history);

                switch (tab.getPosition()) {
                    case 0:
                        text_history.setTextColor(getResources().getColor(R.color.pink_color));
                        break;

                    case 1:
                        text_history.setTextColor(getResources().getColor(R.color.pink_color));
                        break;
                }
                tab.setCustomView(v);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                TextView text_history = v.findViewById(R.id.text_history);

                switch (tab.getPosition()) {
                    case 0:
                        text_history.setTextColor(getResources().getColor(R.color.black));
                        break;
                    case 1:
                        text_history.setTextColor(getResources().getColor(R.color.black));
                        break;
                }
                tab.setCustomView(v);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT){
            if (fragment1 instanceof LoginPhoneF) {
                fragment1.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final Resources resources;
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        @SuppressLint("WrongConstant")
        public ViewPagerAdapter(final Resources resources, FragmentManager fm) {
            super(fm, 0);
            this.resources = resources;
        }

        @Override
        public Fragment getItem(int position) {
            final Fragment result;
            switch (position) {
                case 0:
                    result =LoginPhoneF.newInstance(userModel, fromWhere);
                    break;
                case 1:
                    result =LoginEmailF.newInstance(userModel, fromWhere);
                    break;
                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        /**
         * Get the Fragment by position
         *
         * @param position tab position of the fragment
         * @return
         */
        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

    }

}