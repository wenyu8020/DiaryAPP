package com.example.diaryapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "diary_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 建立通知管理器
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 創建通知頻道（僅在 Android 8.0 以上需要）
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "日記提醒",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("提醒寫日記的通知頻道");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // 設定通知內容
        Intent notificationIntent = new Intent(context, MainActivity.class); // 修改為你的主頁活動
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("寫日記提醒")
                .setContentText("時間到了，來寫一下今天的日記吧！")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // 發送通知
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}
