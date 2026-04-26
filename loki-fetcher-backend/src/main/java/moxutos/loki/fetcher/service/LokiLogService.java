package moxutos.loki.fetcher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moxutos.loki.client.core.LokiClientRegistry;
import moxutos.loki.client.domain.response.LokiQueryRangeResultDto;
import moxutos.loki.client.domain.response.LokiResultDto;
import moxutos.loki.client.rest.LokiHttpClientApi;
import org.springframework.stereotype.Service;
import moxutos.loki.fetcher.dto.LokiRequestDTO;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class LokiLogService {

    private final LokiClientRegistry lokiClients;
    private final FileSaveService fileSaveService;


    /**
     * Метод для получения данных от Api loki
     *
     * @param queryParams параметры для поиска лога
     */
    public LokiQueryRangeResultDto fetchLogs(LokiRequestDTO queryParams) {
        try {
            String logQuery = queryParams.getQueryString();
            LokiHttpClientApi lokiHttpClientApi = lokiClients.get("test");
            Mono<LokiQueryRangeResultDto> response = lokiHttpClientApi.getQueryRangeResult(
                    logQuery,
                    queryParams.getTimestamp(),
                    queryParams.getEndAsUnix(),
                    queryParams.getLimit(),
                    queryParams.getDirection());
            try {
                 return response.block();
            } catch (Exception e) {
                log.error("Ошибка преобразования ответа в строку: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            log.error("Ошибка обращения к API loki {}", e.toString());
            // Докидываем 0,1 секунды, чтобы попробовать получить данные с нового интервала
            queryParams.setTimestamp(queryParams.getTimestamp() + 100_000_000);
            throw new RuntimeException();
        }
    }


    public void getLogs(LokiRequestDTO queryParams) {
        try {
            fileSaveService.createFolder(String.format("%s_%s_logs_%s",
                    queryParams.getSystem(),
                    queryParams.getApplication(),
                    queryParams.getStartTime().toString().substring(0, 10)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Выполняется получение логов сервиса {} c инстанса {} c {} по {}",
                queryParams.getApplication(), queryParams.getInstance(),
                queryParams.getStartTime(), queryParams.getEndTime());

        queryParams.setTimestamp(queryParams.getStartAsUnix());

        while (queryParams.getStartAsUnix() < queryParams.getEndAsUnix()) {
            // TODO убрать весь этот страшный мусор и сделать нормально
            LokiQueryRangeResultDto response = null;
            try {
                response = fetchLogs(queryParams);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }

            if (response != null && response.getData() != null) {
                for (LokiResultDto resultDto : response.getData().getResult()) {
                    resultDto.getValues().forEach(value -> log.info(value.getLogLine()));
                }
            }
            break;
        }
    }
}
