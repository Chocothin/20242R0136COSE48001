package com.example.choco_planner.common.domain;

public class CustomUser {
    private Long id;
    private String username;
    private String name;

    public CustomUser(Long id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }
}
