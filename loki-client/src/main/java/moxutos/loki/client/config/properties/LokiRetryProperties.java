package moxutos.loki.client.config.properties;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * Настройки ретраев для loki.
 */
@Getter
@Setter
public class LokiRetryProperties {

    /**
     * Количество ретраев.
     */
    @Positive
    private int retryCount = 3;

    /**
     * Интервал для ретраев.
     */
    private Duration retryInterval = Duration.ofMillis(1000);
}
