package com.johnston.lmhapp.LaundryView;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.johnston.lmhapp.BaseFragment;
import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.MealMenus.DownloadNewMenuAsync;
import com.johnston.lmhapp.PermissionAsync;
import com.johnston.lmhapp.PermissionFailedDialog;
import com.johnston.lmhapp.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Tom on 02/06/2014.
 */
public class LaundryViewFragment extends BaseFragment
{
    private View view;
    private long startTime = 0;
    private ArrayList<String> KatieLee;
    private ArrayList<String> NewOldHall;
    private ArrayList<String> Talbot;
    private static TextView Status;
    private final Handler statusHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            if(message.what==-1){
                handler.obtainMessage(-1).sendToTarget();
            }
            String update = (String)message.obj;
            if(Status!=null){
                Status.setText(update);
            }
        }
    };
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {


            if (message.what == -1) {
                setFinishedRefreshing();
                MainActivity main = (MainActivity) getActivity();
                if (main != null) {
                    main.stopRefresh(1);
                }
                if (view == null) {
                    return;
                }
                showMessage(getResources().getString(R.string.somethingWentWrong));
                return;
            }
            if (message.what == 0) {
                startTime = (Long) message.obj;
                setFinishedRefreshing();
            } else if (message.what == 1) {
                KatieLee = (ArrayList<String>) message.obj;
                if (view != null) {
                    addEntriesToList(R.id.KatieLee, KatieLee);
                }
            } else if (message.what == 2) {
                NewOldHall = (ArrayList<String>) message.obj;
                if (view != null) {
                    addEntriesToList(R.id.NewOldHall, NewOldHall);
                }
            } else if (message.what == 3) {
                MainActivity main = (MainActivity) getActivity();
                if (main != null) {
                    main.stopRefresh(1);
                }
                Talbot = (ArrayList<String>) message.obj;
                if (view != null) {
                    addEntriesToList(R.id.Talbot, Talbot);
                    showCards();
                }
            }
            if(message.what==4) {
                Runnable updateTime = new Runnable() {

                    @Override
                    public void run() {
                        long runningTime = System.currentTimeMillis() - startTime;
                        int minutes = (int) runningTime / (1000 * 60);
                        int seconds = (int) (runningTime / (1000)) % 60;
                        Status.setText("Last updated: " + minutes + "m " + seconds + "s ago.");
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.post(updateTime);
            }
        }
    };

    private MenuItem actionRefresh;

    void addEntriesToList(int resource, ArrayList<String> entries) {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(resource);
        linearLayout.removeAllViews();
        Activity activity = getActivity();
        if(activity==null){
            return;
        }
        for (int i = 0; i < entries.size(); i++) {
            TextView tv = new TextView(activity);
            tv.setText(entries.get(i));

            View divider = new View(activity);
            divider.setBackgroundColor(Color.parseColor("#1f000000"));
            divider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));

            linearLayout.addView(tv);
            linearLayout.addView(divider);
        }
    }

    @Override
    public void loadData() {
        handler.removeCallbacksAndMessages(null);
        refreshing = true;
        MainActivity main = (MainActivity) getActivity();
        main.startRefresh(1);
        if(!finished)
        {
            showProgressBar();
        }
        Handler permissionHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 0) {
//                Success!
                    try {
                        URL KatieLee = new URL("http://classic.laundryview.com/laundry_room.php?lr=870043400887");
                        URL NewOldHall = new URL("http://classic.laundryview.com/laundry_room.php?lr=870043400853");
                        URL Talbot = new URL("http://classic.laundryview.com/laundry_room.php?lr=870043400855");
                        new LaundryViewAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, statusHandler, handler, KatieLee, NewOldHall, Talbot);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else if (message.what == 1) {
//                Failure
                    handler.obtainMessage(-1).sendToTarget();
                    PermissionFailedDialog newFragment = PermissionFailedDialog.newInstance((String) message.obj);
                    newFragment.show(getFragmentManager(), "PERMISSION DENIED");
                }else if(message.what==2){
                    new DownloadNewMenuAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity(), false, null);
                } else {
//                Something has gone wrong checking.
                    handler.obtainMessage(-1).sendToTarget();
                }
            }
        };
        new PermissionAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity().getApplicationContext(), permissionHandler, statusHandler, "LaundryView");
    }

    @Override
    public View getScrollingView()
    {
        return view;
    }

    void showProgressBar() {
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view2)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view3)).setVisibility(View.GONE);
    }
    void showMessage(String message){
        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.VISIBLE);
        ((TextView)view.findViewById(R.id.nothingToShow)).setText(message);
        (view.findViewById(R.id.card_view)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view2)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view3)).setVisibility(View.GONE);
    }
    void showCards(){
        (view.findViewById(R.id.card_view)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.card_view2)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.card_view3)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.laundry_view, container, false);
        }
        Status = (TextView) view.findViewById(R.id.Status);

        if (refreshing) {
            MainActivity main = (MainActivity) getActivity();
            main.startRefresh(1);
            showProgressBar();
        } else if (!finished) {
            loadData();
        } else {
            showCards();
            handler.obtainMessage(4).sendToTarget();
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
