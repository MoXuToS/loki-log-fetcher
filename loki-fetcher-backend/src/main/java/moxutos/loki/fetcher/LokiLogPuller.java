package moxutos.loki.fetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LokiLogPuller {

    public static void main(String[] args) {
        SpringApplication.run(LokiLogPuller.class, args);
    }

}
