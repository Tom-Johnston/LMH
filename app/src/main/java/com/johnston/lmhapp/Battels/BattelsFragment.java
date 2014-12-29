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
    View view;
    ArrayList<String> entries = new ArrayList<>();
    Boolean finished = false;
    Boolean refreshing = false;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {


            MainActivity main = (MainActivity) getActivity();
            if (main != null) {
                main.stopRefresh(2);
            }
            if (view != null) {
                (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
            }

            if (message.what == -1) {
                refreshing = false;
                finished = true;
                if (view == null) {
                    return;
                }
                TextView nothingToShow = (TextView) view.findViewById(R.id.nothingToShow);
                nothingToShow.setVisibility(View.VISIBLE);
                nothingToShow.setText("Something has gone wrong.");
                return;
            }

            finished = true;
            refreshing = false;
            entries = (ArrayList<String>) message.obj;

            if (view == null) {
                return;
            }
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

            if (entries.size() > 0) {
                BattelsRecyclerAdapter battelsRecyclerAdapter = new BattelsRecyclerAdapter(entries);
                recyclerView.setAdapter(battelsRecyclerAdapter);
                (view.findViewById(R.id.progressBarContainer)).setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                view.findViewById(R.id.Status).setVisibility(View.GONE);
            } else {
                (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
                TextView nothingToShow = (TextView) view.findViewById(R.id.nothingToShow);
                nothingToShow.setVisibility(View.VISIBLE);
                nothingToShow.setText(getResources().getString(R.string.nothingToShow));
            }
        }
    };
    MenuItem actionRefresh;

    public void LoadBattels() {
        refreshing = true;
        (view.findViewById(R.id.Status)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBarContainer)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
        (view.findViewById(R.id.my_recycler_view)).setVisibility(View.GONE);
        MainActivity main = (MainActivity) this.getActivity();
        main.startRefresh(2);
        byte b = 2;
        main.getInfo(view, handler, b);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.battels, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if (refreshing) {
            MainActivity main = (MainActivity) this.getActivity();
            main.startRefresh(2);
            MainActivity.Status = (android.widget.TextView) view.findViewById(R.id.Status);
            (view.findViewById(R.id.Status)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.progressBarContainer)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
            (view.findViewById(R.id.my_recycler_view)).setVisibility(View.GONE);
        } else if (finished) {
            if (entries.size() > 0) {
                BattelsRecyclerAdapter battelsRecyclerAdapter = new BattelsRecyclerAdapter(entries);
                recyclerView.setAdapter(battelsRecyclerAdapter);
                (view.findViewById(R.id.progressBarContainer)).setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                view.findViewById(R.id.Status).setVisibility(View.GONE);
            } else {
                (view.findViewById(R.id.progressBarContainer)).setVisibility(View.VISIBLE);
                (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
                (view.findViewById(R.id.nothingToShow)).setVisibility(View.VISIBLE);
            }
        } else {
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
