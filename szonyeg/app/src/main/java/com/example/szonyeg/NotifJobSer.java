package com.example.szonyeg;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotifJobSer extends JobService {
    private  Notif mNotificationHelper;

    @Override
    public boolean onStartJob(JobParameters params) {
        mNotificationHelper = new Notif(getApplicationContext());
        mNotificationHelper.send("Végre Vehetsz Valamit! \\ (•◡•) /");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

