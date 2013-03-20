package com.sogou.upd.passport.mq;

import java.io.IOException;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class Send {

	static final String QUEUE_NAME = "passport_queue1";
	static final String EXCHANGE_NAME = "passport";
	static final String ROUTING_KEY = "routing.a1.service";

	/**
	 * @param args
	 * @throws java.io.IOException
	 * @throws InterruptedException
	 * @throws com.rabbitmq.client.ConsumerCancelledException
	 * @throws com.rabbitmq.client.ShutdownSignalException
	 */
	public static void main(String[] args) throws IOException,
			ShutdownSignalException, ConsumerCancelledException,
			InterruptedException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setVirtualHost("upd");
		factory.setHost("10.16.172.44");
		factory.setUsername("upd_passport");
		factory.setPassword("upd_passport");
		factory.setPort(5672);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		String message = "Hello World!";
		
		channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());
		System.out.println(" [x] Sent '" + message + "'");

		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(QUEUE_NAME, true, consumer);

		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		message = new String(delivery.getBody());
		System.out.println(" [x] Received '" + message + "'");

		channel.close();
		connection.close();

	}

}
