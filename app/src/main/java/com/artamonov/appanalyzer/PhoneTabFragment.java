package com.artamonov.appanalyzer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class PhoneTabFragment extends Fragment {

    public static PhoneTabFragment newInstance() {
        PhoneTabFragment fragment= new PhoneTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;


    }
}
