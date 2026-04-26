package moxutos.loki.fetcher.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import moxutos.loki.fetcher.config.LokiLogsFeignConfig;

@FeignClient(name = "loki-service", url = "${loki.address}", configuration = LokiLogsFeignConfig.class)
public interface LokiLogsFeignClient {

    /**
     * Метод по отправки запроса к loki
     * для получения названия пода/ip инстанса
     *
     * @param query фильтры запроса
     * @param start начальная временная точка сбора логов
     * @param end конечная дата сбора логов
     */
    @GetMapping(value = "/loki/api/v1/series", produces = "application/json")
    String getInstances(
            @RequestParam("match[]") String query,
            @RequestParam("start") long start,
            @RequestParam("end") long end
    );
}
