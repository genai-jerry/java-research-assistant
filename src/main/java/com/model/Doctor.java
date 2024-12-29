package com.model;

public class Doctor {
    private String name;
    private String specialty;
    private String location;
    private String phoneNumber;
    private String email;

    public Doctor(String name, String specialty, String location, String phoneNumber, String email) {
        this.name = name;
        this.specialty = specialty;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String toString() {
        return "Doctor{" +
            "name='" + name + '\'' +
            ", specialty='" + specialty + '\'' +
            ", location='" + location + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", email='" + email + '\'' +
            '}';
    }
}
