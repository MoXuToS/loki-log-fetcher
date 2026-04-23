package moxutos.loki.client.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LokiStreamType {

    STDOUT("stdout"),
    STDERR("stderr");

    private final String value;
}
