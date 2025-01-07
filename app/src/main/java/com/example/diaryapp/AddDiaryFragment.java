package com.example.diaryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.diaryapp.data.Diary;
import com.example.diaryapp.data.DiaryViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddDiaryFragment extends Fragment {

    private DiaryViewModel diaryViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_diary, container, false);

        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        EditText titleEditText = view.findViewById(R.id.edit_title);
        EditText contentEditText = view.findViewById(R.id.edit_content);
        Button saveButton = view.findViewById(R.id.save_button);

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String content = contentEditText.getText().toString();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(requireContext(), "請填寫完整資訊", Toast.LENGTH_SHORT).show();
            } else {
                Diary diary = new Diary(title, content, date);
                diaryViewModel.insert(diary);
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
            }
        });

        return view;
    }
}
