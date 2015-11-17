package com.johnston.lmhapp.Home;

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

import com.johnston.lmhapp.BaseFragment;
import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.MealMenus.DownloadNewMenuAsync;
import com.johnston.lmhapp.PermissionAsync;
import com.johnston.lmhapp.PermissionFailedDialog;
import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Johnston on 29/09/2014.
 */
public class HomeFragment extends BaseFragment
{
    private final int localFragmentNumber = 0;
    private static TextView Status = null;
    private TweetRecyclerAdapter tweetAdapter;
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
            if(tweetAdapter != null)
            {
                tweetAdapter.updateStatus(update);
            }
        }
    };
    private MenuItem actionRefresh;
    private ArrayList<Tweet> tweets = new ArrayList<>();
    private Bitmap[] profilePictures;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {


            setFinishedRefreshing();
            if (message.what == -1) {
                if (view == null) {
                    return;
                }
                showMessage(getResources().getString(R.string.somethingWentWrong));
                return;
            }

            Object[] objects = (Object[]) message.obj;
            tweets = (ArrayList<Tweet>) objects[0];
            profilePictures = (Bitmap[]) objects[1];

            if (view == null) {
                return;
            }

            if (tweets.size() > 0) {
                RecyclerView tweetList = (RecyclerView) view.findViewById(R.id.my_recycler_view);
                tweetAdapter = new TweetRecyclerAdapter(tweets, profilePictures);
                tweetList.setAdapter(tweetAdapter);
                showCards();
            } else {
                showMessage(getResources().getString(R.string.nothingToShow));
            }
        }
    };

    @Override
    public void loadData() {
        refreshing = true;
        if(finished){
            setStartedRefreshing();
        }else{
            showProgressBar();
        }
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

    @Override
    public View getScrollingView()
    {
        return view.findViewById(R.id.my_recycler_view);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        view = inflater.inflate(R.layout.home_fragment, container, false);

        RecyclerView tweetList = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        tweetList.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (refreshing) {
            MainActivity.Status = (android.widget.TextView) view.findViewById(R.id.Status);
            if(!finished){
                showProgressBar();
            }else{
                setStartedRefreshing();
            }
        } else if (finished) {
            if (tweets.size() > 0) {
                tweetList.setAdapter(new TweetRecyclerAdapter(tweets, profilePictures));
                showCards();
            } else {
                showMessage(getResources().getString(R.string.nothingToShow));
            }
        } else {
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
