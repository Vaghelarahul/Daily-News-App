package com.example.android.newsstand.sync;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;


public class ScheduleNewsFirebaseJob {

    private static final String JOB_TAG = "job-tag";
    private static final int JOB_SCHEDULE_TIME_HOURS = 2;
    private static final int JOB_SCHEDULE_TIME_MINUTES = (int) TimeUnit.HOURS.toMinutes(JOB_SCHEDULE_TIME_HOURS);
    private static final int JOB_SCHEDULE_TIME_SECOND = (int) TimeUnit.MINUTES.toSeconds(JOB_SCHEDULE_TIME_MINUTES);

    public static boolean isJobScheduled = false;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    synchronized public static void scheduleJob(Context context) {

        if (isJobScheduled) {
            return;
        }

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job constraint = dispatcher.newJobBuilder()
                .setService(NewsFirebaseJobService.class)
                .setTag(JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setReplaceCurrent(true)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setTrigger(Trigger.executionWindow(
                        0,
                        JOB_SCHEDULE_TIME_SECOND
                ))
                .build();

        dispatcher.schedule(constraint);
        isJobScheduled = true;
    }

}
