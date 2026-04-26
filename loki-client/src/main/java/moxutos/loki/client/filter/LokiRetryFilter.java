package moxutos.loki.client.filter;

import lombok.experimental.UtilityClass;
import moxutos.loki.client.config.properties.LokiRetryProperties;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Конфигурация для ретраев.
 */
@UtilityClass
public class LokiRetryFilter {

    private static final double JITTER = 0.3;

    /**
     * Создать фильтр на основе ретрай настроек для ретраев.
     * @param props настройки
     * @return фильтр ретраев.
     */
    public ExchangeFilterFunction createRetryFilter(LokiRetryProperties props) {
        return (request, next) ->
            next.exchange(request)
                .retryWhen(
                    Retry.backoff(
                            props.getRetryCount(),
                            props.getRetryInterval()
                        )
                        .jitter(JITTER)
                        .filter(LokiRetryFilter::isRetryable)
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure())
                );
    }

    private boolean isRetryable(Throwable t) {
        if (t instanceof WebClientResponseException w) {
            return w.getStatusCode().is5xxServerError() || w.getStatusCode().value() == 429;
        }

        return t instanceof IOException || t instanceof TimeoutException || t instanceof WebClientRequestException;
    }
}


