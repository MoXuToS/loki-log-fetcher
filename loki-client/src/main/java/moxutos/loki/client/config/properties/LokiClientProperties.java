package moxutos.loki.client.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties для настройки http client для взаимодействия с Grafana Loki.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "http.client.loki")
public class LokiClientProperties {

    @NotBlank
    private String url;

    private Boolean authorizationEnabled = false;

    @Positive
    private int responseTimeout = 30000;

    @Positive
    private int connectTimeout = 5000;

    @Positive
    private int readTimeout = 20000;

    @Positive
    private int writeTimeout = 5000;
}
