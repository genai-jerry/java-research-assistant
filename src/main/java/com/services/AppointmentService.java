package com.services;

import java.util.HashMap;
import java.util.Map;

import com.model.Doctor;

public class AppointmentService {
    private static Map<String, Doctor> appointments = new HashMap<>();

    public static void addAppointment(String date, Doctor doctor) {
        appointments.put(date, doctor);
    }

    public static void removeAppointment(String date) {
        appointments.remove(date);
    }

    public static Map<String, Doctor> getAppointments() {
        return appointments;
    }

    public static Doctor getAppointment(String date) {
        return appointments.get(date);
    }

    public static boolean hasAppointment(String doctorName, String date) {
        return appointments.containsKey(date) && appointments.get(date).getName().equals(doctorName);
    }
}
