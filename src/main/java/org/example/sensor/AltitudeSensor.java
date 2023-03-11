package org.example.sensor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.common.AltitudeSensorData;
import org.example.common.QueueEnum;
import org.example.common.SensorConnection;
import org.example.common.SensorData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class AltitudeSensor implements Runnable {

    private String queueName;

    private static final double cruisingHeight = 31000.0;
    private static final double cruisingHeightMax = 38000.0;

    @Override
    public void run() {
        //Altitude Simulation
        try (Connection con = SensorConnection.getConnection()) {
            Channel ch = con.createChannel();
            ch.exchangeDeclare(QueueEnum.Altitude.getName(), BuiltinExchangeType.DIRECT);
            double altitude = 0.0;

            //TODO Take Off
            while(Double.compare(altitude,cruisingHeight) < 0){
                double climbHeight = new Random().nextDouble() * 2000;
                altitude += climbHeight;
                publish(queueName, AltitudeSensorData.builder().name(queueName).id(UUID.randomUUID().toString()).meter(altitude).build(),ch);
                Thread.sleep(500);
            }

            //Todo Cruising

            //TODO Landing

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void publish(String queueName, SensorData data, Channel channel) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        channel.basicPublish(queueName, "", null, objectMapper.writeValueAsString(data).getBytes());
        log.info("Data {} Sent", data);
    }
}
