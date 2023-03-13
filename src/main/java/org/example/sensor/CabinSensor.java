package org.example.sensor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.AltitudeSensorData;
import org.example.common.CabinSensorData;
import org.example.common.QueueEnum;
import org.example.common.SensorConnection;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class CabinSensor implements Runnable {
    public static final double pressureMin = 1;
    public static final double pressureMax = 1013;
    private String queueName;

    @Override
    public void run() {
        //Cabin Simulation
        try (Connection con = SensorConnection.getConnection()) {
            Channel ch = con.createChannel();
            ch.exchangeDeclare(QueueEnum.Cabin.getName(), BuiltinExchangeType.DIRECT);
            double cabin = 0.0;

            //Take off
            while (cabin < 1013) {
                cabin -= new Random().nextDouble(950, 1013);
                publish(queueName, CabinSensorData.builder()
                        .name(queueName)
                        .id(UUID.randomUUID().toString())
                        .pressure(cabin)
                        .open(false)// Set open false as it is take off
                        .timestamp(Instant.now())
                        .build(), ch);
                Thread.sleep(300);
            }
            //Cruising when react cruising pressure
            for (int i = 0; i < 10; i++) {
                cabin = new Random().nextDouble(300, 400);
                publish(queueName, CabinSensorData.builder()
                        .name(queueName)
                        .id(UUID.randomUUID().toString())
                        .open(true)
                        .timestamp(Instant.now())
                        .build(), ch);
                Thread.sleep(500);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void publish(String queueName, CabinSensorData data, Channel channel) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        channel.basicPublish(queueName, "", null, objectMapper.writeValueAsString(data).getBytes());
        log.info("{} Data {} Sent", getClass().getSimpleName(), data);
    }
}
