package ru.loki.fetcher.dto;

import lombok.Builder;
import lombok.Data;
import ru.loki.fetcher.config.LokiRequestDTOConfig;
import ru.loki.fetcher.service.TimeService;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * DTO для передачи параметров запроса к Loki.
 */
@Data
@Builder(toBuilder = true)
public class LokiRequestDTO {
    private String system;
    private String env;
    private int limit;
    private String application;
    private String direction;
    private String Instance;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long timestamp;

    /**
     * Создает экземпляр DTO на основе конфигурации.
     *
     * @param config конфигурация с параметрами для запроса
     * @return заполненный объект LokiRequestDTO
     */
    public static LokiRequestDTO create(LokiRequestDTOConfig config) {
        return LokiRequestDTO.builder()
                .system(config.getSystem())
                .env(config.getEnv())
                .limit(config.getBatchSize())
                .application(config.getApplication())
                .direction("forward")
                .startTime(config.getFrom())
                .endTime(config.getEnd())
                .build();
    }

    /**
     * Формирует строку запроса для Loki в формате LogQL.
     *
     * @return строка запроса с подставленными параметрами
     *         (например: {system="system", env="env", container="app", pod=~"pod-.*"})
     */
    public String getQueryString() {
        return String.format(
                "{system=\"%s\", env=\"%s\", container=\"%s\", pod=~\"%s\"}",
                this.getSystem(),
                this.getEnv(),
                this.getApplication(),
                this.getInstance()
        );
    }

    public String getInstanceQuery() {
        return String.format(
                "{system=\"%s\", env=\"%s\", container=\"%s\"}",
                this.getSystem(),
                this.getEnv(),
                this.getApplication()
        );
    }

    public LokiRequestDTO copy() {
        return this.toBuilder().build();
    }

    /**
     * Конвертирует startTime в Unix-время (секунды с эпохи).
     *
     * @return количество секунд с 1970-01-01T00:00:00Z с учетом системной временной Moscow
     */
    public long getStartAsUnix() {
        return TimeService.localDateTimeToNanos(this.startTime);
    }

    /**
     * Конвертирует endTime в Unix-время (секунды с эпохи).
     *
     * @return количество секунд с 1970-01-01T00:00:00Z с учетом системной Moscow
     */
    public long getEndAsUnix() {
        return TimeService.localDateTimeToNanos(this.endTime);
    }
}
