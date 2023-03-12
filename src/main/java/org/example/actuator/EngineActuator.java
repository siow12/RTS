package org.example.actuator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.example.common.ActuatorConnection;
import org.example.common.AltitudeSensorData;
import org.example.common.Constants;
import org.example.common.QueueEnum;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Slf4j
public class EngineActuator extends AbstractActuator implements Runnable {

    private AtomicReference<AirplaneData> airplaneData;

    @Override
    public void run() {
        try {

            //Setup Connection
            Connection connection = ActuatorConnection.getConnection();
            Channel channel = connection.createChannel();
            String queueName = channel.queueDeclare(QueueEnum.Altitude.getName(), false, false, false, null).getQueue();
            channel.queueBind(queueName, QueueEnum.Altitude.getName(), "");

            //Read Data from queue
            channel.basicConsume(QueueEnum.Altitude.getName(), true, (tag, msg) -> {

                //Convert byte to string
                String m = new String(msg.getBody(), StandardCharsets.UTF_8);

                //COnvert string (JSON format) to Object
                ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
                AltitudeSensorData data = objectMapper.readValue(m, AltitudeSensorData.class);
                log.info("Message Received: {}", data);
                //Engine Logic


                if (data.getMeter() < Constants.cruisingHeightMin && data.getIncrease() && !data.getDecrease()) {//Take off

                    airplaneData.updateAndGet(a -> {
                        a.setEnginePowerInPercentage(0.5);
                        return a;
                    });
                    log.info("Take off, Airplane engine power set to {}", 0.5 * 100);
                }
                if (!data.getDecrease() && !data.getIncrease()) {// Cruising

                    //Cruising height require more engine power
                    airplaneData.updateAndGet(a -> {
                        a.setEnginePowerInPercentage(1.0);
                        return a;
                    });
                    log.info("Cruising, Airplane engine power set to {}%", 1.0 * 100);
                }
                if ((data.getMeter() > Constants.LandingHeight) && !data.getIncrease() && data.getDecrease()) {//Post Landing
                    airplaneData.updateAndGet(a -> {
                        a.setEnginePowerInPercentage(0.1);
                        return a;
                    });
                    log.info("Post Landing, Airplane engine power set to {}%", 0.1 * 100);
                }
                if (data.getMeter() < Constants.LandingHeight && !data.getIncrease()) {//Landing
                    airplaneData.updateAndGet(a -> {
                        a.setEnginePowerInPercentage(0.1);
                        return a;
                    });
                    log.info("Landing, Airplane engine power set to {}%", 0.1 * 100);
                }
                //log.info("Airplane Status: {}", currentData);
            }, consumerTag -> {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
