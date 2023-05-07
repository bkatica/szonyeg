package com.example.szonyeg;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Alarm  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new Notif(context).send("It's time to shop something!");
    }
}
