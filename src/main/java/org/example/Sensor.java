package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.common.QueueEnum;
import org.example.sensor.AltitudeSensor;
import org.example.sensor.CabinSensor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Sensor {
    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        //Altitude
        //executorService.execute(new AltitudeSensor(QueueEnum.Altitude.getName()));

        //Cabin
        executorService.execute(new CabinSensor(QueueEnum.Cabin.getName()));

        executorService.shutdown();
        System.out.println("All tasks completed.");
    }
}
