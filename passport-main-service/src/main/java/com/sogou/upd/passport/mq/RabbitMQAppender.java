package com.sogou.upd.passport.mq;

import com.google.common.collect.Lists;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-8-30 Time: 上午11:47 To change this template use File | Settings | File Templates.
 */
public class RabbitMQAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private String host = "localhost";
    private int port = 5672;
    private String queueName = "test";
    private String username = "guest";
    private String password = "guest";
    private String virtualHost = "/";


    private ConnectionFactory connectionFactory;
    private Connection connection;
    private List<Connection> connections;
    private Channel channel;
    private List<Channel> channels;
    private Random random;

    private byte[] chan_flags;
    private int flag=0;

    PatternLayoutEncoder encoder;

    @Override
    public void start() {
        if (this.encoder == null) {
            addError("No encoder set for the appender named ["+ name +"].");
            return;
        }

        boolean err = false;

        try {
            synchronized (this) {
                connectionFactory = new ConnectionFactory();
                connectionFactory.setHost(host);
                connectionFactory.setPort(port);
                connectionFactory.setVirtualHost(virtualHost);
                connectionFactory.setUsername(username);
                connectionFactory.setPassword(password);

               // connection = connectionFactory.newConnection();
                connections = new LinkedList<>();
                channels = new LinkedList<>();
                for (int i=0; i<10; i++) {
                    Connection conn = connectionFactory.newConnection();
                    connections.add(conn);
                    for (int j=0; j<20; j++) {
                        channels.add(conn.createChannel());
                    }

                }


               // channel = connection.createChannel();
               /* for (int i=0; i<100; i++) {
                    channels.add(connections.get(new Random().nextInt(50)).createChannel());
                }*/
                random = new Random();
                chan_flags = new byte[200];
            }
            // encoder.init(System.out);
        } catch (IOException e) {
            addError("initial connection failed", e);
            err = true;
        }
        if (!err) {
            super.start();
        }
    }

    public void append(ILoggingEvent event) {
        // output the events as formatted by our layout
        try {
            // this.encoder.doEncode(event);
            long start = System.currentTimeMillis();
            String msg = encoder.getLayout().doLayout(event);
            /*if (channel == null || !channel.isOpen()) {
                System.out.println("---------------------Channel is null---------------------");
                if (connection == null || !connection.isOpen()) {
                    System.out.println("---------------------Connection is null---------------------");
                    synchronized (connection) {
                        if (connection == null || !connection.isOpen()) {
                            System.out.println("---------------------Connection is null also---------------------");
                            connection = connectionFactory.newConnection();
                        }
                    }
                }
                synchronized (channel) {
                    if (channel == null || !channel.isOpen()) {
                        System.out.println("---------------------Channel is null also---------------------");
                        channel = connection.createChannel(1);
                    }
                }
            }*/
            Channel chan;
            int flg;
/*            synchronized (channels) {
                // int r = random.nextInt(200);
                // int i = chan_flags[r];
                int i = chan_flags[(++flag)%200];

                int count = 100;
                while (i == 1 && count-- != 0) {
                    i = chan_flags[(++flag)%200];
                }
                flag = flag % 200;
                chan = channels.get(flag);
                chan_flags[flag] = 1;
                flg = flag;
            }*/
            chan = channels.get(random.nextInt(200));

            chan.basicPublish("", queueName, null, msg.getBytes());
            // chan_flags[flg] = 0;
            //channel.basicPublish("", queueName, null, msg.getBytes());
            // channels.get(new Random().nextInt(50)).basicPublish("", queueName, null, msg.getBytes());
            System.out.println(System.currentTimeMillis()-start);
            //
        } catch (IOException e) {
            addError("append failed: ", e);
        }

    }

    @Override
    public void stop() {
        try {
            synchronized (this) {
                if (channel.isOpen()) {
                    channel.close();
                }
                if (connection.isOpen()) {
                    connection.close();
                }
            }
        } catch (IOException e) {
            addError("stop failed: ", e);
        } finally {

        }
    }

    public PatternLayoutEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(PatternLayoutEncoder encoder) {
        this.encoder = encoder;
    }

/*
    @Override
    public void append(ILoggingEvent eventObject) {
        //To change body of implemented methods use File | Settings | File Templates.
    }*/

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

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }
}
