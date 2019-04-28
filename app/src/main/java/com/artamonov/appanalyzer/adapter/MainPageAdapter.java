package com.artamonov.appanalyzer.adapter;

import android.util.Log;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.artamonov.appanalyzer.MainActivity;
import com.artamonov.appanalyzer.PhoneTabFragment;
import java.util.ArrayList;
import java.util.List;

public class MainPageAdapter extends FragmentPagerAdapter {

    private List<String> fragmentTitlesList = new ArrayList<>();

    public MainPageAdapter(FragmentManager fm) {
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
                return PhoneTabFragment.newInstance();
            case 1:
                return PhoneTabFragment.newInstance();
            case 2:
                return PhoneTabFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {

        Log.i(MainActivity.TAG, "SectionPageAdapter - getCount: " + fragmentTitlesList.size());
        return fragmentTitlesList.size();
    }
}
