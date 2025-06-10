package com.inhatc.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase myFirebase;
    DatabaseReference myDB_Reference = null;
    HashMap<String, Object> User_Value = null;
    private FirebaseAuth mAuth;

    EditText edtName;
    EditText edtEmail;
    EditText edtNickname;
    EditText edtPassword;
    Button btnSignup;
    String strHeader = "User Information";
    String strUName = null;
    String strUEmail = null;
    String strUNickname = null;
    String strUPassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ImageView ic_arrow = (ImageView) findViewById(R.id.ic_arrow);
        edtName = (EditText) findViewById(R.id.edtName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtNickname = (EditText) findViewById(R.id.edtNickname);
        edtPassword = (EditText) findViewById(R.id.edtLoginPassword);

        btnSignup = (Button) findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(this);

        myFirebase = FirebaseDatabase.getInstance();
        myDB_Reference = myFirebase.getReference();
        mAuth = FirebaseAuth.getInstance();

        User_Value = new HashMap<>();

        ic_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {
//        1. 이미 등록된 회원인지 확인
//        2. 등록된 회원이 아니라면 회원가입
//        3. 등록된 회원이라면 회원가입 X, 로그인 시도하라는 안내창

        if(v.getId() == R.id.btnSignup) {
            strUName = edtName.getText().toString().trim();
            strUEmail = edtEmail.getText().toString().trim();
            strUNickname = edtNickname.getText().toString().trim();
            strUPassword = edtPassword.getText().toString().trim();

            if (strUNickname.isEmpty() || strUName.isEmpty() || strUEmail.isEmpty() || strUPassword.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Firebase Realtime Database에서 ID 중복 확인
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User Information");
            userRef.orderByChild("Nickname").equalTo(strUNickname).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        Toast.makeText(SignupActivity.this, "이미 사용 중인 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 2. 중복 ID가 아니라면 Firebase Authentication으로 회원가입 시도
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(strUEmail, strUPassword)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()) {
                                        // 3. 회원가입 성공 시 userValue를 DB에 저장
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        String uid = user.getUid();

                                        User_Value.put("Nickname", strUNickname);
                                        User_Value.put("Email", strUEmail);
                                        User_Value.put("Name", strUName);

                                        userRef.child(uid).setValue(User_Value);

                                        Toast.makeText(SignupActivity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        String error = "회원가입 실패";
                                        if(task.getException() != null && task.getException().getMessage() != null) {
                                            if(task.getException().getMessage().contains("already in use")) {
                                                error = "이미 가입된 이메일 입니다.";
                                            } else {
                                                error = "오류 : " + task.getException().getMessage();
                                            }
                                        }

                                        Toast.makeText(SignupActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SignupActivity.this, "닉네임 확인 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void mSet_FirebaseDatabase(boolean bFlag) {
        if(bFlag) {
            myDB_Reference.child(strHeader).child(strUEmail).setValue(User_Value);
        }
    }
}