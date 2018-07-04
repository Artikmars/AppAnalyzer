package com.artamonov.appanalyzer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.artamonov.appanalyzer.MainActivity.TAG;

public class PermissionsTabFragment extends Fragment {

 /*   @BindView(R.id.requested_app_permissions)
    TextView tvRequestedPermissions;
    @BindView(R.id.granted_app_permissions)
    TextView tvGrantedPermissions;*/

    public static PermissionsTabFragment newInstance() {
        PermissionsTabFragment fragment = new PermissionsTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permissions_tab, container, false);
        return view;
    }
}
