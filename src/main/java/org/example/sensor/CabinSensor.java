package org.example.sensor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.CabinSensorData;
import org.example.common.QueueEnum;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class CabinSensor implements Runnable {

    private String queueName;

    private CountDownLatch countDownLatch;
    
    private Channel channel;

    @Override
    public void run() {
        //Cabin Simulation
        try {
            channel.exchangeDeclare(QueueEnum.Cabin.getName(), BuiltinExchangeType.DIRECT);


                double cabin = new Random().nextDouble(300, 1013);
                publish(queueName, CabinSensorData.builder()
                        .name(queueName)
                        .pressure(cabin)
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

    public void publish(String queueName, CabinSensorData data, Channel channel) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        channel.basicPublish(queueName, "", null, objectMapper.writeValueAsString(data).getBytes());
        log.info("{} Data {} Sent", getClass().getSimpleName(), data);
    }
}
