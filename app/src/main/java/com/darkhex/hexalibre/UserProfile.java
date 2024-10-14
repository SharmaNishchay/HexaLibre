package com.darkhex.hexalibre;

public class UserProfile {
    private String name;
    private String email;
    private String rollNo;
    private String branch;
    private String semester;
    private String uid;

    public UserProfile(String name, String email, String rollNo, String branch, String semester,String uid) {
        this.name = name;
        this.email = email;
        this.rollNo = rollNo;
        this.branch = branch;
        this.semester = semester;
        this.uid=uid;
    }

    // Constructor
    public UserProfile() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getBranch() {
        return branch;
    }

    public String getSemester() {
        return semester;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
