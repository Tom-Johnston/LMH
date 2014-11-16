package com.johnston.lmhapp.EPOS;

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
import com.johnston.lmhapp.MealMenus.MenuListAdapter;
import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Tom on 02/06/2014.
 */
public class EPOSFragment extends Fragment {
    Boolean finished = false;
    View view;
    MainActivity Main;
    ArrayList<String> transactions;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            ListView lv = (ListView) view.findViewById(R.id.transactionsListView);
            (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
            Context context = getActivity().getBaseContext();
            transactions = (ArrayList<String>) message.obj;
            MenuListAdapter adapter = new MenuListAdapter(context, R.layout.listview, transactions);
            lv.setAdapter(adapter);
            finished = true;
        }
    };

    public void GetEpos() {
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        finished = false;
        byte b = 1;
        Main.getInfo(view, handler, b);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        view = inflater.inflate(R.layout.epos_layout, container, false);
        Main = (MainActivity) getActivity();
        if (finished) {
            ListView lv = (ListView) view.findViewById(R.id.transactionsListView);
            Context context = getActivity().getBaseContext();
            MenuListAdapter adapter = new MenuListAdapter(context, R.layout.listview, transactions);
            lv.setAdapter(adapter);
        } else {
            GetEpos();
        }
        return view;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_refresh);
        item.setEnabled(true);
        item.setVisible(true);
    }
}