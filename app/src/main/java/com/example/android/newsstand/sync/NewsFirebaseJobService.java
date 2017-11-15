package com.example.android.newsstand.sync;

public class NewsFirebaseJobService extends com.firebase.jobdispatcher.JobService {

    private static final String LOG_TAG = NewsFirebaseJobService.class.getSimpleName();
    private TaskForScheduledJob.backgroundTask backTask;

    @Override
    public boolean onStartJob(final com.firebase.jobdispatcher.JobParameters job) {
        backTask = new TaskForScheduledJob.backgroundTask();
        backTask.execute(this);

        jobFinished(job, false);
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        if (backTask != null) backTask.cancel(true);
        return false;
    }


}
