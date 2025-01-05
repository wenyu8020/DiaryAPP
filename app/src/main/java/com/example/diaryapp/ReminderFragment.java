package com.example.diaryapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Calendar;

public class ReminderFragment extends Fragment {

    private TimePicker timePicker;
    private Button btnSetReminder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        timePicker = view.findViewById(R.id.timePicker);
        btnSetReminder = view.findViewById(R.id.btnSetReminder);

        // 設置按鈕點擊事件
        btnSetReminder.setOnClickListener(v -> setDailyReminder());

        return view;
    }

    private void setDailyReminder() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // 設定提醒時間
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // 如果時間已過，將日期加一天
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        // 建立提醒的 Intent 和 PendingIntent
        Intent intent = new Intent(getContext(), ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // 設定 AlarmManager
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);

            Toast.makeText(getContext(), "提醒設定成功！時間：" + hour + ":" + minute, Toast.LENGTH_SHORT).show();
        }
    }
}
