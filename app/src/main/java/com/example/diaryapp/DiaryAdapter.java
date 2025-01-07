package com.example.diaryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.diaryapp.data.Diary;

import java.util.List;

public class DiaryAdapter extends BaseAdapter {

    private final Context context;
    private List<Diary> diaries;

    public DiaryAdapter(Context context, List<Diary> diaries) {
        this.context = context;
        this.diaries = diaries;
    }

    @Override
    public int getCount() {
        return diaries.size();
    }

    @Override
    public Object getItem(int position) {
        return diaries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return diaries.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView titleTextView = convertView.findViewById(android.R.id.text1);
        TextView dateTextView = convertView.findViewById(android.R.id.text2);

        Diary diary = diaries.get(position);
        titleTextView.setText(diary.getTitle());
        dateTextView.setText(diary.getDate());

        return convertView;
    }

    public void updateData(List<Diary> newDiaries) {
        this.diaries = newDiaries;
        notifyDataSetChanged();
    }
}
