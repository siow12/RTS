package org.example.common;


import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AltitudeSensorData extends SensorData{
    private Double feet;

    private Boolean increase;

    private Boolean decrease;
}
