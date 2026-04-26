package moxutos.loki.client.customizer;

import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.webclient.WebClientCustomizer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Добавить метрики для обращения к grafana loki.
 */
@Component
@Order(0)
@RequiredArgsConstructor
public class LokiObservationCustomizer implements WebClientCustomizer {

    private final ObjectProvider<ObservationRegistry> registryProvider;

    @Override
    public void customize(@NotNull WebClient.Builder builder) {
        ObservationRegistry registry = registryProvider.getIfAvailable();

        if (registry != null) {
            builder.observationRegistry(registry);
        }
    }
}
