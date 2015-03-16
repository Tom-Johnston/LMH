package com.johnston.lmhapp.Home;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.MealMenus.DownloadNewMenuAsync;
import com.johnston.lmhapp.PermissionAsync;
import com.johnston.lmhapp.PermissionFailedDialog;
import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Johnston on 29/09/2014.
 */
public class HomeFragment extends Fragment {
    private static TextView Status = null;
    private final Handler statusHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == -1) {
                handler.obtainMessage(-1).sendToTarget();
            }
            String update = (String) message.obj;
            if (Status != null) {
                Status.setText(update);
            }
        }
    };
    private MenuItem actionRefresh;
    private Boolean finished = false;
    private Boolean refreshing = false;
    private View view;
    private ArrayList<Tweet> tweets = new ArrayList<>();
    private Bitmap[] profilePictures;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {


            refreshing = false;
            MainActivity main = (MainActivity) getActivity();
            if (main != null) {
                main.stopRefresh(0);
            }
            if (message.what == -1) {
                finished = true;
                if (view == null) {
                    return;
                }
                showMessage(getResources().getString(R.string.somethingWentWrong));
                return;
            }

            finished = true;
            Object[] objects = (Object[]) message.obj;
            tweets = (ArrayList<Tweet>) objects[0];
            profilePictures = (Bitmap[]) objects[1];

            if (view == null) {
                return;
            }

            if (tweets.size() > 0) {
                RecyclerView tweetList = (RecyclerView) view.findViewById(R.id.my_recycler_view);
                tweetList.setAdapter(new TweetRecyclerAdapter(tweets, profilePictures));
                showTweets();
            } else {
                showMessage(getResources().getString(R.string.nothingToShow));
            }
        }
    };

    public void loadTweeterFeed() {
        refreshing = true;
        showProgressBar();
        MainActivity main = (MainActivity) getActivity();
        main.startRefresh(0);
        Handler permissionHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 0) {
//                Success!
                    new TwitterScraperAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, handler, getActivity(), statusHandler);
                } else if (message.what == 1) {
//                Failure
                    handler.obtainMessage(-1, "Unable to get permission.").sendToTarget();
                    PermissionFailedDialog newFragment = PermissionFailedDialog.newInstance((String) message.obj);
                    newFragment.show(getFragmentManager(), "PERMISSION DENIED");
                }else if(message.what==2){
                    new DownloadNewMenuAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity(), false, null);
                } else {
//                Something has gone wrong checking.
                    handler.obtainMessage(-1, "Unable to get permission.").sendToTarget();
                }
            }
        };

        new PermissionAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity().getApplicationContext(), permissionHandler, statusHandler, "HomeFragment");

    }

    void showTweets(){
        view.findViewById(R.id.Status).setVisibility(View.GONE);
        view.findViewById(R.id.my_recycler_view).setVisibility(View.VISIBLE);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        view.findViewById(R.id.nothingToShow).setVisibility(View.GONE);
    }
    void showMessage(String message){
        view.findViewById(R.id.Status).setVisibility(View.VISIBLE);
        view.findViewById(R.id.my_recycler_view).setVisibility(View.GONE);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        view.findViewById(R.id.nothingToShow).setVisibility(View.VISIBLE);
        ((TextView)view.findViewById(R.id.nothingToShow)).setText(message);
    }
    void showProgressBar(){
        view.findViewById(R.id.Status).setVisibility(View.VISIBLE);
        view.findViewById(R.id.my_recycler_view).setVisibility(View.GONE);
        view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        view.findViewById(R.id.nothingToShow).setVisibility(View.GONE);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        view = inflater.inflate(R.layout.home_fragment, container, false);
        Status  = (TextView)view.findViewById(R.id.Status);
        RecyclerView tweetList = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        tweetList.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (refreshing) {
            MainActivity main = (MainActivity) getActivity();
            main.startRefresh(0);
            showProgressBar();
        } else if (finished) {
            if (tweets.size() > 0) {
                tweetList.setAdapter(new TweetRecyclerAdapter(tweets, profilePictures));
                showTweets();
            } else {
                showMessage(getResources().getString(R.string.nothingToShow));
            }
        } else {
            loadTweeterFeed();
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
