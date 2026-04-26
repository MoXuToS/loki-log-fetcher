package moxutos.loki.client.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Статус ответа от loki.
 */
@Getter
@AllArgsConstructor
public enum LokiStatus {

    @JsonProperty("success")
    SUCCESS
}
