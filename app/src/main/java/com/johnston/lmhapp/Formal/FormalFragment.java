package com.johnston.lmhapp.Formal;

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
import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Tom on 11/11/2014.
 */
public class FormalFragment extends BaseFragment {
    final int localFragmentNumber = 4;
    private ArrayList<String> entries = new ArrayList<>();
    private ArrayList<String> listOfMeals;
    private ArrayList<ArrayList<String>> listOfListsOfPeople;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {


            if (message.what == -1) {
                setFinishedRefreshing();
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
                setFinishedRefreshing();
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

    public void loadData() {
        refreshing = true;
        if(finished){
            setStartedRefreshing();
        }else{
            showProgressBar();
        }
        MainActivity main = (MainActivity) this.getActivity();
        main.getInfo(view, handler, (byte) localFragmentNumber);
    }

    @Override
    public View getScrollingView()
    {
        return view.findViewById(R.id.my_recycler_view);
    }

    public void showListofPeopleGoing(int position) {
        FormalDetailsDialog dialog = FormalDetailsDialog.newInstance(listOfListsOfPeople.get(position-1));
        dialog.show(getFragmentManager(), "details");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.formal_layout, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if (refreshing) {
            MainActivity.Status = (android.widget.TextView) view.findViewById(R.id.Status);
            if(!finished){
                showProgressBar();
            }else{
                setStartedRefreshing();
            }
        } else if (finished) {
            if (entries.size() > 0) {
                FormalRecyclerAdapter formalRecyclerAdapter = new FormalRecyclerAdapter(entries, listOfMeals);
                recyclerView.setAdapter(formalRecyclerAdapter);
                showCards();
            } else {
                showMessage(getResources().getString(R.string.nothingToShow));
            }
        } else {
            loadData();
//            Get all the info.
        }

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fragmentNumber = localFragmentNumber;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_refresh);
        item.setEnabled(true);
        item.setVisible(true);
    }
}
