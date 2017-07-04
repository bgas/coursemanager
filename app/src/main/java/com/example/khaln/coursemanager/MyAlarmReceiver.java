package com.example.khaln.coursemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by khaln on 7/2/17.
 */

public class MyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // Action to be performed.
        String alarmText = "CourseManager Alarm: " + intent.getStringExtra("alarmMessage");
        Toast.makeText(context, alarmText, Toast.LENGTH_LONG).show(); // For example

        wl.release();


        // Your code to execute when the alarm triggers
        // and the broadcast is received.
    }

}
