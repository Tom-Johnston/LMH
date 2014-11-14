package com.johnston.lmhapp.Battels;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            ListView lv = (ListView) view.findViewById(R.id.battelsListView);
            (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            Context context = getActivity().getBaseContext();
            entries = (ArrayList<String>) message.obj;
            BattelsListAdapter adapter = new BattelsListAdapter(context, R.layout.battels_list_item, entries);
            lv.setAdapter(adapter);
            finished = true;
        }
    };

    public void LoadBattels() {
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.battelsListView)).setVisibility(View.GONE);
        MainActivity main = (MainActivity) this.getActivity();
        byte b = 2;
        main.getInfo(view, handler, b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.battels, container, false);
        if (finished) {
            ListView lv = (ListView) view.findViewById(R.id.battelsListView);
            Context context = getActivity().getBaseContext();
            BattelsListAdapter adapter = new BattelsListAdapter(context, R.layout.battels_list_item, entries);
            lv.setAdapter(adapter);
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
        MenuItem item = menu.findItem(R.id.action_refresh);
        item.setEnabled(true);
        item.setVisible(true);
    }

}
