package com.atelie.service.notification;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class NotificationConfiguration {
    @Value("${proxy.host}")
    private String proxyHost;
    @Value("${proxy.port}")
    private int proxyPort;

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory;
        if (proxyHost == null || proxyHost.isBlank()) {
            factory = new HttpComponentsClientHttpRequestFactory();
        } else {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setProxy(proxy)
                    .build();
            factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        }
        factory.setConnectionRequestTimeout(5000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }
}
