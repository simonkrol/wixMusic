package com.example.simon.betterlayoutwix;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TimePicker;


/**
 * Created by Simon on 2016-05-10.
 */

public class NumberChoiceActivity extends Activity {

    private int nStart = 5;
    private int nEnd = 0;
    private TimePicker chooseTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number);
        chooseTime=(TimePicker) findViewById(R.id.timePicker);
        chooseTime.setIs24HourView(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nStart = extras.getInt("Time");
            nEnd = extras.getInt("MTime");

        }
        nEnd -= nEnd % 5;
    }
}
