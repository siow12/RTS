package org.example.actuator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.example.common.*;

import java.nio.charset.StandardCharsets;
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class TailActuator extends AbstractActuator implements Runnable{
    public void run() {
        try {

            //Setup Connection
            Connection connection = ActuatorConnection.getConnection();
            Channel channel = connection.createChannel();
            String queueName = channel.queueDeclare(QueueEnum.Direction.getName(), false, false, false, null).getQueue();
            channel.queueBind(queueName, QueueEnum.Direction.getName(), "");

            //Read Data from queue
            channel.basicConsume(QueueEnum.Direction.getName(), true, (tag, msg) -> {

                //Convert byte to string
                String m = new String(msg.getBody(), StandardCharsets.UTF_8);

                //Convert string (JSON format) to Object
                ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
                DirectionSensorData data = objectMapper.readValue(m, DirectionSensorData.class);
                log.info("Message Received: {}", data);
                //Cabin Logic

                if (data.getAngle() > 90 && data.getAngle() < 180) {
                    airplaneData.updateAndGet(a -> {
                        a.setDirectionOfTail(Constants.tailDirectionLeft);
                        return a;
                    });
                    log.info("Airplane tail turn Left!");
                    log.info("Airplane data = {}", airplaneData.get());
                } else if (data.getAngle() >= 180 && data.getAngle() < 270)
                {
                    airplaneData.updateAndGet(a -> {
                        a.setDirectionOfTail(Constants.tailDirectionRight);
                        return a;
                    });
                    log.info("Airplane tail turn Right!");
                    log.info("Airplane data = {}", airplaneData.get());
                }

            }, consumerTag -> {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
