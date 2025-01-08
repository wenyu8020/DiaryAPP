package com.example.diaryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.diaryapp.data.Diary;
import com.example.diaryapp.data.DiaryViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private DiaryViewModel diaryViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView listView = view.findViewById(R.id.diary_list);
        Button addButton = view.findViewById(R.id.add_diary_button);

        ArrayAdapter<Diary> adapter = new ArrayAdapter<Diary>(requireContext(), 0, new ArrayList<>()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.diary_list_item, parent, false);
                }

                Diary diary = getItem(position);

                ImageView photoImage = convertView.findViewById(R.id.photo_image);
                TextView timeText = convertView.findViewById(R.id.date_text);
                TextView noteText = convertView.findViewById(R.id.title_text);

                // 使用文件路徑加載圖片
                if (diary.getImageUri() != null) {
                    File imgFile = new File(diary.getImageUri());
                    if (imgFile.exists()) {
                        photoImage.setImageURI(Uri.fromFile(imgFile));
                    } else {
                        photoImage.setImageResource(R.drawable.default_image); // 顯示默認圖片
                    }
                } else {
                    photoImage.setImageResource(R.drawable.default_image); // 顯示默認圖片
                }

                timeText.setText(diary.getDate());
                noteText.setText(diary.getTitle());

                return convertView;
            }
        };
        listView.setAdapter(adapter);

        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);
        diaryViewModel.getAllDiaries().observe(getViewLifecycleOwner(), diaries -> {
            adapter.clear();
            adapter.addAll(diaries);
            adapter.notifyDataSetChanged();
        });

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddEditDiaryActivity.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Diary selectedDiary = adapter.getItem(position);
            Intent intent = new Intent(requireActivity(), AddEditDiaryActivity.class);
            intent.putExtra("diaryId", selectedDiary.getId());
            startActivity(intent);
        });

        return view;
    }
}
