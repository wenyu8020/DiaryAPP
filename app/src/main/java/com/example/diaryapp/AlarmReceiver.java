package com.example.diaryapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    // NotificationManager 與 Notification 物件
    private NotificationManager notificationManager;
    private Notification notification;

    // 建立通知的唯一 ID
    private final static int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "提醒接收成功");

        // 點擊通知後，啟動日記的主頁面
        Intent notifyIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notifyIntent,
                PendingIntent.FLAG_IMMUTABLE);

        // 發送提醒通知
        sendNotification(context, pendingIntent);
    }

    // 建立並發送通知的方法
    private void sendNotification(Context context, PendingIntent pendingIntent) {
        // 獲取 NotificationManager
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 建立通知頻道 (Android 8.0 及以上需要頻道)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "diary_reminder_channel";
            String channelName = "日記提醒頻道";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);

            // 使用頻道建立通知
            notification = new Notification.Builder(context, channelId)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.nav_reminder)
                    .setContentTitle("提醒寫日記")
                    .setContentText("記得寫日記記錄今天的點滴哦！")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 100, 200, 300, 400, 500})
                    .build();
        } else {
            // 不需要頻道的通知 (Android 8.0 以下)
            notification = new Notification.Builder(context)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.nav_reminder)
                    .setContentTitle("提醒寫日記")
                    .setContentText("記得寫日記記錄今天的點滴哦！")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 100, 200, 300, 400, 500})
                    .build();
        }

        // 發送通知
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}

