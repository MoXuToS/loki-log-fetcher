package moxutos.loki.fetcher;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import moxutos.loki.fetcher.service.LogPullerService;

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
        logPullerService.startPullingLogs();
    }
}
