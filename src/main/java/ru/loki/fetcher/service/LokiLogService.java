package ru.loki.fetcher.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.loki.fetcher.dto.LokiRequestDTO;
import ru.loki.fetcher.dto.LokiResponseDTO;
import ru.loki.fetcher.dto.logs.LogsResponseDTO;
import ru.loki.fetcher.dto.logs.ResultDTO;
import ru.loki.fetcher.feign.LokiLogsFeignClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class LokiLogService {
    private final LokiLogsFeignClient lokiClient;
    private final ObjectMapper objectMapper;
    private final FileSaveService fileSaveService;


    /**
     * Метод для получения данных от Api loki
     *
     * @param queryParams параметры для поиска лога
     */
    public String fetchLogs(LokiRequestDTO queryParams) {
        try {
            String logQuery = queryParams.getQueryString();
            Object response = lokiClient.getLogs(
                    logQuery,
                    queryParams.getTimestamp(),
                    queryParams.getEndAsUnix(),
                    queryParams.getLimit(),
                    queryParams.getDirection());
            try {
                return response.toString();
            } catch (Exception e) {
                throw new RuntimeException("Ошибка преобразования ответа в строку");
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

        AtomicInteger retries = new AtomicInteger(0);
        int currentRetries = retries.incrementAndGet();
        log.info("Выполняется получение логов сервиса {} c инстанса {} c {} по {}",
                queryParams.getApplication(), queryParams.getInstance(),
                queryParams.getStartTime(), queryParams.getEndTime());

        queryParams.setTimestamp(queryParams.getStartAsUnix());
        String filename = FileSaveService.clearFilename(String.format("%s_%s-%s",
                queryParams.getInstance(),
                queryParams.getStartTime(),
                queryParams.getEndTime()));
        // Используется для Логирования
        long startTimestamp = queryParams.getStartAsUnix();
        long timeRange = queryParams.getEndAsUnix() - queryParams.getStartAsUnix();

        while (queryParams.getStartAsUnix() < queryParams.getEndAsUnix()) {
            LokiResponseDTO<LogsResponseDTO> response = null;
            try {
                String lokiResponse = fetchLogs(queryParams);
                try {
                    response = objectMapper.readValue(lokiResponse,
                            new TypeReference<LokiResponseDTO<LogsResponseDTO>>() {});
                } catch (Exception e) {
                    log.error("Ошибка: {}", e.toString());
                }
            } catch (Exception e) {
                log.error(Arrays.toString(e.getStackTrace()));
            }

            if(response == null) {
                currentRetries = retries.incrementAndGet();
                if(currentRetries == 3)
                    break;
                log.warn("Обращение к API Loki не успешно, осталось попыток {}", 3 - currentRetries);
            }
            else if(response.getData().getResult().isEmpty()) {
                log.info("По введенным параметрам не удалось ничего найти");
                break;
            }
            else {
                retries.set(0);
                List<Map.Entry<String, List<String>>> allEntries = new ArrayList<>();

                for (ResultDTO result : response.getData().getResult()) {
                    String pod = result.getStream().get("pod");
                    result.getValues().forEach(entry ->
                            allEntries.add(new AbstractMap.SimpleEntry<>(pod, entry)));
                }

                allEntries.sort(Comparator.comparingLong(
                        entry -> Long.parseLong(entry.getValue().get(0))
                ));

                for (Map.Entry<String, List<String>> entry : allEntries) {
                    List<String> logData = entry.getValue();
                    fileSaveService.saveToFile(
                            String.format(logData.get(1)),
                            filename
                    );
                }
                if(allEntries.size() < queryParams.getLimit())
                    break;

                queryParams.setTimestamp(Long.parseLong(allEntries.get(allEntries.size() - 1).getValue().get(0)) + 1);

                queryParams.setStartTime(TimeService.getLocalDateTime(queryParams.getTimestamp()));
                double progress = (double) (queryParams.getTimestamp() - startTimestamp) / timeRange * 100;
                log.info("Текущий прогресс: {}%", String.format("%.2f", progress));
            }
        }
    }
}
