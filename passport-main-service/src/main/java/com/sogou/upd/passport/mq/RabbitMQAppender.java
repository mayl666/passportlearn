package com.sogou.upd.passport.mq;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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

    private static final int DEFAULT_CONNSIZE = 10;
    private static final int DEFAULT_CHANSIZE = 20;

    private int connSize = DEFAULT_CONNSIZE;
    private int chanSize = DEFAULT_CHANSIZE;
    private int allChanSize = DEFAULT_CONNSIZE * DEFAULT_CHANSIZE;


    private ConnectionFactory connectionFactory;
    private List<Connection> connections;
    private List<Channel> channels;
    private Random random;

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

                connections = new ArrayList<>();
                channels = new ArrayList<>();
                for (int i=0; i<connSize; i++) {
                    Connection conn = connectionFactory.newConnection();
                    connections.add(conn);
                    for (int j=0; j<chanSize; j++) {
                        channels.add(conn.createChannel());
                    }
                }

                // handle sizes
                if (connSize <= 0) {
                    connSize = DEFAULT_CONNSIZE;
                }
                if (chanSize <= 0) {
                    chanSize = DEFAULT_CHANSIZE;
                }
                allChanSize = connSize * chanSize;

                // other opers
                random = new Random();
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

    @Override
    public void append(ILoggingEvent event) {
        try {
            if (!isStarted()) {
                return;
            }
            String msg = encoder.getLayout().doLayout(event);
            Channel channel = getChannelAvail();
            if (channel == null) {
                return;
            }
            channel.basicPublish("", queueName, null, msg.getBytes());
        } catch (IOException e) {
            addError("append failed: ", e);
        }
    }

    protected Channel getChannelAvail() {
        int idx = random.nextInt(allChanSize);
        int conn_idx = idx / chanSize;
        Channel channel = channels.get(idx);
        if (!channel.isOpen()) {
            synchronized (channels) {
                if (channel.isOpen()) {
                    return channel;
                }
                try {
                    channel.close();
                } catch (IOException e) {
                    addError("channel close failed: ", e);
                }
                Connection connection = connections.get(idx / chanSize);
                if (!connection.isOpen()) {
                    try {
                        connection.close();
                    } catch (IOException e) {
                        addError("connection close failed: ", e);
                    }

                    try {
                        connection = connectionFactory.newConnection();
                        connections.set(conn_idx, connection);
                    } catch (IOException e) {
                        addError("new connection failed: ", e);
                        return null;
                    }

                }
                try {
                    channel = connection.createChannel();
                    channels.set(idx, channel);
                } catch (IOException e) {
                    addError("channel create failed: ", e);
                    return null;
                }
            }
        }
        return channel;
    }

    @Override
    public void stop() {
        try {
            synchronized (this) {
                Iterator<Channel> iterator = channels.iterator();
                Channel channel;
                while (iterator.hasNext()) {
                    channel = iterator.next();
                    channel.close();
                }
                Iterator<Connection> iter = connections.iterator();
                Connection connection;
                while (iter.hasNext()) {
                    connection = iter.next();
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

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public int getConnSize() {
        return connSize;
    }

    public void setConnSize(int connSize) {
        this.connSize = connSize;
    }

    public int getChanSize() {
        return chanSize;
    }

    public void setChanSize(int chanSize) {
        this.chanSize = chanSize;
    }
}
