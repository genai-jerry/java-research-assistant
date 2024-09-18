package com.aiclient;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class IO {
    private final Conversation conversation;
    private final Scanner scanner;

    public IO(Conversation conversation) {
        this.conversation = conversation;
        this.scanner = new Scanner(System.in);
    }

    public void startChat() {
        conversation.startConversation();
        System.out.println("Type in your questions for the Tennis expert. Type 'exit' to end the conversation.");

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("exit")) {
                break;
            }

            CompletableFuture<Void> responseFuture = conversation.sendMessage(userInput);
            responseFuture.join(); // Wait for the response

            // Print the AI's response
            String aiResponse = conversation.getMessages().get(conversation.getMessages().size() - 1).getContent();
            System.out.println("Tennis Expert: " + aiResponse);
        }

        System.out.println("Chat ended. Thank you for using the Tennis Expert AI!");
        scanner.close();
    }
}
