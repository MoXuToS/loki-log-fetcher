package ru.loki.fetcher.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.loki.fetcher.config.LokiRequestDTOConfig;
import ru.loki.fetcher.dto.LokiRequestDTO;
import ru.loki.fetcher.dto.LokiResponseDTO;
import ru.loki.fetcher.dto.instance.InstanceResponseDTO;
import ru.loki.fetcher.feign.LokiLogsFeignClient;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogPullerService {
    private final LokiRequestDTOConfig queryParamsBuilder;
    private final ObjectMapper objectMapper;
    private final LokiLogsFeignClient lokiClient;
    private final ExecuteLogService executeLogService;
    
    @Value("${loki.query.pod:#{null}}")
    private String podName;
    
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
            List<String> instances = new ArrayList<String>();
            Set<String> uniquePods = new HashSet<String>();
            Assert.notNull(response, "Получен пустой ответ от Loki");
            if(!response.getData().isEmpty()) {
                for(InstanceResponseDTO instance : response.getData()) {
                    String pod = instance.getPod();
                    if(!uniquePods.contains(pod)) {
                        instances.add(pod);
                        uniquePods.add(pod);
                    }
                }
                if (podName == null)
                  executeLogService.executeLogs(instances);
                else {
                  for (String instance : instances) {
                    if (instance.equals(podName)) {
                      executeLogService.executeLogs(Collections.singletonList(instance));
                      break;
                    }
                  }
                }
            }
            else {
                log.error("Было не найдено не одного инстанса для сбора логов");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
