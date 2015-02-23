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
import android.widget.TextView;

import com.johnston.lmhapp.LaundryView.LaundryViewAsync;
import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.PermissionAsync;
import com.johnston.lmhapp.PermissionFailedDialog;
import com.johnston.lmhapp.R;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Johnston on 10/09/2014.
 */
public class MenuFragment extends Fragment {
    Boolean starting;
    MenuItem actionRefresh;
    Boolean finished = false;
    Boolean refreshing = false;
    private View view;
    private Context context;
    private ArrayList<String> meals = new ArrayList<>();

    public void checkForPermission(){
        refreshing = true;
        MainActivity main = (MainActivity) getActivity();
        main.startRefresh(5);
        showProgressBar();
        Handler permissionHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 0) {
//                Success!
                    new DownloadNewMenuAsync().execute(context, false, handler);
                } else if (message.what == 1) {
//                Failure
                    handler.obtainMessage(-1).sendToTarget();
                    PermissionFailedDialog newFragment = PermissionFailedDialog.newInstance((String) message.obj);
                    newFragment.show(getFragmentManager(), "PERMISSION DENIED");
                }else if(message.what==2){
//                    Do nothing. We are downloading a new menu anyway.

                } else {
//                Something has gone wrong checking.
                    handler.obtainMessage(-1).sendToTarget();
                }
            }
        };
        new PermissionAsync().execute(getActivity().getApplicationContext(), permissionHandler,null,"MenuFragment");
    }

    public void startMenu() {
        MainActivity main = (MainActivity) getActivity();
        main.startRefresh(5);
        showProgressBar();

        File file = new File(context.getFilesDir(), "Menu.txt");
        if (!file.exists()) {
//                No menu. Get a new menu();
        } else {
            starting = true;
            new MenuAsync().execute(context, handler);

        }
    }

    public void showMenu() {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        MenuRecyclerAdapter menuRecyclerAdapter = new MenuRecyclerAdapter(meals);
        recyclerView.setAdapter(menuRecyclerAdapter);

        MainActivity main = (MainActivity) getActivity();
        if (main != null) {
            main.stopRefresh(5);
        }
        if (meals.size() > 0) {
            showCards();
        } else {
            showMessage(getResources().getString(R.string.nothingToShow));
        }

        finished = true;
        refreshing = false;
    }

    public void showCards(){
        (view.findViewById(R.id.my_recycler_view)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
    }
    public void showProgressBar(){
        (view.findViewById(R.id.my_recycler_view)).setVisibility(View.GONE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
    }
    public void showMessage(String message){
        (view.findViewById(R.id.my_recycler_view)).setVisibility(View.GONE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.VISIBLE);
        ((TextView)view.findViewById(R.id.nothingToShow)).setText(message);

    }


    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        context = this.getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.menu_layout, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if (refreshing) {
            MainActivity main = (MainActivity) getActivity();
            main.startRefresh(5);
            MainActivity.Status = (android.widget.TextView) view.findViewById(R.id.Status);
            showProgressBar();
        } else if (!finished) {
            startMenu();
        } else {
            showMenu();
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
            if (message.what == -1) {
                refreshing = false;
                finished = false;
                MainActivity main = (MainActivity) getActivity();
                if (main != null) {
                    main.stopRefresh(5);
                }

                if (view == null) {
                    return;
                }
                showMessage(getResources().getString(R.string.somethingWentWrong));
                return;
            }
            if (message.what == 0) {
                meals = (ArrayList<String>) message.obj;
                if (starting) {
                    if (meals.size() == 0) {
                        checkForPermission();
                    } else if (view != null) {
                        showMenu();
                    }
                } else if (view != null) {
                    showMenu();
                }
            } else {
                starting = false;
                new MenuAsync().execute(context, handler);
            }
        }
    };


}