package moxutos.loki.fetcher.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moxutos.loki.fetcher.service.LogPullerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Tag(name = "Logs API", description = "Работа с логами v1")
public class LogsController {

    private final LogPullerService logPullerService;

    @GetMapping
    @Operation(summary = "Запуск получения логов", description = "Инициирует фоновый процесс сбора логов")
    @ApiResponse(responseCode = "200", description = "Запрос принят")
    public Mono<Void> getLogs() {
        Mono.fromRunnable(logPullerService::startPullingLogs)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(null, error -> log.error("Ошибка при сборе логов", error));

        return Mono.empty();
    }
}
