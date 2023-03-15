package org.example.actuator;

import lombok.Data;

@Data
public class AirplaneData {
    private double enginePowerInPercentage;

    private boolean oxygenMaskOpen;

    private String directionOfTail;

    //TODO add more field
}
