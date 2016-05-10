package com.example.simon.betterlayoutwix;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.IOException;


public class MainActivity extends Activity implements MediaPlayer.OnCompletionListener
{
    private int progress=0;
    private ProgressBar progressBar;
    private boolean btnChoice=false;
    private boolean reset=true;
    private ImageButton playButton,resetButton,timeButton;
    protected AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
    protected AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
    MediaPlayer mp=new MediaPlayer();
    int curSong=0;
    SongManager playList;
    int CurrentTime;
    Handler h = new Handler();
    YourClass youRunnable = new YourClass();
    boolean allowed=true;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        playButton = (ImageButton) findViewById(R.id.playButton);
        timeButton = (ImageButton) findViewById(R.id.timeButton);
        resetButton = (ImageButton) findViewById(R.id.resetButton);
        //help=(ImageButton)findViewById(R.id.help);
        playButton.setImageResource(R.drawable.play_resource);
        resetButton.setImageResource(R.drawable.reset_resource);
        timeButton.setImageResource(R.drawable.time_resource);
        playList = new SongManager();

        CurrentTime = 5;
        printSongs(false);
        if(playList.totalTime==0)
        {
            invalidPlay("No music found.");
            allowed=false;
        }

        playButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                if(!allowed)
                {
                    invalidPlay("No music found.");
                }
                else if (mp.isPlaying())
                {
                    if (mp != null)
                    {
                        mp.pause();
                        playButton.setImageResource(R.drawable.play_resource);
                    }
                }
                else if(btnChoice==true)
                {
                    if(reset)
                    {
                        playList.createIndex(CurrentTime * 60);
                        progressBar.setMax(playList.totalPlay);
                        curSong = 0;
                        reset=false;
                        playSong();
                        h.postDelayed(youRunnable, 1000);
                        playButton.setImageResource(R.drawable.pause_resource);
                    }
                    else
                    {
                        playButton.setImageResource(R.drawable.pause_resource);
                        h.postDelayed(youRunnable, 1000);
                        mp.start();
                    }

                }
                else invalidPlay("Please choose a time before playing");

            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(!allowed)
                {
                    invalidPlay("No music found.");
                }
                else if (!reset)
                {
                    invalidPlay("Please reset before choosing a new time.");
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), NumberChoiceActivity.class);
                    intent.putExtra("MTime", (playList.totalTime / 60));
                    intent.putExtra("Time", CurrentTime);
                    startActivityForResult(intent, 1);

                }

            }
        });
        resetButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                if(mp.isPlaying())
                {

                    mp.pause();

                }
                Reset();
            }
        });
        /*help.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivityForResult(intent,1);
            }
        });*/
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                CurrentTime = data.getExtras().getInt("Time");
                btnChoice = true;
            }
            if (resultCode == Activity.RESULT_CANCELED)
            {
                //Write your code if there's no result
            }
        }
    }

    protected void invalidPlay(String x)
    {
        TextView txtView = (TextView) findViewById(R.id.textViewError);

        txtView.startAnimation(fadeIn);
        txtView.setText(x);
        txtView.startAnimation(fadeOut);
        fadeIn.setDuration(1500);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(1500);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(2200 + fadeIn.getStartOffset());
    }
    protected void playSong()
    {
        try
        {
            mp.setOnCompletionListener(this);
            mp.reset();
            mp.setDataSource(playList.toPlay.get(curSong).location);
            mp.prepare();
            mp.start();
            printSongs(true);
            //mp.seekTo((playList.toPlay.get(curSong).time*1000)-10000);
            curSong++;
        }
        catch(IOException e)
        {
            //Do nothing
        }
    }
    public void onCompletion(MediaPlayer mp)
    {

        if(curSong<playList.toPlay.size())
        {
            playSong();
        }
        else //if(Beep==true)
        {
            //playBeep();
            mp.reset();
            Reset();
        }
        //else
        // {
        // Reset();
        // mp.reset();
        //}
    }
    public class YourClass implements Runnable
    {
        public void run()
        {
            progress++;
            progressBar.setProgress(progress);
            if (progress < playList.totalPlay&&mp.isPlaying()) h.postDelayed(youRunnable, 1000);


        }
    }
    public void printSongs(boolean x)
    {
        TextView up = (TextView) findViewById(R.id.textViewUp);
        TextView upNext = (TextView) findViewById(R.id.textViewUpNext);
        if(x)
        {
            up.setText("Playing:" + playList.toPlay.get(curSong).songName);
            if ((curSong + 1) < playList.toPlay.size())
            {
                upNext.setText("Next Up:" + playList.toPlay.get(curSong + 1).songName);
            }
            else upNext.setText("Next Up:");
        }
        else
        {
            up.setText("Playing:");
            upNext.setText("Next Up:");
        }
    }
    public void Reset()
    {
        reset=true;
        printSongs(false);
        playButton.setImageResource(R.drawable.play_resource);
        progress=0;
        progressBar.setProgress(progress);
    }
}
