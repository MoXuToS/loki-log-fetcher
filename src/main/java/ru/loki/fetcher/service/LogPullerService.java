package ru.loki.fetcher.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.loki.fetcher.config.LokiRequestDTOConfig;
import ru.loki.fetcher.dto.LokiRequestDTO;
import ru.loki.fetcher.dto.LokiResponseDTO;
import ru.loki.fetcher.dto.instance.InstanceResponseDTO;
import ru.loki.fetcher.feign.LokiLogsFeignClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogPullerService {
    private final LokiRequestDTOConfig queryParamsBuilder;
    private final ObjectMapper objectMapper;
    private final LokiLogsFeignClient lokiClient;
    private final ExecuteLogService executeLogService;

    private String getInstances() {
        LokiRequestDTO requestDTO = LokiRequestDTO.create(queryParamsBuilder);
        try {
            String instanceQuery = requestDTO.getInstanceQuery();
            Object response = lokiClient.getInstances(
                    instanceQuery,
                    // Вычитаем 6 часов, чтобы гарантировано получить все инстансы
                    // Какой-то прикол API loki для маленьких time range
                    requestDTO.getStartAsUnix() - 21_600_000_000_000L,
                    requestDTO.getEndAsUnix()
            );
            log.info("Успешно получили список инстансов");
            try {
                return response.toString();
            } catch (Exception e) {
                throw new RuntimeException("Ошибка преобразования ответа в строку");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startPullingLogs() {
        try {
            String lokiResponse = getInstances();
            LokiResponseDTO<List<InstanceResponseDTO>> response = null;
            try {
                response = objectMapper.readValue(lokiResponse,
                        new TypeReference<LokiResponseDTO<List<InstanceResponseDTO>>>() {});
            } catch (Exception e) {
                log.error("Ошибка: {}", e.toString());
            }
            List<String> incstancies = new ArrayList<String>();
            Set<String> uniquePods = new HashSet<String>();
            assert response != null;
            if(!response.getData().isEmpty()) {
                for(InstanceResponseDTO instance : response.getData()) {
                    String pod = instance.getPod();
                    if(!uniquePods.contains(pod)) {
                        incstancies.add(pod);
                        uniquePods.add(pod);
                    }
                }
                executeLogService.executeLogs(incstancies);
            }
            else {
                log.error("Было не найдено не одного инстанса для сбора логов");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
