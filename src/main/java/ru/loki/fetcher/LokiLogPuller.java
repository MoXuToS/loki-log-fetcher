package ru.loki.fetcher;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.loki.fetcher.service.LokiLogService;

@SpringBootApplication
@EnableFeignClients
@RequiredArgsConstructor
public class LokiLogPuller implements CommandLineRunner {
    private final LokiLogService logGetter;

    public static void main(String[] args) {
        SpringApplication.run(LokiLogPuller.class, args);
    }

    @Override
    public void run(String[] args) {
        logGetter.getLogs();
    }
}
