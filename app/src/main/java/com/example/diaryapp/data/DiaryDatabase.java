package com.example.diaryapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Diary.class}, version = 8, exportSchema = false) // 將版本號從 1 改為 2
public abstract class DiaryDatabase extends RoomDatabase {
    private static DiaryDatabase instance;

    public abstract DiaryDao diaryDao();

    public static synchronized DiaryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            DiaryDatabase.class, "diary_database")
                    .fallbackToDestructiveMigration() // 添加這行
                    .build();
        }
        return instance;
    }
}





