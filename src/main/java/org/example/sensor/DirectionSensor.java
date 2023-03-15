package org.example.sensor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.DirectionSensorData;
import org.example.common.QueueEnum;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class DirectionSensor implements Runnable {

    private String queueName;

    private CountDownLatch countDownLatch;

    private Channel channel;

    @Override
    public void run() {
        //Direction Simulation
        try {
            channel.exchangeDeclare(QueueEnum.Direction.getName(), BuiltinExchangeType.DIRECT);


            double direction = new Random().nextDouble(90, 270);
            publish(queueName, DirectionSensorData.builder()
                    .name(queueName)
                    .angle(direction)
                    .id(UUID.randomUUID().toString())
                    .timestamp(Instant.now())
                    .build(), channel);
            Thread.sleep(500);


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void publish(String queueName, DirectionSensorData data, Channel channel) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        channel.basicPublish(queueName, "", null, objectMapper.writeValueAsString(data).getBytes());
        log.info("{} Data {} Sent", getClass().getSimpleName(), data);
    }
}
