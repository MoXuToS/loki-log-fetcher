package ru.loki.fetcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.loki.fetcher.service.LogPullerService;

@Slf4j
@SpringBootApplication
@EnableFeignClients
@RequiredArgsConstructor
public class LokiLogPuller implements CommandLineRunner {
    private final LogPullerService logPullerService;

    public static void main(String[] args) {
        SpringApplication.run(LokiLogPuller.class, args);
    }

    @Override
    public void run(String[] args) {
        log.info("*********************************");
        log.info("|   LokiLogPuller    Started    |");
        log.info("*********************************");
        logPullerService.startPullingLogs();
    }
}
