package com.artamonov.appanalyzer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PermissionsTabFragment extends Fragment {

    public static PermissionsTabFragment newInstance() {
        PermissionsTabFragment fragment = new PermissionsTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permissions_tab, container, false);
        TextView tvDangerousPermissions = view.findViewById(R.id.dangerous_permissions);
        TextView tvPermissionGroups = view.findViewById(R.id.permission_groups);
        TextView tvPermissionGroupsAmount = view.findViewById(R.id.permission_groups_amount);
        tvDangerousPermissions.setText(MainActivity.appList.getDangerousPermissionsAmount());
        tvPermissionGroups.setText(MainActivity.appList.getPermissionGroups());
        tvPermissionGroupsAmount.setText(MainActivity.appList.getPermissionGroupsAmount());
        return view;
    }
}
