package com.orb.battambang.reception;

public class Patient {
    private Integer queueNo;
    private String name;
    private String DOB;
    private Integer age;
    private Character sex;
    private String phoneNumber;
    private String address;

    // Constructor
    public Patient(Integer queueNo, String name, String DOB, Integer age, Character sex, String phoneNumber, String address) {
        this.queueNo = queueNo;
        this.name = name;
        this.DOB = DOB;
        this.age = age;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // Getters
    public Integer getQueueNo() {
        return queueNo;
    }

    public String getName() {
        return name;
    }
    public String getDOB() {
        return DOB;
    }

    public Integer getAge() {
        return age;
    }

    public Character getSex() {
        return sex;
    }

    public String getPhoneNumber() {
        return this.phoneNumber == null ? "" : this.phoneNumber;
    }
    public String getAddress() {
        return this.address == null ? "" : this.address;
    }

    // Setters (if needed, not mandatory for TableView)
    public void setQueueNo(Integer QueueNo) {
        this.queueNo = queueNo;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setSex(Character sex) {
        this.sex = sex;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

