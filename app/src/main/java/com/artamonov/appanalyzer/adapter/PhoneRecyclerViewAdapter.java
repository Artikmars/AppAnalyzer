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


public class PhoneRecyclerViewAdapter extends RecyclerView.Adapter<PhoneRecyclerViewAdapter.ViewHolder> {

    private final List<String> searchApps = null;
    private final List<String> searchLinks = null;
    private final Context context = null;
    // private static ItemClickListener listener;

    public PhoneRecyclerViewAdapter() {
//        this.searchApps = searchApps;
//        this.searchLinks = arrayLinks;
//        this.context = context;
        //  listener = itemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.phone_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
      //  final String appItem = searchApps.get(position);

        switch (position) {
            case 0:   viewHolder.mainItemName.setText("Applications");break;
            case 1:  viewHolder.mainItemName.setText("Applications Categories");break;
            case 2:  viewHolder.mainItemName.setText("Average Permissions Amount per Category");break;
            case 3:  viewHolder.mainItemName.setText("Potentially Dangerous Applications");break;
        }

//        viewHolder.linearLayout.setOnClickListener(view -> {
//            String appLink = searchLinks.get(position);
//            Intent intent = new Intent(context, SearchDetailActivity.class);
//            intent.putExtra("appLink", appLink);
//            context.startActivity(intent);
//        });

    }

    @Override
    public int getItemCount() {
      //  return searchApps.size();
        return 4;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mainItemName;
        private final TextView mainItemAmount;

         ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainItemName = itemView.findViewById(R.id.main_item_name);
            mainItemAmount = itemView.findViewById(R.id.main_item_amount);
            // itemView.setOnClickListener(this);
        }
    }
}
