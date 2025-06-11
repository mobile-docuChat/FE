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
                    addMessage(message, true);
                    userInput.setText("");

                    addMessage("AI 응답 예시입니다. 궁금한거 더 있어요?", false);

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
}
