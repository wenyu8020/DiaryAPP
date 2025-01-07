package com.example.diaryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.diaryapp.data.Diary;
import com.example.diaryapp.data.DiaryViewModel;

public class AddEditDiaryActivity extends AppCompatActivity {

    private DiaryViewModel diaryViewModel;
    private int diaryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_diary);

        EditText titleEditText = findViewById(R.id.edit_title);
        EditText contentEditText = findViewById(R.id.edit_content);
        Button saveButton = findViewById(R.id.save_button);

        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        // 如果有傳遞 diaryId，則進入編輯模式
        if (getIntent().hasExtra("diaryId")) {
            diaryId = getIntent().getIntExtra("diaryId", -1);
            diaryViewModel.getAllDiaries().observe(this, diaries -> {
                for (Diary diary : diaries) {
                    if (diary.getId() == diaryId) {
                        titleEditText.setText(diary.getTitle());
                        contentEditText.setText(diary.getContent());
                    }
                }
            });
        }

        // 保存按鈕的點擊事件
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String content = contentEditText.getText().toString();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(AddEditDiaryActivity.this, "請填寫完整資訊", Toast.LENGTH_SHORT).show();
                return;
            }

            if (diaryId == -1) {
                diaryViewModel.insert(new Diary(title, content));
            } else {
                Diary diary = new Diary(title, content);
                diary.setId(diaryId);
                diaryViewModel.update(diary);
            }

            finish();
        });
    }
}
