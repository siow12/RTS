package org.example;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.example.common.QueueEnum;
import org.example.common.SensorConnection;
import org.example.sensor.AltitudeSensor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Sensor {
    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        //Altitude
        executorService.submit(new AltitudeSensor(QueueEnum.Altitude.getName()));


        executorService.shutdown();
        System.out.println("All tasks completed.");
    }
}
