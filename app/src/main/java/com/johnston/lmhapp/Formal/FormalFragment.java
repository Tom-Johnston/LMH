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
import android.widget.AdapterView;

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Tom on 11/11/2014.
 */
public class FormalFragment extends Fragment {
    View view;
    ArrayList<String> entries;
    ArrayList<String> listOfMeals;
    ArrayList<ArrayList<String>> listOfListsOfPeople;
    Boolean finished = false;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
            if(message.what==0){
                entries = (ArrayList<String>)message.obj;

            }else if(message.what==1){
                listOfMeals = (ArrayList<String>) message.obj;
            }else if(message.what==2){
                listOfListsOfPeople = (ArrayList<ArrayList<String>>) message.obj;
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                FormalRecyclerAdapter formalRecyclerAdapter = new FormalRecyclerAdapter(entries,listOfMeals);
                recyclerView.setAdapter(formalRecyclerAdapter);
            }
        }
    };

    public void GetTheData() {
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        MainActivity main = (MainActivity) this.getActivity();
        byte b = 4;
        main.getInfo(view, handler, b);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.formal_layout, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        GetTheData();
        if (finished) {
            // No need to get all the info again
        } else {
//            Get all the info.
        }

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_refresh);
        item.setEnabled(false);
        item.setVisible(false);
    }

    AdapterView.OnItemClickListener onItemClickListener =  new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MainActivity main = (MainActivity)getActivity();
            String[] info = new String[5];
            position = position * 6;
//            Date
//            Name
//            Number Gone
//            Number Left
//            ID
            info[0]=entries.get(position);
            info[1]=entries.get(position+1);
            info[2]=entries.get(position+3);
            info[3]=entries.get(position+4);
            info[4]=entries.get(position+5);
            main.getDetails(info);
        }
    };
}
