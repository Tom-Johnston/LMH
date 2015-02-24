package com.johnston.lmhapp.Home;

import java.util.Comparator;

/**
 * Created by Johnston on 29/09/2014.
 */
class TweetComparator implements Comparator<Tweet> {
    @Override
    public int compare(Tweet tweet, Tweet tweet2) {
        Long time = tweet.id;
        Long time2 = tweet2.id;
        return time2.compareTo(time);
    }
}
