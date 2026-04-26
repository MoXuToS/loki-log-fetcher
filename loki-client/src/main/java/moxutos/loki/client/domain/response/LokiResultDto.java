package moxutos.loki.client.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moxutos.loki.client.serialization.LokiValueDeserializer;

import java.util.List;

/**
 * Результат ответа от loki.
 */
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
    @JsonDeserialize(contentUsing = LokiValueDeserializer.class)
    List<LokiValueDto> values;
}
