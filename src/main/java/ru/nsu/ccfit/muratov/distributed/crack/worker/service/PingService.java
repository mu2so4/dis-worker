package ru.nsu.ccfit.muratov.distributed.crack.worker.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.nsu.ccfit.muratov.distributed.crack.worker.dto.PortDto;

@Component
@EnableScheduling
public class PingService {
    private static final String uriTemplate = "http://manager:8080/internal/api/manager/accounting";

    @Value("${server.port}")
    private int port;

    @Scheduled(fixedRate = 3000)
    public void greetManager() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        restTemplate.postForEntity(uriTemplate, new PortDto(port), PortDto.class);
    }

    @PreDestroy
    public void fareManager() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        restTemplate.delete(uriTemplate, new PortDto(port), PortDto.class);
    }
}
