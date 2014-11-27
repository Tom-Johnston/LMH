package com.johnston.lmhapp.EPOS;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
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
    MenuItem actionRefresh;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
            (view.findViewById(R.id.card_view)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.card_view2)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.card_view3)).setVisibility(View.VISIBLE);
            transactions = (ArrayList<String>) message.obj;
            addEntriesToList();
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

    public void GetEpos() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView actionRefreshView = (ImageView) inflater.inflate(R.layout.action_refresh,null);
        Animation an = AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.rotate_animation);
        an.setRepeatCount(Animation.INFINITE);
        actionRefreshView.setAnimation(an);
        actionRefresh.setActionView(actionRefreshView);
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.card_view)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view2)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view3)).setVisibility(View.GONE);
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
            View divider = new View(getActivity());
            divider.setBackgroundColor(Color.parseColor("#1f000000"));
            divider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2));

            linearLayout.addView(tv);
            linearLayout.addView(divider);
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
            Handler startHandler = new Handler();
            Runnable startRunnable = new Runnable() {
                @Override
                public void run() {
                    GetEpos();
                }
            };
            startHandler.post(startRunnable);
        }
        return view;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        actionRefresh = menu.findItem(R.id.action_refresh);
        actionRefresh.setEnabled(true);
        actionRefresh.setVisible(true);
    }
}
