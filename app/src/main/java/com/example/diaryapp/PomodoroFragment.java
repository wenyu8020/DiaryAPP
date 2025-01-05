package com.example.diaryapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class PomodoroFragment extends Fragment {

    private static final long WORK_TIME = 25 * 60 * 1000; // 25 分鐘
    private static final long REST_TIME = 5 * 60 * 1000;  // 5 分鐘

    private TextView tvTimeLeft;
    private ProgressBar pbTimer;
    private Button btnPlayPause;
    private CountDownTimer timer;
    private boolean isRunning = false;
    private boolean isWorkTime = true; // 標記是否為工作時間
    private long timeLeftInMillis = WORK_TIME; // 預設為工作時間

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        // 初始化元件
        tvTimeLeft = view.findViewById(R.id.tvTimeLeft);
        pbTimer = view.findViewById(R.id.pbTimer);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);

        pbTimer.setMax((int) (WORK_TIME / 1000)); // 設定進度條的最大值

        // 按鈕監聽器
        btnPlayPause.setOnClickListener(v -> {
            if (isRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        updateTimerText();
        return view;
    }

    private void startTimer() {
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
                pbTimer.setProgress((int) (timeLeftInMillis / 1000));
            }

            @Override
            public void onFinish() {
                // 播放提醒音效
                MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.alarm);
                mediaPlayer.start();

                // 切換階段
                if (isWorkTime) {
                    isWorkTime = false; // 切換到休息階段
                    timeLeftInMillis = REST_TIME;
                    pbTimer.setMax((int) (REST_TIME / 1000)); // 重設進度條最大值
                } else {
                    isWorkTime = true; // 切換到工作階段
                    timeLeftInMillis = WORK_TIME;
                    pbTimer.setMax((int) (WORK_TIME / 1000)); // 重設進度條最大值
                }

                updateTimerText();
                startTimer(); // 自動啟動下一階段
            }
        }.start();

        isRunning = true;
        btnPlayPause.setText("Pause");
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
        }
        isRunning = false;
        btnPlayPause.setText("Start");
    }

    private void updateTimerText() {
        long minutes = (timeLeftInMillis / 1000) / 60;
        long seconds = (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimeLeft.setText(timeLeftFormatted);

        int progress = (int) (timeLeftInMillis / 1000);
        pbTimer.setProgress(progress);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
    }
}
