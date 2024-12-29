package com.aiclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.genaipeople.openai.Chat;
import com.genaipeople.openai.Role;
import com.genaipeople.openai.message.Message;
import com.genaipeople.openai.text.ChatRequest;
import com.genaipeople.openai.text.ChatResponse;
import com.genaipeople.openai.tool.Function;
import com.genaipeople.openai.tool.Tool;
import com.genaipeople.openai.tool.type.PropertyDetails;

public class Conversation {
    private final String apiKey = "API_KEY";
    private final Chat chat = new Chat(apiKey);
    Message systemMessage = null;
    public Message startConversation() {
       String systemPrompt = "You are an assistant at a doctor's clinic that is called 'Smilyfe'. Your name is Anjali. " +
                "You can help the patient find a doctor, schedule an appointment, and answer any questions about the clinic.";
        
        systemMessage = new Message(systemPrompt, Role.system);
        return systemMessage;
    }

    private List<Tool> retrieveFunctions() {
        Function lookupDoctor = new Function("lookup_doctor", "Lookup a doctor by name");
        lookupDoctor.addParameter(new Object() {
            @PropertyDetails(description = "The name of the doctor to lookup")
            public String doctorName;
        }.getClass(), "doctorDetails", "string", null, true);

        Function scheduleAppointment = new Function("schedule_appointment", "Schedule an appointment with a doctor");
        scheduleAppointment.addParameter(new Object() {
            @PropertyDetails(description = "The name of the doctor to schedule an appointment with")
            public String doctorName;
            @PropertyDetails(description = "The date of the appointment to schedule")
            public String appointmentDate;
        }.getClass(), "appointmentDetails", "string", null, true);

        return Arrays.asList(lookupDoctor, scheduleAppointment);
    }

    public Message seekInput() {
        String content;
        Message userMessage;
        
        System.out.println("Enter your response:");
        content = System.console().readLine();
        userMessage = new Message(content, Role.user);
        return userMessage;
    }

    public CompletableFuture<ChatResponse> sendMessage(Message userMessage) {
        List<Message> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);
        ChatRequest request = new ChatRequest(messages, apiKey);
        request.setTools(retrieveFunctions());
        request.setModel("gpt-4o");
        return chat.complete(request);
    }
}
