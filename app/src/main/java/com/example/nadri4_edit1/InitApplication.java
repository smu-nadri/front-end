package com.example.nadri4_edit1;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class InitApplication extends Application {
    private Context mContext;

    public static final Integer HIGHLIGHT = 0, DAILY = 1;

    public static final String HIGHLIGHT_NOTIFICATION_CHANNEL_ID = "highlight_channel_id";
    public static final String HIGHLIGHT_NOTIFICATION_TAG = "highlight_tag";

    public static final String DAILY_NOTIFICATION_CHANNEL_ID = "daily_channel_id";
    public static final String DAILY_NOTIFICATION_TAG = "daily_tag";


    @Override
    public void onCreate() {
        super.onCreate();
        ReqServer.android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        CharSequence highlight_name = "하이라이트 알림";
        String highlight_description = "하이라이트가 도착하면 알림을 받습니다.";

        CharSequence daily_name = "일일 기록 알림";
        String daily_description = "일일 기록을 위한 알림을 받습니다.";

        createNotificationChannel(HIGHLIGHT_NOTIFICATION_CHANNEL_ID, highlight_name, highlight_description);
        createNotificationChannel(DAILY_NOTIFICATION_CHANNEL_ID, daily_name, daily_description);
        //deleteNotificationChannel(HIGHLIGHT_NOTIFICATION_CHANNEL_ID);

        createHighlightWorkRequest();
        createDailyWorkRequest();
        //deleteWorkRequest();

        printWorkRequest();



        try {
            //deleteFile("facelist.tmp");
            FileInputStream fis = openFileInput("facelist.tmp");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<FaceRecognize.Recognition> loadFaces = (ArrayList<FaceRecognize.Recognition>) ois.readObject();
            ois.close();

            FaceRecognitionAPI.setRegistered(loadFaces);
            int lastIdx = FaceRecognitionAPI.getRegistered().size() - 1;
            int nextId = FaceRecognitionAPI.getRegistered().get(lastIdx).getId() + 1;
            FaceRecognitionAPI.setFaceId(nextId);

        } catch (FileNotFoundException e){
            FaceRecognitionAPI.setFaceId(0);
        }
        catch (IOException | ClassNotFoundException e){
            Log.e("InitApplication", String.valueOf(e));
        }
    }

    private void createNotificationChannel(String id, CharSequence name, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void deleteNotificationChannel(String channelId) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId);
        }
    }

    private void createHighlightWorkRequest(){
        long delay = calculateDelay(HIGHLIGHT);
        //요청 만들기
        //하루 간격으로 마지막 15분 안에 실행, 딜레이는 처음에만 적용됨
        PeriodicWorkRequest highlightWorkRequest = new PeriodicWorkRequest.Builder(HighlightWorker.class, 24, TimeUnit.HOURS,1, TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build();

        //관리자 만들고 요청 넣기
        //유니크이름을 만들어서 있으면 그대로 유지하는 방식으로 진행
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueueUniquePeriodicWork("HIGHLIGHT", ExistingPeriodicWorkPolicy.KEEP, highlightWorkRequest);
    }

    private void createDailyWorkRequest(){
        long delay = calculateDelay(DAILY);
        //요청 만들기
        PeriodicWorkRequest dailyWorkRequest = new PeriodicWorkRequest.Builder(DailyAlertsWorker.class, 24, TimeUnit.HOURS,1, TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build();

        //관리자 만들고 요청 넣기
        //유니크이름을 만들어서 있으면 그대로 유지하는 방식으로 진행
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueueUniquePeriodicWork("DAILY", ExistingPeriodicWorkPolicy.KEEP, dailyWorkRequest);
    }

    private void deleteWorkRequest() {
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.cancelAllWork();    //모든 요청 취소하기
        workManager.pruneWork();        //취소 혹은 완료된 요청 지우기
    }

    private void printWorkRequest() {
        try {
            WorkManager workManager = WorkManager.getInstance(this);
            List<WorkInfo> workInfoList = null;

            WorkQuery workQuery = WorkQuery.Builder.fromStates(Arrays.asList(WorkInfo.State.FAILED, WorkInfo.State.CANCELLED,
                    WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED, WorkInfo.State.SUCCEEDED, WorkInfo.State.RUNNING))
                    .build();
            workInfoList = workManager.getWorkInfos(workQuery).get();

            for(int i = 0; i<workInfoList.size(); i++) {
                Log.d("workinfo", i + " : " + workInfoList.get(i));
            }
        } catch (ExecutionException e) {
            Log.e("workinfo", String.valueOf(e));
        } catch (InterruptedException e) {
            Log.d("workinfo", String.valueOf(e));
        }
    }

    synchronized private long calculateDelay(Integer mode){
        Calendar noticeTime = Calendar.getInstance();
        if(mode == HIGHLIGHT) {
            noticeTime.set(Calendar.HOUR_OF_DAY, 10);
        }
        else if(mode == DAILY){
            noticeTime.set(Calendar.HOUR_OF_DAY, 22);
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
