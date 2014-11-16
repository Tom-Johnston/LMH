package com.johnston.lmhapp.MealMenus;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Tom on 08/09/2014.
 */
public class WidgetListViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
//        int appWidgetId = intent.getIntExtra(
//                AppWidgetManager.EXTRA_APPWIDGET_ID,
//                AppWidgetManager.INVALID_APPWIDGET_ID);

        return (new WidgetListProvider(this.getApplicationContext(), intent));
    }
}
