package com.example.diaryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.diaryapp.data.Diary;
import com.example.diaryapp.data.DiaryViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private DiaryViewModel diaryViewModel;
    private ArrayAdapter<Diary> adapter;
    private List<Diary> allDiaries = new ArrayList<>(); // 保存所有日記
    private List<Diary> filteredDiaries = new ArrayList<>(); // 篩選後的日記

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView listView = view.findViewById(R.id.diary_list);
        Button addButton = view.findViewById(R.id.add_diary_button);
        SearchView searchView = view.findViewById(R.id.search_view);

        adapter = new ArrayAdapter<Diary>(requireContext(), 0, filteredDiaries) {
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
            allDiaries.clear();
            allDiaries.addAll(diaries);
            filterDiaries(searchView.getQuery().toString());
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

        // 搜尋功能
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterDiaries(newText);
                return true;
            }
        });

        return view;
    }

    // 根據搜尋條件篩選日記
    private void filterDiaries(String query) {
        filteredDiaries.clear();
        for (Diary diary : allDiaries) {
            if (TextUtils.isEmpty(query) || diary.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredDiaries.add(diary);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
