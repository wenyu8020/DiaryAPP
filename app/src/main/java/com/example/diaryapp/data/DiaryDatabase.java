package com.example.diaryapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Diary.class}, version = 1, exportSchema = false)
public abstract class DiaryDatabase extends RoomDatabase {

    private static volatile DiaryDatabase INSTANCE;

    public abstract DiaryDao diaryDao();

    public static DiaryDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (DiaryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DiaryDatabase.class, "diary_database").build();
                }
            }
        }
        return INSTANCE;
    }
}

