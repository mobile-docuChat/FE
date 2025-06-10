package com.inhatc.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edtEmail;
    private EditText edtPw;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView ic_arrow = (ImageView) findViewById(R.id.ic_arrow);
        edtEmail = (EditText) findViewById(R.id.edtLoginEmail);
        edtPw = (EditText) findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();

        // 돌아가기
        ic_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼 클릭
        btnLogin.setOnClickListener(view -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPw.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        // 로그인 성공 시
                        if(task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "환영합니다.", Toast.LENGTH_SHORT).show();

                            // 다음 화면으로 이동
                            Intent intent = new Intent(LoginActivity.this, ChatListActivity.class);
                            startActivity(intent);
                            finish();
                        } else{
                            Toast.makeText(LoginActivity.this, "로그인 실패 : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("LoginError", task.getException().toString());
                        }
                    });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

//    1. 가입된 회원인지 확인
//    2. 정보가 일치하다면 로그인
//    3. 없는 정보라면 가입되지 않았다
//    4. +) 회원정보 찾기 -> 이름으로 검색 -> 어느 플랫폼 이메일인지 알려주기 -> 이메일 일치할 경우 비밀번호 변경
}