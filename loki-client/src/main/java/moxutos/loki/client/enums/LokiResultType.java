package moxutos.loki.client.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LokiResultType {

    MATRIX("matrix"),
    STREAMS("streams");

    private final String resultType;
}
