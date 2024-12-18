package com.example.conductordesign;

public class Driver {
    private int id;
    private String name;
    private String phone;
    private String status;

    public Driver(int id, String name, String phone, String status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }
}
