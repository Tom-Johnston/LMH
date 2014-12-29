package com.johnston.lmhapp.Home;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Johnston on 11/12/2014.
 */
public class TweetRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final List<Tweet> tweets;
    final Bitmap[] profilePictures;

    public TweetRecyclerAdapter(List<Tweet> passedtweets, Bitmap[] passedprofilePictures) {
        tweets = passedtweets;
        profilePictures = passedprofilePictures;
    }



    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public static class StatusHolder extends RecyclerView.ViewHolder {
        public StatusHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.status, viewGroup, false);
            return new StatusHolder(v);
        }
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tweet_item, viewGroup, false);
        return new TweetHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ((TextView) holder.itemView).setText("Finished");
        return;
        }
        position--;
        TweetHolder tweetHolder = (TweetHolder) holder;
        Tweet tweet = tweets.get(position);
        if (tweet.pictureIndex > -1) {
            tweetHolder.profilePicture.setImageBitmap(profilePictures[tweet.pictureIndex]);
        }
        if (tweet.bmp != null) {
            tweetHolder.tweetImage.setImageBitmap(tweet.bmp);
            tweetHolder.tweetImage.setVisibility(View.VISIBLE);
        } else {
            tweetHolder.tweetImage.setVisibility(View.GONE);
        }
        long timedifference = System.currentTimeMillis() - tweet.time;
        String timeString;
        if (timedifference > 1000 * 60 * 60 * 24) {
//            Over 24 hours old. Will display the date it was posted.
            Date date = new Date();
            date.setTime(tweet.time);
            timeString = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(date);
        } else if (timedifference > 1000 * 60 * 60) {
            timeString = Long.toString(timedifference / (1000 * 60 * 60)) + "h";
        } else if (timedifference > 1000 * 60) {
            timeString = Long.toString(timedifference / (1000 * 60)) + "m";
        } else {
            timeString = "Less than a minute ago";
        }
        tweetHolder.time.setText(timeString);
        tweetHolder.screenName.setText(Html.fromHtml(tweet.screenName));
        tweetHolder.handle.setText(tweet.handle);
        tweetHolder.tweetText.setText(Html.fromHtml(tweet.Text));
        tweetHolder.tweetText.setMovementMethod(LinkMovementMethod.getInstance());
        if (tweet.retweet) {
            tweetHolder.retweet.setVisibility(View.VISIBLE);
            tweetHolder.retweet.setText("Retweeted by " + tweet.retweeter);

        } else {
            tweetHolder.retweet.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return tweets.size()+1;
    }

    public static class TweetHolder extends RecyclerView.ViewHolder {
        public TextView retweet;
        public ImageView profilePicture;
        public TextView screenName;
        public TextView handle;
        public TextView time;
        public TextView tweetText;
        public ImageView tweetImage;

        public TweetHolder(View itemView) {
            super(itemView);
            retweet = (TextView) itemView.findViewById(R.id.retweet);
            profilePicture = (ImageView) itemView.findViewById(R.id.profilePicture);
            screenName = (TextView) itemView.findViewById(R.id.screenName);
            handle = (TextView) itemView.findViewById(R.id.handle);
            time = (TextView) itemView.findViewById(R.id.time);
            tweetText = (TextView) itemView.findViewById(R.id.tweetText);
            tweetImage = (ImageView) itemView.findViewById(R.id.tweetImage);
        }
    }


}
