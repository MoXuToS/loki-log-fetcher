package moxutos.loki.client.rest;

import moxutos.loki.client.domain.response.LokiQueryRangeResultDto;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

/**
 * WebClient для создания запросов к loki.
 */
public interface LokiHttpClientApi {

    /**
     * Метод по отправки запроса к loki
     * для получения логов
     *
     * @param query фильтры запроса
     * @param start начальная временная точка сбора логов
     * @param end конечная дата сбора логов
     * @param limit количество строчек логов
     * @param direction порядок сортировки логов
     */
    @GetExchange("/loki/api/v1/query_range")
    Mono<LokiQueryRangeResultDto> getQueryRangeResult(
            @RequestParam("query") String query,
            @RequestParam("start") long start,
            @RequestParam("end") long end,
            @RequestParam("limit") int limit,
            @RequestParam("direction") String direction
    );
}
