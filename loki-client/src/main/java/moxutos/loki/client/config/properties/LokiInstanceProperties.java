package moxutos.loki.client.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * Настройки клиента loki.
 */
@Getter
@Setter
public class LokiInstanceProperties {

    /**
     * Url для обращения к loki.
     */
    @NotBlank
    private String url;

    /**
     * Включена ли авторизация для loki.
     */
    private Boolean authorizationEnabled = false;

    /**
     * Время ответа от loki.
     */
    @Positive
    private int responseTimeout = 30000;

    /**
     * Время подключения к loki.
     */
    @Positive
    private int connectTimeout = 5000;

    /**
     * Время чтения ответа от loki.
     */
    @Positive
    private int readTimeout = 20000;


    /**
     * Время запроса к loki.
     */
    @Positive
    private int writeTimeout = 5000;

    /**
     * Настройки ретраев.
     */
    private LokiRetryProperties retry;
}
