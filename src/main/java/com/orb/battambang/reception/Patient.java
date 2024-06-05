package com.orb.battambang.reception;

public class Patient {
    private Integer queueNo;
    private String name;
    private Integer age;
    private Character sex;
    private String phoneNumber;

    // Constructor
    public Patient(Integer queueNo, String name, Integer age, Character sex, String phoneNumber) {
        this.queueNo = queueNo;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public Integer getQueueNo() {
        return queueNo;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Character getSex() {
        return sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Setters (if needed, not mandatory for TableView)
    public void setQueueNo(Integer QueueNo) {
        this.queueNo = queueNo;
    }

    public void setName(String name) {
        this.name = name;
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
}

