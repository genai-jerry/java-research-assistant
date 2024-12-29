package com.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.model.Doctor;

public class DoctorLookupService {
    private static List<Doctor> doctors = new ArrayList<>();

    public DoctorLookupService() {
    }

    public static void addDoctor(Doctor doctor) {
        doctors.add(doctor);
    }

    public static void removeDoctor(Doctor doctor) {
        doctors.remove(doctor);
    }

    public static List<Doctor> lookupDoctor(String name) {
        // strip any title like "Dr." or "Dr" from the name
        String doctorName = name.replaceAll("^(Dr\\. |Dr )", "");   
        return doctors.stream()
                .filter(doctor -> doctor.getName().equals(doctorName))
                .collect(Collectors.toList());
    }
}
