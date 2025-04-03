package ru.loki.fetcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.loki.fetcher.config.LokiRequestDTOConfig;
import ru.loki.fetcher.dto.LokiRequestDTO;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecuteLogService {
    private final LokiRequestDTOConfig queryParamsBuilder;

    private final LokiLogService logService;

    public void executeLogs(List<String> instances) {
        int ThreadsCount = instances.size();
        log.info("Было найдено {} инстансов", ThreadsCount);
        ExecutorService executeService = Executors.newFixedThreadPool(ThreadsCount);
        log.info("Было инициировано {} потоков", ThreadsCount);
        LokiRequestDTO requestDTOTemplate = LokiRequestDTO.create(queryParamsBuilder);

        List<CompletableFuture<Void>> tasks = new ArrayList<>();

        for (String instance : instances) {
            LokiRequestDTO ThreadParams = requestDTOTemplate.copy();
            ThreadParams.setInstance(instance);
            CompletableFuture<Void> task = CompletableFuture.runAsync(
                    () -> {
                        try {
                            logService.getLogs(ThreadParams);
                        } catch (Exception e) {
                            log.error("Возникла ошибка при обработке запроса к {}", instance);
                        }
                    },
                    executeService
            );

            tasks.add(task);
        }

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                tasks.toArray(new CompletableFuture[0]));

        try {
            allTasks.join();
        } catch (Exception e) {
            log.error("Возникла проблема в работе {}", e.toString());
        } finally {
            executeService.shutdown();
        }
    }

}
