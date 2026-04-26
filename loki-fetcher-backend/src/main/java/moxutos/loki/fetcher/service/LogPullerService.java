package moxutos.loki.fetcher.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moxutos.loki.client.core.LokiClientRegistry;
import moxutos.loki.client.rest.LokiHttpClientApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import moxutos.loki.fetcher.config.LokiRequestDTOConfig;
import moxutos.loki.fetcher.dto.LokiRequestDTO;
import moxutos.loki.fetcher.dto.LokiResponseDTO;
import moxutos.loki.fetcher.dto.instance.InstanceResponseDTO;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogPullerService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final LokiRequestDTOConfig queryParamsBuilder;
    private final LokiClientRegistry lokiClients;
    private final ExecuteLogService executeLogService;

    @Value("${loki.query.pod:#{null}}")
    private String podName;

    private String getInstances() {
        LokiRequestDTO requestDTO = LokiRequestDTO.create(queryParamsBuilder);
        try {
            String instanceQuery = requestDTO.getInstanceQuery();
            LokiHttpClientApi lokiHttpClientApi = lokiClients.get("test");
            Mono<String> response = lokiHttpClientApi.getSeries(
                    instanceQuery,
                    // Вычитаем 6 часов, чтобы гарантировано получить все инстансы
                    // Какой-то прикол API loki для маленьких time range
                    requestDTO.getStartAsUnix() - 21_600_000_000_000L,
                    requestDTO.getEndAsUnix()
            );
            log.info("Успешно получили список инстансов");
            try {
                return response.block().toString();
            } catch (Exception e) {
                throw new RuntimeException("Ошибка преобразования ответа в строку");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startPullingLogs() {
        // TODO переписать на реактивную модель
        try {
            String lokiResponse = getInstances();
            LokiResponseDTO<List<InstanceResponseDTO>> response = null;
            try {
                response = OBJECT_MAPPER.readValue(lokiResponse,
                        new TypeReference<>() {});
            } catch (Exception e) {
                log.error("Ошибка: {}", e.toString());
            }
            List<String> instances = new ArrayList<>();
            Set<String> uniquePods = new HashSet<>();
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
