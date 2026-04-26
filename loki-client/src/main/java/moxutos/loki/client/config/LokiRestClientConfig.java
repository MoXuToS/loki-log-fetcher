package moxutos.loki.client.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import moxutos.loki.client.config.properties.LokiClientProperties;
import moxutos.loki.client.config.properties.LokiInstanceProperties;
import moxutos.loki.client.core.LokiClientRegistry;
import moxutos.loki.client.rest.LokiHttpClientApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webclient.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Конфигурация для {@link LokiHttpClientApi}
 */
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(LokiClientProperties.class)
public class LokiRestClientConfig {

    private final LokiClientProperties props;
    private final ObjectProvider<WebClientCustomizer> customizers;

    /**
     * Создать бин репозитория с клиентами loki.
     * @return {@link LokiClientRegistry}
     */
    @Bean
    public LokiClientRegistry lokiClients() {
        Map<String, LokiHttpClientApi> clients = Optional.ofNullable(props.getDatasources())
                .filter(MapUtils::isNotEmpty)
                .map(this::getClients)
                .orElseThrow(() -> new IllegalStateException("Не найдено не одного источника loki"));

        return new LokiClientRegistry(clients);
    }

    private Map<String, LokiHttpClientApi> getClients(Map<String, LokiInstanceProperties> datasources) {
        return datasources.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> createClient(entry.getValue()),
                        (a, b) -> a,
                        HashMap::new
                ));
    }

    private LokiHttpClientApi createClient(LokiInstanceProperties cfg) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(cfg.getResponseTimeout()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, cfg.getConnectTimeout())
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(cfg.getReadTimeout()))
                                .addHandlerLast(new WriteTimeoutHandler(cfg.getWriteTimeout()))
                );

        WebClient.Builder builder = WebClient.builder()
                .baseUrl(cfg.getUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient));

        customizers.orderedStream().forEach(c -> c.customize(builder));
        WebClient client = builder.build();
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory
                        .builderFor(WebClientAdapter.create(client))
                        .build();

        return factory.createClient(LokiHttpClientApi.class);
    }
}
