package com.johnston.lmhapp.Home;

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


public class TwitterScraperAsync extends AsyncTask<Handler, Void, ArrayList<Tweet>> {


    @Override
    protected ArrayList<Tweet> doInBackground(Handler... handlers) {
        Handler handler = handlers[0];
        Bitmap[] profilePictures;
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        ArrayList<String> ProfilePictureURLS = new ArrayList<String>();
        try {
            URL[] urls = new URL[5];
            urls[0] = new URL("https://twitter.com/LMHJCR");
            urls[1] = new URL("https://twitter.com/LMHITManager");
            urls[2] = new URL("https://twitter.com/lmhbursar");
            urls[3] = new URL("https://twitter.com/UniofOxford");
            urls[4] = new URL("https://twitter.com/OxfordUnion");

            long cutOffTime = 0;
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
                int numberOfTweets = 0;
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
                    if (inputLine.contains("data-nav=\"tweets\"")) {
                        start = inputLine.indexOf("title=") + 7;
                        numberOfTweets = Integer.parseInt((inputLine.substring(start, inputLine.indexOf(" Tweets"))).replaceAll(",", ""));
                    }

                    if (inputLine.contains("Icon Icon--retweet\"") && id != null) {
                        // Send previous Tweet to system.
                        Tweet tweet = new Tweet();
                        tweet.handle = handle;
                        tweet.screenName = name;
                        tweet.retweet = retweeted;
                        if (retweeted) {
                            tweet.retweeter = retweeter;
                        } else if (time < cutOffTime) {
//                             No point catching tweets we will discard.
//                            It has been moved here as retweeting an old tweet could discard a load of new tweets.
                            break;
                        }

                        tweet.pictureIndex = pictureIndex;
                        tweet.time = time;
                        tweet.Text = body;
                        tweet.id = Long.parseLong(id);

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

                    if (inputLine.contains("data-tweet-id")) {
                        start = inputLine.indexOf("data-tweet-id") + 15;
                        id = inputLine.substring(start,
                                inputLine.indexOf("\"", start));
                    }

                    if (inputLine.contains("data-retweet-id=")) {
                        start = inputLine.indexOf("data-retweet-id=") + 17;
                        id = inputLine.substring(start, inputLine.indexOf("\"", start));
                    }

                    if (inputLine.contains("<img class=\"avatar js-action-profile-avatar\"") || inputLine.contains("<img class=\"ProfileTweet-avatar js-action-profile-avatar\"")) {
                        start = inputLine.indexOf("src=\"") + 5;
                        pictureString = inputLine.substring(start, inputLine.indexOf("\"", start));
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
                    }
                }

                if (time > cutOffTime && numberOfTweets > 18) {
                    cutOffTime = time;
                }
            }
            Comparator<Tweet> tweetComparator = new TweetComparator();
            Collections.sort(tweets, tweetComparator);
            for (int i = tweets.size() - 1; i > -1; i--) {
                if (tweets.get(i).time < cutOffTime) {
                    tweets.remove(i);
                } else {
                    break;
                }
            }
            System.out.println(tweets.size());

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
            handler.obtainMessage(0, objects).sendToTarget();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    @Override
    protected void onPostExecute(ArrayList<Tweet> tweets) {


    }

}
