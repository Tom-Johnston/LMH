package com.johnston.lmhapp.EPOS;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.johnston.lmhapp.MainActivity;
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
            transactions = (ArrayList<String>) message.obj;
            addEntriesToList();
            finished = true;
        }
    };

    public void GetEpos() {
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        finished = false;
        byte b = 1;
        Main.getInfo(view, handler, b);
    }


    public void addEntriesToList(){
        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.transactionList);
        for(int i=0;i<transactions.size();i++){
            String data = transactions.get(i);
            String code = data.substring(0, 2);
            String message = data.substring(2);
            TextView tv = new TextView(getActivity());
            tv.setText(message);
            if (code.equals("12")) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } else if (code.equals("02")) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }
            linearLayout.addView(tv);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        view = inflater.inflate(R.layout.new_epos_layout, container, false);
        Main = (MainActivity) getActivity();
        if (finished) {
            addEntriesToList();
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
