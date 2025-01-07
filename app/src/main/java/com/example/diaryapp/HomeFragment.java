package com.example.diaryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.diaryapp.data.Diary;
import com.example.diaryapp.DiaryAdapter;
import com.example.diaryapp.data.DiaryViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private DiaryViewModel diaryViewModel;
    private DiaryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView listView = view.findViewById(R.id.diary_list);
        adapter = new DiaryAdapter(getContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);
        diaryViewModel.getAllDiaries().observe(getViewLifecycleOwner(), new Observer<List<Diary>>() {
            @Override
            public void onChanged(List<Diary> diaries) {
                adapter.updateData(diaries);
            }
        });

        Button addButton = view.findViewById(R.id.add_diary_button);
        addButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_homeFragment_to_addDiaryFragment);
        });

        return view;
    }
}
