package org.example.common;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractConnection {

    private static ConnectionFactory factory;
    private static Connection connection;

    AbstractConnection() {
    }

    public static synchronized Connection getConnection() throws Exception {
        if (connection == null || !connection.isOpen()) {
            if (factory == null) {
                //Connect Config
                Logger.getLogger("com.rabbitmq.client").setLevel(Level.OFF);
                factory = new ConnectionFactory();
                factory.setHost("localhost");

            }
            connection = factory.newConnection();
        }
        return connection;
    }
}
