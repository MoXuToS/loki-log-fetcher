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
import java.util.List;

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
                    requestDTO.getStartAsUnix(),
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
            assert response != null;
            if(!response.getData().isEmpty())
                for(InstanceResponseDTO instance : response.getData()) {
                    incstancies.add(instance.getPod());
                }
            else {
                log.error("Было не найдено не одного инстанса для сбора логов");
                throw new RuntimeException();
            }
            executeLogService.executeLogs(incstancies);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
