package com.artamonov.appanalyzer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.artamonov.appanalyzer.MainActivity.TAG;

public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.ViewHolder> {

    private static ItemClickListener listener;
    private final List<AppList> appList;
    private Context context;

    public AppRecyclerViewAdapter(Context context, List<AppList> appList, ItemClickListener itemClickListener) {
        this.context = context;
        this.appList = appList;
        this.listener = itemClickListener;
        Log.i(TAG, " Adapter is constructed ");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list, parent, false);
        Log.i(TAG, " onCreateViewHolder invoked ");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppRecyclerViewAdapter.ViewHolder holder, int position) {
        Log.i(TAG, " onBindViewHolder " + position);
        AppList applicationList =  appList.get(position);
        Log.i(TAG, " onBindViewHolder: appName: " + applicationList.getName());
        holder.appName.setText(applicationList.getName());
        holder.appLogo.setImageDrawable(applicationList.getIcon());

    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView appLogo;
        private final TextView appName;

        ViewHolder(View itemView) {
            super(itemView);
            appLogo = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            listener.onItemClick(position);

        }
    }
}
