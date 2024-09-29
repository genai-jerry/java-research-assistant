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
    private final String apiKey = "";
    private final Chat chat = new Chat(apiKey);
    Message systemMessage = null;
    public void startConversation() {
        System.out.println("Do you want to input a custom system prompt? (y/n)");
        String response = System.console().readLine().toLowerCase();
        
        String systemPrompt;
        if (response.equalsIgnoreCase("y")) {
            System.out.println("Enter your custom system prompt:");
            systemPrompt = System.console().readLine();
        } else {
            systemPrompt = "You are an expert in the sport of Tennis. " +
                "You can answer any tennis-related questions with detailed knowledge about rules, " +
                "techniques, history, players, and tournaments.";
        }
        
        systemMessage = new Message(systemPrompt, Role.SYSTEM);
    }

    public void seekInput() {
        System.out.println("Do you want to send a message that includes image (y/n default:n)? (text/image)");
        String messageType = System.console().readLine().toLowerCase();
        
        String content;
        Message userMessage;
        
        if (messageType.equalsIgnoreCase("y")) {
            System.out.println("Enter the image URL:");
            String imageUrl = System.console().readLine();
            System.out.println("Enter your message text:");
            content = System.console().readLine();
            userMessage = new Message(content, imageUrl, Role.USER);
        } else {
            System.out.println("Enter your message:");
            content = System.console().readLine();
            userMessage = new Message(content, Role.USER);
        }
        
        messages.add(userMessage);
    }

    public CompletableFuture<Void> sendMessage() {
        messages.add(systemMessage);
        ChatRequest request = new ChatRequest(messages, "apiKey");
        request.setModel("gpt-4o");
        return chat.complete(request).thenAccept((response) -> {
            messages.clear();
            System.out.println("Response: " + response);
            Message aiMessage = response.getChoices().get(0).getMessage();
            messages.add(aiMessage);
        }).exceptionally((exception) -> {
            System.err.println("Error sending message: " + exception.getMessage());
            messages.clear();
            return null;
        });
    }

    public List<Message> getMessages() {
        return messages;
    }
}
