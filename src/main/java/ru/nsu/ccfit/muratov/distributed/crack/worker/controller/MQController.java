package ru.nsu.ccfit.muratov.distributed.crack.worker.controller;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.muratov.distributed.crack.worker.dto.RequestDto;

import java.util.logging.Logger;

@Service
@EnableRabbit
public class MQController {
    Logger logger = Logger.getLogger(MQController.class.getCanonicalName());


    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void newCompleteTask(@Payload RequestDto dto) {
        logger.info("got message through mq " + dto.toString());
    }
}
