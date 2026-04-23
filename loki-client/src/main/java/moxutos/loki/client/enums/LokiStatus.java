package moxutos.loki.client.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LokiStatus {

    SUCCESS("success");

    private final String status;
}
