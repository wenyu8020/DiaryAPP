package com.example.diaryapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.diaryapp.data.Diary;
import com.example.diaryapp.data.DiaryViewModel;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Calendar;

public class AddEditDiaryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private DiaryViewModel diaryViewModel;
    private int diaryId = -1;

    private ImageView imagePreview;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_diary);

        EditText titleEditText = findViewById(R.id.edit_title);
        EditText contentEditText = findViewById(R.id.edit_content);
        EditText dateEditText = findViewById(R.id.edit_date);
        Button saveButton = findViewById(R.id.save_button);
        Button selectImageButton = findViewById(R.id.select_image_button);
        imagePreview = findViewById(R.id.image_preview);

        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        // 日期選擇器
        dateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (DatePicker view, int year1, int month1, int day1) -> {
                String selectedDate = year1 + "-" + (month1 + 1) + "-" + day1;
                dateEditText.setText(selectedDate);
            }, year, month, day).show();
        });

        if (getIntent().hasExtra("diaryId")) {
            diaryId = getIntent().getIntExtra("diaryId", -1);
            diaryViewModel.getAllDiaries().observe(this, diaries -> {
                for (Diary diary : diaries) {
                    if (diary.getId() == diaryId) {
                        titleEditText.setText(diary.getTitle());
                        contentEditText.setText(diary.getContent());
                        dateEditText.setText(diary.getDate());
                        if (diary.getImageUri() != null) {
                            imagePreview.setImageURI(Uri.parse(diary.getImageUri()));
                        }
                    }
                }
            });
        }

        // 選擇圖片
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // 保存按鈕
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String content = contentEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String imageUriString = selectedImageUri != null ? selectedImageUri.toString() : null;

            if (title.isEmpty() || content.isEmpty() || date.isEmpty()) {
                Toast.makeText(AddEditDiaryActivity.this, "請填寫完整資訊", Toast.LENGTH_SHORT).show();
                return;
            }

            if (diaryId == -1) {
                diaryViewModel.insert(new Diary(title, content, date, imageUriString)); // 保存圖片 URI
            } else {
                Diary diary = new Diary(title, content, date, imageUriString);
                diary.setId(diaryId);
                diaryViewModel.update(diary);
            }

            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagePreview.setImageURI(selectedImageUri);
        }
    }
}
