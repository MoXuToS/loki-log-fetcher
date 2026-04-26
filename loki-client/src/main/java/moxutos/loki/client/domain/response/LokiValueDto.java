package moxutos.loki.client.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Значение полученное в результате получения логов в query_range.
 * <a href="https://grafana.com/docs/loki/latest/reference/loki-http-api/#step-versus-interval">Loki HTTP API</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LokiValueDto {
    long epochNanos;
    private String logLine;

}
