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
public class TweetRecyclerAdapter extends RecyclerView.Adapter<TweetRecyclerAdapter.TweetHolder> {
    final List<Tweet> tweets;
    final Bitmap[] profilePictures;

    public static class TweetHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
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


    public TweetRecyclerAdapter(List<Tweet> passedtweets, Bitmap[] passedprofilePictures){
        tweets = passedtweets;
        profilePictures = passedprofilePictures;
    }

    @Override
    public TweetHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tweet_item, viewGroup, false);
        return new TweetHolder(v);
    }

    @Override
    public void onBindViewHolder(TweetHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        if (tweet.pictureIndex > -1) {
            holder.profilePicture.setImageBitmap(profilePictures[tweet.pictureIndex]);
        }
        if (tweet.bmp != null) {
            holder.tweetImage.setImageBitmap(tweet.bmp);
            holder.tweetImage.setVisibility(View.VISIBLE);
        } else {
            holder.tweetImage.setVisibility(View.GONE);
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
        holder.time.setText(timeString);
        holder.screenName.setText(Html.fromHtml(tweet.screenName));
        holder.handle.setText(tweet.handle);
        holder.tweetText.setText(Html.fromHtml(tweet.Text));
        holder.tweetText.setMovementMethod(LinkMovementMethod.getInstance());
        if (tweet.retweet) {
            holder.retweet.setVisibility(View.VISIBLE);
            holder.retweet.setText("Retweeted by " + tweet.retweeter);

        } else {
            holder.retweet.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


}
