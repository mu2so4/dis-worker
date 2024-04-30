package ru.nsu.ccfit.muratov.distributed.crack.worker.service;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.nsu.ccfit.muratov.distributed.crack.worker.dto.PortDto;
import ru.nsu.ccfit.muratov.distributed.crack.worker.dto.WorkerIdDto;

@Component
@EnableScheduling
public class PingService {
    private static final String uriTemplate = "http://manager:8080/internal/api/manager/accounting";

    @Value("${server.port}")
    private int port;

    private String id;

    @Scheduled(fixedRate = 3000)
    public void greetManager() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        ResponseEntity<WorkerIdDto> response = restTemplate.postForEntity(uriTemplate, new PortDto(port), WorkerIdDto.class);
        WorkerIdDto dto = response.getBody();
        if(dto != null) {
            id = dto.getId();
        }
    }

    @PreDestroy
    public void fareManager() {
        if(id == null) {
            return;
        }
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        restTemplate.delete(uriTemplate + "/" + id, new PortDto(port), PortDto.class);
    }
}
