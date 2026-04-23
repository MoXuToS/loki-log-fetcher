package moxutos.loki.client.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import moxutos.loki.client.config.properties.LokiClientProperties;
import moxutos.loki.client.rest.LokiHttpClientApi;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;

import reactor.netty.http.client.HttpClient;
import java.time.Duration;

/**
 * Конфигурация для {@link LokiHttpClientApi}
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(LokiClientProperties.class)
@ImportHttpServices(group = LokiRestClientConfig.GROUP_NAME, types = LokiHttpClientApi.class)
public class LokiRestClientConfig implements WebClientHttpServiceGroupConfigurer {

    public static final String GROUP_NAME = "loki";

    private final LokiClientProperties props;

    @Override
    public void configureGroups(@NotNull Groups<WebClient.Builder> groups) {
        groups.filterByName(GROUP_NAME).forEachClient((name, builder) ->
                builder
                        .baseUrl(props.getUrl())
                        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .clientConnector(new ReactorClientHttpConnector(httpClient()))
        );
    }

    private HttpClient httpClient() {
        return HttpClient.create()
                .responseTimeout(Duration.ofMillis(props.getResponseTimeout()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.getConnectTimeout())
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(props.getReadTimeout()))
                                .addHandlerLast(new WriteTimeoutHandler(props.getWriteTimeout()))
                );
    }
}
