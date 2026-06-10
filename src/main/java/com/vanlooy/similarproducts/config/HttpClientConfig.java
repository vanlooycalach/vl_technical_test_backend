package com.vanlooy.similarproducts.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestClient productApiRestClient(
            @Value("${existing-apis.base-url}") String baseUrl,
            @Value("${existing-apis.connect-timeout-ms}") long connectTimeoutMs,
            @Value("${existing-apis.response-timeout-ms}") long responseTimeoutMs,
            @Value("${existing-apis.max-connections}") int maxConnections) {

        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(maxConnections)
                .setMaxConnPerRoute(maxConnections)
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setConnectTimeout(Timeout.ofMilliseconds(connectTimeoutMs))
                        .build())
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setResponseTimeout(Timeout.ofMilliseconds(responseTimeoutMs))
                        .build())
                .build();

        return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .baseUrl(baseUrl)
                .build();
    }
}
