package com.scu.imageseg.utils;

import com.scu.imageseg.config.RabbitMQConfig;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQUtil {
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private RabbitAdmin rabbitAdmin;
    private static final String EXCHANGE_NAME = "exchange_topics_inform";

    // 动态创建队列
    public void addQueue(String queueName, String routingKey) {
        Queue queue = new Queue(queueName, true); // 持久化队列
        amqpAdmin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue)
                .to(new TopicExchange(RabbitMQConfig.EXCHANGE_TOPICS_INFORM))
                .with(routingKey);

        amqpAdmin.declareBinding(binding);
    }

    // 动态删除队列
    public Boolean deleteQueue(String queueName){
        return amqpAdmin.deleteQueue(queueName);
    }

    /**
     * 动态创建队列并绑定到交换机
     * @param queueName   队列名称
     * @param routingKey  绑定的路由键
     */
    public void createAndBindQueue(String queueName, String routingKey) {
        // 1. 创建队列
        Queue queue = new Queue(queueName, true);
        rabbitAdmin.declareQueue(queue);

        // 2. 创建绑定
        Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, EXCHANGE_NAME, routingKey, null);
        amqpAdmin.declareBinding(binding);

        System.out.println("成功绑定队列：" + queueName + " 到交换机：" + EXCHANGE_NAME + "，使用路由键：" + routingKey);
    }
}
