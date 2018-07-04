package com.artamonov.appanalyzer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;

public class MainDetailTabFragment extends Fragment {


    public static MainDetailTabFragment newInstance() {
        MainDetailTabFragment fragment = new MainDetailTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_detail_tab, container, false);
        TextView tvAppSource = view.findViewById(R.id.detailed_app_source);
        TextView tvFirstInstallTime = view.findViewById(R.id.first_install_time);
        TextView tvLastRunTime = view.findViewById(R.id.detailed_app_last_run_time);
        TextView tvLastUpdatedTime = view.findViewById(R.id.detailed_app_last_update_time);

        tvAppSource.setText(MainActivity.appList.getAppSource());
        tvFirstInstallTime.setText(MainActivity.appList.getFirstInstallTime());
        tvLastRunTime.setText(MainActivity.appList.getLastRunTime());
        tvLastUpdatedTime.setText(MainActivity.appList.getLastUpdateTime());

        return view;
    }
}
