package com.johnston.lmhapp.MealMenus;

import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.TextView;

import com.johnston.lmhapp.BaseFragment;
import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.PermissionAsync;
import com.johnston.lmhapp.PermissionFailedDialog;
import com.johnston.lmhapp.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Johnston on 10/09/2014.
 */
public class MenuFragment extends BaseFragment {
    private  final int localFragmentNumber = 5;
    private TextView status;
    private MenuRecyclerAdapter menuRecyclerAdapter;
    private Boolean justDownloadedNewMenu = false;
    private MenuItem actionRefresh;
    private Context context;
    private ArrayList<String> meals = new ArrayList<>();
    Handler statusHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if(status!=null){
                status.setText((String) message.obj);
            }
            if(menuRecyclerAdapter != null){
                menuRecyclerAdapter.updateStatus((String) message.obj);
            }

        }
    };

    public void loadData() {
        refreshing = true;
        setStartedRefreshing();

        Handler permissionHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 0) {
//                Success!
                    new DownloadNewMenuAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context, false, handler, statusHandler);
                } else if (message.what == 1) {
//                Failure
                    handler.obtainMessage(-1).sendToTarget();
                    PermissionFailedDialog newFragment = PermissionFailedDialog.newInstance((String) message.obj);
                    newFragment.show(getFragmentManager(), "PERMISSION DENIED");
                }else if(message.what==2) {
//                    Do nothing. We are downloading a new menu anyway.
                }else if(message.what == MainActivity.STATUS_UPDATE){
                    handler.obtainMessage(MainActivity.STATUS_UPDATE,message.obj);
                } else {
//                Something has gone wrong checking.
                    handler.obtainMessage(-1).sendToTarget();
                }
            }
        };
        new PermissionAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity().getApplicationContext(), permissionHandler, "MenuFragment");
    }

    @Override
    public View getScrollingView()
    {
        return view.findViewById(R.id.my_recycler_view);
    }

    void startMenu() {
        setStartedRefreshing();
        File file = new File(context.getCacheDir(), "Menu.txt");
        if (!file.exists()) {
//                No menu. Get a new menu();
            loadData();
        } else {
            new MenuAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context, handler, statusHandler);

        }
    }

    void showMenu() {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        menuRecyclerAdapter = new MenuRecyclerAdapter(meals);
        recyclerView.setAdapter(menuRecyclerAdapter);

        if (meals.size() > 0) {
            showCards();
        } else {
            showMessage(getResources().getString(R.string.nothingToShow));
        }

        setFinishedRefreshing();
    }

    protected void showMessage(String message){
        super.showMessage(message);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        context = this.getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.menu_layout, container, false);
        status = (TextView) view.findViewById(R.id.Status);
        fragmentNumber = localFragmentNumber;
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if (refreshing)
        {
            setStartedRefreshing();
        }
        if (finished) {
            showMenu();
        }
        if(!refreshing && !finished){
            startMenu();
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

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == -1) {
                refreshing = false;
                finished = true;

                if (view == null) {
                    return;
                }
                setFinishedRefreshing();
                showMessage(getResources().getString(R.string.somethingWentWrong));
                return;
            }else if (message.what == 0) {
                meals = (ArrayList<String>) message.obj;
                if(meals.size() == 0 && justDownloadedNewMenu == false){
                    justDownloadedNewMenu = true;
                    loadData();
                }else if (view != null){
                    showMenu();
                }
            } else {
                new MenuAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context, handler, statusHandler);
            }
        }
    };


}