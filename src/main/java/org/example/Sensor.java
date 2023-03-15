package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.example.common.QueueEnum;
import org.example.common.SensorConnection;
import org.example.sensor.AltitudeSensor;
import org.example.sensor.CabinSensor;
import org.example.sensor.DirectionSensor;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

@Slf4j
public class Sensor {
    public static void main(String[] args) throws Exception {

        CountDownLatch lock = new CountDownLatch((int) Constants.totalTime);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime()
                .availableProcessors());

        //Connection
        Connection connection = SensorConnection.getConnection();
        Channel channel = connection.createChannel();


        //Timer to sync all the sensor simulation
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                lock.countDown();
            }
        }, 0, Constants.interval);

        //Sensor
        ScheduledFuture<?> altitudeSensor = scheduledExecutor.scheduleAtFixedRate(
                new AltitudeSensor(QueueEnum.Altitude.getName(),
                        lock, channel), 0, Constants.interval, TimeUnit.MILLISECONDS);

        ScheduledFuture<?> cabinSensor = scheduledExecutor.scheduleAtFixedRate(
                new CabinSensor(QueueEnum.Cabin.getName(),
                        lock, channel), 0, Constants.interval, TimeUnit.MILLISECONDS);

        ScheduledFuture<?> directionSensor = scheduledExecutor.scheduleAtFixedRate(
                new DirectionSensor(QueueEnum.Direction.getName(),
                        lock, channel), 0, Constants.interval, TimeUnit.MILLISECONDS);

        //Wait the countdown to 0
        lock.await();

        //Stop the timer
        timer.cancel();

        //abort all sensor as flight simulation done
        altitudeSensor.cancel(true);
        cabinSensor.cancel(true);
        directionSensor.cancel(true);

        //Close channel
        channel.close();
        connection.close();

        scheduledExecutor.shutdown();
    }
}
