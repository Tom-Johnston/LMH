package com.johnston.lmhapp.Home;

import android.app.Fragment;
import android.graphics.Bitmap;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Johnston on 29/09/2014.
 */
public class HomeFragment extends Fragment {
    MenuItem actionRefresh;
    Boolean finished = false;
    Boolean refreshing = false;
    private View view;
    private ArrayList<Tweet> tweets = new ArrayList<>();
    private Bitmap[] profilePictures;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {

            finished = true;
            refreshing = false;
            MainActivity main = (MainActivity) getActivity();
            if (main != null) {
                main.stopRefresh(0);
            }
            if(view!=null) {

                view.findViewById(R.id.PM1).setVisibility(View.GONE);
            }
            if(message.what==-1){
                return;
            }

            Object[] objects = (Object[]) message.obj;
            tweets = (ArrayList<Tweet>) objects[0];
            profilePictures = (Bitmap[]) objects[1];

            if(view==null){
                return;
            }

            final TextView nothingToShow = (TextView) view.findViewById(R.id.nothingToShow);
            if (tweets.size() > 0) {
                RecyclerView tweetList = (RecyclerView) view.findViewById(R.id.tweetList);
                tweetList.setAdapter(new TweetRecyclerAdapter(tweets, profilePictures));
                tweetList.setVisibility(View.VISIBLE);
                nothingToShow.setVisibility(View.GONE);
            } else {
                nothingToShow.setVisibility(View.VISIBLE);
            }
        }
    };

    public void loadTweeterFeed() {
        refreshing = true;
        MainActivity main = (MainActivity) getActivity();
        main.startRefresh(0);
        RecyclerView tweetList = (RecyclerView) view.findViewById(R.id.tweetList);
        tweetList.setVisibility(View.GONE);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.PM1);
        progressBar.setVisibility(View.VISIBLE);
        final TextView nothingToshow = (TextView) view.findViewById(R.id.nothingToShow);
        nothingToshow.setVisibility(View.GONE);
        new TwitterScraperAsync().execute(handler,getActivity());
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
        RecyclerView tweetList = (RecyclerView) view.findViewById(R.id.tweetList);
        tweetList.setLayoutManager( new LinearLayoutManager(getActivity()));
        if (refreshing) {
            MainActivity main = (MainActivity) getActivity();
            main.startRefresh(0);
            tweetList.setVisibility(View.GONE);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.PM1);
            progressBar.setVisibility(View.VISIBLE);
            final TextView nothingToShow = (TextView) view.findViewById(R.id.nothingToShow);
            nothingToShow.setVisibility(View.VISIBLE);
        } else if (finished) {
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.PM1);
            final TextView nothingToShow = (TextView) view.findViewById(R.id.nothingToShow);
            if (tweets.size() > 0) {
                tweetList.setAdapter(new TweetRecyclerAdapter(tweets, profilePictures));
                tweetList.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                nothingToShow.setVisibility(View.GONE);
            } else {
                tweetList.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                nothingToShow.setVisibility(View.VISIBLE);
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
