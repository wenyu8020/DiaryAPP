package com.example.diaryapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;



public class ActivityLogin extends AppCompatActivity {

    private FirebaseAuth mAuth; // Firebase Authentication
    private EditText emailInput, passwordInput;
    private Button loginButton, signupButton;
    private GoogleSignInClient googleSignInClient;

    ShapeableImageView imageView;
    TextView name, mail;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {

                @Override

                public void onActivityResult(ActivityResult result) {
                    Log.d("GoogleSignIn", "onActivityResult triggered");
                    Log.d("GoogleSignIn", "Result code: " + result.getResultCode());


                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d("GoogleSignIn", "Sign-In successful");

                        Log.d("GoogleSignIn", "進來了!兄弟們，進來了!!!");

                        Intent data = result.getData();
                        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            // 獲取 GoogleSignInAccount，處理成功的登入結果

                            GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                            String email = signInAccount.getEmail();
                            String displayName = signInAccount.getDisplayName();
                            String idToken = signInAccount.getIdToken();

                            // 將結果顯示給用戶
                            Toast.makeText(ActivityLogin.this,
                                    "Google Sign-In Successful\nName: " + displayName + "\nEmail: " + email,
                                    Toast.LENGTH_LONG).show();

                            // 登入成功後跳轉到 MainActivity
                            Log.d("GoogleSignIn", "Sign in success");
                            Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Optional: call finish() if you want to close the login activity

                        } catch (ApiException e) {
                            // Google Sign-In 失敗，顯示錯誤信息
                            Toast.makeText(ActivityLogin.this,
                                    "Google Sign-In Failed: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        Log.e("GoogleSignIn", "Sign-In canceled by user or failed.");
                    } else {
                        Log.e("GoogleSignIn", "Unknown result code: " + result.getResultCode());
                    }
                }
            }
    );





    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(signInIntent);
        Log.d("GoogleSignIn", "in啦");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // 確保配置正確
                .requestEmail()
                .build();

        // 初始化 GoogleSignInClient
//        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(ActivityLogin.this, gso);
//        googleSignInClient = GoogleSignIn.getClient(ActivityLogin.this, gso);
        googleSignInClient.signOut();


        // 綁定按鈕點擊事件
        SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle(); // 啟動 Google 登入流程

            }
        });






    // 配置視窗邊距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化 FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // 初始化 UI 元件
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);
//        googleButton = findViewById(R.id.google_sign_in_button);


                // 註冊按鈕事件
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(ActivityLogin.this, "請輸入 Email 和 Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ActivityLogin.this, "註冊成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivityLogin.this, "註冊失敗：" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // 登入按鈕事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(ActivityLogin.this, "請輸入 Email 和 Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(ActivityLogin.this, "登入成功！歡迎：" + user.getEmail(), Toast.LENGTH_SHORT).show();
                                // TODO: 跳轉到主頁
                                Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Optional: call finish() if you want to close the login activity
                            } else {
                                Toast.makeText(ActivityLogin.this, "登入失敗：" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
