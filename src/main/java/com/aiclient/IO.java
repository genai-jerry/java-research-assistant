package com.aiclient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.genaipeople.openai.FinishReason;
import com.genaipeople.openai.message.Message;
import com.genaipeople.openai.response.Choice;
import com.genaipeople.openai.response.Function;
import com.genaipeople.openai.response.ToolCall;
import com.genaipeople.openai.text.ChatResponse;
import com.model.Doctor;
import com.services.AppointmentService;
import com.services.DoctorLookupService;

public class IO {
    private final Conversation conversation;
    private Object waitObject = new Object();
    public IO(Conversation conversation) {
        this.conversation = conversation;
    }

    public void startChat() {
        Message message = conversation.startConversation();

        while (true) {
            CompletableFuture<ChatResponse> responseFuture = conversation.sendMessage(message);
            responseFuture.thenAccept((response) -> {
                if(response != null) {
                    for(Choice choice : response.getChoices()) {
                        if(choice.getFinishReason().equals(FinishReason.tool_calls.toString())) {
                            for(ToolCall toolCall : choice.getMessage().getToolCalls()) {
                                handleFunctionCall(toolCall.getFunction());
                            }
                        }
                        else {
                            System.out.println(choice.getMessage().getContent().getContent());
                        }
                    }
                }
                synchronized (waitObject) {
                    waitObject.notify();
                }
            }).exceptionally((exception) -> {
                exception.printStackTrace();
                System.err.println("Error sending message: " + exception.getMessage());
                synchronized (waitObject) {
                    waitObject.notify();
                }
                return null;
            });; // Wait for the response

            synchronized (waitObject) {
                try {
                    waitObject.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            message = conversation.seekInput();
            if(message.getContent().getContent().equalsIgnoreCase("exit")) {
                break;
            }
        }
    }

    public void handleFunctionCall(Function function) {
        switch(function.getName()) {
            case "lookup_doctor":
                System.out.println("Lookup doctor: " + function.getParameters());
                @SuppressWarnings("unchecked")
                Map<String, Object> doctorDetails = (Map<String, Object>) function.getParameters().get("doctorDetails");
                handleLookupDoctor(doctorDetails);
                break;
            case "schedule_appointment":
                System.out.println("Schedule appointment: " + function.getParameters());
                @SuppressWarnings("unchecked")
                Map<String, Object> appointmentDetails = (Map<String, Object>) function.getParameters().get("appointmentDetails");
                handleScheduleAppointment(appointmentDetails);
                break;
            default:
                System.out.println("Please let me know how I can help you");
                break;
        }
    }

    private void handleLookupDoctor(Map<String, Object> doctorDetails) {
        System.out.println(doctorDetails);
        if(doctorDetails.containsKey("doctorName")) {
        String doctorName = (String) doctorDetails.get("doctorName");
        List<Doctor> doctors = DoctorLookupService.lookupDoctor(doctorName);
        if(doctors.isEmpty()) {
            System.out.println("No doctor found with name: " + doctorName);
        }
        else {
            if(doctors.size() == 1) {
                System.out.println("Doctor name: " + doctors.get(0).getName());
            }
            else {
                System.out.println("Multiple doctors found with name: " + doctorName);
            }
        }
    }

    private void handleScheduleAppointment(Map<String, Object> appointmentDetails) {
        System.out.println("Schedule appointment: " + appointmentDetails);
        String doctorName = (String) appointmentDetails.get("doctorName");
        String appointmentDate = (String) appointmentDetails.get("appointmentDate");
        Doctor doctor = DoctorLookupService.lookupDoctor(doctorName).get(0);
        if(AppointmentService.hasAppointment(doctorName, appointmentDate)) {
            System.out.println("Appointment already exists for " + doctorName + " on " + appointmentDate);
        }
        else {
            AppointmentService.addAppointment(appointmentDate, doctor);
            System.out.println("Appointment scheduled for " + doctorName + " on " + appointmentDate);
        }
    }
}
