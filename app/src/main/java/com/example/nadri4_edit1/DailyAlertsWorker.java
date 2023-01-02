package com.example.nadri4_edit1;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DailyAlertsWorker extends Worker {
    public DailyAlertsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Intent intent = new Intent(getApplicationContext(), AlbumMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), InitApplication.DAILY_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_nadri_notice)
                .setContentTitle("나드리 알림")
                .setContentText("오늘 하루를 기록하실래요?")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(InitApplication.DAILY_NOTIFICATION_TAG, InitApplication.DAILY, notification);
        return Result.success();
    }
}
