package com.sogou.upd.passport.mq;


import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloWorldConfiguration {

//	protected final String helloWorldQueueName = "passport_queue1";
//	protected final String helloWorldRoutingName = "routing.a1.service";
//
//	@Bean
//	public ConnectionFactory connectionFactory() {
//		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("10.16.172.44",5672);
//		connectionFactory.setUsername("upd_passport");
//		connectionFactory.setPassword("upd_passport");
//        connectionFactory.setVirtualHost("upd");
//
//
//		return connectionFactory;
//	}
//
//	@Bean
//	public AmqpAdmin amqpAdmin() {
//		return new RabbitAdmin(connectionFactory());
//	}
//
//	@Bean
//	public RabbitTemplate rabbitTemplate() {
//		RabbitTemplate template = new RabbitTemplate(connectionFactory());
//        template.setExchange("passport");
//        template.setQueue("passport_queue1");
//		//The routing key is set to the name of the queue by the broker for the default exchange.
//		template.setRoutingKey(this.helloWorldRoutingName);
//
//		//Where we will synchronously receive messages from
////		template.setQueue(this.helloWorldQueueName);
//		return template;
//	}

//	@Bean
//	// Every queue is bound to the default direct exchange
//	public Queue helloWorldQueue() {
//		return new Queue(this.helloWorldQueueName,true);
//	}

	/*
	@Bean 
	public Binding binding() {
		return declare(new Binding(helloWorldQueue(), defaultDirectExchange()));
	}*/
	
	/*	
	@Bean
	public TopicExchange helloExchange() {
		return declare(new TopicExchange("hello.world.exchange"));
	}*/
	
	/*
	public Queue declareUniqueQueue(String namePrefix) {
		Queue queue = new Queue(namePrefix + "-" + UUID.randomUUID());
		rabbitAdminTemplate().declareQueue(queue);
		return queue;
	}
	
	// if the default exchange isn't configured to your liking....
	@Bean Binding declareP2PBinding(Queue queue, DirectExchange exchange) {
		return declare(new Binding(queue, exchange, queue.getName()));
	}
	
	@Bean Binding declarePubSubBinding(String queuePrefix, FanoutExchange exchange) {
		return declare(new Binding(declareUniqueQueue(queuePrefix), exchange));
	}
	
	@Bean Binding declarePubSubBinding(UniqueQueue uniqueQueue, TopicExchange exchange) {
		return declare(new Binding(uniqueQueue, exchange));
	}
	
	@Bean Binding declarePubSubBinding(String queuePrefix, TopicExchange exchange, String routingKey) {
		return declare(new Binding(declareUniqueQueue(queuePrefix), exchange, routingKey));
	}*/

}
