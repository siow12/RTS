package org.example.sensor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.AltitudeSensorData;
import org.example.common.Constants;
import org.example.common.QueueEnum;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class AltitudeSensor implements Runnable {
    private String queueName;

    private CountDownLatch countDownLatch;

    private Channel channel;


    @Override
    public void run() {
        //Altitude Simulation
        try {
            channel.exchangeDeclare(QueueEnum.Altitude.getName(), BuiltinExchangeType.DIRECT);
            long count = countDownLatch.getCount();

            if (countDownLatch.getCount() > Constants.takeOffState) {//Take off
                long elapsedTime = Constants.totalTime - count;
                long increment = Constants.takeOff - 1;
                double avgIncrementPerCount = Constants.cruisingHeightMin / (double) increment;
                double altitude = avgIncrementPerCount * elapsedTime;
                publish(queueName, AltitudeSensorData.builder()
                        .name(queueName)
                        .id(UUID.randomUUID().toString())
                        .feet(altitude)
                        .increase(true)
                        .decrease(false)
                        .timestamp(Instant.now())
                        .build(), channel);

            } else if (countDownLatch.getCount() > Constants.cruisingState) {
                double altitude = new Random().nextDouble(30000, 40000);
                publish(queueName, AltitudeSensorData.builder()
                        .name(queueName)
                        .id(UUID.randomUUID().toString())
                        .feet(altitude)
                        .increase(false)
                        .decrease(false)
                        .timestamp(Instant.now())
                        .build(), channel);

            } else if (countDownLatch.getCount() > Constants.postLandingState) {
                double avgDecrement = (Constants.cruisingHeightMin - Constants.LandingHeight) / Constants.postLanding;
                long elapseTime = countDownLatch.getCount() - Constants.postLanding;
                double altitude = (avgDecrement * (double) elapseTime) + Constants.LandingHeight;
//                System.out.println(count + ": " + (Constants.totalTime - count));
//                System.out.println(altitude);

                publish(queueName, AltitudeSensorData.builder()
                        .name(queueName)
                        .id(UUID.randomUUID().toString())
                        .feet(altitude)
                        .increase(false)
                        .decrease(true)
                        .timestamp(Instant.now())
                        .build(), channel);

            } else if (countDownLatch.getCount() > Constants.landingState) {
                double avgDecrement = (Constants.LandingHeight) / Constants.landing;
                long elapseTime = countDownLatch.getCount();
                double altitude = (avgDecrement * (double) elapseTime);

                publish(queueName, AltitudeSensorData.builder()
                        .name(queueName)
                        .id(UUID.randomUUID().toString())
                        .feet(altitude)
                        .increase(false)
                        .decrease(true)
                        .timestamp(Instant.now())
                        .build(), channel);

            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public void publish(String queueName, AltitudeSensorData data, Channel channel) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        channel.basicPublish(queueName, "", null, objectMapper.writeValueAsString(data).getBytes());
        log.info("{} Data {} Sent", getClass().getSimpleName(), data);
    }
}
