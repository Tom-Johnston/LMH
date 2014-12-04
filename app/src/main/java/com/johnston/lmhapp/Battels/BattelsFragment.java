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

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Tom on 27/10/2014.
 */
public class BattelsFragment extends Fragment {
    View view;
    ArrayList<String> entries;
    Boolean finished = false;
    MenuItem actionRefresh;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            entries = (ArrayList<String>) message.obj;

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            BattelsRecyclerAdapter battelsRecyclerAdapter = new BattelsRecyclerAdapter(entries);
            recyclerView.setAdapter(battelsRecyclerAdapter);
            (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            finished = true;
            MainActivity main = (MainActivity) getActivity();
            if(main!=null){
            main.stopRefresh(2);
            }

        }
    };

    public void LoadBattels() {
        (view.findViewById(R.id.Status)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
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
        if (finished) {
            (view.findViewById(R.id.Status)).setVisibility(View.GONE);
            BattelsRecyclerAdapter battelsRecyclerAdapter = new BattelsRecyclerAdapter(entries);
            recyclerView.setAdapter(battelsRecyclerAdapter);
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
