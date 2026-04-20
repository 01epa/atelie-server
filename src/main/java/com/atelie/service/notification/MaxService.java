package com.atelie.service.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(
        name = "max.enabled",
        havingValue = "true"
)
public class MaxService implements NotificationService {
    private final RestTemplate restTemplate;
    @Value("${max.webhook}")
    private String webhook;

    public MaxService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<String> send(String message) {
        List<String> errors = new ArrayList<>();
        ResponseEntity<String> response = restTemplate.postForEntity(
                webhook,
                Map.of("text", message),
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            errors.add("MAX HTTP error: " + response.getStatusCode());
        }
        return errors;
    }
}