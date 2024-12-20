package com.qboxus.binder.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }

    public void addFrag(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    public void removeFrag(int index) {
        mFragmentList.remove(index);
    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }

    // Added method to retrieve fragments by position
    public Fragment getFragment(int position) {
        if (position >= 0 && position < mFragmentList.size()) {
            return mFragmentList.get(position);
        }
        throw new IndexOutOfBoundsException("Invalid fragment index: " + position);
    }
}
