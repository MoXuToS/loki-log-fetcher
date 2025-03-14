package ru.loki.fetcher.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.loki.fetcher.config.LokiRequestDTOConfig;
import ru.loki.fetcher.dto.LokiRequestDTO;
import ru.loki.fetcher.dto.LokiResponseDTO;
import ru.loki.fetcher.dto.ResultDTO;
import ru.loki.fetcher.feign.LokiLogsFeignClient;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class LokiLogService {
    private final LokiLogsFeignClient lokiClient;
    private final LokiRequestDTOConfig queryParamsBuilder;
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


    public void getLogs() {
        LokiRequestDTO queryParams = LokiRequestDTO.create(queryParamsBuilder);
        log.info("Выполняется получение логов сервиса {} c {} по {}",
                queryParams.getApplication(), queryParams.getStartTime(), queryParams.getEndTime());

        queryParams.setPodPattern(queryParams.getApplication() + "-.*");
        queryParams.setTimestamp(queryParams.getStartAsUnix());
        String filename = FileSaveService.clearFilename(String.format("log_%s_%s_%s.log",
                queryParams.getApplication(),
                queryParams.getStartTime(),
                queryParams.getEndTime()));

        while (queryParams.getStartAsUnix() < queryParams.getEndAsUnix()) {
            LokiResponseDTO response = null;
            try {
                String lokiResponse = fetchLogs(queryParams);
                try {

                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    response = objectMapper.readValue(lokiResponse, LokiResponseDTO.class);
                } catch (Exception e) {
                    log.error("Ошибка: {}", e.toString());
                }
            } catch (Exception e) {
                log.error(Arrays.toString(e.getStackTrace()));
            }

            if (response != null && !response.getData().getResult().isEmpty()) {
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
                            String.format("\"%s\" | \"%s\"", entry.getKey(), logData.get(1)),
                            filename
                    );
                }
                if(allEntries.size() < queryParams.getLimit())
                    break;

                queryParams.setTimestamp(Long.parseLong(allEntries.get(allEntries.size() - 1).getValue().get(0)) + 1);
            }

            queryParams.setStartTime(TimeService.getLocalDateTime(queryParams.getTimestamp()));
            log.info("Текущий прогресс: {}", queryParams.getStartTime());
        }
    }
}
