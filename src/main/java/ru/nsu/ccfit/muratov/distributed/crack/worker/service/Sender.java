package ru.nsu.ccfit.muratov.distributed.crack.worker.service;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

@EnableScheduling
@Service
public class Sender<T> {
    private static final Logger logger = Logger.getLogger(Sender.class.getCanonicalName());

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.response.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.response.routing.key}")
    private String routingJsonKey;

    private final Queue<T> unsentMessages = new LinkedList<>();

    public boolean sendMessage(T message) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingJsonKey, message);
        }
        catch (AmqpException e) {
            logger.severe(() -> "failed to send response: " + e.getMessage());
            if(!message.equals(unsentMessages.peek())) {
                synchronized (unsentMessages) {
                    unsentMessages.add(message);
                }
            }
            return false;
        }
        return true;
    }

    @Scheduled(fixedRate = 10000)
    private void resendMessages() {
        while(!unsentMessages.isEmpty()) {
            T request = unsentMessages.peek();
            if(sendMessage(request)) {
                logger.info(() -> "response " + request + " resent successfully");
                unsentMessages.remove();
            }
            else {
                break;
            }
        }
    }
}
