package moxutos.loki.client.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Наименование потока с ответом от loki.
 */
@Getter
@AllArgsConstructor
public enum LokiStreamType {

    @JsonProperty("stdout")
    STDOUT,

    @JsonProperty("stderr")
    STDERR
}
