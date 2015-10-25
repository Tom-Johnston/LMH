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
    private View view;
    private ArrayList<String> entries = new ArrayList<>();
    private ArrayList<String> listOfMeals;
    private ArrayList<ArrayList<String>> listOfListsOfPeople;
    private Boolean finished = false;
    private Boolean refreshing = false;
    private final Handler handler = new Handler() {
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
                showMessage(getResources().getString(R.string.somethingWentWrong));
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
                    showCards();
                } else {
                    showMessage(getResources().getString(R.string.nothingToShow));
                }

            }
        }
    };

    public void GetTheData() {
        refreshing = true;
        showProgressBar();

        MainActivity main = (MainActivity) this.getActivity();
        byte b = 4;
        main.startRefresh(4);
        main.getInfo(view, handler, b);
    }

    public void showListofPeopleGoing(int position) {
        FormalDetailsDialog dialog = FormalDetailsDialog.newInstance(listOfListsOfPeople.get(position-1));
        dialog.show(getFragmentManager(), "details");
    }

    void showCards(){
        (view.findViewById(R.id.Status)).setVisibility(View.GONE);
        (view.findViewById(R.id.my_recycler_view)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
    }
    void showMessage(String message){
        (view.findViewById(R.id.Status)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.VISIBLE);
        ((TextView)view.findViewById(R.id.nothingToShow)).setText(message);
        (view.findViewById(R.id.my_recycler_view)).setVisibility(View.GONE);
    }
    void showProgressBar(){
        (view.findViewById(R.id.Status)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
        (view.findViewById(R.id.my_recycler_view)).setVisibility(View.GONE);
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
            showProgressBar();
        } else if (finished) {
            if (entries.size() > 0) {
                FormalRecyclerAdapter formalRecyclerAdapter = new FormalRecyclerAdapter(entries, listOfMeals);
                recyclerView.setAdapter(formalRecyclerAdapter);
                showCards();
            } else {
                showMessage(getResources().getString(R.string.nothingToShow));
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
