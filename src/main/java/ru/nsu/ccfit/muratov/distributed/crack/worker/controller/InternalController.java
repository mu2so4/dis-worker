package ru.nsu.ccfit.muratov.distributed.crack.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.ccfit.muratov.distributed.crack.worker.dto.RequestDto;
import ru.nsu.ccfit.muratov.distributed.crack.worker.dto.ResponseDto;
import ru.nsu.ccfit.muratov.distributed.crack.worker.service.CrackService;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/internal/api/worker/hash/crack")
public class InternalController {
    @Autowired
    private CrackService service;

    private static final Logger logger = Logger.getLogger(InternalController.class.getCanonicalName());

    private final WebClient client = WebClient.create("http://manager:8080/internal/api/manager/hash/crack/request");

    @PostMapping(value = "/task", consumes = "application/json", produces = "application/json")
    public void completeTask(@RequestBody RequestDto request) {
        logger.info(() -> "received request " + request.getRequestId());
        //todo async performing for quick response
        List<String> result = service.crack(request.getHash(), request.getMaxLength());
        String[] arrResult = new String[result.size()];
        result.toArray(arrResult);

        ResponseDto response = new ResponseDto(request.getRequestId(), arrResult);
        sendTaskResponse(response);
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
