package moxutos.loki.fetcher;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Тест на поднятие контекста.
 */
@Slf4j
@SpringBootTest
class LokiLogPullerTest {

    @Test
    @DisplayName("Тест на поднятие контекста")
    void contextLoad() {
        Assertions.assertDoesNotThrow(() -> log.info("Тест на поднятие контекста"));
    }
}
