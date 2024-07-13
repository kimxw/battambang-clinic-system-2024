package com.orb.battambang.login;

public class Staff {
    private int staffID;
    private String firstName;
    private String lastName;
    private String username;
    private String primaryRole;
    private boolean admin;
    private boolean reception;
    private boolean triage;
    private boolean education;
    private boolean consultation;
    private boolean pharmacy;
    private String location;

    public Staff(int staffID, String firstName, String lastName, String username, String primaryRole,
                 boolean admin, boolean reception, boolean triage, boolean education, boolean consultation,
                 boolean pharmacy) {
        this.staffID = staffID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.primaryRole = primaryRole;
        this.admin = admin;
        this.reception = reception;
        this.triage = triage;
        this.education = education;
        this.consultation = consultation;
        this.pharmacy = pharmacy;
    }

    public Staff() {}


    public int getStaffID() {
        return staffID;
    }


    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public String getUsername() {
        return username;
    }


    public String getPrimaryRole() {
        return primaryRole;
    }


    public boolean isAdmin() {
        return admin;
    }


    public boolean isReception() {
        return reception;
    }


    public boolean isTriage() {
        return triage;
    }


    public boolean isEducation() {
        return education;
    }


    public boolean isConsultation() {
        return consultation;
    }


    public boolean isPharmacy() {
        return pharmacy;
    }

    public String getLocation() {
        return this.location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "staffID=" + staffID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", primaryRole='" + primaryRole + '\'' +
                ", admin=" + admin +
                ", reception=" + reception +
                ", triage=" + triage +
                ", education=" + education +
                ", consultation=" + consultation +
                ", pharmacy=" + pharmacy +
                '}';
    }
}

