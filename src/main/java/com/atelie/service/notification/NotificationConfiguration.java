package com.atelie.service.notification;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Timeout;
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
    @Value("${proxy.username:}")
    private String proxyUsername;
    @Value("${proxy.password:}")
    private String proxyPassword;

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory;
        if (proxyHost == null || proxyHost.isBlank()) {
            factory = new HttpComponentsClientHttpRequestFactory();
        } else {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(Timeout.ofSeconds(10))
                    .setResponseTimeout(Timeout.ofSeconds(30))
                    .setConnectionRequestTimeout(Timeout.ofSeconds(10))
                    .build();
            HttpClientBuilder httpClientBuilder = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .setProxy(proxy);
            if (proxyUsername != null && !proxyUsername.isBlank()
                    && proxyPassword != null && !proxyPassword.isBlank()) {
                BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        new AuthScope(proxy),
                        new UsernamePasswordCredentials(proxyUsername, proxyPassword.toCharArray())
                );
                httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
            }
            factory = new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build());
        }
        factory.setConnectionRequestTimeout(10_000);
        factory.setReadTimeout(30_000);
        return new RestTemplate(factory);
    }
}
