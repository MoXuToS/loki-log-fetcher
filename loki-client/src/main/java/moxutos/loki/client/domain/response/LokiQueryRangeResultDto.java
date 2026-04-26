package moxutos.loki.client.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moxutos.loki.client.enums.LokiStatus;

/**
 * Ответ от loki типа query_range.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LokiQueryRangeResultDto {

    LokiStatus status;
    LokiQueryRangeDataDto data;
}
