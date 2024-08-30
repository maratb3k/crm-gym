package com.example.crm_gym.models;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trainee extends User {
    private Date dateOfBirth;
    private String address;

    public Trainee(int id, String firstName, String lastName, String username, String password, boolean isActive, Date dateOfBirth, String address) {
        super(id, firstName, lastName, username, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Trainee{" +
                "userId=" + getUserId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", isActive=" + isActive() +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                '}';
    }
}
