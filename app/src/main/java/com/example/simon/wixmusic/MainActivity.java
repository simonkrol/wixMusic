package com.example.simon.wixmusic;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

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
    TextView timeLeft;
    int curSong=0;
    SongManager playList;
    boolean toBeep=false;
    int CurrentTime;
    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
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
        timeLeft=(TextView) findViewById(R.id.timeLeft);
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
                        curSong = 0;
                        Log.d("LogLog",String.valueOf(CurrentTime));
                        playList.createIndex(CurrentTime * 60);
                        Log.d("LogLog",String.valueOf(playList.toPlay.size()));
                        if(curSong<playList.toPlay.size())
                        {
                            progressBar.setMax(playList.totalPlay);

                            reset = false;
                            playSong();
                            h.postDelayed(youRunnable, 1000);
                            playButton.setImageResource(R.drawable.pause_resource);
                        }
                        else invalidPlay("No songs short enough");
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
                    //showDialog(Dialog_ID);
                    Intent intent = new Intent(getApplicationContext(), TimeChoice.class);
                    intent.putExtra("MTime", (playList.totalTime / 60));
                    intent.putExtra("Time", CurrentTime);
                    intent.putExtra("Beep", toBeep);
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

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                CurrentTime = data.getExtras().getInt("Time");
                toBeep=data.getExtras().getBoolean("Beep");
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
            //mp.seekTo((playList.toPlay.get(curSong).time*1000)-2000);
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
        else if(toBeep==true)
        {
            Beep(2);
            mp.reset();
            Reset();
        }
        else
        {
            Reset();
            mp.reset();
        }
    }
    public void Beep(int num)
    {
        for(int i=0; i<num;i++)
        {
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
        }

    }
    public class YourClass implements Runnable
    {
        public void run()
        {
            if(progress<playList.totalPlay&&mp.isPlaying()) {
                progress++;
                if (((playList.totalPlay - progress) % 60) < 10) {
                    timeLeft.setText(((playList.totalPlay - progress) / 60 + ":0" + (playList.totalPlay - progress) % 60));
                } else {
                    timeLeft.setText(((playList.totalPlay - progress) / 60 + ":" + (playList.totalPlay - progress) % 60));
                }
                progressBar.setProgress(progress);
                h.postDelayed(youRunnable, 1000);
            }
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
        timeLeft.setText("");
        playButton.setImageResource(R.drawable.play_resource);
        progress=0;
        progressBar.setProgress(progress);
    }
    public void onBackPressed()
    {
        super.onBackPressed();
        mp.stop();
    }
}
