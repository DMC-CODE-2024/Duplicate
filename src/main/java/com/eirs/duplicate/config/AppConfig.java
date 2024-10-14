package com.eirs.duplicate.config;

import com.eirs.duplicate.constants.DBType;
import com.eirs.duplicate.dto.FileDataDto;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Configuration
public class AppConfig {


    @Value("${eirs.notification.url}")
    private String notificationUrl;

    @Value("${feature-name}")
    private String featureName;

    @Value("${dependent.feature-name}")
    private String dependentFeatureName;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    public DBType getDbType() {
        return driverClassName.startsWith("com.mysql") ? DBType.MYSQL : driverClassName.startsWith("oracle") ? DBType.ORACLE :
                DBType.NONE;
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(60_000);
        clientHttpRequestFactory.setReadTimeout(60_000);
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        return restTemplate;
    }

    @Bean
    public Map<String, NavigableMap<LocalDateTime, Set<FileDataDto>>> timeSeriesMap() {
        return new ConcurrentHashMap<>();
    }
}
