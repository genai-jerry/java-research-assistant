package com.aiclient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.genaipeople.openai.Chat;
import com.genaipeople.openai.Role;
import com.genaipeople.openai.message.Message;
import com.genaipeople.openai.text.ChatRequest;

public class Conversation {
    private final List<Message> messages = new ArrayList<>();
    private final Chat chat = new Chat("api-key");

    public void startConversation() {
        String systemPrompt = "You are an expert in the sport of Tennis. " +
        "You can answer any tennis-related questions with detailed knowledge about rules, " +
        "techniques, history, players, and tournaments.";
        Message systemMessage = new Message(systemPrompt,Role.SYSTEM);
        messages.add(systemMessage);
    }

    public CompletableFuture<Void> sendMessage(String content) {
        Message userMessage = new Message(content, Role.USER);
        messages.add(userMessage);
        ChatRequest request = new ChatRequest(messages, "apiKey");
        request.setModel("gpt-4o");
        return chat.complete(request).thenAccept((response) -> {
            Message aiMessage = response.getChoices().get(0).getMessage();
            messages.add(aiMessage);
        }).exceptionally((exception) -> {
            System.err.println("Error sending message: " + exception.getMessage());
            return null;
        });
    }

    public List<Message> getMessages() {
        return messages;
    }
}
