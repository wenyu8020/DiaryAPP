package com.example.diaryapp.data;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class DiaryViewModel extends AndroidViewModel {

    private final DiaryRepository repository;
    private final LiveData<List<Diary>> allDiaries;

    public DiaryViewModel(Application application) {
        super(application);
        repository = new DiaryRepository(application);
        allDiaries = repository.getAllDiaries();
    }

    public LiveData<List<Diary>> getAllDiaries() {
        return allDiaries;
    }

    public void insert(Diary diary) {
        repository.insert(diary);
    }
}
