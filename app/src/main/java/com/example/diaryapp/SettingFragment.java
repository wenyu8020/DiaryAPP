package com.example.diaryapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.UUID;


public class SettingFragment extends Fragment {


    private static final int PICK_IMAGE_REQUEST = 1;


    private EditText etName, etBirthday;
    private ImageView profileImage;
    private Button btnSave, btnChangePicture;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private Uri imageUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);


        // 初始化 Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");


        // 初始化 UI 元件
        profileImage = view.findViewById(R.id.profile_image);
        etName = view.findViewById(R.id.et_name);
        etBirthday = view.findViewById(R.id.et_birthday);
        btnSave = view.findViewById(R.id.btn_save);
        btnChangePicture = view.findViewById(R.id.btn_change_picture);


        // 加載使用者資料
        loadUserData();


        // 更換頭像按鈕事件
        btnChangePicture.setOnClickListener(v -> openFileChooser());


        // 儲存按鈕事件
        btnSave.setOnClickListener(v -> saveUserData());


        return view;
    }


    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);


            uploadImageToFirebase();
        }
    }


    private void uploadImageToFirebase() {
        if (imageUri != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(getContext(), "請先登入", Toast.LENGTH_SHORT).show();
                return;
            }


            String userId = currentUser.getUid();
            String fileName = UUID.randomUUID().toString(); // 為文件生成唯一名稱
            StorageReference fileReference = storageReference.child(userId).child(fileName);


            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    // 更新 Firebase Database 中的頭像 URL
                    databaseReference.child(userId).child("profileUrl").setValue(uri.toString());
                    Toast.makeText(getContext(), "頭像更新成功", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "頭像更新失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(getContext(), "未選擇圖片", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "請先登入", Toast.LENGTH_SHORT).show();
            return;
        }


        String userId = currentUser.getUid();


        // 從 Realtime Database 中加載使用者資料
        databaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String name = task.getResult().child("name").getValue(String.class);
                String birthday = task.getResult().child("birthday").getValue(String.class);
                String profileUrl = task.getResult().child("profileUrl").getValue(String.class);


                etName.setText(name);
                etBirthday.setText(birthday);


                if (profileUrl != null) {
                    Glide.with(this).load(profileUrl).into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.ic_profile_placeholder); // 預設圖片
                }
            } else {
                Toast.makeText(getContext(), "未找到使用者資料", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveUserData() {
        String name = etName.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();


        if (name.isEmpty() || birthday.isEmpty()) {
            Toast.makeText(getContext(), "請填寫完整資料", Toast.LENGTH_SHORT).show();
            return;
        }


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "請先登入", Toast.LENGTH_SHORT).show();
            return;
        }


        String userId = currentUser.getUid();
        databaseReference.child(userId).child("name").setValue(name);
        databaseReference.child(userId).child("birthday").setValue(birthday);


        Toast.makeText(getContext(), "資料儲存成功", Toast.LENGTH_SHORT).show();
    }
}





