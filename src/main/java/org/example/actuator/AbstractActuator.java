package org.example.actuator;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractActuator {
    protected AtomicReference<AirplaneData> airplaneData;
}
