package org.example.actuator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.example.common.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class OxygenMaskActuator extends AbstractActuator implements Runnable{


    @Override
    public void run() {
        try {

            //Setup Connection
            Connection connection = ActuatorConnection.getConnection();
            Channel channel = connection.createChannel();
            String queueName = channel.queueDeclare(QueueEnum.Cabin.getName(), false, false, false, null).getQueue();
            channel.queueBind(queueName, QueueEnum.Cabin.getName(), "");

            //Read Data from queue
            channel.basicConsume(QueueEnum.Cabin.getName(), true, (tag, msg) -> {

                //Convert byte to string
                String m = new String(msg.getBody(), StandardCharsets.UTF_8);

                //Convert string (JSON format) to Object
                ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
                CabinSensorData data = objectMapper.readValue(m, CabinSensorData.class);
                log.info("Message Received: {}", data);
                //Cabin Logic

                if (data.getPressure() > Constants.pressureMax) {//Take off
                    airplaneData.updateAndGet(a -> {
                        a.setOxygenMaskOpen(false);
                        return a;
                    });
                    log.info("Take off, Airplane cabin pressure {}%", 1.0 * 100);
                }
                if (data.getPressure() >= Constants.pressureMin) {
                    airplaneData.updateAndGet(a -> {
                        a.setOxygenMaskOpen(true);
                        return a;
                    });
                    log.info("Cruising, Airplane cabin pressure lesser than {}%", 0.4 * 100);
                }

                //log.info("Airplane Status: {}", currentData);
            }, consumerTag -> {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
