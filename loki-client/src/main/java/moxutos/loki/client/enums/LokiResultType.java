package moxutos.loki.client.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Тип ответа от loki.
 */
@Getter
@AllArgsConstructor
public enum LokiResultType {

    @JsonProperty("matrix")
    MATRIX,

    @JsonProperty("streams")
    STREAMS
}
