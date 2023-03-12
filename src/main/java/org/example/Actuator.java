package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.example.actuator.AirplaneData;
import org.example.actuator.EngineActuator;
import org.example.common.ActuatorConnection;
import org.example.common.AltitudeSensorData;
import org.example.common.QueueEnum;
import org.example.common.SensorConnection;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class Actuator {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        AtomicReference<AirplaneData> airplaneData = new AtomicReference<>(new AirplaneData());
        executorService.execute(new EngineActuator(airplaneData));
        executorService.shutdown();
    }
}
