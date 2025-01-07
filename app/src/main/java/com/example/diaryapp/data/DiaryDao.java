package com.example.diaryapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DiaryDao {

    @Insert
    void insert(Diary diary);

    @Query("SELECT * FROM diary_table ORDER BY id DESC")
    LiveData<List<Diary>> getAllDiaries();
}
