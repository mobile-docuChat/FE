package com.inhatc.final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatItem> chatList;

    public ChatAdapter(List<ChatItem> chatList) {
        this.chatList = chatList;
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView chatTitle;

        public ChatViewHolder(View itemView) {
            super(itemView);
            chatTitle = itemView.findViewById(R.id.chatTitle);
        }
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatItem chatItem = chatList.get(position);
        holder.chatTitle.setText(chatItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void deleteItem(int position) {
        chatList.remove(position);
        notifyItemRemoved(position);
    }
}