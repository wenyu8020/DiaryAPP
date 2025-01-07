package com.example.diaryapp;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class DiaryListActivity<Diary> extends AppCompatActivity {

    private ListView diaryListView;
    private Button addDiaryButton;

    private DiaryDb diaryDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayout();

        findView();

        setView();

        setListener();
    }

    private void setLayout(){  // 用來設定 Layout
        setContentView(R.layout.fragment_home);
    }

    private void findView(){ // 將需要的 View 拉出來
        diaryListView = (ListView) findViewById(R.id.diary_list);
        addDiaryButton = (Button) findViewById(R.id.add_diary_button);
    }

    private void setView(){
        if(diaryDb == null) {  // 設定 View 的畫面及資料
            diaryDb = new DiaryDb(this);
        }
        ArrayList<Diary> diaries = diaryDb.getAllDiaries();

        DiaryAdapter diaryAdapter = new DiaryAdapter(this, diaries);
        diaryListView.setAdapter(diaryAdapter);
    }

    private void setListener(){ // 設定 View 的互動功能

        addDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryListActivity.this, DiaryEditActivity.class);
                startActivity(intent);
            }
        });

        diaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Diary diary = (Diary)diaryListView.getItemAtPosition(position);
                Intent intent = DiaryReadActivity.createIntent(DiaryListActivity.this, diary);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setView();
            }
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        diaryDb.close();
    }
}