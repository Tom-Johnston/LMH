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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Johnston on 29/09/2014.
 */
public class HomeFragment extends Fragment {
    private View view;
    private ArrayList<Tweet> tweets;
    private Bitmap[] profilePictures;
    private Boolean checking;
    MenuItem actionRefresh;

    public void loadTweeterFeed() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView actionRefreshView = (ImageView) inflater.inflate(R.layout.action_refresh,null);
        Animation an = AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.rotate_animation);
        an.setRepeatCount(Animation.INFINITE);
        actionRefreshView.setAnimation(an);
        actionRefresh.setActionView(actionRefreshView);
        final ListView listView = (ListView) view.findViewById(R.id.tweetList);
        listView.setVisibility(View.GONE);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.PM1);
        progressBar.setVisibility(View.VISIBLE);
        final Context context = this.getActivity();
        Handler handler = new Handler() {
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
                Object[] objects = (Object[]) message.obj;
                tweets = (ArrayList<Tweet>) objects[0];
                profilePictures = (Bitmap[]) objects[1];

                listView.setAdapter(new TweetListAdapter(context, R.layout.tweet_item, tweets, profilePictures));
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
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
        checking = true;
        view = inflater.inflate(R.layout.home_fragment, container, false);
        if (tweets != null) {
            final ListView listView = (ListView) view.findViewById(R.id.tweetList);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.PM1);
            progressBar.setVisibility(View.GONE);
            listView.setAdapter(new TweetListAdapter(this.getActivity(), R.layout.tweet_item, tweets, profilePictures));
        } else {
            Handler startHandler = new Handler();
            Runnable startRunnable = new Runnable() {
                @Override
                public void run() {
                    loadTweeterFeed();
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
