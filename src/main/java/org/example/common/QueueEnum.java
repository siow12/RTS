package org.example.common;


import lombok.Getter;

@Getter
public enum QueueEnum {
    Altitude("Altitude"),
    Cabin("Cabin"),
    Direction("Direction");


    private String name;

    QueueEnum(String name) {
        this.name = name;
    }
}
