package com.example.diaryapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DiaryEditActivity extends AppCompatActivity {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 0x1000;
    private static final int REQUEST_GALLERY = 0x1001;

    private LinearLayout contentLayout;
    private Button saveButton;

    private Date date = new Date();

    private ArrayList<Pair<View, String>> contents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayout();

        findView();

        setView();

        setListener();
    }

    private void setLayout() {
        setContentView(R.layout.diary_edit_activity);
    }

    private void findView() {
        contentLayout = (LinearLayout) findViewById(R.id.content_layout);
        saveButton = (Button) findViewById(R.id.save_button);
    }

    private void setView() {
        setTitle(new SimpleDateFormat("yyyy, MM/dd").format(date));
        addEditText("");
    }

    private void setListener() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveDiary();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.diary_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_set_time) {
            setupTime();
        } else if (item.getItemId() == R.id.action_add_photo) {
            openGallery();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addEditText(String text) {
        EditText editText = new EditText(this);
        editText.setTextColor(0xff4a4a4a);
        editText.setTextSize(18);
        editText.setText(text);
        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.requestFocus();

        contents.add(new Pair<View, String>(editText, text));
        contentLayout.addView(editText);
    }

    private void addImageView(Uri uri) {
        if (contents.get(contents.size() - 1).first instanceof EditText) {
            EditText editText = (EditText) contents.get(contents.size() - 1).first;
            if (editText.getText().toString().length() == 0) {
                contents.remove(contents.size() - 1);
                contentLayout.removeView(editText);
            }
        }

        ImageView imageView = new ImageView(this);
        imageView.setImageURI(uri);
        imageView.setPadding(10, 10, 10, 10);

        contents.add(new Pair<View, String>(imageView, uri.toString()));
        contentLayout.addView(imageView);

        addEditText("");
    }

    private void setupTime() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                date.setYear(year - 1900);
                date.setMonth(month);
                date.setDate(day);
                date.setHours(1);
                date.setMinutes(1);
                date.setSeconds(1);

                setTitle(new SimpleDateFormat("yyyy, MM/dd").format(date));
            }

        }, year, month, day).show();
    }

    private void openGallery() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setMessage("Please authorize me.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(DiaryEditActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {

                Uri uri = data.getData();
                addImageView(uri);
            }
        }
    }

    private void saveDiary() throws Exception {
        Diary diary = new Diary();

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < contents.size(); i++) {
            Pair<View, String> content = contents.get(i);

            if (content.first instanceof EditText) {
                if (TextUtils.isEmpty(diary.previewNote)) {
                    diary.previewNote = ((EditText) content.first).getText().toString();
                }

                JSONObject editObject = new JSONObject();
                editObject.put("view", "EditText");
                editObject.put("string", ((EditText) content.first).getText().toString());
                jsonArray.put(editObject);
            } else if (content.first instanceof ImageView) {
                if (TextUtils.isEmpty(diary.previewPhotoUri)) {
                    diary.previewPhotoUri = content.second;
                }

                JSONObject imageObject = new JSONObject();
                imageObject.put("view", "ImageView");
                imageObject.put("string", content.second);
                jsonArray.put(imageObject);

                Log.v("DiaryEditActivity", "[saveDiary] " + content.second);
            }
        }

        diary.time = date.getTime();
        diary.diaryContent = jsonArray.toString();

        DiaryDb db = new DiaryDb(this);
        db.createDiary(diary);
        db.close();

        finish();
    }
}

