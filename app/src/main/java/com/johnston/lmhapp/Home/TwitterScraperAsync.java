package com.johnston.lmhapp.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.net.ssl.HttpsURLConnection;


class TwitterScraperAsync extends AsyncTask<Object, String, Void> {
    private Handler statusHandler;

    @Override
    protected Void doInBackground(Object[] params) {
        Handler handler = (Handler) params[0];
        Context context = (Context) params[1];
        statusHandler = (Handler)params[2];

//        Get the previously saved pictures.
        publishProgress("Checking Old Information");
        SharedPreferences pictureList = context.getSharedPreferences("PictureList", 0);
        int previousSize = pictureList.getInt("previousSize", 0);
        Long previousNumber = pictureList.getLong("previousNumber", 0);
        ArrayList<String> pictureURLs = new ArrayList<>();
        ArrayList<Long> pictureIDs = new ArrayList<>();
        ArrayList<Boolean> pictureUsed = new ArrayList<>();
        String workingLine;
        for (int i = 0; i < previousSize; i++) {
            workingLine = pictureList.getString(Integer.toString(i), "null");
            pictureURLs.add(workingLine.substring(0, workingLine.indexOf("¬")));
            pictureIDs.add(Long.parseLong(workingLine.substring(workingLine.indexOf("¬") + 1)));
            pictureUsed.add(false);
        }


        Bitmap[] profilePictures;
        ArrayList<Tweet> tweets = new ArrayList<>();
        ArrayList<String> ProfilePictureURLS = new ArrayList<>();
        try {
            URL[] urls = new URL[4];
            urls[0] = new URL("https://twitter.com/UniofOxford");
            urls[1] = new URL("https://twitter.com/LMHJCR");
            urls[2] = new URL("https://twitter.com/LMHITManager");
            urls[3] = new URL("https://twitter.com/lmhbursar");

            long cutOffTime = 0;
            for (int j = 0; j < urls.length; j++) {
                URL url = urls[j];
                publishProgress("Getting Tweets From: "+url.toString());

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
                        for (int i = 0; i < previousSize; i++) {
                            if (photoUrl.equals(pictureURLs.get(i))) {
                                pictureUsed.set(i, true);
                                File file = new File(context.getCacheDir(), Long.toString(pictureIDs.get(i)));
                                InputStream in2 = new FileInputStream(file);
                                picture = BitmapFactory.decodeStream(in2);
                                break;
                            }
                        }
                        if (picture == null) {
                            InputStream in2 = new java.net.URL(photoUrl).openStream();
                            previousNumber++;
                            File file = new File(context.getCacheDir(), Long.toString(previousNumber));
                            FileOutputStream fos = new FileOutputStream(file);
                            int length;
                            byte[] buffer = new byte[1024];
                            while ((length = in2.read(buffer)) > -1) {
                                fos.write(buffer, 0, length);
                            }
                            fos.close();

                            in2 = new FileInputStream(file);
                            picture = BitmapFactory.decodeStream(in2);

                            pictureIDs.add(previousNumber);
                            pictureURLs.add(photoUrl);
                            pictureUsed.add(true);
                        }

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

                    if (inputLine.contains("js-tweet-text")) {
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
            publishProgress("Sorting Tweets");
            Comparator<Tweet> tweetComparator = new TweetComparator();
            Collections.sort(tweets, tweetComparator);
            for (int i = tweets.size() - 1; i > -1; i--) {
                if (tweets.get(i).time < cutOffTime) {
                    tweets.remove(i);
                } else {
                    break;
                }
            }

//            Get the pictures;

            publishProgress("Getting Pictures");
            profilePictures = new Bitmap[ProfilePictureURLS.size()];
            for (int i = 0; i < ProfilePictureURLS.size(); i++) {
                try {
                    String url = ProfilePictureURLS.get(i);
                    for (int j = 0; j < previousSize; j++) {
                        if (url.equals(pictureURLs.get(j))) {
                            pictureUsed.set(j, true);
                            File file = new File(context.getCacheDir(), Long.toString(pictureIDs.get(j)));
                            InputStream in2 = new FileInputStream(file);
                            profilePictures[i] = BitmapFactory.decodeStream(in2);
                            break;
                        }
                    }
                    if (profilePictures[i] == null) {
                        InputStream in2 = new java.net.URL(url).openStream();

                        previousNumber++;
                        File file = new File(context.getCacheDir(), Long.toString(previousNumber));
                        FileOutputStream fos = new FileOutputStream(file);
                        int length;
                        byte[] buffer = new byte[1024];
                        while ((length = in2.read(buffer)) > -1) {
                            fos.write(buffer, 0, length);
                        }
                        fos.close();

                        in2 = new FileInputStream(file);
                        profilePictures[i] = BitmapFactory.decodeStream(in2);

                        pictureIDs.add(previousNumber);
                        pictureURLs.add(url);
                        pictureUsed.add(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
//            Save the new stuff.
            publishProgress("Saving New Information");
            SharedPreferences.Editor editor = pictureList.edit();

            int number = 0;
            int size = pictureIDs.size();
            for (int i = 0; i < size; i++) {
                if (pictureUsed.get(i)) {
                    editor.putString(Integer.toString(number), pictureURLs.get(i) + "¬" + Long.toString(pictureIDs.get(i)));
                    number++;
                } else {
                    File file = new File(context.getCacheDir(), Long.toString(pictureIDs.get(i)));
                    file.delete();
                }
            }
            editor.putInt("previousSize", number);
            editor.putLong("previousNumber", previousNumber);
            editor.apply();

            Object[] objects = new Object[2];
            objects[0] = tweets;
            objects[1] = profilePictures;
            handler.obtainMessage(0, objects).sendToTarget();
        } catch (MalformedURLException e) {
            handler.obtainMessage(-1, "Error getting tweets: MalformedURLException").sendToTarget();
            e.printStackTrace();
        } catch (IOException e) {
            handler.obtainMessage(-1, "Error getting tweets: IOException. Check your network connection.").sendToTarget();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        statusHandler.obtainMessage(0, values[0]).sendToTarget();
    }

}
