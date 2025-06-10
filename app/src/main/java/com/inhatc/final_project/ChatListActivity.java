package com.inhatc.final_project;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatItem> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        recyclerView = findViewById(R.id.chatRecyclerView);

        //예시 채팅 데이터
        chatList = new ArrayList<>();
        chatList.add(new ChatItem(1, "소득세 관련 챗봇 1"));
        chatList.add(new ChatItem(2, "소득세 관련 챗봇 2"));
        chatList.add(new ChatItem(3, "소득세 관련 챗봇 3"));

        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                showDeleteDialog(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                Paint paint = new Paint();
                paint.setColor(Color.RED);

                if (dX < 0) {
                    Rect rect = new Rect(itemView.getRight() + (int) dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                    c.drawRect(rect, paint);

                    Drawable icon = ContextCompat.getDrawable(ChatListActivity.this, R.drawable.delete);
                    if (icon != null) {
                        int iconSize = 60;
                        int iconLeft = itemView.getRight() - iconSize - 32;
                        int iconTop = itemView.getTop() + (itemView.getHeight() - iconSize) / 2;
                        icon.setBounds(iconLeft, iconTop, iconLeft + iconSize, iconTop + iconSize);
                        icon.draw(c);
                    }
                }
            }
        });

        helper.attachToRecyclerView(recyclerView);
    }

    private void showDeleteDialog(int position) {
        ChatItem item = chatList.get(position);
        String chatRoomName = item.getTitle();

        new AlertDialog.Builder(this)
                .setTitle("삭제 확인")
                .setMessage("[" + chatRoomName + "]\n삭제하시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> chatAdapter.deleteItem(position))
                .setNegativeButton("취소", (dialog, which) -> chatAdapter.notifyItemChanged(position))
                .setCancelable(false)
                .show();
    }
}
