package com.artamonov.appanalyzer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.artamonov.appanalyzer.data.database.AppList;

import java.util.List;

public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.ViewHolder> {

    private static ItemClickListener listener;
    private List<AppList> appList;
    private Context context;

    public AppRecyclerViewAdapter(Context context, List<AppList> appList, ItemClickListener itemClickListener) {
        this.context = context;
        this.appList = appList;
        listener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppRecyclerViewAdapter.ViewHolder holder, int position) {
        AppList applicationList =  appList.get(position);
        holder.appName.setText(applicationList.getName());
        holder.appLogo.setImageDrawable(applicationList.getIcon());

    }

    void setAppList(List<AppList> appList) {
        this.appList = appList;
        notifyDataSetChanged();
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
