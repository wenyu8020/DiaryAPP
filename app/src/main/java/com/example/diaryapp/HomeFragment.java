package com.example.diaryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.diaryapp.data.Diary;
import com.example.diaryapp.data.DiaryViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private DiaryViewModel diaryViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加載佈局
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 初始化 ListView 和按鈕
        ListView listView = view.findViewById(R.id.diary_list);
        Button addButton = view.findViewById(R.id.add_diary_button);

        // 配置適配器
        ArrayAdapter<Diary> adapter = new ArrayAdapter<Diary>(requireContext(), 0, new ArrayList<>()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.diary_list_item, parent, false);
                }

                // 獲取日記對象
                Diary diary = getItem(position);

                // 綁定視圖
                ImageView photoImage = convertView.findViewById(R.id.photo_image);
                TextView timeText = convertView.findViewById(R.id.date_text);
                TextView noteText = convertView.findViewById(R.id.title_text);

                // 顯示圖片
                if (diary.getImageUri() != null) {
                    photoImage.setImageURI(Uri.parse(diary.getImageUri()));
                } else {
                    photoImage.setImageResource(R.drawable.default_image); // 顯示默認圖片
                }

                // 顯示日期和標題
                timeText.setText(diary.getDate());
                noteText.setText(diary.getTitle());

                return convertView;
            }
        };
        listView.setAdapter(adapter);

        // 初始化 ViewModel
        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        // 觀察數據變化並更新列表
        diaryViewModel.getAllDiaries().observe(getViewLifecycleOwner(), new Observer<List<Diary>>() {
            @Override
            public void onChanged(List<Diary> diaries) {
                adapter.clear();
                adapter.addAll(diaries);
                adapter.notifyDataSetChanged();
            }
        });

        // 新增日記按鈕
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddEditDiaryActivity.class);
            startActivity(intent);
        });

        // 點擊列表項目
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Diary selectedDiary = adapter.getItem(position);
            Intent intent = new Intent(requireActivity(), AddEditDiaryActivity.class);
            intent.putExtra("diaryId", selectedDiary.getId());
            startActivity(intent);
        });

        return view;
    }
}
