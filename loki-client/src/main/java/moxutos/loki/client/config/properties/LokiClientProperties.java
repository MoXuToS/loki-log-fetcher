package moxutos.loki.client.config.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Properties для настройки источников данных для взаимодействия с Grafana Loki.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "http.client.loki")
public class LokiClientProperties {

    @NotEmpty
    Map<String, LokiInstanceProperties> datasources;
}
