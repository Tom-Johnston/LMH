package com.johnston.lmhapp.MealMenus;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Johnston on 10/09/2014.
 */
public class MenuFragment extends Fragment {
    Boolean starting;
    int startat = -1;
    private View view;
    private Context context;
    private ArrayList<String> meals = new ArrayList<String>();
    private MenuListAdapter adapter;
    MenuItem actionRefresh;

    public void downloadNewMenu() {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setVisibility(View.INVISIBLE);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.PM1);
        pb.setVisibility(View.VISIBLE);
        new DownloadNewMenuAsync().execute(context, false, handler);
    }

    void startMenu() {
        MainActivity main = (MainActivity) getActivity();
        main.startRefresh(5);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.PM1);
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
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        MenuRecyclerAdapter formalRecyclerAdapter = new MenuRecyclerAdapter(meals);
        recyclerView.setAdapter(formalRecyclerAdapter);

        MainActivity main = (MainActivity) getActivity();
        main.stopRefresh(5);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.PM1);
        recyclerView.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        context = this.getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.menu_layout, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if (meals.size() == 0) {
            startMenu();
        } else {
            MenuRecyclerAdapter formalRecyclerAdapter = new MenuRecyclerAdapter(meals);
            recyclerView.setAdapter(formalRecyclerAdapter);
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
                meals = (ArrayList<String>) message.obj;
                if (starting) {
                    if (meals.size()==0) {
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