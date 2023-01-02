package com.example.nadri4_edit1;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NoticeWorker extends Worker {

    Context mContext;
    public final Integer HIGHLIGHT = 0, DAILY = 1;

    public NoticeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("HWA", "주기적 알림 작업자 실행");
        WorkManager workManager = WorkManager.getInstance(mContext);

        long delay = calculateDelay(DAILY);
        OneTimeWorkRequest dailyWorkRequest = new OneTimeWorkRequest.Builder(DailyAlertsWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build();
        workManager.enqueueUniqueWork("DAILY", ExistingWorkPolicy.REPLACE, dailyWorkRequest);


        delay = calculateDelay(HIGHLIGHT);
        OneTimeWorkRequest highlightWorkRequest = new OneTimeWorkRequest.Builder(HighlightWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build();
        workManager.enqueueUniqueWork("HIGHLIGHT", ExistingWorkPolicy.REPLACE, highlightWorkRequest);

        return Result.success();
    }

    synchronized private long calculateDelay(Integer mode){
        Calendar noticeTime = Calendar.getInstance();
        if(mode == HIGHLIGHT) {
            noticeTime.set(Calendar.HOUR_OF_DAY, 10);
            noticeTime.set(Calendar.MINUTE, 0);
        }
        else if(mode == DAILY){
            noticeTime.set(Calendar.HOUR_OF_DAY, 22);
            noticeTime.set(Calendar.MINUTE, 0);
        }
        Calendar todayTime = Calendar.getInstance();
        long delay = (noticeTime.getTimeInMillis() - todayTime.getTimeInMillis())/1000;
        if(delay < 0) {
            noticeTime.add(Calendar.DAY_OF_YEAR, 1);
            delay = (noticeTime.getTimeInMillis() - todayTime.getTimeInMillis())/1000;
        }
        Log.d("HWA", "delay : " + delay);
        return delay;
    }
}
