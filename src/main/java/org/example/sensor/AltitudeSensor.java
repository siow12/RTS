package org.example.sensor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class AltitudeSensor implements Runnable {

    private static final double cruisingHeightMin = 31000.0;
    private static final double cruisingHeightMax = 38000.0;
    private String queueName;

    @Override
    public void run() {
        //Altitude Simulation
        try (Connection con = SensorConnection.getConnection()) {
            Channel ch = con.createChannel();
            ch.exchangeDeclare(QueueEnum.Altitude.getName(), BuiltinExchangeType.DIRECT);
            double altitude = 0.0;

            //Take Off
            while (Double.compare(altitude, cruisingHeightMin) < 0) {
                double climbHeight = new Random().nextDouble() * 1200;
                altitude += climbHeight;
                publish(queueName, AltitudeSensorData.builder()
                        .name(queueName)
                        .id(UUID.randomUUID().toString())
                        .meter(altitude)
                        .increase(true)
                        .decrease(false)
                        .timestamp(Instant.now())
                        .build(), ch);

                Thread.sleep(200);
            }

            //Cruising when react cruising height
            for(int i=0; i<10; i++){
                altitude = new Random().nextDouble(30000, 40000);
                publish(queueName, AltitudeSensorData.builder()
                        .name(queueName)
                        .id(UUID.randomUUID().toString())
                        .meter(altitude)
                        .increase(false)
                        .decrease(false)
                        .timestamp(Instant.now())
                        .build(), ch);
                Thread.sleep(500);
            }

            //Post Landing
            while(altitude > Constants.LandingHeight){
                altitude -= new Random().nextDouble(500, 1000);
                publish(queueName, AltitudeSensorData.builder()
                        .name(queueName)
                        .id(UUID.randomUUID().toString())
                        .meter(altitude)
                        .increase(false)// Set increase false as it is landing
                        .decrease(true)
                        .timestamp(Instant.now())
                        .build(), ch);
                Thread.sleep(300);
            }

            //Landing
            while(altitude > 0){
                altitude -= new Random().nextDouble(50, 200);
                publish(queueName, AltitudeSensorData.builder()
                        .name(queueName)
                        .id(UUID.randomUUID().toString())
                        .meter(altitude)
                        .increase(false)// Set increase false as it is landing
                        .decrease(true)
                        .timestamp(Instant.now())
                        .build(), ch);
                Thread.sleep(300);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void publish(String queueName, AltitudeSensorData data, Channel channel) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        channel.basicPublish(queueName, "", null, objectMapper.writeValueAsString(data).getBytes());
        log.info("{} Data {} Sent", getClass().getSimpleName(), data);
    }
}
