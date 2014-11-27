package com.johnston.lmhapp.Formal;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Tom on 23/11/2014.
 */
public class FormalDetailsFragment extends Fragment {
    View view;
    String menu;
    ArrayList<String> entries;
    Boolean finished = false;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if(message.what==0){
                menu = (String)message.obj;
                ((TextView)view.findViewById(R.id.formalMenu)).setText(menu);
            }else{
                entries = (ArrayList<String>)message.obj;
                ListView listView = (ListView) view.findViewById(R.id.formalListOfPeople);
                listView.setAdapter(new FormalDetailsListAdapter(getActivity(),R.layout.formal_details_list_item,entries));

            }
        }
    };

    public static FormalDetailsFragment newInstance(String[] info) {
        FormalDetailsFragment f = new FormalDetailsFragment();
        Bundle args = new Bundle();
        args.putStringArray("args",info);
        f.setArguments(args);
        return f;
    }

    public void GetTheData() {
        MainActivity main = (MainActivity) this.getActivity();
        byte b = 5;
        main.getInfo(view, handler, b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        String[] info = args.getStringArray("args");
        view = inflater.inflate(R.layout.formal_details_layout, container, false);
        ((TextView)view.findViewById(R.id.formalName)).setText(info[1]);
        ((TextView)view.findViewById(R.id.formalDate)).setText(info[0]);
        GetTheData();
        if (finished) {
            // No need to get all the info again
//            TODO
        } else {
//            TODO
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
}
