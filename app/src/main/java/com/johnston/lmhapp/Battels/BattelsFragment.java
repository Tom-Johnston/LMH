package com.johnston.lmhapp.Battels;

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
 * Created by Tom on 27/10/2014.
 */
public class BattelsFragment extends BaseFragment
{
    private TextView Status;
    private BattelsRecyclerAdapter battelsRecyclerAdapter;
    private final int localFragmentNumber = 2;
    private ArrayList<String> entries = new ArrayList<>();
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {


            if (view == null) {
                return;
            }
            if(message.what == MainActivity.STATUS_UPDATE){
                if(!finished && Status !=null){
                    Status.setText((String)message.obj);
                }
                if(battelsRecyclerAdapter != null){
                    battelsRecyclerAdapter.updateStatus((String) message.obj);
                }
                return;
            }else if (message.what == -1) {
                setFinishedRefreshing();
                showMessage(getResources().getString(R.string.somethingWentWrong));
                return;
            }

            entries = (ArrayList<String>) message.obj;
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

            if (entries.size() > 0 && message.what == 0) {
                battelsRecyclerAdapter = new BattelsRecyclerAdapter(entries);
                recyclerView.setAdapter(battelsRecyclerAdapter);
                showCards();
            }else if(entries.size() > 0 && message.what == 1){
                battelsRecyclerAdapter.notifyItemRangeChanged(0,battelsRecyclerAdapter.getItemCount());
                setFinishedRefreshing();
            } else {
                showMessage(getResources().getString(R.string.nothingToShow));
            }
        }
    };

    private MenuItem actionRefresh;

    @Override
    public void loadData() {
        refreshing = true;
        setStartedRefreshing();
        MainActivity main = (MainActivity) this.getActivity();
        main.getInfo(handler, (byte) localFragmentNumber);
    }

    @Override
    public View getScrollingView()
    {
        return view.findViewById(R.id.my_recycler_view);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.battels_layout, container, false);
        fragmentNumber = localFragmentNumber;
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if (refreshing) {
            setStartedRefreshing();
        }
        if (finished) {
            if (entries.size() > 0) {
                battelsRecyclerAdapter = new BattelsRecyclerAdapter(entries);
                recyclerView.setAdapter(battelsRecyclerAdapter);
                showCards();
            } else {
                showMessage(getResources().getString(R.string.nothingToShow));
            }
        }
        if(!finished && !refreshing) {
            loadData();
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
