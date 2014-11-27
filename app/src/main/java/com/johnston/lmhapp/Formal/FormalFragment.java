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
import android.widget.AdapterView;
import android.widget.ListView;

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Tom on 11/11/2014.
 */
public class FormalFragment extends Fragment {
    View view;
    ArrayList<String> entries;
    Boolean finished = false;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
           if(message.what==0){
            entries = (ArrayList<String>)message.obj;
               ListView listView = (ListView)view.findViewById(R.id.battelsListView);
               listView.setAdapter(new FormalListAdapter(getActivity(),R.layout.formal_list_item,entries));
               listView.setOnItemClickListener(onItemClickListener);
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
        view = inflater.inflate(R.layout.battels, container, false);
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
