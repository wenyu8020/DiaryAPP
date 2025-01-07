package com.example.diaryapp.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaryapp.R;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private final List<Diary> diaries;
    private final OnDiaryClickListener onDiaryClickListener;

    public interface OnDiaryClickListener {
        void onDiaryClick(Diary diary);
    }

    public DiaryAdapter(List<Diary> diaries, OnDiaryClickListener onDiaryClickListener) {
        this.diaries = diaries;
        this.onDiaryClickListener = onDiaryClickListener;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        Diary diary = diaries.get(position);
        holder.tvDate.setText(diary.getDate());
        holder.tvSubject.setText(diary.getSubject());

        holder.itemView.setOnClickListener(v -> onDiaryClickListener.onDiaryClick(diary));
    }

    @Override
    public int getItemCount() {
        return diaries.size();
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvSubject;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvSubject = itemView.findViewById(R.id.tv_subject);
        }
    }
}


