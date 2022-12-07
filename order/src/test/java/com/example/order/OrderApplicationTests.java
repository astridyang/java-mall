package com.example.order;

import com.example.order.entity.OrderReturnReasonEntity;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest
class OrderApplicationTests {

    @Resource
    AmqpAdmin amqpAdmin;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage(){
        OrderReturnReasonEntity reason = new OrderReturnReasonEntity();
        reason.setId(1L);
        reason.setCreateTime(new Date());
        reason.setName("some reason");
        rabbitTemplate.convertAndSend("java-exchange","java",reason);
    }

    @Test
    public void testExchange(){
        DirectExchange directExchange = new DirectExchange("java-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
    }

    @Test
    public void testQueue(){
        Queue queue = new Queue("java-queue", true, false, false);
        amqpAdmin.declareQueue(queue);

    }

    @Test
    public void testBinding(){
        Binding binding = new Binding("java-queue", Binding.DestinationType.QUEUE, "java-exchange", "java", null);
        amqpAdmin.declareBinding(binding);
    }


    @Test
    void contextLoads() {

    }

}
