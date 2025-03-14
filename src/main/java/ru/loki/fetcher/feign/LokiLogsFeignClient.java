package ru.loki.fetcher.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.loki.fetcher.config.LokiLogsFeignConfig;

@FeignClient(name = "loki-service", url = "${loki.address}", configuration = LokiLogsFeignConfig.class)
public interface LokiLogsFeignClient {

    /**
     * Метод по отправки запроса к loki
     * для получению логов
     *
     * @param query фильтры запроса
     * @param start начальная временная точка сбора логов
     * @param end конечная дата сбора логов
     * @param limit количество строчек логов
     * @param direction порядок сортировки логов
     */
    @GetMapping(value = "/loki/api/v1/query_range", produces = "application/json")
    Object getLogs(
            @RequestParam("query") String query,
            @RequestParam("start") long start,
            @RequestParam("end") long end,
            @RequestParam("limit") int limit,
            @RequestParam("direction") String direction
    );
}
