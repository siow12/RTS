package org.example;

import org.example.common.AltitudeSensorData;
import org.example.common.QueueEnum;
import org.example.common.SensorData;

import java.util.Random;

public class Main {


    private static final double cruisingHeightMin = 31000.0;
    private static final double cruisingHeightMax = 38000.0;
    public static void main(String[] args) throws InterruptedException {
        double altitude = 0.0;

//        //TODO Cruising
//        while(altitude < 31000){
//            double climbHeight = new Random().nextDouble() * 2000;
//            altitude += climbHeight;
//            System.out.println(altitude);
//            Thread.sleep(500);
//        }

        var d = AltitudeSensorData.builder().name("name").build();
        System.out.println(d);
    }
}
