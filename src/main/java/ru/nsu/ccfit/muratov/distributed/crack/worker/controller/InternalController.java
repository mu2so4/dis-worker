package ru.nsu.ccfit.muratov.distributed.crack.worker.controller;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.ccfit.muratov.distributed.crack.worker.dto.RequestDto;
import ru.nsu.ccfit.muratov.distributed.crack.worker.dto.ResponseDto;
import ru.nsu.ccfit.muratov.distributed.crack.worker.service.CrackService;

import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/internal/api/worker/hash/crack")
public class InternalController {
    @Autowired
    private CrackService service;

    private static final Logger logger = Logger.getLogger(InternalController.class.getCanonicalName());

    private final WebClient client = WebClient.create("http://localhost:8080/internal/api/manager/hash/crack/request");

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Value("${crack.limit}")
    private long timeLimit;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void completeTask(@RequestBody RequestDto request) {
        logger.info(() -> "received request " + request.getRequestId());

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

                ResponseDto response = new ResponseDto(request.getRequestId(), arrResult);
                sendTaskResponse(response);
                return null;
            });

    }

    private void sendTaskResponse(ResponseDto dto) {
        client
                .patch()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }
}
