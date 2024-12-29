package com.aiclient;

import com.model.Doctor;
import com.services.DoctorLookupService;

public class AIClient {
    public static void main(String[] args) {
        Conversation conversation = new Conversation();
        initializeDoctors();
        IO io = new IO(conversation);
        io.startChat();
    }

    private static void initializeDoctors() {
        DoctorLookupService.addDoctor(new Doctor("John Doe", "Cardiologist", "123 Main St, Anytown, USA", "555-1234", "john.doe@example.com"));
        DoctorLookupService.addDoctor(new Doctor("Jane Smith", "Pediatrician", "456 Elm St, Anytown, USA", "555-5678", "jane.smith@example.com"));
        DoctorLookupService.addDoctor(new Doctor("Alice Johnson", "Dermatologist", "789 Oak St, Anytown, USA", "555-9101", "alice.johnson@example.com"));
        DoctorLookupService.addDoctor(new Doctor("Bob Brown", "Orthopedic Surgeon", "101 Pine St, Anytown, USA", "555-1213", "bob.brown@example.com"));
        DoctorLookupService.addDoctor(new Doctor("Charlie Green", "Neurologist", "202 Maple St, Anytown, USA", "555-1415", "charlie.green@example.com"));
        DoctorLookupService.addDoctor(new Doctor("Diana White", "Gynecologist", "303 Birch St, Anytown, USA", "555-1617", "diana.white@example.com"));
    }
}
