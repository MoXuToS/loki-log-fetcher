package moxutos.loki.client.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LokiResultDto {

    /**
     * Метаданные ответа.
     */
    LokiResultStreamDto stream;

    /**
     * Результат.
     */
    List<LokiValueDto> values;
}
