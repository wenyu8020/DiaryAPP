package com.example.diaryapp;

public class DiaryDb {

    private Context context;
    private EasySQLiteDatabase sqLiteDatabase;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "diary_db";
    private static final String DB_TABLE = "diary_table";

    private static final String C_TIME = "c_time";
    private static final String C_PREVIEW_NOTE = "c_preview_note";
    private static final String C_PREVIEW_PHOTO_URI = "c_preview_photo_url";
    private static final String C_DIARY_CONTENT = "c_diary_content";

    private static final String[] COLUMNS = new String[]{C_TIME, C_PREVIEW_NOTE, C_PREVIEW_PHOTO_URI, C_DIARY_CONTENT};

    public DiaryDb(Context context) {
        sqLiteDatabase = new EasySQLiteDatabase(context, DB_VERSION, DB_NAME, DB_TABLE, COLUMNS);
        sqLiteDatabase.open();
    }

    public long createDiary(Diary diary) {
        return sqLiteDatabase.create(String.valueOf(diary.time), diary.previewNote, diary.previewPhotoUri, diary.diaryContent);
    }

    public ArrayList<Diary> getAllDiaries() {
        ArrayList<Diary> diaries = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.getAll();
        if (cursor != null && cursor.moveToFirst()) {

            do {

                String timeString = cursor.getString(cursor.getColumnIndex(C_TIME));
                String previewNoteString = cursor.getString(cursor.getColumnIndex(C_PREVIEW_NOTE));
                String previewPhotoUriString = cursor.getString(cursor.getColumnIndex(C_PREVIEW_PHOTO_URI));
                String diaryContentString = cursor.getString(cursor.getColumnIndex(C_DIARY_CONTENT));

                Diary diary = new Diary();
                diary.time = Long.parseLong(timeString);
                diary.previewNote = previewNoteString;
                diary.previewPhotoUri = previewPhotoUriString;
                diary.diaryContent = diaryContentString;

                diaries.add(diary);

            } while (cursor.moveToNext());

            cursor.close();
        }

        return diaries;
    }

    public void close() {
        sqLiteDatabase.close();
    }

}
