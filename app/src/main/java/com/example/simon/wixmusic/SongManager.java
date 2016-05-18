package com.example.simon.wixmusic;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 2016-05-10.
 */
public class SongManager
{
    final String Path=new String(Environment.getExternalStorageDirectory().getPath()+"/Music");
    private List<Song> songList = new ArrayList<Song>();
    public List<Song> toPlay;
    public int totalTime=0;
    public int totalPlay=0;
    Context context;

    File directory;

    public SongManager()
    {

        directory= new File(Path);
        if(directory.isDirectory())
        {
            getPlaylist();


        }

    }

    public List<Song> getPlaylist()
    {

        if (directory.listFiles(new FileExtensionFilter())!=null&&directory.listFiles(new FileExtensionFilter()).length != 0)
        {
            Song temp;
            for (File search : directory.listFiles(new FileExtensionFilter()))
            {
                String tempName = search.getName().substring(0, (search.getName().length() - 4));
                String location = search.getPath();
                temp = new Song(tempName, location);
                songList.add(temp);
                totalTime += temp.time;
            }
            sort(); //Only call if their are files in directory
        }

        return songList;
    }


    public void sort()
    {
        mergeSort(songList,0,songList.size()-1);
    }
    public List<Song> getToPlay(int target)
    {
        createIndex(target);
        return toPlay;
    }



    class FileExtensionFilter implements FilenameFilter
    {
        public boolean accept(File dir, String name)
        {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }

    private static List<Song> merge(List<Song> items, int start, int mid, int end)
    {
        Song[] temp = new Song[items.size()];
        int pos1 = start;
        int pos2 = mid + 1;
        int spot = start;

        while (!(pos1 > mid && pos2 > end))
        {
            if((pos1>mid)||((pos2<=end)&&(items.get(pos2).compareTo(items.get(pos1))<0)))
            {
                temp[spot] = items.get(pos2);
                pos2 += 1;
            }
            else
            {
                temp[spot]= items.get(pos1);
                pos1 += 1;
            }
            spot += 1;
        }

	/* copy values from temp back to items */
        for (int i = start; i <= end; i++)
        {
            items.set(i,temp[i]);
        }
        return items;
    }
    public static List<Song> mergeSort(List<Song> items, int start, int end)
    {
        if (start < end)
        {
            int mid = (start + end) / 2;
            mergeSort(items, start, mid);
            mergeSort(items, mid + 1, end);
            items=merge(items, start, mid, end);
        }
        for(int i=0;i<items.size();i++)
        {
            items.get(i).index = i;
        }
        return items;
    }

    public List<Song> createIndex(int dSeconds)
    {
        toPlay=new ArrayList<Song>();
        int randSong;
        int cSeconds=0;
        int fail=0;
        int timeDiff=0;
        final int maxSeconds=15;
        if(songList.size()>1)timeDiff=(songList.get(songList.size()-1).time)-(songList.get(songList.size()-2).time);		//Find the difference between the biggest and second biggest song

        while((dSeconds-cSeconds>maxSeconds||cSeconds>dSeconds)&&fail<1000)				//While our time isn't correct and fails is small enough
        {
            randSong=(int)(Math.random()*(songList.size()+(Math.random()/2)));					//Calculate randSong to pick, greater weighting to highest song
            if(randSong>=songList.size())randSong=(songList.size()-1);						//If greater than index allows, set to highest value
            if(valid(randSong)&&cSeconds<=dSeconds)										//Check if song is valid and if time is still okay
            {
                cSeconds+=songList.get(randSong).time;										//Add time to current Seconds
                toPlay.add(songList.get(randSong));										//Add song to the toPlay playList
            }
            else if(cSeconds>dSeconds)													//If we failed to add a song, remove a song
            {
                randSong=(int)(Math.random()*toPlay.size());							//Generate a random song to remove
                cSeconds-=toPlay.get(randSong).time;									//Reduce current seconds by the length of the song
                toPlay.remove(randSong);												//Remove song from the toPlay playList
            }
            fail++;																		//Increment fail so no infinite loop can exist

        }
        getTotalPlay();
        return toPlay;																	//Return playList
    }
    public boolean valid(int toCheck)
    {
        for(int i=0;i<toPlay.size();i++)
        {
            if(toCheck==toPlay.get(i).index)return false;
        }
        return true;
    }
    public void getTotalPlay()
    {
        totalPlay=0;
        for(int i=0;i<toPlay.size();i++)
        {
            totalPlay+=toPlay.get(i).time;
        }
    }

}

