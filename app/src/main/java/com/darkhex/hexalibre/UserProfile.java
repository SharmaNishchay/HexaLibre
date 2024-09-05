package com.darkhex.hexalibre;

public class UserProfile {
    private String name;
    private String email;
    private int honorScore;
    private String rollNumber;

    // Constructor
    public UserProfile(String name, String email, int honorScore, String rollNumber) {
        this.name = name;
        this.email = email;
        this.honorScore = honorScore;
        this.rollNumber = rollNumber;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getHonorScore() {
        return honorScore;
    }

    public String getRollNumber() {
        return rollNumber;
    }
}
