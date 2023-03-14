package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.actuator.AirplaneData;
import org.example.actuator.EngineActuator;
import org.example.actuator.OxygenMaskActuator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
public class Actuator {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        AtomicReference<AirplaneData> airplaneData = new AtomicReference<>(new AirplaneData());
        executorService.execute(EngineActuator.builder().airplaneData(airplaneData).build());
        //executorService.execute(OxygenMaskActuator.builder().airplaneData(airplaneData).build());
        executorService.shutdown();

    }
}
