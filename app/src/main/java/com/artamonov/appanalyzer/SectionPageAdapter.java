package com.artamonov.appanalyzer;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class SectionPageAdapter extends FragmentPagerAdapter {

    private List<String> fragmentTitlesList = new ArrayList<>();

    public SectionPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setPageTitles(String titles) {
        fragmentTitlesList.add(titles);
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitlesList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MainDetailTabFragment.newInstance();
            case 1:
                return GooglePlayTabFragment.newInstance();
            case 2:
                return PermissionsTabFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {

        Log.i(MainActivity.TAG, "SectionPageAdapter - getCount: " + fragmentTitlesList.size());
        return fragmentTitlesList.size();
    }
}
