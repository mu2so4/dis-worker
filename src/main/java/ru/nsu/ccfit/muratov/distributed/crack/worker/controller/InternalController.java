package ru.nsu.ccfit.muratov.distributed.crack.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
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

    @PostMapping(value = "/task", consumes = "application/json", produces = "application/json")
    public RequestDto completeTask(@RequestBody RequestDto request) {
        logger.info(() -> "got request " + request.getRequestId());
        List<String> result = service.crack(request.getHash(), request.getMaxLength());
        String[] arrResult = new String[result.size()];
        result.toArray(arrResult);

        ResponseDto response = new ResponseDto(request.getRequestId(), arrResult);
        sendTaskResponse(response);
        return request;
    }

    private void sendTaskResponse(ResponseDto dto) {
        String uriTemplate = "http://localhost:8080/internal/api/manager/hash/crack/request";
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        HttpEntity<ResponseDto> request = new HttpEntity<>(dto);
        restTemplate.patchForObject(uriTemplate, request, ResponseDto.class);
    }
}
