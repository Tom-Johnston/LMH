package com.johnston.lmhapp.MealMenus;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.johnston.lmhapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Tom on 08/09/2014.
 */
class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {
    private final List<String> Options = new ArrayList<>();
    private Context context = null;
//    private int appWidgetId;

    public WidgetListProvider(Context icontext, Intent intent) {
        String incoming = intent.getStringExtra("Options");
        context = icontext;
//        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                AppWidgetManager.INVALID_APPWIDGET_ID);
        StringTokenizer stringTokenizer = new StringTokenizer(incoming, "¬");
        while (stringTokenizer.hasMoreTokens()) {
            Options.add(stringTokenizer.nextToken());
        }
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return Options.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_listview_item);
//        LauncherActivity.ListItem listItem = (LauncherActivity.ListItem)listItemList.get(position);
        String option = Options.get(position);
        remoteView.setTextViewText(R.id.ListViewBody, option);
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}