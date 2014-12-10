package com.johnston.lmhapp.Home;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Johnston on 29/09/2014.
 */
public class TweetListAdapter extends ArrayAdapter<Tweet> {
    final Context context;
    final int resource;
    final List<Tweet> tweets;
    final Bitmap[] profilePictures;


    public TweetListAdapter(Context passedcontext, int passedresource, List<Tweet> passedtweets, Bitmap[] passedprofilePictures) {
        super(passedcontext, passedresource, passedtweets);
        context = passedcontext;
        resource = passedresource;
        tweets = passedtweets;
        profilePictures = passedprofilePictures;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = tweets.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
        }
        TextView ScreenName = (TextView) convertView.findViewById(R.id.textView);
        TextView handle = (TextView) convertView.findViewById(R.id.textView2);
        TextView TweetText = (TextView) convertView.findViewById(R.id.textView3);
        if (tweet.pictureIndex > -1) {
            ImageView profileImage = (ImageView) convertView.findViewById(R.id.imageView);
            profileImage.setImageBitmap(profilePictures[tweet.pictureIndex]);
        }
        ImageView image = (ImageView) convertView.findViewById(R.id.tweetImage);
        if (tweet.bmp != null) {

            image.setImageBitmap(tweet.bmp);
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.GONE);
        }
        TextView time = (TextView) convertView.findViewById(R.id.time);
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
        time.setText(timeString);
        ScreenName.setText(Html.fromHtml(tweet.screenName));
        handle.setText(tweet.handle);
        TweetText.setText(Html.fromHtml(tweet.Text));
        TweetText.setMovementMethod(LinkMovementMethod.getInstance());
        TextView retweet = (TextView) convertView.findViewById(R.id.retweet);
        if (tweet.retweet) {
            retweet.setVisibility(View.VISIBLE);
            retweet.setText("Retweeted by " + tweet.retweeter);

        } else {
            retweet.setVisibility(View.GONE);
        }
        return convertView;
    }

}
