package com.inhatc.final_project;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class CreateRoomActivity extends AppCompatActivity  implements View.OnClickListener {

    private EditText chatbotNameEdt; //채팅방 이름
    private EditText fileNameEdt; //파일 이름
    private TextView fileUploadButton; //파일 업로드 이름
    private Button confirmButton; //확인

    private Uri selectedFileUri = null; //file path
    private static final int PICK_FILE_REQUEST_CODE = 101; //file select request

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_room);

        chatbotNameEdt = findViewById(R.id.chatbotNameEdt);
        fileNameEdt = findViewById(R.id.file);
        fileUploadButton = findViewById(R.id.fileUploadBtn);
        confirmButton = findViewById(R.id.btnConfirm);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.createRoom), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fileUploadButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        ImageView ic_arrow = (ImageView) findViewById(R.id.ic_arrow);

        // 돌아가기
        ic_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fileUploadBtn) {
            openFileChooser(); //file select
        } else if (v.getId() == R.id.btnConfirm) {
            String chatbotName = chatbotNameEdt.getText().toString().trim();

            if (chatbotName.isEmpty()) {
                Toast.makeText(this, "채팅방 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedFileUri != null) {
                String editedFileName = fileNameEdt.getText().toString().trim();
                String orgFileName = getFileNameFromUri(selectedFileUri);

                String finalFileName = editedFileName.isEmpty() ? orgFileName : editedFileName;
            }

            Intent intent = new Intent(CreateRoomActivity.this, ChatRoomActivity.class);
            intent.putExtra("chatbotName", chatbotName);
            startActivity(intent);
        }
    }
    //file선택 , MIMI_TYPES == 파일 형식(file type)
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] fileTypes = {
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.hancom.hwp"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, fileTypes);
        startActivityForResult(Intent.createChooser(intent, "문서 선택"), PICK_FILE_REQUEST_CODE);
    }

    // Handle selected file, 선택한 파일에 대한 정보
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                String fileName = getFileNameFromUri(selectedFileUri);
                fileNameEdt.setText(fileName);
            }
        }
    }

    //  only file name from selected file and edit
//    private String getFileNameFromUri(Uri uri ) {
//        String result = "";
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            if (idx >= 0) result = cursor.getString(idx);
//            cursor.close();
//        }
//        return result;
//    }

    private String getFileNameFromUri(Uri uri) {
        String result = "";

        if (uri == null) return result;

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx != -1) {
                    result = cursor.getString(idx);
                } else {
                    // fallback: 파일 이름 추출 실패 시 URI 경로 사용
                    result = uri.getLastPathSegment();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "파일 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
        }

        return result;
    }


}
