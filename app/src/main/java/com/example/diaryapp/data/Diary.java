package com.example.diaryapp.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "diary_table")
public class Diary {

    @PrimaryKey(autoGenerate = true)
    private int id;
    String title;
    String content;
    String date;
    String imageUri;

    // Room 使用的構造方法
    public Diary(String title, String content, String date, String imageUri) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.imageUri = imageUri;
    }

    // 無參構造方法 (可選，供其他用途使用)
    @Ignore
    public Diary() {
    }

    // Getters 和 Setters
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

    public String getDate() {
        return date;
    }

    public String getImageUri() {
        return imageUri;
    }
}


