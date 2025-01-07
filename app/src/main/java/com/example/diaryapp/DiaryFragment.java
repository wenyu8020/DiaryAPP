package com.example.diaryapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class DiaryFragment extends Fragment {

    private EditText etDate, etSubject, etContent;
    private Button btnSaveDiary, btnDeleteDiary, btnShareDiary;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String diaryId; // 用於識別日記

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        // 初始化 Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");

        // 初始化 UI 元件
        etDate = view.findViewById(R.id.et_date);
        etSubject = view.findViewById(R.id.et_subject);
        etContent = view.findViewById(R.id.et_content);
        btnSaveDiary = view.findViewById(R.id.btn_save_diary);
        btnDeleteDiary = view.findViewById(R.id.btn_delete_diary); // 新增刪除按鈕
        btnShareDiary = view.findViewById(R.id.btn_share_diary); // 分享按鈕

        // 獲取傳遞過來的日記資料
        Bundle args = getArguments();
        if (args != null) {
            diaryId = args.getString("diaryId");
            String date = args.getString("date", ""); // 默認值防止為 null
            String subject = args.getString("subject", "");
            String content = args.getString("content", "");

            // 顯示資料到編輯框
            etDate.setText(date);
            etSubject.setText(subject);
            etContent.setText(content);
        }

        // 儲存按鈕事件
        btnSaveDiary.setOnClickListener(v -> saveDiary());

        // 刪除按鈕事件
        btnDeleteDiary.setOnClickListener(v -> confirmDeleteDiary());

        return view;
    }

    private void saveDiary() {
        String date = etDate.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(subject) || TextUtils.isEmpty(content)) {
            Toast.makeText(getContext(), "請填寫完整資料", Toast.LENGTH_SHORT).show();
            return;
        }

        // 獲取當前使用者的 ID
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // 如果 diaryId 為空，則為新日記生成唯一 ID
        if (diaryId == null) {
            diaryId = databaseReference.child(userId).child("diaries").push().getKey();
        }

        // 確保 diaryId 不為空
        if (diaryId != null) {
            // 指向日記的資料庫路徑
            DatabaseReference userDiaryRef = databaseReference.child(userId).child("diaries").child(diaryId);

            // 覆蓋日記內容
            userDiaryRef.child("date").setValue(date);
            userDiaryRef.child("subject").setValue(subject);
            userDiaryRef.child("content").setValue(content);

            Toast.makeText(getContext(), "日記已儲存", Toast.LENGTH_SHORT).show();

            // 返回到上一個 Fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            Toast.makeText(getContext(), "儲存日記時發生錯誤", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteDiary() {
        if (diaryId == null) {
            Toast.makeText(getContext(), "日記不存在，無法刪除", Toast.LENGTH_SHORT).show();
            return;
        }

        // 彈出確認對話框
        new AlertDialog.Builder(getContext())
                .setTitle("刪除日記")
                .setMessage("確定要刪除此日記嗎？此操作無法恢復。")
                .setPositiveButton("刪除", (dialog, which) -> deleteDiary())
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteDiary() {
        // 獲取當前使用者的 ID
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // 指向日記的資料庫路徑
        DatabaseReference userDiaryRef = databaseReference.child(userId).child("diaries").child(diaryId);

        // 刪除日記
        userDiaryRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "日記已刪除", Toast.LENGTH_SHORT).show();

                    // 返回到上一個 Fragment
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "刪除日記失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void shareDiary() {
        String date = etDate.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(subject) || TextUtils.isEmpty(content)) {
            Toast.makeText(getContext(), "請確認日記內容是否完整", Toast.LENGTH_SHORT).show();
            return;
        }

        // 分享的日記內容
        String shareText = "日期: " + date + "\n" +
                "題目: " + subject + "\n" +
                "內容: " + content;

        // 創建分享 Intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "我的日記: " + subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // 啟動選擇器
        startActivity(Intent.createChooser(shareIntent, "分享到..."));
    }
}
