package com.example.diaryapp.models;

public class Diary {
    private String diaryId; // 日記的唯一 ID
    private String date;
    private String subject;
    private String content;

    // 空構造函數 (Firebase Realtime Database 需要)
    public Diary() {
    }

    public Diary(String diaryId, String date, String subject, String content) {
        this.diaryId = diaryId;
        this.date = date;
        this.subject = subject;
        this.content = content;
    }

    // Getter 和 Setter 方法
    public String getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(String diaryId) {
        this.diaryId = diaryId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}



