package ru.nsu.ccfit.muratov.distributed.crack.worker.controller;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.muratov.distributed.crack.worker.dto.RequestDto;
import ru.nsu.ccfit.muratov.distributed.crack.worker.dto.ResponseDto;
import ru.nsu.ccfit.muratov.distributed.crack.worker.service.CrackService;
import ru.nsu.ccfit.muratov.distributed.crack.worker.service.Sender;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Service
public class InternalController {
    @Autowired
    private CrackService service;

    @Autowired
    private Sender<ResponseDto> sender;

    private static final Logger logger = Logger.getLogger(InternalController.class.getCanonicalName());

    @Value("${crack.limit}")
    private long timeLimit;

    private final Queue<String> latestRequests = new LinkedList<>();

    private static final int MAX_SIZE = 5;

    @RabbitListener(queues = "${rabbitmq.request.queue.name}")
    public void completeTask(@Payload RequestDto request) {
        String requestId = request.getRequestId();
        if(latestRequests.contains(requestId)) {
            logger.info("received again request " + requestId + ". Skipping");
            return;
        }
        logger.info(() -> "received request " + requestId);

        CompletableFuture<List<String>> crackFuture = CompletableFuture
            .supplyAsync(() -> service.crack(request.getHash(), request.getMaxLength()))
            .completeOnTimeout(null, timeLimit, TimeUnit.MILLISECONDS)
            .thenApply((result) -> {
                String[] arrResult;
                if(result != null) {
                    arrResult = new String[result.size()];
                    result.toArray(arrResult);
                }
                else {
                    arrResult = null;
                }

                latestRequests.add(requestId);
                if(latestRequests.size() > MAX_SIZE) {
                    latestRequests.remove();
                }
                ResponseDto response = new ResponseDto(requestId, arrResult);
                sendTaskResponse(response);
                return null;
            });

        crackFuture.join();
    }

    private void sendTaskResponse(ResponseDto dto) {
        sender.sendMessage(dto);
    }
}
