package com.example.simon.wixmusic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;


/**
 * Created by Simon on 2016-05-11.
 */
public class TimeChoice extends Activity
{
    ImageButton done,beep;
    TextView curTime;
    NumberPicker np, np2;
    boolean toBeep;
    int toPrint;
    int nStart=5;
    int nEnd;
    int hours, minutes;
    protected AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
    protected AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time);
        curTime=(TextView)findViewById(R.id.timeView);
        done=(ImageButton)findViewById(R.id.doneButton);
        beep=(ImageButton)findViewById(R.id.beepButton);
        done.setImageResource(R.drawable.done_resource);

        np = (NumberPicker)findViewById(R.id.numberPicker);
        np.setWrapSelectorWheel(false);
        np2 = (NumberPicker)findViewById(R.id.numberPicker2);
        np2.setWrapSelectorWheel(false);

        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            nStart=extras.getInt("Time");
            nEnd=extras.getInt("MTime");
            toBeep=extras.getBoolean("Beep");
        }
        if(toBeep==false)beep.setImageResource(R.drawable.nobeep_resource);
        else beep.setImageResource(R.drawable.beep_resource);
        hours=nEnd/60;
        if(hours>=1)
        {
            np2.setMaxValue(hours-1);
            np.setMaxValue(59);
        }
        else
        {
            minutes=nEnd%60;
            np2.setMaxValue(0);
            np.setMaxValue(minutes);
        }
        hours=nStart/60;
        minutes=nStart%60;
        np.setValue(minutes);
        np2.setValue(hours);






        done.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                minutes=np.getValue();
                hours=np2.getValue();
                toPrint=minutes+(60*hours);
                if(toPrint<5)
                {
                    invalidPlay("Number too small, please increase to at least 5", false);
                }
                else
                {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Time", toPrint);
                    returnIntent.putExtra("Beep", toBeep);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
        beep.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                assign();
            }
        });


    }
    public void assign()
    {
        if(toBeep)
        {
            invalidPlay("Cycle end signal disabled", true);
            toBeep=false;
            beep.setImageResource(R.drawable.nobeep_resource);
        }
        else
        {
            invalidPlay("Cycle end signal enabled", true);
            toBeep=true;
            beep.setImageResource(R.drawable.beep_resource);
        }
    }
    protected void invalidPlay(String x, boolean type)
    {
        if(type)curTime.setTextColor(Color.parseColor("#BBDEFB"));
        else curTime.setTextColor(Color.parseColor("#dc0000"));
        curTime.startAnimation(fadeIn);
        curTime.setText(x);
        curTime.startAnimation(fadeOut);
        fadeIn.setDuration(1500);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(1500);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(2200 + fadeIn.getStartOffset());
    }

}
