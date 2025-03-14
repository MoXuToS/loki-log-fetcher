package ru.loki.fetcher.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TimeService {

    /**
     * Метод для получения даты
     *
     * @param timestamp unix-time
     */
    static LocalDateTime getLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochSecond(
                timestamp / 1_000_000_000,
                timestamp % 1_000_000_000
        );
        return instant.atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime();
    }

}
