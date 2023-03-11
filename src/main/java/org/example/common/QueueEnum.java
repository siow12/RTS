package org.example.common;


import lombok.Getter;

@Getter
public enum QueueEnum {
    Altitude("Altitude");

    private String name;

    QueueEnum(String name) {
        this.name = name;
    }
}
