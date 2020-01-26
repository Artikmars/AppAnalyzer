package com.artamonov.appanalyzer.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ApplicationsWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ApplicationsWidgetFactory(getApplicationContext());
    }

}