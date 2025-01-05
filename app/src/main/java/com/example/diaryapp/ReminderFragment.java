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
    private Calendar calendar;
    private AlarmManager alarmManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        timePicker = view.findViewById(R.id.timePicker);
        btnSetReminder = view.findViewById(R.id.btnSetReminder);

        // 初始化時間選擇器
        timePicker.setIs24HourView(true);

        btnSetReminder.setOnClickListener(v -> setDailyAlarm());

        return view;
    }

    private void setDailyAlarm() {
        // 取得使用者選擇的時間
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // 設定提醒時間
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 若設定的時間早於目前時間，則設定為隔天
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // 設定 AlarmManager 以每天提醒
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        // 修正 PendingIntent 的標誌
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
        // Log.d("ReminderFragment", "鬧鐘已設定: " + calendar.getTime());

        // 顯示設定成功訊息
        String text = "提醒時間已設定為每天 " + hour + ":" + (minute < 10 ? "0" + minute : minute);
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show();
    }
}