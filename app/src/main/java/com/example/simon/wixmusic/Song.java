package com.example.simon.wixmusic;

import android.media.MediaMetadataRetriever;

/**
 * Created by Simon on 2016-05-10.
 */
public class Song {
    String songName;
    String location;
    int time;
    int index;
    MediaMetadataRetriever metaRetriever;
    public Song(String sN, String loc)
    {
        songName=sN;
        location =loc;
        time=findTime();

    }
    public int findTime()
    {
        metaRetriever=new MediaMetadataRetriever();
        metaRetriever.setDataSource(location);
        String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long dur = Long.parseLong(duration);
        return (int)(dur/1000);
    }
    public int compareTo(Song right)
    {
        if(time==right.time)return 0;
        else if(time<right.time)return -1;
        else return 1;
    }
}
