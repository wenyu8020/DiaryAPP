package com.example.diaryapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
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
import androidx.core.content.FileProvider;
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
        selectImageButton.setOnClickListener(v -> showImageSelectionDialog());

        // 設置保存按鈕
        saveButton.setOnClickListener(v -> saveDiary());

        // 設置分享按鈕
        Button shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(v -> {
            if (selectedImagePath != null) {
                shareDiaryWithImage();  // 如果有圖片，分享文字和圖片
            } else {
                shareDiaryText();  // 否則只分享文字
            }
        });

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

    // 顯示圖片選擇選單
    private void showImageSelectionDialog() {
        String[] options = {"從預設圖片中選擇", "從本地存儲中選擇"};

        new AlertDialog.Builder(this)
                .setTitle("選擇圖片")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showPresetImageDialog(); // 預設圖片選擇
                    } else if (which == 1) {
                        openFilePicker(); // 本地存儲選擇
                    }
                })
                .show();
    }

    // 顯示預設圖片的選單
    private void showPresetImageDialog() {
        // 預設圖片資源 ID
        int[] presetImages = {R.drawable.sample_spring, R.drawable.sample_summer, R.drawable.sample_autumn, R.drawable.sample_winter};

        // 預設圖片的名稱
        String[] imageNames = {"春", "夏", "秋", "冬"};

        new AlertDialog.Builder(this)
                .setTitle("選擇一張預設圖片")
                .setItems(imageNames, (dialog, which) -> {
                    int selectedImageResId = presetImages[which];
                    savePresetImageToInternalStorage(selectedImageResId);
                })
                .show();
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

    // 保存預設圖片到內部存儲
    private void savePresetImageToInternalStorage(int imageResId) {
        try {
            // 從資源中加載圖片
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResId);

            // 創建文件名
            String fileName = "IMG_PRESET_" + System.currentTimeMillis() + ".jpg";
            File directory = getFilesDir();
            File file = new File(directory, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            selectedImagePath = file.getAbsolutePath(); // 保存路徑
            imagePreview.setImageURI(Uri.fromFile(file)); // 預覽圖片
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "無法保存預設圖片", Toast.LENGTH_SHORT).show();
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

    private void shareDiaryText() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        String date = dateEditText.getText().toString();

        // 構建分享內容
        String diaryText = "日記標題: " + title + "\n" +
                "日期: " + date + "\n" +
                "內容: \n" + content;

        // 建立 Intent 來分享文字
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, diaryText);

        // 開啟選擇應用的介面
        startActivity(Intent.createChooser(shareIntent, "選擇應用程式"));
    }

    private void shareDiaryWithImage() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        String date = dateEditText.getText().toString();

        // 構建分享內容
        String diaryText = "日記標題: " + title + "\n" +
                "日期: " + date + "\n" +
                "內容: \n" + content;

        // 檢查是否有圖片
        if (selectedImagePath != null) {
            File imageFile = new File(selectedImagePath);

            // 使用 FileProvider 生成可分享的 URI
            Uri imageUri = FileProvider.getUriForFile(this, "com.example.diaryapp.fileprovider", imageFile);

            // 創建 Bitmap 來載入原始圖片
            Bitmap originalBitmap = BitmapFactory.decodeFile(selectedImagePath);

            // 創建新的 Bitmap，將原始圖片和文字結合
            Bitmap combinedBitmap = combineImageAndText(originalBitmap, diaryText);

            // 創建文件來保存合併後的圖片
            File combinedImageFile = new File(getFilesDir(), "combined_image.jpg");
            try (FileOutputStream out = new FileOutputStream(combinedImageFile)) {
                combinedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "無法保存合併圖片", Toast.LENGTH_SHORT).show();
                return;
            }

            // 使用 FileProvider 生成新的 URI
            Uri combinedImageUri = FileProvider.getUriForFile(this, "com.example.diaryapp.fileprovider", combinedImageFile);

            // 建立 Intent 來分享圖片
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, combinedImageUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, diaryText); // 這樣文字也會一同分享

            // 允許其他應用讀取你的圖片
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // 開啟選擇應用的介面
            startActivity(Intent.createChooser(shareIntent, "選擇應用程式"));
        } else {
            // 如果沒有圖片，則只分享文字
            shareDiaryText();
        }
    }

    private Bitmap combineImageAndText(Bitmap originalBitmap, String text) {
        // 創建一個與原始圖片相同大小的新的 Bitmap
        Bitmap combinedBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), originalBitmap.getConfig());

        // 創建 Canvas 來將圖片和文字繪製到新的 Bitmap 上
        Canvas canvas = new Canvas(combinedBitmap);

        // 先將原始圖片畫到畫布上
        canvas.drawBitmap(originalBitmap, 0, 0, null);

        // 設置文字的樣式
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);  // 白色文字
        paint.setTextSize(50);  // 設置文字大小
        paint.setStyle(Paint.Style.FILL);  // 填充樣式
        paint.setTextAlign(Paint.Align.LEFT);  // 左對齊

        // 設置文字的起始位置
        float x = 20;
        float y = originalBitmap.getHeight() - 100;  // 文字距離底部的距離

        // 將文字繪製到畫布上
        canvas.drawText(text, x, y, paint);

        return combinedBitmap;
    }
}

