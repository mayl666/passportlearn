package com.sogou.upd.passport.dao;

import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: mayan
 * Date: 13-3-15
 * Time: 下午6:39
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:mq/rabbitConfiguration.xml"})

public class TestRabbitMq extends AbstractJUnit4SpringContextTests {

    @Inject
    RabbitTemplate rabbitTemplate;

    @Test
    public void testProducer(){
        rabbitTemplate.convertAndSend("test test3");
    }

    @Test
    public void testConsumer(){
        System.out.println("Received: " + rabbitTemplate.receiveAndConvert());
    }

}
