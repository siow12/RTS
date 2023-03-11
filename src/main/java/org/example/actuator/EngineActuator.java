package org.example.actuator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.example.common.ActuatorConnection;
import org.example.common.AltitudeSensorData;
import org.example.common.QueueEnum;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Slf4j
public class EngineActuator extends AbstractActuator implements Runnable {

    private AtomicReference<AirplaneData> airplaneData;

    @Override
    public void run() {
        try {
            Connection connection = ActuatorConnection.getConnection();
            Channel channel = connection.createChannel();
            String queueName =  channel.queueDeclare(QueueEnum.Altitude.getName(),false,false,false,null).getQueue();
            channel.queueBind(queueName, QueueEnum.Altitude.getName(), "");
            channel.basicConsume(QueueEnum.Altitude.getName(), true, (tag, msg)->{
                String m = new String(msg.getBody(), StandardCharsets.UTF_8);
                ObjectMapper objectMapper = new ObjectMapper();
                AltitudeSensorData data = objectMapper.readValue(m, AltitudeSensorData.class);
                AirplaneData currentData =  airplaneData.updateAndGet((d)->{
                    d.setAltitudeInMeter(data.getMeter());
                    return d;
                });
                log.info("Message Received: {}", data);
                log.info("Airplane Status: {}",  currentData);
            }, consumerTag -> {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
