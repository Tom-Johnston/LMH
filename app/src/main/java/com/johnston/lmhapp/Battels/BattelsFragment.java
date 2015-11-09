package com.johnston.lmhapp.Battels;

import android.app.Fragment;
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

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Tom on 27/10/2014.
 */
public class BattelsFragment extends Fragment {
    private View view;
    private ArrayList<String> entries = new ArrayList<>();
    private Boolean finished = false;
    private Boolean refreshing = false;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {


            if(message.what != 0) {
                MainActivity main = (MainActivity) getActivity();
                if (main != null) {
                    main.stopRefresh(2);
                }

                refreshing = false;
            }
            finished = true;
            if (view == null) {
                return;
            }

            if (message.what == -1) {
                showMessage(getResources().getString(R.string.somethingWentWrong));
                return;
            }

            entries = (ArrayList<String>) message.obj;
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

            if (entries.size() > 0 && message.what == 0) {
                BattelsRecyclerAdapter battelsRecyclerAdapter = new BattelsRecyclerAdapter(entries);
                recyclerView.setAdapter(battelsRecyclerAdapter);
                showCards();
            }else if(entries.size() > 0 && message.what == 1){
                RecyclerView.Adapter battelsRecyclerAdapter = recyclerView.getAdapter();
                battelsRecyclerAdapter.notifyItemRangeChanged(0,battelsRecyclerAdapter.getItemCount()-2);
            } else {
                showMessage(getResources().getString(R.string.nothingToShow));
            }
        }
    };

    private MenuItem actionRefresh;

    public void LoadBattels() {
        refreshing = true;
        showProgressBar();
        MainActivity main = (MainActivity) this.getActivity();
        main.startRefresh(2);
        byte b = 2;
        main.getInfo(view, handler, b);
    }

    void showProgressBar(){
        view.findViewById(R.id.Status).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
        (view.findViewById(R.id.my_recycler_view)).setVisibility(View.GONE);
    }

    void showMessage(String message){
        view.findViewById(R.id.Status).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.VISIBLE);
        ((TextView)view.findViewById(R.id.nothingToShow)).setText(message);
        (view.findViewById(R.id.my_recycler_view)).setVisibility(View.GONE);
    }
    void showCards(){
        (view.findViewById(R.id.Status)).setVisibility(View.GONE);
        (view.findViewById(R.id.my_recycler_view)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.battels_layout, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if (refreshing) {
            MainActivity main = (MainActivity) this.getActivity();
            main.startRefresh(2);
            MainActivity.Status = (android.widget.TextView) view.findViewById(R.id.Status);
            showProgressBar();
        }
        if (finished) {
            if (entries.size() > 0) {
                BattelsRecyclerAdapter battelsRecyclerAdapter = new BattelsRecyclerAdapter(entries);
                recyclerView.setAdapter(battelsRecyclerAdapter);
                showCards();
            } else {
                showMessage(getResources().getString(R.string.nothingToShow));
            }
        }
        if(!finished && !refreshing) {
            LoadBattels();
        }


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        actionRefresh = menu.findItem(R.id.action_refresh);
        actionRefresh.setEnabled(true);
        actionRefresh.setVisible(true);
    }

}
