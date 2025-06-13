package com.inhatc.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;

public class ChatRoomActivity extends AppCompatActivity {

    private LinearLayout chatMsg;
    private EditText userInput; //user input
    private ImageButton sendBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        ImageView ic_arrow = (ImageView) findViewById(R.id.ic_arrow);

        // 돌아가기
        ic_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        chatMsg = findViewById(R.id.chatContainer);
        userInput = findViewById(R.id.messageInput);
        sendBtn = findViewById(R.id.sendBtn);
        ScrollView scrollView = findViewById(R.id.scrollV);

        TextView chatTitle = findViewById(R.id.chatTitle);
        String chatbotName = getIntent().getStringExtra("chatbotName"); //chatbot name at the top
        chatTitle.setText(chatbotName);

        chatMsg = findViewById(R.id.chatContainer);
        userInput = findViewById(R.id.messageInput);
        sendBtn = findViewById(R.id.sendBtn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = userInput.getText().toString().trim();

                if (!message.isEmpty()) {
                    //adding user msg
                    sendChatMessage(message);
                    userInput.setText("");

                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                }
            }
        });
    }

    private void addMessage(String text, boolean isUser) {
        TextView messageView = new TextView(this);
        messageView.setText(text);
        messageView.setPadding(24, 16, 24, 16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 8, 16, 8);

        if (isUser) {
            params.gravity = android.view.Gravity.END; //오른쪽, right
            messageView.setBackgroundResource(R.drawable.user_message_bg);
        } else {
            params.gravity = android.view.Gravity.START; //왼쪽, left
            messageView.setBackgroundResource(R.drawable.ai_message_bg);
        }

        messageView.setLayoutParams(params);
        chatMsg.addView(messageView);
    }

    //AI에 userMessage 보내고 answer 받아오는 메서드
    private void sendChatMessage(String userMessage) {
        ChatRequest request = new ChatRequest(userMessage, "default");
        ApiService api = RetrofitClient.getApiService();
        addMessage(userMessage, true);
        api.sendMessage(request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful()) {
                    String answer = response.body().getAnswer();
                    addMessage(answer, false);
                } else {
                    addMessage("응답 오류: " + response.message(), false);
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                addMessage("통신 실패: " + t.getMessage(), false);
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }
}
