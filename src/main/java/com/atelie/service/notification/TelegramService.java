package com.atelie.service.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private final List<String> chatIds;

    public TelegramService(RestTemplate restTemplate,
                           @Value("${telegram.chat-ids}") String chatIds) {
        this.restTemplate = restTemplate;
        this.chatIds = Arrays.asList(chatIds.split(","));
    }

    @Override
    public List<String> send(String message) {
        List<String> errors = new ArrayList<>();
        String url = "https://api.telegram.org/bot" + token + "/sendMessage";
        chatIds.forEach(chatId -> {
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
                errors.add("Telegram(chatId=" + chatId + ") HTTP error: " + response.getStatusCode());
            }
            Map body = response.getBody();
            if (body == null || !Boolean.TRUE.equals(body.get("ok"))) {
                errors.add("Telegram(chatId=" + chatId + ") API error: " + body);
            }
        });
        return errors;
    }
}