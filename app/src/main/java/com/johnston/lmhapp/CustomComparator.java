package com.johnston.lmhapp;

import java.util.Comparator;

/**
 * Created by Johnston on 29/09/2014.
 */
public class CustomComparator implements Comparator<Tweet> {
    @Override
    public int compare(Tweet tweet, Tweet tweet2) {
        Long time = tweet.time;
        Long time2 = tweet2.time;
        return time2.compareTo(time);
    }
}
