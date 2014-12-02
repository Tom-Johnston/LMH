package com.johnston.lmhapp.MealMenus;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Johnston on 10/09/2014.
 */
public class MenuFragment extends Fragment {
    int firstVisibleItem;
    Boolean starting;
    int startat = -1;
    private View view;
    private Context context;
    private ArrayList<String> meals = new ArrayList<String>();
    private MenuListAdapter adapter;
    MenuItem actionRefresh;

    public void downloadNewMenu() {
        ListView lv = (ListView) view.findViewById(R.id.mealList);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.PM1);
        lv.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        new DownloadNewMenuAsync().execute(context, false, handler);
    }

    void startMenu() {
        MainActivity main = (MainActivity) getActivity();
        main.startRefresh(5);
        ListView lv = (ListView) view.findViewById(R.id.mealList);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.PM1);
        lv.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        File file = new File(context.getFilesDir(), "Menu.txt");
        if (!file.exists()) {
//                No menu. Get a new menu();
            new DownloadNewMenuAsync().execute(context, false, handler);
        } else {
//          Check if we should get a fresh menu. Note we will display the menu even if it is old.
            starting = true;
            new MenuAsync().execute(context, handler);

        }
    }

    public void showMenu() {
//        0 for old
        MainActivity main = (MainActivity) getActivity();
        main.stopRefresh(5);
        ListView lv = (ListView) view.findViewById(R.id.mealList);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.PM1);
        lv.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        Context context = this.getActivity().getBaseContext();
        adapter = new MenuListAdapter(context, R.layout.listview, meals);
        lv.setAdapter(adapter);
        lv.setSelection(startat);


    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        context = this.getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.menu_layout, container, false);
        if (meals.size() == 0) {
            Handler startHandler = new Handler();
            Runnable startRunnable = new Runnable() {
                @Override
                public void run() {
                    startMenu();
                }
            };
            startHandler.post(startRunnable);

        } else {
            ListView lv = (ListView) view.findViewById(R.id.mealList);
            lv.setVisibility(View.VISIBLE);
            Context context = this.getActivity().getBaseContext();
            adapter = new MenuListAdapter(context, R.layout.listview, meals);
            lv.setAdapter(adapter);
            lv.setSelection(startat);
        }

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        actionRefresh = menu.findItem(R.id.action_refresh);
        actionRefresh.setEnabled(true);
        actionRefresh.setVisible(true);
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 0) {
                Object[] objects = (Object[]) message.obj;
                startat = (Integer) objects[1];
                meals = (ArrayList<String>) objects[0];
                if (starting) {
                    String lastEntry = meals.get(meals.size() - 1);
                    if (lastEntry.equals("00") || lastEntry.equals("10")) {
                        new DownloadNewMenuAsync().execute(context, false, handler);
                    } else {
                        showMenu();
                    }
                } else {
                    showMenu();
                }
            } else {
                starting = false;
                new MenuAsync().execute(context, handler);
            }
        }
    };


}