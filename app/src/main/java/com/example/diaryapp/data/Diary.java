package com.example.diaryapp.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diary_table")
public class Diary {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;

    public Diary(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}

