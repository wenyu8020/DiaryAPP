package com.example.diaryapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Diary.class}, version = 2, exportSchema = false) // 將版本號從 1 更新到 2
public abstract class DiaryDatabase extends RoomDatabase {
    private static DiaryDatabase instance;

    public abstract DiaryDao diaryDao();

    public static synchronized DiaryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            DiaryDatabase.class, "diary_database")
                    .fallbackToDestructiveMigration() // 加入這行
                    .build();
        }
        return instance;
    }
}




