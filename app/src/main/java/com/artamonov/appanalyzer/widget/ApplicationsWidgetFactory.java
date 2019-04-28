package com.artamonov.appanalyzer.widget;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;
import com.artamonov.appanalyzer.MainActivity;
import com.artamonov.appanalyzer.R;
import com.artamonov.appanalyzer.data.database.AppList;
import java.util.List;

class ApplicationsWidgetFactory implements RemoteViewsFactory {

    private final Context context;
    private List<AppList> widgetAppsList;

    ApplicationsWidgetFactory(Context context) {
        this.context = context;
        widgetAppsList = MainActivity.applicationsWidgetListUnsorted;
    }

    @Override
    public void onCreate() {}

    @Override
    public int getCount() {
        return widgetAppsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rView =
                new RemoteViews(context.getPackageName(), R.layout.applicaions_widget_item);
        rView.setTextViewText(
                R.id.tvItemApp,
                widgetAppsList.get(position).getName()
                        + ": "
                        + widgetAppsList.get(position).getOfflineTrust());
        return rView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {}

    @Override
    public void onDestroy() {}
}
