package com.johnston.lmhapp.LaundryView;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Tom on 02/06/2014.
 */
public class LaundryViewFragment extends Fragment {
    Boolean Loaded = false;
    View view;
    long startTime = 0;
    ArrayList<String> KatieLee;
    ArrayList<String> NewOldHall;
    ArrayList<String> Talbot;
    MenuItem actionRefresh;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
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
            if(message.what==0){
                startTime = (Long) message.obj;
                Loaded = true;
            }else if(message.what==1){
                KatieLee = (ArrayList<String>) message.obj;
                addEntriesToList(R.id.KatieLee,KatieLee);
            }else if(message.what==2){
                NewOldHall = (ArrayList<String>) message.obj;
                addEntriesToList(R.id.NewOldHall,NewOldHall);
            }else if(message.what==3){
                Talbot = (ArrayList<String>) message.obj;
                addEntriesToList(R.id.Talbot,Talbot);
                (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
                (view.findViewById(R.id.card_view)).setVisibility(View.VISIBLE);
                (view.findViewById(R.id.card_view2)).setVisibility(View.VISIBLE);
                (view.findViewById(R.id.card_view3)).setVisibility(View.VISIBLE);
            }
        }
    };


    public void addEntriesToList(int resource,ArrayList<String> entries ){
        LinearLayout linearLayout = (LinearLayout)view.findViewById(resource);
        linearLayout.removeAllViews();
        for(int i=0;i<entries.size();i++){
            TextView tv = new TextView(getActivity());
            tv.setText(entries.get(i));

            View divider = new View(getActivity());
            divider.setBackgroundColor(Color.parseColor("#1f000000"));
            divider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2));

            linearLayout.addView(tv);
            linearLayout.addView(divider);
        }
    }

    public void LoadStatus() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView actionRefreshView = (ImageView) inflater.inflate(R.layout.action_refresh,null);
        Animation an = AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.rotate_animation);
        an.setRepeatCount(Animation.INFINITE);
        actionRefreshView.setAnimation(an);
        actionRefresh.setActionView(actionRefreshView);
        handler.removeCallbacksAndMessages(null);
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.card_view)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view2)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view3)).setVisibility(View.GONE);
        try {
            URL KatieLee = new URL("http://classic.laundryview.com/laundry_room.php?lr=870043400887");
            URL NewOldHall = new URL("http://classic.laundryview.com/laundry_room.php?lr=870043400853");
            URL Talbot = new URL("http://classic.laundryview.com/laundry_room.php?lr=870043400855");
            new LaundryViewAsync().execute(view, handler, KatieLee, NewOldHall, Talbot, this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.new_laundry_view, container, false);
        }
        if (!Loaded) {
            Handler startHandler = new Handler();
            Runnable startRunnable = new Runnable() {
                @Override
                public void run() {
                    LoadStatus();
                }
            };
            startHandler.post(startRunnable);
        } else {
            final TextView timerView = (TextView) view.findViewById(R.id.LastUpdate);
            Runnable updateTime = new Runnable() {

                @Override
                public void run() {
                    long runningTime = System.currentTimeMillis() - startTime;
                    int minutes = (int) runningTime / (1000 * 60);
                    int seconds = (int) (runningTime / (1000)) % 60;
                    timerView.setText("Last updated: " + minutes + "m " + seconds + "s ago.");
                    handler.postDelayed(this, 1000);
                }
            };
            handler.post(updateTime);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        actionRefresh = menu.findItem(R.id.action_refresh);
        actionRefresh.setEnabled(true);
        actionRefresh.setVisible(true);

    }
}
