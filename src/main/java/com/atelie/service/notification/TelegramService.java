package com.atelie.service.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@ConditionalOnProperty(
        name = "telegram.enabled",
        havingValue = "true"
)
public class TelegramService implements NotificationService {
    private final RestTemplate restTemplate;

    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.chat-id}")
    private String chatId;

    public TelegramService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void send(String message) {
        String url = "https://api.telegram.org/bot" + token + "/sendMessage";
        ResponseEntity<Map> response = restTemplate.postForEntity(
                url,
                new HttpEntity<>(
                        Map.of(
                                "chat_id", chatId,
                                "text", message,
                                "parse_mode", "HTML"
                        )
                ),
                Map.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Telegram HTTP error: " + response.getStatusCode());
        }
        Map body = response.getBody();
        if (body == null || !Boolean.TRUE.equals(body.get("ok"))) {
            throw new RuntimeException("Telegram API error: " + body);
        }
    }
}