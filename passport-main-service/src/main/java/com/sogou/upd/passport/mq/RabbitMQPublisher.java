package com.sogou.upd.passport.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.List;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-8-30 Time: 下午7:38 To change this template use File | Settings | File Templates.
 */
public class RabbitMQPublisher {
    private String host = "localhost";
    private int port = 5672;
    private String queueName = "test";
    private String username = "guest";
    private String password = "guest";
    private String virtualHost = "/";



    private ConnectionFactory connectionFactory;
    private Connection connection;
    //private static List<Connection> connections;
    private Channel channel;
    //private List<Channel> channels;

    // PatternLayoutEncoder encoder;

    public RabbitMQPublisher() {

        boolean err = false;

        try {
                connectionFactory = new ConnectionFactory();
                connectionFactory.setHost("10.146.32.57");
                connectionFactory.setPort(5672);
                connectionFactory.setVirtualHost("/storm_host");
                connectionFactory.setUsername("davion");
                connectionFactory.setPassword("davion");

                connection = connectionFactory.newConnection();
               /* connections = new LinkedList<>();
                for (int i=0; i<50; i++) {
                    connections.add(connectionFactory.newConnection());
                }*/
                channel = connection.createChannel(100);
                /*for (int i=0; i<100; i++) {
                    channels.add(connections.get(new Random().nextInt(50)).createChannel());
                }*/
            // encoder.init(System.out);
        } catch (IOException e) {
            err = true;
        }

    }

    public void append(String msg) {
        // output the events as formatted by our layout
        try {
            // this.encoder.doEncode(event);
            long start = System.currentTimeMillis();

            channel.basicPublish("", queueName, null, msg.getBytes());
            System.out.println(System.currentTimeMillis()-start);
            // channels.get(new Random().nextInt(100)).basicPublish("", queueName, null, msg.getBytes());
        } catch (IOException e) {
        }

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }
}
