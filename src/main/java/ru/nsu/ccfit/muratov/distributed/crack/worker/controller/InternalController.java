package ru.nsu.ccfit.muratov.distributed.crack.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.ccfit.muratov.distributed.crack.worker.dto.RequestDto;
import ru.nsu.ccfit.muratov.distributed.crack.worker.dto.ResponseDto;
import ru.nsu.ccfit.muratov.distributed.crack.worker.service.CrackService;

import java.util.List;

@RestController
@RequestMapping(value = "/internal/api/worker/hash/crack")
public class InternalController {
    @Autowired
    private CrackService service;

    @PostMapping(value = "/task", consumes = "application/json", produces = "application/json")
    public ResponseDto completeTask(@RequestBody RequestDto request) {
        List<String> result = service.crack(request.getHash(), request.getMaxLength());
        String[] arrResult = new String[result.size()];
        result.toArray(arrResult);

        ResponseDto response = new ResponseDto();
        response.setRequestId(request.getRequestId());
        response.setWords(arrResult);
        return response;
    }
}
