package com.aiclient;

public class AIClient {
    public static void main(String[] args) {
        Conversation conversation = new Conversation();
        IO io = new IO(conversation);
        io.startChat();
    }
}
