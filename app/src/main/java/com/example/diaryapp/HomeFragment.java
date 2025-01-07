package com.example.diaryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView listView = view.findViewById(R.id.diary_list);
        Button addButton = view.findViewById(R.id.add_diary_button);

        // 配置適配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        // 初始化 ViewModel
        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        // 觀察日記數據變化
        diaryViewModel.getAllDiaries().observe(getViewLifecycleOwner(), new Observer<List<Diary>>() {
            @Override
            public void onChanged(List<Diary> diaries) {
                List<String> displayList = new ArrayList<>();
                for (Diary diary : diaries) {
                    String displayText = diary.getDate() + "\n" + diary.getTitle(); // 日期 + 標題
                    displayList.add(displayText);
                }
                adapter.clear();
                adapter.addAll(displayList);
                adapter.notifyDataSetChanged();
            }
        });

        // 點擊列表項進入編輯頁面
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(requireActivity(), AddEditDiaryActivity.class);
                intent.putExtra("diaryId", diaryViewModel.getAllDiaries().getValue().get(position).getId());
                startActivity(intent);
            }
        });

        // 點擊新增日記按鈕
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddEditDiaryActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
