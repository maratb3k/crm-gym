package com.example.crm_gym.models;

public class Trainer extends User {
    private String specialization;

    public Trainer(int id, String firstName, String lastName, String username, String password, boolean isActive, String specialization) {
        super(id, firstName, lastName, username, password, isActive);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "Id=" + getUserId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", isActive=" + isActive() +
                ", specialization=" + getSpecialization() +
                '}';
    }
}
