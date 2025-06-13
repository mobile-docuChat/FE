package com.inhatc.final_project;

public class ChatRequest {
    private String message;
    private String session_id;

    public ChatRequest(String message, String session_id) {
        this.message = message;
        this.session_id = session_id;
    }
}

