package com.artamonov.appanalyzer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.artamonov.appanalyzer.R;
import com.artamonov.appanalyzer.SearchDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class SearchAppsRecyclerViewAdapter
        extends RecyclerView.Adapter<SearchAppsRecyclerViewAdapter.ViewHolder> {

    private final List<String> searchApps;
    private final List<String> searchLinks;
    private final Context context;
    // private static ItemClickListener listener;

    public SearchAppsRecyclerViewAdapter(
            List<String> searchApps, ArrayList<String> arrayLinks, Context context) {
        this.searchApps = searchApps;
        this.searchLinks = arrayLinks;
        this.context = context;
        //  listener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.search_app_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final String appItem = searchApps.get(position);
        viewHolder.appName.setText(appItem);

        viewHolder.linearLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String appLink = searchLinks.get(position);
                        Intent intent = new Intent(context, SearchDetailActivity.class);
                        intent.putExtra("appLink", appLink);
                        context.startActivity(intent);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return searchApps.size();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView appName;
        private final LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.search_app_name);
            linearLayout = itemView.findViewById(R.id.linear_layout_apps);
            // itemView.setOnClickListener(this);
        }
    }
}
