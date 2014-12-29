package com.johnston.lmhapp.Formal;

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
 * Created by Tom on 11/11/2014.
 */
public class FormalFragment extends Fragment {
    View view;
    ArrayList<String> entries = new ArrayList<>();
    ArrayList<String> listOfMeals;
    ArrayList<ArrayList<String>> listOfListsOfPeople;
    Boolean finished = false;
    Boolean refreshing = false;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {


            if (message.what == -1) {
                refreshing = false;
                finished = true;
                MainActivity main = (MainActivity) getActivity();
                if (main != null) {
                    main.stopRefresh(4);
                }
                if (view == null) {
                    return;
                }
                view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                TextView nothingToShow = (TextView) view.findViewById(R.id.nothingToShow);
                nothingToShow.setVisibility(View.VISIBLE);
                nothingToShow.setText("Something has gone wrong.");
                return;
            }

            if (message.what == 0) {
                entries = (ArrayList<String>) message.obj;

            } else if (message.what == 1) {
                listOfMeals = (ArrayList<String>) message.obj;
            } else if (message.what == 2) {
                MainActivity main = (MainActivity) getActivity();
                if (main != null) {
                    main.stopRefresh(4);
                }
                finished = true;
                refreshing = false;
                listOfListsOfPeople = (ArrayList<ArrayList<String>>) message.obj;
                if (view == null) {
                    return;
                }
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
                if (entries.size() > 0) {
                    FormalRecyclerAdapter formalRecyclerAdapter = new FormalRecyclerAdapter(entries, listOfMeals);
                    recyclerView.setAdapter(formalRecyclerAdapter);
                    (view.findViewById(R.id.progressBarContainer)).setVisibility(View.GONE);
                } else {
                    (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
                    (view.findViewById(R.id.nothingToShow)).setVisibility(View.VISIBLE);
                }

            }
        }
    };

    public void GetTheData() {
        refreshing = true;
        (view.findViewById(R.id.Status)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBarContainer)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);

        MainActivity main = (MainActivity) this.getActivity();
        byte b = 4;
        main.startRefresh(4);
        main.getInfo(view, handler, b);
    }

    public void showListofPeopleGoing(int position) {
        FormalDetailsDialog dialog = FormalDetailsDialog.newInstance(listOfListsOfPeople.get(position));
        dialog.show(getFragmentManager(), "details");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.formal_layout, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if (refreshing) {
            MainActivity main = (MainActivity) this.getActivity();
            main.startRefresh(4);
            MainActivity.Status = (android.widget.TextView) view.findViewById(R.id.Status);
            (view.findViewById(R.id.Status)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.progressBarContainer)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
        } else if (finished) {
            if (entries.size() > 0) {
                FormalRecyclerAdapter formalRecyclerAdapter = new FormalRecyclerAdapter(entries, listOfMeals);
                recyclerView.setAdapter(formalRecyclerAdapter);
                (view.findViewById(R.id.progressBarContainer)).setVisibility(View.GONE);
            } else {
                (view.findViewById(R.id.progressBarContainer)).setVisibility(View.VISIBLE);
                (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
                (view.findViewById(R.id.nothingToShow)).setVisibility(View.VISIBLE);
            }
        } else {
            GetTheData();
//            Get all the info.
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
        MenuItem item = menu.findItem(R.id.action_refresh);
        item.setEnabled(true);
        item.setVisible(true);
    }
}
