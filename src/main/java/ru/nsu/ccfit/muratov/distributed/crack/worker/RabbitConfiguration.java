package ru.nsu.ccfit.muratov.distributed.crack.worker;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class RabbitConfiguration {
    private static final Logger logger = Logger.getLogger(RabbitConfiguration.class.getCanonicalName());

    @Value("${rabbitmq.request.queue.name}")
    private String requestQueue;

    @Value("${rabbitmq.request.exchange.name}")
    private String requestExchange;

    @Value("${rabbitmq.request.routing.key}")
    private String requestRoutingJsonKey;

    // spring bean for queue (store json messages)
    @Bean
    public Queue requestQueue() {
        return new Queue(requestQueue);
    }

    // spring bean for rabbitmq exchange
    @Bean
    public TopicExchange requestExchange() {
        return new TopicExchange(requestExchange);
    }

    // binding between json queue and exchange using routing key
    @Bean
    public Binding requestBinding() {
        return BindingBuilder
                .bind(requestQueue())
                .to(requestExchange())
                .with(requestRoutingJsonKey);
    }

    @Value("${rabbitmq.response.queue.name}")
    private String responseQueue;

    @Value("${rabbitmq.response.exchange.name}")
    private String responseExchange;

    @Value("${rabbitmq.response.routing.key}")
    private String responseRoutingJsonKey;

    // spring bean for queue (store json messages)
    @Bean
    public Queue responseQueue() {
        return new Queue(responseQueue);
    }

    // spring bean for rabbitmq exchange
    @Bean
    public TopicExchange responseExchange() {
        return new TopicExchange(responseExchange);
    }

    // binding between json queue and exchange using routing key
    @Bean
    public Binding responseBinding() {
        return BindingBuilder
                .bind(responseQueue())
                .to(responseExchange())
                .with(responseRoutingJsonKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}

