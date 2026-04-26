package moxutos.loki.client.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moxutos.loki.client.enums.LokiResultType;

import java.util.List;

/**
 * Основная dto с ответом от loki.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LokiQueryRangeDataDto {

    LokiResultType resultType;

    List<LokiResultDto> result;
}
