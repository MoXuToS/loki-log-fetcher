package ru.loki.fetcher.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Configuration
@ConfigurationProperties(prefix = "loki.query")
@Data
public class LokiRequestDTOConfig {

    private String system;

    private String env;

    private int batchSize;

    private String application;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime from;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
}