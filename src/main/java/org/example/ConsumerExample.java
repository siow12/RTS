package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.common.QueueEnum;

public class ConsumerExample {

    private static final String QUEUE_NAME = QueueEnum.Altitude.getName();

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare the queue and start consuming messages
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String consumerTag = channel.basicConsume(QUEUE_NAME, true, (consumerTagLambda, deliveryLambda) -> {
            String message = new String(deliveryLambda.getBody(), "UTF-8");
            System.out.println("Received message: " + message);

            // Check the message content to determine if we need to stop consuming messages
            if ("stop".equals(message)) {
                channel.basicCancel(consumerTagLambda);
            }
        }, consumerTagLambda -> {});

        // Clean up
        channel.close();
        connection.close();
    }
}