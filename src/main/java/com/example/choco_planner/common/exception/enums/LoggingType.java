package com.example.choco_planner.common.exception.enums;

public enum LoggingType {
    API("API"),
    SERVICE("SERVICE"),
    CLIENT("CLIENT");

    private final String description;

    LoggingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

