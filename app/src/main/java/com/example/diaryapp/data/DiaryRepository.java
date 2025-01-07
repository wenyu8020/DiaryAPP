package com.example.diaryapp.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiaryRepository {

    private final DiaryDao diaryDao;
    private final LiveData<List<Diary>> allDiaries;
    private final ExecutorService executorService;

    public DiaryRepository(Application application) {
        DiaryDatabase database = DiaryDatabase.getDatabase(application);
        diaryDao = database.diaryDao();
        allDiaries = diaryDao.getAllDiaries();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Diary>> getAllDiaries() {
        return allDiaries;
    }

    public void insert(Diary diary) {
        executorService.execute(() -> diaryDao.insert(diary));
    }
}
