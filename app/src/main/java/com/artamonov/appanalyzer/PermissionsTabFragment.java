package com.artamonov.appanalyzer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        TextView tvDangerousPermissions = view.findViewById(R.id.dangerous_permissions);
        TextView tvPermissionGroups = view.findViewById(R.id.permission_groups);
        TextView tvPermissionGroupsAmount = view.findViewById(R.id.permission_groups_amount);
        tvDangerousPermissions.setText(MainActivity.appList.getDangerousPermissionsAmount());
        tvPermissionGroups.setText(MainActivity.appList.getPermissionGroups());
        tvPermissionGroupsAmount.setText(MainActivity.appList.getPermissionGroupsAmount());
        return view;
    }
}
