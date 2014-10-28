package com.johnston.lmhapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
            Context context = getActivity().getBaseContext();
            entries = (ArrayList<String>) message.obj;
            BattelsListAdapter adapter = new BattelsListAdapter(context, R.layout.battels_list_item, entries);
            lv.setAdapter(adapter);
            finished = true;
        }
    };

    public void LoadBattels() {
        MainActivity main = (MainActivity) this.getActivity();
        byte b = 2;
        main.getInfo(view, handler, b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.battels, container, false);
        LoadBattels();


        return view;
    }

}
