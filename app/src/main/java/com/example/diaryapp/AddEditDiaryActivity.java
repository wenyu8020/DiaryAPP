package com.example.diaryapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.diaryapp.data.Diary;
import com.example.diaryapp.data.DiaryViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class AddEditDiaryActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;

    private DiaryViewModel diaryViewModel;
    private int diaryId = -1; // 用於判斷是新增還是編輯模式
    private String selectedImagePath; // 保存圖片的路徑

    private EditText titleEditText, contentEditText, dateEditText;
    private ImageView imagePreview;
    private Button saveButton, selectImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_diary);

        // 初始化視圖
        titleEditText = findViewById(R.id.edit_title);
        contentEditText = findViewById(R.id.edit_content);
        dateEditText = findViewById(R.id.edit_date);
        imagePreview = findViewById(R.id.image_preview);
        saveButton = findViewById(R.id.save_button);
        selectImageButton = findViewById(R.id.select_image_button);

        // 初始化 ViewModel
        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        // 設置日期選擇器
        dateEditText.setOnClickListener(v -> showDatePicker());

        // 設置選擇圖片按鈕
        selectImageButton.setOnClickListener(v -> openFilePicker());

        // 設置保存按鈕
        saveButton.setOnClickListener(v -> saveDiary());

        // 如果是編輯模式，載入現有日記數據
        if (getIntent().hasExtra("diaryId")) {
            diaryId = getIntent().getIntExtra("diaryId", -1);
            diaryViewModel.getAllDiaries().observe(this, diaries -> {
                for (Diary diary : diaries) {
                    if (diary.getId() == diaryId) {
                        titleEditText.setText(diary.getTitle());
                        contentEditText.setText(diary.getContent());
                        dateEditText.setText(diary.getDate());
                        selectedImagePath = diary.getImageUri();
                        if (selectedImagePath != null) {
                            imagePreview.setImageURI(Uri.fromFile(new File(selectedImagePath)));
                        }
                    }
                }
            });
        }
    }

    // 顯示日期選擇器
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (DatePicker view, int year1, int month1, int day1) -> {
            String selectedDate = year1 + "-" + (month1 + 1) + "-" + day1;
            dateEditText.setText(selectedDate);
        }, year, month, day).show();
    }

    // 打開文件選擇器
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // 只選擇圖片
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "選擇圖片"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedImagePath = saveImageToInternalStorage(uri);
                if (selectedImagePath != null) {
                    imagePreview.setImageURI(Uri.fromFile(new File(selectedImagePath)));
                } else {
                    Toast.makeText(this, "無法保存圖片", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 保存圖片到內部存儲
    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
            File directory = getFilesDir();
            File file = new File(directory, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 保存日記數據
    private void saveDiary() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        String date = dateEditText.getText().toString();

        if (title.isEmpty() || content.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "請填寫完整資訊", Toast.LENGTH_SHORT).show();
            return;
        }

        Diary diary = new Diary(title, content, date, selectedImagePath);
        if (diaryId == -1) {
            diaryViewModel.insert(diary);
        } else {
            diary.setId(diaryId);
            diaryViewModel.update(diary);
        }

        finish();
    }
}

