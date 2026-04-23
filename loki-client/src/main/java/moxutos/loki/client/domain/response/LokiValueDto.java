package moxutos.loki.client.domain.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    /**
     * <a href="https://grafana.com/docs/loki/latest/reference/loki-http-api/#step-versus-interval">Loki HTTP API</a>
     * @param values значения логов
     */
    @JsonCreator
    public LokiValueDto(List<String> values) {
        if (values == null || values.size() < 2) {
            throw new IllegalArgumentException("Некорректные данные от grafana loki: " + values);
        }

        this.epochNanos = Long.parseLong(values.get(0));
        this.logLine = values.get(1);
    }
}
