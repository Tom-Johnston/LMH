package com.johnston.lmhapp.Home;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Johnston on 29/09/2014.
 */
public class HomeFragment extends Fragment {
    private View view;
    private ArrayList<Tweet> tweets;
    private Bitmap[] profilePictures;
    MenuItem actionRefresh;
    Boolean finished=false;
    Boolean refreshing=false;

    public void loadTweeterFeed() {
        refreshing=true;
        MainActivity main = (MainActivity) getActivity();
        main.startRefresh(0);
        final ListView listView = (ListView) view.findViewById(R.id.tweetList);
        listView.setVisibility(View.GONE);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.PM1);
        final TextView nothingToSHow = (TextView)view.findViewById(R.id.nothingToShow);
        progressBar.setVisibility(View.VISIBLE);
        final Context context = this.getActivity();
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                finished=true;
                refreshing=false;
                MainActivity main = (MainActivity) getActivity();
                if(main!=null) {
                    main.stopRefresh(0);
                }
                Object[] objects = (Object[]) message.obj;
                tweets = (ArrayList<Tweet>) objects[0];
                profilePictures = (Bitmap[]) objects[1];
                if(tweets.size()>0){
                    listView.setAdapter(new TweetListAdapter(context, R.layout.tweet_item, tweets, profilePictures));
                    listView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    nothingToSHow.setVisibility(View.GONE);
                }else{
                    progressBar.setVisibility(View.GONE);
                    nothingToSHow.setVisibility(View.VISIBLE);
                }


            }
        };
        new TwitterScraperAsync().execute(handler);
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
        if(refreshing){
            final ListView listView = (ListView) view.findViewById(R.id.tweetList);
            listView.setVisibility(View.GONE);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.PM1);
            progressBar.setVisibility(View.VISIBLE);
            final TextView nothingToSHow = (TextView)view.findViewById(R.id.nothingToShow);
            nothingToSHow.setVisibility(View.VISIBLE);
        } else if (finished) {
            MainActivity main = (MainActivity) getActivity();
            main.startRefresh(0);
            final ListView listView = (ListView) view.findViewById(R.id.tweetList);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.PM1);
            final TextView nothingToSHow = (TextView)view.findViewById(R.id.nothingToShow);
            if(tweets.size()>0){
                listView.setAdapter(new TweetListAdapter(this.getActivity(), R.layout.tweet_item, tweets, profilePictures));
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                nothingToSHow.setVisibility(View.GONE);
            }else{
                progressBar.setVisibility(View.GONE);
                nothingToSHow.setVisibility(View.VISIBLE);
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
