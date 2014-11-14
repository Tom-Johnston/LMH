package com.johnston.lmhapp.LaundryView;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Tom on 02/06/2014.
 */
public class LaundryViewFragment extends Fragment {
    Boolean Loaded = false;
    View view;
    long startTime = 0;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            startTime = (Long) message.obj;
            Loaded = true;
        }
    };

    public void LoadStatus() {
        handler.removeCallbacksAndMessages(null);
        TextView timer = (TextView) view.findViewById(R.id.LastUpdate);
        timer.setText("");
        TextView tv1 = (TextView) view.findViewById(R.id.KatieLee);
        TextView tv2 = (TextView) view.findViewById(R.id.NewOldHall);
        TextView tv3 = (TextView) view.findViewById(R.id.Talbot);
        tv1.setVisibility(View.GONE);
        tv2.setVisibility(View.GONE);
        tv3.setVisibility(View.GONE);
        ProgressBar P1 = (ProgressBar) view.findViewById(R.id.P1);
        ProgressBar P2 = (ProgressBar) view.findViewById(R.id.P2);
        ProgressBar P3 = (ProgressBar) view.findViewById(R.id.P3);
        P1.setVisibility(View.VISIBLE);
        P2.setVisibility(View.VISIBLE);
        P3.setVisibility(View.VISIBLE);
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
            view = inflater.inflate(R.layout.laundry_view, container, false);
        }
        if (!Loaded) {
            LoadStatus();
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
        MenuItem item = menu.findItem(R.id.action_refresh);
        item.setEnabled(true);
        item.setVisible(true);
    }
}
