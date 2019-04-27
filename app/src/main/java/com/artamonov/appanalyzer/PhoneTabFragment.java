package com.artamonov.appanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artamonov.appanalyzer.adapter.PhoneRecyclerViewAdapter;

public class PhoneTabFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_tab, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);

        RecyclerView rvMain = view.findViewById(R.id.rvMain);
        TextView tvAppsAmount = view.findViewById(R.id.applications_installed);
        TextView tvHighOfflineScoreApps = view.findViewById(R.id.apps_with_high_rating);
        TextView tvMiddleOfflineScoreApps = view.findViewById(R.id.apps_with_middle_rating);
        TextView tvLowOfflineScoreApps = view.findViewById(R.id.apps_with_low_rating);

        rvMain.setLayoutManager(new LinearLayoutManager(view.getContext()));
        PhoneRecyclerViewAdapter phoneRecyclerViewAdapter = new PhoneRecyclerViewAdapter();
        rvMain.setAdapter(phoneRecyclerViewAdapter);

//
//        Intent intent = getIntent();
//        String appsAmount = String.valueOf(intent.getIntExtra("applications_amount", 0));
//        String highOfflineScoreApps = String.valueOf(intent.getIntExtra("high_offline_score_apps", 0));
//        String middleOfflineScoreApps = String.valueOf(intent.getIntExtra("middle_offline_score_apps", 0));
//        String lowOfflineScoreApps = String.valueOf(intent.getIntExtra("low_offline_score_apps", 0));

//        tvAppsAmount.setText(appsAmount);
//        tvHighOfflineScoreApps.setText(highOfflineScoreApps);
//        tvMiddleOfflineScoreApps.setText(middleOfflineScoreApps);
//        tvLowOfflineScoreApps.setText(lowOfflineScoreApps);

        return view;
    }

    public static PhoneTabFragment newInstance() {
        PhoneTabFragment fragment= new PhoneTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;









    }
}
