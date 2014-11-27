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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
    MenuItem actionRefresh;
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
            if(actionRefresh.getActionView()!=null){
                actionRefresh.getActionView().getAnimation().setRepeatCount(0);
                actionRefresh.getActionView().getAnimation().setAnimationListener( new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
//                        actionRefresh.getActionView().clearAnimation();
                        actionRefresh.setActionView(null);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }

        }
    };

    public void LoadBattels() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView actionRefreshView = (ImageView) inflater.inflate(R.layout.action_refresh,null);
        Animation an = AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.rotate_animation);
        an.setRepeatCount(Animation.INFINITE);
        actionRefreshView.setAnimation(an);
        actionRefresh.setActionView(actionRefreshView);
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
            Handler startHandler = new Handler();
            Runnable startRunnable = new Runnable() {
                @Override
                public void run() {
                    LoadBattels();
                }
            };
            startHandler.post(startRunnable);

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
