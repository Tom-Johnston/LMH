package com.johnston.lmhapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.net.ssl.HttpsURLConnection;


public class TwitterScraper extends AsyncTask<Handler, Void, ArrayList<Tweet>> {


    @Override
    protected ArrayList<Tweet> doInBackground(Handler... handlers) {
        Handler handler = handlers[0];
        Bitmap[] profilePictures;
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        ArrayList<String> ProfilePictureURLS = new ArrayList<String>();
        try {
            URL[] urls = new URL[2];
            urls[0] = new URL("https://twitter.com/LMHJCR");
            urls[1] = new URL("https://twitter.com/LMHITManager");
            for (int j = 0; j < urls.length; j++) {
                URL url = urls[j];
                // Note normal size is 48x48
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), "UTF-8"));
                String inputLine;
                String id = null;
                Boolean retweeted = false;
                String handle = null;
                String name = null;
                String retweeter = null;
                String body = "";
                long time = 0;
                int bodyStart;
                int bodyEnd;
                int start;
                int pictureIndex = -1;
                String pictureString;
                Bitmap picture = null;
                while (true) {
                    inputLine = in.readLine();
                    if (inputLine == null) {
                        break;
                    }
                    if (inputLine.contains("Icon Icon--retweet\"") && id != null) {
                        // Send previous Tweet to system.
                        Tweet tweet = new Tweet();
                        tweet.handle = handle;
                        tweet.screenName = name;
                        tweet.retweet = retweeted;
                        if (retweeted) {
                            tweet.retweeter = retweeter;
                        }

                        tweet.pictureIndex = pictureIndex;
                        tweet.time = time;
                        tweet.Text = body;

                        tweets.add(tweet);
                        if (picture != null) {
                            tweet.bmp = picture;
                            picture = null;
                        }
                        body = "";
                        pictureIndex = -1;
                        id = null;
                        retweeted = false;
                    }
                    if (inputLine.contains("<img class=\"TwitterPhoto-mediaSource\"")) {
                        inputLine = in.readLine();
                        start = inputLine.indexOf("src=\"") + 5;
                        String photoUrl = inputLine.substring(start, inputLine.indexOf("\"", start));
                        InputStream in2 = new java.net.URL(photoUrl).openStream();
                        picture = BitmapFactory.decodeStream(in2);
                    }

                    if (inputLine.contains("data-Tweet-id")) {
//                        System.out.println(handle);
                        start = inputLine.indexOf("data-Tweet-id") + 15;
                        id = inputLine.substring(start,
                                inputLine.indexOf("\"", start));
                    }
                    if (inputLine.contains("<img class=\"avatar js-action-profile-avatar\"") || inputLine.contains("<img class=\"ProfileTweet-avatar js-action-profile-avatar\"")) {
                        start = inputLine.indexOf("src=\"") + 5;
                        pictureString = inputLine.substring(start, inputLine.indexOf("\"", start));
//                        System.out.println("pictureString  " + pictureString);
                        for (int i = 0; i < ProfilePictureURLS.size(); i++) {
                            if (ProfilePictureURLS.get(i).equals(pictureString)) {
                                pictureIndex = i;
                            }
                        }
                        if (pictureIndex == -1) {
                            pictureIndex = ProfilePictureURLS.size();
                            ProfilePictureURLS.add(pictureString);
                        }
                    }

                    if (inputLine.contains("data-screen-name")) {
                        start = inputLine.indexOf("data-screen-name") + 18;
                        handle = "@"
                                + inputLine.substring(start,
                                inputLine.indexOf("\"", start));
                    }
                    if (inputLine.contains("data-name")) {
                        start = inputLine.indexOf("data-name") + 11;
                        name = inputLine.substring(start,
                                inputLine.indexOf("\"", start));
                    }
                    if (inputLine.contains("data-retweeter")) {
                        start = inputLine.indexOf("data-retweeter") + 16;
                        retweeted = true;
                        retweeter = inputLine.substring(start,
                                inputLine.indexOf("\"", start));
                    }
                    if (inputLine.contains("data-time")) {
                        start = inputLine.indexOf("data-time") + 11;
                        time = 1000 * Long.parseLong(inputLine.substring(start, inputLine.indexOf("\"", start)));
                    }
                    if (inputLine.contains("ProfileTweet-text")) {
                        in.readLine();
                        in.readLine();
                        inputLine = in.readLine();
                        bodyEnd = 0;
                        String bodySegment;
                        body = inputLine.substring(inputLine.indexOf(">") + 1, inputLine.indexOf("</p>"));
                        body = body.replace("\"/", "\"https://twitter.com/");
                        while (true) {
                            bodyStart = body.indexOf("class=", bodyEnd);
                            bodyEnd = body.indexOf("\"", bodyStart + 7);
                            if (bodyEnd == -1 || bodyStart == -1) {
                                break;
                            }
                            bodySegment = body.substring(bodyStart, bodyEnd);
                            body = body.replace(bodySegment, "");
                        }
//                        System.out.println(body);
                    }

                }
            }
            Comparator<Tweet> tweetComparator = new CustomComparator();
            Collections.sort(tweets, tweetComparator);
//            Get the pictures;
            profilePictures = new Bitmap[ProfilePictureURLS.size()];
            for (int i = 0; i < ProfilePictureURLS.size(); i++) {
                try {
                    InputStream in2 = new java.net.URL(ProfilePictureURLS.get(i)).openStream();
                    profilePictures[i] = BitmapFactory.decodeStream(in2);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

            }
            Object[] objects = new Object[2];
            objects[0] = tweets;
            objects[1] = profilePictures;
//            System.out.println("size of tweets" + tweets.size());
            handler.obtainMessage(0, objects).sendToTarget();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tweets;
    }

    @Override
    protected void onPostExecute(ArrayList<Tweet> tweets) {


    }

}
